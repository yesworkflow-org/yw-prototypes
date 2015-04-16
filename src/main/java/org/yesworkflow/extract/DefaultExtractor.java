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
import org.yesworkflow.annotations.End;
import org.yesworkflow.annotations.In;
import org.yesworkflow.annotations.Out;
import org.yesworkflow.annotations.Param;
import org.yesworkflow.annotations.Qualification;
import org.yesworkflow.annotations.Uri;
import org.yesworkflow.exceptions.YWToolUsageException;

public class DefaultExtractor implements Extractor {

    static private Language DEFAULT_LANGUAGE = Language.GENERIC;
    
    private LanguageModel languageModel = null;
    private BufferedReader sourceReader = null;
    private List<String> sources;
    private List<String> lines;
    private List<String> comments;
    private List<Annotation> annotations;
    private YWKeywords keywordMapping;
    private KeywordMatcher keywordMatcher;
    
    @SuppressWarnings("unused")
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
    public DefaultExtractor setLanguageBySource(String sourceFilePath) throws YWToolUsageException {
    
        Language language = LanguageModel.languageForFileName(sourceFilePath);
        if (language != null) {
            languageModel = new LanguageModel(language);
        } else {
            throw new YWToolUsageException("Cannot identify language of source file.");
        }
        return this;
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
                sources.add((String)value);
            } else if (value instanceof List) {
                sources.addAll((List<? extends String>) value);
            } else {
                throw new Exception("Value of graph.sources must be a list of strings");
            }
        } else if (key.equalsIgnoreCase("reader")) {
            this.sourceReader = new BufferedReader((Reader)value);
        } else if (key.equalsIgnoreCase("language")) {
            Language language = Language.toLanguage(value);
            languageModel = new LanguageModel(language);
        } else if (key.equalsIgnoreCase("languageModel")) {
            languageModel = (LanguageModel)value;
        } else if (key.equalsIgnoreCase("comment")) {        
            languageModel = new LanguageModel();
            languageModel.singleDelimiter((String)value);
        }
        
        return this;
    }
    
    @Override
    public Language getLanguage() {
        return languageModel.getLanguage();
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

        if (sourceReader != null) {
            extractLines(sourceReader);
        } else if (sources != null && sources.size() > 0) {
            if (sources.size() == 1) {
                extractLines(sources.get(0));
            }
        } else {
            throw new Exception("No source files provided to extractor.");
        }

        extractComments();
        extractAnnotations();
        
        if (comments.isEmpty()) {
            stderrStream.println("WARNING: No YW comments found in source code.");
        }        
        
        return this;
    }
    
    private void extractLines(String sourcePath) throws IOException, YWToolUsageException {
        
        if (sourcePath.isEmpty() || sourcePath.trim().equals("-")) {
            Reader reader = new InputStreamReader(System.in);
            extractLines(new BufferedReader(reader));
        } else {
            if (languageModel == null) {
                setLanguageBySource(sourcePath);
            }
            extractLines(getFileReaderForPath(sourcePath));
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
    
    private void extractLines(BufferedReader reader) throws IOException {

        if (this.languageModel == null) {
            this.languageModel = new LanguageModel(this.DEFAULT_LANGUAGE);
        }

        // extract all comments from script using the language model
        CommentMatcher commentMatcher = new CommentMatcher(languageModel);
        List<String> allCommentLines = commentMatcher.getCommentsAsLines(reader);

        // select only the comments that contain YW keywords,
        // trimming characters preceding the first YW keyword in each
        lines = keywordMatcher.match(allCommentLines, true);
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
                case BEGIN: annotation = new Begin(s);  break;
                case END:   annotation = new End(s);    break;
                case IN:    annotation = new In(s);     break;
                case OUT:   annotation = new Out(s);    break;
                case AS:    annotation = new As(s);		break;
                case PARAM: annotation = new Param(s);  break;
                case URI:   annotation = new Uri(s);    break;
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
}
