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
import org.yesworkflow.annotations.Uri;
import org.yesworkflow.config.YWConfiguration;
import org.yesworkflow.exceptions.YWToolUsageException;

public class DefaultExtractor implements Extractor {

    static private Language DEFAULT_LANGUAGE = Language.GENERIC;
    
    private LanguageModel globalLanguageModel = null;
    private Language lastLanguage = null;
    private BufferedReader sourceReader = null;
    private List<String> sources;
    private List<String> lines;
    private List<String> comments;
    private List<Annotation> annotations;
    private YWKeywords keywordMapping;
    private KeywordMatcher keywordMatcher;
    private String commentListingPath;    
    private PrintStream stdoutStream = null;
    private PrintStream stderrStream = null;

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
        }
        
        return this;
    }
    
    @Override
    public Language getLanguage() {
        return lastLanguage;
    }

    @Override
    public List<String> getLines() {
        return lines;
    }

    @Override
    public List<String> getComments() {
        return comments;
    }

	@Override
	public List<Annotation> getAnnotations() {
		return annotations;
	}

    @Override
    public DefaultExtractor extract() throws Exception {

        lines = new LinkedList<String>();
        
        if (sourceReader != null) {
            extractLines(sourceReader, globalLanguageModel);
        } else if (sources == null || 
                   sources.size() == 0 || 
                   sources.size() == 1 && (sources.get(0).trim().isEmpty() || 
                                           sources.get(0).trim().equals("-"))) {
            extractLinesFromStdin();
        } else {
            extractLinesFromFiles(sources);
        }
        
        writeCommentListing();
        extractComments();
        extractAnnotations();
        
        if (comments.isEmpty()) {
            stderrStream.println("WARNING: No YW comments found in source code.");
        }        
        
        return this;
    }
    
    private void extractLinesFromStdin() throws IOException, YWToolUsageException {
        Reader reader = new InputStreamReader(System.in);
        extractLines(new BufferedReader(reader), globalLanguageModel);
    }

    private void extractLinesFromFiles(List<String> sourcePaths) throws IOException, YWToolUsageException {
        for (String sourcePath : sourcePaths) {
            LanguageModel languageModel = null;
            if (globalLanguageModel != null) {
                languageModel = globalLanguageModel;
            } else {
                Language language = LanguageModel.languageForFileName(sourcePath);
                if (language != null) {
                    languageModel = new LanguageModel(language);
                }
            }
            extractLines(getFileReaderForPath(sourcePath), languageModel);
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
    
    private void extractLines(BufferedReader reader, LanguageModel languageModel) throws IOException {

        if (languageModel == null) {
            languageModel = new LanguageModel(DEFAULT_LANGUAGE);
        }

        lastLanguage = languageModel.getLanguage();
        
        // extract all comments from script using the language model
        CommentMatcher commentMatcher = new CommentMatcher(languageModel);
        List<String> allCommentLines = commentMatcher.getCommentsAsLines(reader);

        // select only the comments that contain YW keywords,
        // trimming characters preceding the first YW keyword in each
        lines.addAll(keywordMatcher.match(allCommentLines, true));
    }

    private void writeCommentListing() throws IOException {
        if (commentListingPath != null) {
            StringBuffer linesBuffer = new StringBuffer();
            for (String line : lines) {
                linesBuffer.append(line);
                linesBuffer.append(System.getProperty("line.separator"));
            }
            writeTextToFileOrStdout(commentListingPath, linesBuffer.toString());
        }
    }

    private void writeTextToFileOrStdout(String path, String text) throws IOException {
        PrintStream stream = (path == null || path.equals(YWConfiguration.EMPTY_VALUE) || path.equals("-")) ?
                             stdoutStream : new PrintStream(path);
        stream.print(text);
        if (stream != stdoutStream) {
            stream.close();
        }
    }

    private void extractComments() throws Exception {
    	
    	comments = new LinkedList<String>();

        for (String commentLine : lines) {
        	List<String> commentsOnLine = findCommentsOnLine(commentLine, keywordMatcher);
        	for (String comment : commentsOnLine) {
        		comments.add(comment);
        	}
        }
    }
    
    private void extractAnnotations() throws Exception {
    	
    	annotations = new LinkedList<Annotation>();

    	Annotation primaryAnnotation = null;
    	for (String s : comments) {
    		
            Tag tag = KeywordMatcher.extractInitialKeyword(s, keywordMapping);
            
            Annotation annotation = null;
            switch(tag) {
                case BEGIN:  annotation = new Begin(s);  break;
                case CALL:   annotation = new Call(s);    break;
                case END:    annotation = new End(s);    break;
                case IN:     annotation = new In(s);     break;
                case OUT:    annotation = new Out(s);    break;
                case AS:     annotation = new As(s);     break;
                case PARAM:  annotation = new Param(s);  break;
                case RETURN: annotation = new Return(s); break;
                case URI:    annotation = new Uri(s);    break;   
            }

            if (annotation instanceof Qualification) {
            	if (primaryAnnotation != null) {
                	primaryAnnotation.qualifyWith((Qualification)annotation);
            	} else {
            		throw new Exception("Qualification annotation found before primary annotation.");
            	}
            } else {
            	primaryAnnotation = annotation;
                annotations.add(annotation);
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
