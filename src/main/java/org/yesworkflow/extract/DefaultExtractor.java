package org.yesworkflow.extract;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jooq.Record;
import org.jooq.Result;
import org.yesworkflow.Language;
import org.yesworkflow.LanguageModel;
import org.yesworkflow.YWKeywords;
import org.yesworkflow.YWKeywords.Tag;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.annotations.As;
import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.Call;
import org.yesworkflow.annotations.Desc;
import org.yesworkflow.annotations.End;
import org.yesworkflow.annotations.FileUri;
import org.yesworkflow.annotations.In;
import org.yesworkflow.annotations.Log;
import org.yesworkflow.annotations.Out;
import org.yesworkflow.annotations.Param;
import org.yesworkflow.annotations.Qualification;
import org.yesworkflow.annotations.Return;
import org.yesworkflow.annotations.UriAnnotation;
import org.yesworkflow.config.YWConfiguration;
import org.yesworkflow.db.Table;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.exceptions.YWToolUsageException;
import org.yesworkflow.query.QueryEngine;

import static org.yesworkflow.db.Table.*;
import static org.yesworkflow.db.Column.*;

public class DefaultExtractor implements Extractor {

    static private Language DEFAULT_LANGUAGE = Language.GENERIC;
    static private QueryEngine DEFAULT_QUERY_ENGINE = QueryEngine.SWIPL;
    
    private YesWorkflowDB ywdb;
    private LanguageModel globalLanguageModel = null;
    private Language lastLanguage = null;
    private QueryEngine queryEngine = DEFAULT_QUERY_ENGINE;
    private BufferedReader sourceReader = null;
    private List<String> sourcePaths;
    private List<Annotation> allAnnotations;
    private List<Annotation> primaryAnnotations;
    private YWKeywords keywordMapping;
    private KeywordMatcher keywordMatcher;
    private String commentListingPath;
    private String factsFile = null;
    private String skeletonFile = null;
    private String skeleton = null;
    private Map<String, String> extractFacts = null;
    private PrintStream stdoutStream = null;
    private PrintStream stderrStream = null;
    private List<String> sourceCodeList;

    private Long nextAnnotationId = 1L;

    public DefaultExtractor(YesWorkflowDB ywdb) {
        this(ywdb, System.out, System.err);
    }

    public DefaultExtractor(YesWorkflowDB ywdb, PrintStream stdoutStream, PrintStream stderrStream) {
        this.ywdb = ywdb;
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
    public List<String> getSourceCodeList() {
        if (sourceCodeList == null) {
            return null;
        }

        return sourceCodeList;
    }
	
	@Override
    public Map<String, String>  getFacts() throws IOException {
        if (extractFacts == null) {
            extractFacts = new ExtractFacts(ywdb, this.queryEngine, allAnnotations).build().facts();
        }
        return extractFacts;
    }	
	
    @Override
    public DefaultExtractor extract() throws Exception {
        
        extractCommentsFromSources();        
        writeCommentListing();
        extractAnnotations();
        writeSkeletonFile();
        
        if (ywdb.getRowCount(ANNOTATION) == 0) {
            stderrStream.println("WARNING: No YW comments found in source code.");
        }

        if (factsFile != null) {;
            writeTextsToFilesOrStdout(factsFile, getFacts());
        }
        
        return this;
    }

    private void extractCommentsFromSources() throws IOException, YWToolUsageException, SQLException {

        // read source code from reader if provided
        if (sourceReader != null) {
            
            extractLinesCommentsFromReader(null, sourceReader, globalLanguageModel);
            extractSourceCode(sourceReader);
        
        // otherwise read source code from stdin if source path is empty or just a dash
        } else if (sourcePathsEmptyOrDash(sourcePaths)) {
            
            Reader reader = new InputStreamReader(System.in);
            extractLinesCommentsFromReader(null, new BufferedReader(reader), globalLanguageModel);
            extractSourceCode(new BufferedReader(reader));
        
        // else read source code from each file in the list of source paths
        } else {
            
            for (String path : sourcePaths) {
                Long sourceId = ywdb.insertSource(path);
                LanguageModel languageModel = languageModelForSourceFile(path);
                extractLinesCommentsFromReader(sourceId, fileReaderForPath(path), languageModel);
                extractSourceCode(fileReaderForPath(path));
            }
        }
    }
    
    private boolean sourcePathsEmptyOrDash(List<String> sourcePaths) {
        return sourcePaths == null || 
                sourcePaths.size() == 0 || 
                sourcePaths.size() == 1 && (sourcePaths.get(0).trim().isEmpty() || 
                                            sourcePaths.get(0).trim().equals("-"));
    }
    
    private LanguageModel languageModelForSourceFile(String sourcePath) {
        LanguageModel languageModel = null;
        if (globalLanguageModel != null) {
            languageModel = globalLanguageModel;
        } else {
            Language language = LanguageModel.languageForFileName(sourcePath);
            if (language != null) {
                languageModel = new LanguageModel(language);
            }
        }
        return languageModel;
    }
    
    private void extractLinesCommentsFromReader(Long sourceId, BufferedReader reader, LanguageModel languageModel) throws IOException, SQLException {
        if (languageModel == null)  languageModel = new LanguageModel(DEFAULT_LANGUAGE);
        lastLanguage = languageModel.getLanguage();
        CommentMatcher commentMatcher = new CommentMatcher(ywdb, languageModel);
        commentMatcher.extractComments(sourceId, reader);
    }

    private BufferedReader fileReaderForPath(String path) throws YWToolUsageException {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            throw new YWToolUsageException("Input file not found: " + path);
        }

        return reader;
    }

    private void extractSourceCode(BufferedReader reader) throws IOException {
        String currLine = "";
        String fullSource = "";
        while((currLine = reader.readLine()) != null) {
            fullSource = fullSource + currLine;
        }
        sourceCodeList.add(fullSource);
    }
        
    private void writeCommentListing() throws IOException {
        if (commentListingPath != null) {
            writeTextToFileOrStdout(commentListingPath, DefaultExtractor.commentsAsString(ywdb));
        }
    }

    private void writeSkeletonFile() throws IOException {
        if (skeletonFile != null) {
            writeTextToFileOrStdout(skeletonFile, this.getSkeleton());
        }
    }

    private void writeTextsToFilesOrStdout(String path, Map<String,String> texts) throws IOException {
    
        if (path.equals(YWConfiguration.EMPTY_VALUE) || path.equals("-")) {
            for (Map.Entry<String, String> entry : texts.entrySet()) {
                this.stdoutStream.print(entry.getValue());            
            }
        } else if (queryEngine == QueryEngine.CSV) {
             for (Map.Entry<String, String> entry : texts.entrySet()) {
                writeTextToFileOrStdout(path + "_" + entry.getKey() + ".csv", entry.getValue());            
            }
        } else {
            PrintStream stream = new PrintStream(path);
            for (Map.Entry<String, String> entry : texts.entrySet()) {
                stream.print(entry.getValue());            
            }
            stream.close();
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
    
    @SuppressWarnings({ "unchecked" })
    private void extractAnnotations() throws Exception {
    	
    	allAnnotations = new LinkedList<Annotation>();
        primaryAnnotations = new LinkedList<Annotation>();
        Annotation primaryAnnotation = null;
        
        Result<Record> rows = ywdb.jooq().select(ID, SOURCE_ID, LINE_NUMBER, RANK_IN_LINE, COMMENT_TEXT)
                                         .from(Table.COMMENT)
                                         .orderBy(SOURCE_ID, LINE_NUMBER, RANK_IN_LINE)
                                         .fetch();
        
        for (Record comment : rows) {

            Long sourceId = ywdb.getLongValue(comment, SOURCE_ID);
            Long lineNumber = ywdb.getLongValue(comment, LINE_NUMBER);
            String commentText = (String)comment.getValue(COMMENT_TEXT);
        	List<String> annotationStrings = findCommentsOnLine(commentText, keywordMatcher);
        	Long rankInComment = 1L;
        	for (String annotationString: annotationStrings) {

        		Tag tag = KeywordMatcher.extractInitialKeyword(annotationString, keywordMapping);
            
                Annotation annotation = null;
                Long id = nextAnnotationId++;
                switch(tag) {
                
                    case BEGIN:     annotation = new Begin(id, sourceId, lineNumber, annotationString);
                                    break;
                    case CALL:      annotation = new Call(id, sourceId, lineNumber, annotationString);
                                    break;
                    case DESC:      annotation = new Desc(id, sourceId, lineNumber, annotationString, primaryAnnotation);
                                    break;
                    case END:       annotation = new End(id, sourceId, lineNumber, annotationString);
                                    break;
                    case FILE:      annotation = new FileUri(id, sourceId, lineNumber, annotationString, primaryAnnotation);
                                    break;
                    case IN:        annotation = new In(id, sourceId, lineNumber, annotationString);
                                    break;
                    case LOG:       annotation = new Log(id, sourceId, lineNumber, annotationString, (Out) primaryAnnotation);
                                    break;
                    case OUT:       annotation = new Out(id, sourceId, lineNumber, annotationString);
                                    break;
                    case AS:        annotation = new As(id, sourceId, lineNumber, annotationString, primaryAnnotation);
                                    break;
                    case PARAM:     annotation = new Param(id, sourceId, lineNumber, annotationString);
                                    break;
                    case RETURN:    annotation = new Return(id, sourceId, lineNumber, annotationString);
                                    break;
                    case URI:       annotation = new UriAnnotation(id, sourceId, lineNumber, annotationString, primaryAnnotation);
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
                
                ywdb.insertAnnotation(qualifiedAnnotationId, ywdb.getLongValue(comment, ID), rankInComment++,
                                      tag.toString(), annotation.keyword, annotation.value(), 
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

    @SuppressWarnings("unchecked")
    public static String commentsAsString(YesWorkflowDB ywdb) throws IOException {
        StringBuffer comments = new StringBuffer();
        Result<Record> rows = ywdb.jooq().select(ID, SOURCE_ID, LINE_NUMBER, RANK_IN_LINE, COMMENT_TEXT)
                                         .from(Table.COMMENT)
                                         .orderBy(ID, LINE_NUMBER, RANK_IN_LINE)
                                         .fetch();
        for (Record row : rows) {
            comments.append(row.getValue(COMMENT_TEXT));
            comments.append(CommentMatcher.EOL);
        }
        return comments.toString();
    }
}
