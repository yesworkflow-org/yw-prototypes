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
import org.yesworkflow.annotations.FileUri;
import org.yesworkflow.annotations.In;
import org.yesworkflow.annotations.Out;
import org.yesworkflow.annotations.Param;
import org.yesworkflow.annotations.Qualification;
import org.yesworkflow.annotations.Return;
import org.yesworkflow.annotations.UriAnnotation;
import org.yesworkflow.config.YWConfiguration;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.exceptions.YWToolUsageException;
import org.yesworkflow.query.QueryEngine;
import org.yesworkflow.query.QueryEngineModel;

public class DefaultExtractor implements Extractor {

    static private Language DEFAULT_LANGUAGE = Language.GENERIC;
    static private QueryEngine DEFAULT_QUERY_ENGINE = QueryEngine.SWIPL;
    
    private YesWorkflowDB ywdb;
    private LanguageModel globalLanguageModel = null;
    private Language lastLanguage = null;
    private QueryEngine queryEngine = DEFAULT_QUERY_ENGINE;
    private BufferedReader sourceReader = null;
    private List<String> sourcePaths;
    private List<Comment> lines;
    private List<String> comments;
    private List<Annotation> allAnnotations;
    private List<Annotation> primaryAnnotations;
    private YWKeywords keywordMapping;
    private KeywordMatcher keywordMatcher;
    private String commentListingPath;
    private String factsFile = null;
    private String skeletonFile = null;
    private String skeleton = null;
    private String extractFacts = null;
    private PrintStream stdoutStream = null;
    private PrintStream stderrStream = null;

    private Long nextAnnotationId = 1L;

    public DefaultExtractor() throws Exception {
        this(YesWorkflowDB.getGlobalInstance(), System.out, System.err);
    }

    public DefaultExtractor(YesWorkflowDB ywdb) {
        this(ywdb, System.out, System.err);
    }

    public DefaultExtractor(YesWorkflowDB ywdb, PrintStream stdoutStream, PrintStream stderrStream) {
        this.ywdb = ywdb;
        this.stdoutStream = stdoutStream;
        this.stderrStream = stderrStream;
        this.keywordMapping = new YWKeywords();
        this.keywordMatcher = new KeywordMatcher(keywordMapping.getKeywords());
        this.lines = new LinkedList<Comment>();
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
            sourcePaths = new LinkedList<String>();
            if (value instanceof String) {
                for (String token : ((String) value).split("\\s")) {
                    if (!token.trim().isEmpty()) {
                        sourcePaths.add(token);
                    }
                }
            } else if (value instanceof List) {
                sourcePaths.addAll((List<? extends String>) value);
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
        } else if (key.equalsIgnoreCase("skeletonfile")) {
            skeletonFile = (String)value;
        } else if (key.equalsIgnoreCase("queryengine")) {
            queryEngine = QueryEngine.toQueryEngine((String)value);
        }
        
        return this;
    }
    
    @Override
    public Language getLanguage() {
        return lastLanguage;
    }

    @Override
    public List<Comment> getLines() {
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
    public String getSkeleton() {
	    
	    if (skeleton == null) {
            SkeletonBuilder sb = new SkeletonBuilder( getSkeletonCommentDelimiter() + " ");
            for (Annotation annotation : allAnnotations) {
                sb.add(annotation);
            }
            sb.end();
            skeleton = sb.toString(); 
 	    }
	    
        return skeleton;
    }
	
	@Override
    public String getFacts(QueryEngineModel queryEngineModel) {
        if (extractFacts == null) {
            extractFacts = new ExtractFacts(ywdb, queryEngineModel, allAnnotations).build().toString();
        }
        return extractFacts;
    }	
	
    @Override
    public DefaultExtractor extract() throws Exception {
        
        extractLines();        
        writeCommentListing();
        extractAnnotations();
        writeSkeletonFile();
        
        if (comments.isEmpty()) {
            stderrStream.println("WARNING: No YW comments found in source code.");
        }

        if (factsFile != null) {
            QueryEngineModel queryEngineModel = new QueryEngineModel(queryEngine);
            writeTextToFileOrStdout(factsFile, getFacts(queryEngineModel));
        }
        
        return this;
    }

    private void extractLines() throws IOException, YWToolUsageException {

        if (sourceReader != null) {
            Long sourceId = ywdb.insertSource(null);
            extractLinesFromReader(sourceId, sourceReader, globalLanguageModel);
        
        } else if (sourcePaths == null || 
                   sourcePaths.size() == 0 || 
                   sourcePaths.size() == 1 && (sourcePaths.get(0).trim().isEmpty() || 
                                               sourcePaths.get(0).trim().equals("-"))) {
            Reader reader = new InputStreamReader(System.in);
            Long sourceId = ywdb.insertSource(null);
            extractLinesFromReader(sourceId, new BufferedReader(reader), globalLanguageModel);
        
        } else {
            extractLinesFromSourceFiles();
        }
    }
    
    private void extractLinesFromSourceFiles() throws IOException, YWToolUsageException {
        
        for (String sourcePath : sourcePaths) {

            Long sourceId = ywdb.insertSource(sourcePath);
            LanguageModel languageModel = null;
            if (globalLanguageModel != null) {
                languageModel = globalLanguageModel;
            } else {
                Language language = LanguageModel.languageForFileName(sourcePath);
                if (language != null) {
                    languageModel = new LanguageModel(language);
                }
            }
            extractLinesFromReader(sourceId, getFileReaderForPath(sourcePath), languageModel);
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
    
    private void extractLinesFromReader(Long sourceId, BufferedReader reader, LanguageModel languageModel) throws IOException {

        if (languageModel == null) {
            languageModel = new LanguageModel(DEFAULT_LANGUAGE);
        }

        lastLanguage = languageModel.getLanguage();
        
        // extract all comments from script using the language model
        CommentMatcher commentMatcher = new CommentMatcher(ywdb, sourceId, languageModel);
        List<Comment> allComments = commentMatcher.getCommentsAsLines(reader);

        // select only the comments that contain YW keywords,
        // trimming characters preceding the first YW keyword in each
        lines.addAll(keywordMatcher.match(allComments, true));
    }

    private void writeCommentListing() throws IOException {
        if (commentListingPath != null) {
            StringBuffer linesBuffer = new StringBuffer();
            for (Comment line : lines) {
                linesBuffer.append(line.text);
                linesBuffer.append(System.getProperty("line.separator"));
            }
            writeTextToFileOrStdout(commentListingPath, linesBuffer.toString());
        }
    }

    private void writeSkeletonFile() throws IOException {
        if (skeletonFile != null) {
            writeTextToFileOrStdout(skeletonFile, this.getSkeleton());
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
        
        for (Comment sourceLine : lines) {
        	List<String> commentsOnLine = findCommentsOnLine(sourceLine.text, keywordMatcher);
        	for (String comment : commentsOnLine) {
        		comments.add(comment);

        		Tag tag = KeywordMatcher.extractInitialKeyword(comment, keywordMapping);
            
                Annotation annotation = null;
                Long id = nextAnnotationId++;
                switch(tag) {
                
                    case BEGIN:     annotation = new Begin(id, sourceLine, comment);
                                    break;
                    case CALL:      annotation = new Call(id, sourceLine, comment);
                                    break;
                    case END:       annotation = new End(id, sourceLine, comment);
                                    break;
                    case FILE:      annotation = new FileUri(id, sourceLine, comment, primaryAnnotation);
                                    break;
                    case IN:        annotation = new In(id, sourceLine, comment);
                                    break;
                    case OUT:       annotation = new Out(id, sourceLine, comment);
                                    break;
                    case AS:        annotation = new As(id, sourceLine, comment, primaryAnnotation);
                                    break;
                    case PARAM:     annotation = new Param(id, sourceLine, comment);
                                    break;
                    case RETURN:    annotation = new Return(id, sourceLine, comment);
                                    break;
                    case URI:       annotation = new UriAnnotation(id, sourceLine, comment, primaryAnnotation);
                                    break;   
                }
                
                allAnnotations.add(annotation);
    
                Long qualifiedAnnotationId = null;
                if (annotation instanceof Qualification) {
                    qualifiedAnnotationId = primaryAnnotation.id;
                } else {
                	primaryAnnotation = annotation;
                    primaryAnnotations.add(annotation);
                }
                
                ywdb.insertAnnotation(annotation.id, annotation.line.sourceId, 
                                      qualifiedAnnotationId, annotation.line.lineNumber, 
                                      tag.toString(), annotation.keyword, annotation.name, 
                                      annotation.description());

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
    
    private String getSkeletonCommentDelimiter() {
        
        // try to infer language from skeleton file name and return the
        // the single-line comment delimiter if successful
        if (skeletonFile != null) {
            Language language = LanguageModel.languageForFileName(skeletonFile);
            if (language != null) {
                LanguageModel languageModel = new LanguageModel(language);
                if (languageModel.getSingleCommentDelimiters().size() > 0) {
                    return languageModel.getSingleCommentDelimiters().get(0);
                }
            }
        }
        
        // otherwise if a global language was set for the Extractor or a
        // single-line comment delimiter was defined for Extrator, use it
        if (globalLanguageModel != null) {
            if (globalLanguageModel.getSingleCommentDelimiters().size() > 0) {
                return globalLanguageModel.getSingleCommentDelimiters().get(0);
            }
        }
        
        // next fallback to delimiter defined for last language used in extraction
        LanguageModel lastLanguageModel = new LanguageModel(lastLanguage);
        if (lastLanguageModel.getSingleCommentDelimiters().size() > 0) {
            return lastLanguageModel.getSingleCommentDelimiters().get(0) ;
        }
        
        // if all else fails use the default comment delimiter
        return "#";
    }
}
