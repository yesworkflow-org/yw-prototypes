package org.yesworkflow.extract;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yesworkflow.Language;
import org.yesworkflow.LanguageModel;
import org.yesworkflow.YWKeywords;
import org.yesworkflow.YWKeywords.Tag;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.annotations.As;
import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.Call;
import org.yesworkflow.annotations.End;
import org.yesworkflow.annotations.In;
import org.yesworkflow.annotations.Out;
import org.yesworkflow.annotations.Param;
import org.yesworkflow.annotations.Qualification;
import org.yesworkflow.annotations.Return;
import org.yesworkflow.annotations.UriAnnotation;
import org.yesworkflow.config.YWConfiguration;
import org.yesworkflow.exceptions.YWToolUsageException;

public class DefaultExtractor implements Extractor {

    static private Language DEFAULT_LANGUAGE = Language.GENERIC;
    
    private LanguageModel globalLanguageModel = null;
    private Language lastLanguage = null;
    private BufferedReader sourceReader = null;
    private List<String> sources;
    private List<SourceLine> lines;
    private List<String> comments;
    private List<Annotation> allAnnotations;
    private List<Annotation> primaryAnnotations;
    private YWKeywords keywordMapping;
    private KeywordMatcher keywordMatcher;
    private String commentListingPath;    
    private String factsFile = null;
    private String extractFacts = null;
    private PrintStream stdoutStream = null;
    private PrintStream stderrStream = null;

    private Integer nextSourceId = 1;
    private Integer nextAnnotationId = 1;
    
    public DefaultExtractor() {
        this(System.out, System.err);
        this.keywordMapping = new YWKeywords();
        this.keywordMatcher = new KeywordMatcher(keywordMapping.getKeywords());
    }

    public DefaultExtractor(PrintStream stdoutStream, PrintStream stderrStream) {
        this.stdoutStream = stdoutStream;
        this.stderrStream = stderrStream;
        this.keywordMapping = new YWKeywords();
        this.keywordMatcher = new KeywordMatcher(keywordMapping.getKeywords());
    }

    @Override
    public DefaultExtractor configure(Map<String,Object> config) throws Exception {
        if (config != null) {
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                configure(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public DefaultExtractor configure(String key, Object value) throws Exception {
        if (key.equalsIgnoreCase("sources")) {
            sources = new LinkedList<String>();
            if (value instanceof String) {
                for (String token : ((String) value).split("\\s")) {
                    if (!token.trim().isEmpty()) {
                        sources.add(token);
                    }
                }
            } else if (value instanceof List) {
                sources.addAll((List<? extends String>) value);
            } else {
                throw new Exception("Value of sources property must be one or more strings");
            }
        } else if (key.equalsIgnoreCase("language")) {
            Language language = Language.toLanguage(value);
            globalLanguageModel = new LanguageModel(language);
        } else if (key.equalsIgnoreCase("languageModel")) {
            globalLanguageModel = (LanguageModel)value;
        } else if (key.equalsIgnoreCase("comment")) {        
            globalLanguageModel = new LanguageModel();
            globalLanguageModel.singleDelimiter((String)value);
        } else if (key.equalsIgnoreCase("listfile")) {
            commentListingPath = (String)value;
        } else if (key.equalsIgnoreCase("factsfile")) {
            factsFile = (String)value;
        }
        
        return this;
    }
    
    @Override
    public Language getLanguage() {
        return lastLanguage;
    }

    @Override
    public List<SourceLine> getLines() {
        return lines;
    }

    @Override
    public List<String> getComments() {
        return comments;
    }

	@Override
	public List<Annotation> getAnnotations() {
		return primaryAnnotations;
	}
    
	@Override
    public String getFacts() {
        if (extractFacts == null) {
            extractFacts = new ExtractFacts(sources, allAnnotations).build().toString();
        }
        return extractFacts;
    }
	
	
    @Override
    public DefaultExtractor extract() throws Exception {

        lines = new LinkedList<SourceLine>();
        
        if (sourceReader != null) {
            Source source = new Source(nextSourceId++, "__reader__");
            extractLines(source, sourceReader, globalLanguageModel);
            sources = new LinkedList<String>();
            sources.add("_reader__");
        } else if (sources == null || 
                   sources.size() == 0 || 
                   sources.size() == 1 && (sources.get(0).trim().isEmpty() || 
                                           sources.get(0).trim().equals("-"))) {
            sources = new LinkedList<String>();
            sources.add("__stdin__");
            extractLinesFromStdin();
        } else {
            extractLinesFromFiles(sources);
        }
        
        writeCommentListing();
        extractAnnotations();
        
        if (comments.isEmpty()) {
            stderrStream.println("WARNING: No YW comments found in source code.");
        }

        if (factsFile != null) {
            writeTextToFileOrStdout(factsFile, getFacts());
        }
        
        return this;
    }
    
    private void extractLinesFromStdin() throws IOException, YWToolUsageException {
        Reader reader = new InputStreamReader(System.in);
        Source source = new Source(nextSourceId++, "__stdin__");
        extractLines(source, new BufferedReader(reader), globalLanguageModel);
    }

    private void extractLinesFromFiles(List<String> sourcePaths) throws IOException, YWToolUsageException {
        for (String sourcePath : sourcePaths) {
            Source source = new Source(nextSourceId++, sourcePath);
            LanguageModel languageModel = null;
            if (globalLanguageModel != null) {
                languageModel = globalLanguageModel;
            } else {
                Language language = LanguageModel.languageForFileName(sourcePath);
                if (language != null) {
                    languageModel = new LanguageModel(language);
                }
            }
            extractLines(source, getFileReaderForPath(sourcePath), languageModel);
        }
    }

    public BufferedReader getFileReaderForPath(String path) throws YWToolUsageException {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            throw new YWToolUsageException("ERROR: Input file not found: " + path);
        }

        return reader;
    }
    
    private void extractLines(Source source, BufferedReader reader, LanguageModel languageModel) throws IOException {

        if (languageModel == null) {
            languageModel = new LanguageModel(DEFAULT_LANGUAGE);
        }

        lastLanguage = languageModel.getLanguage();
        
        // extract all comments from script using the language model
        CommentMatcher commentMatcher = new CommentMatcher(source, languageModel);
        List<SourceLine> allCommentLines = commentMatcher.getCommentsAsLines(reader);

        // select only the comments that contain YW keywords,
        // trimming characters preceding the first YW keyword in each
        lines.addAll(keywordMatcher.match(allCommentLines, true));
    }

    private void writeCommentListing() throws IOException {
        if (commentListingPath != null) {
            StringBuffer linesBuffer = new StringBuffer();
            for (SourceLine line : lines) {
                linesBuffer.append(line.text);
                linesBuffer.append(System.getProperty("line.separator"));
            }
            writeTextToFileOrStdout(commentListingPath, linesBuffer.toString());
        }
    }
    
    private void writeTextToFileOrStdout(String path, String text) throws IOException {  
        PrintStream stream = (path.equals(YWConfiguration.EMPTY_VALUE) || path.equals("-")) ?
                             this.stdoutStream : new PrintStream(path);
        stream.print(text);
        if (stream != this.stdoutStream) {
            stream.close();
        }
    }
    
    private void extractAnnotations() throws Exception {
    	
    	comments = new LinkedList<String>();
    	allAnnotations = new LinkedList<Annotation>();
        primaryAnnotations = new LinkedList<Annotation>();
        Annotation primaryAnnotation = null;

        for (SourceLine sourceLine : lines) {
        	List<String> commentsOnLine = findCommentsOnLine(sourceLine.text, keywordMatcher);
        	for (String comment : commentsOnLine) {
        		comments.add(comment);

        		Tag tag = KeywordMatcher.extractInitialKeyword(comment, keywordMapping);
            
                Annotation annotation = null;
                Integer id = nextAnnotationId++;
                switch(tag) {
                    case BEGIN:  annotation = new Begin(id, sourceLine, comment);                   break;
                    case CALL:   annotation = new Call(id, sourceLine, comment);                    break;
                    case END:    annotation = new End(id, sourceLine, comment);                     break;
                    case IN:     annotation = new In(id, sourceLine, comment);                      break;
                    case OUT:    annotation = new Out(id, sourceLine, comment);                     break;
                    case AS:     annotation = new As(id, sourceLine, comment, primaryAnnotation);   break;
                    case PARAM:  annotation = new Param(id, sourceLine, comment);                   break;
                    case RETURN: annotation = new Return(id, sourceLine, comment);                  break;
                    case URI:    annotation = new UriAnnotation(id, sourceLine, comment, primaryAnnotation);  break;   
                }
                
                allAnnotations.add(annotation);
    
                if (! (annotation instanceof Qualification)) {
                	primaryAnnotation = annotation;
                    primaryAnnotations.add(annotation);
                }
            }
        }
    }
    
    public static List<String> findCommentsOnLine(String line, KeywordMatcher keywordMatcher) {
    	
    	List<String> comments = new LinkedList<String>();
    	StringBuilder buffer = new StringBuilder();
    	StringBuilder currentComment = new StringBuilder();
    	
    	for (int i = 0; i < line.length(); ++i) {
    		
    		char c = line.charAt(i);
    		buffer.append(c);
    	
    		switch(keywordMatcher.matchesKeyword(buffer.toString())) {
    		
	    		case NO_MATCH:
	    			
	    			if (currentComment.length() > 0) currentComment.append(buffer);
	    			buffer.setLength(0);
	    			break;
	    		
	    		case FULL_MATCH:
	    			
	    			if (currentComment.length() > 0) {
	    				comments.add(currentComment.toString().trim());
	    				currentComment.setLength(0);
	    			}
    				currentComment.append(buffer);
    				buffer.setLength(0);
    				break;
	    			
	    		default:
	    			
	    			break;
    		}
    	}
    	
    	if (currentComment.length() > 0) {
    		currentComment.append(buffer);
    		comments.add(currentComment.toString().trim());
    	}
    	
    	return comments;
    }

    @Override
    public DefaultExtractor reader(Reader reader) {
        this.sourceReader = new BufferedReader(reader);
        return this;
    }
}
