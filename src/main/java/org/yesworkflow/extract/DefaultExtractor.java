package org.yesworkflow.extract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.yesworkflow.LanguageModel;
import org.yesworkflow.LanguageModel.Language;
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

public class DefaultExtractor implements Extractor {

    private BufferedReader sourceReader = null;
    private List<String> lines;
    private List<String> comments;
    private List<Annotation> annotations;
    private YWKeywords keywordMapping;
    private LanguageModel languageModel;
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
    public DefaultExtractor languageModel(LanguageModel lm) {
        languageModel = lm;
        return this;
    }
    
    @Override
    public DefaultExtractor commentDelimiter(String cd) {
        languageModel = new LanguageModel();
        languageModel.singleDelimiter(cd);
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

    
    public DefaultExtractor source(Reader reader) {
        this.sourceReader = new BufferedReader(reader);
        return this;
    }

    @Override
    public DefaultExtractor extract() throws Exception {

        extractLines();
        extractComments();
        extractAnnotations();
        
        if (comments.isEmpty()) {
            stderrStream.println("WARNING: No YW comments found in source code.");
        }
        
        return this;
    }

    private void extractLines() throws IOException {

        // extract all comments from script using the language model
        CommentMatcher commentMatcher = new CommentMatcher(languageModel);
        List<String> allCommentLines = commentMatcher.getCommentsAsLines(sourceReader);

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
