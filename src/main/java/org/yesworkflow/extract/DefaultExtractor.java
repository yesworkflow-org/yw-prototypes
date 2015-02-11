package org.yesworkflow.extract;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.yesworkflow.LanguageModel;
import org.yesworkflow.LanguageModel.Language;
import org.yesworkflow.YWKeywords;
import org.yesworkflow.YWKeywords.Tag;
import org.yesworkflow.comments.BeginComment;
import org.yesworkflow.comments.Comment;
import org.yesworkflow.comments.EndComment;
import org.yesworkflow.comments.InComment;
import org.yesworkflow.comments.OutComment;
import org.yesworkflow.exceptions.YWMarkupException;
import org.yesworkflow.exceptions.YWToolUsageException;

public class DefaultExtractor implements Extractor {

    private BufferedReader sourceReader = null;
    private String sourcePath = null;
    private List<String> ywCommentLines;
    private List<Comment> comments;
    private YWKeywords keywordMapping;
    private LanguageModel languageModel;
    
    @SuppressWarnings("unused")
    private PrintStream stdoutStream = null;
    private PrintStream stderrStream = null;

    @SuppressWarnings("unused")
    private String databasePath = null;

    public DefaultExtractor(PrintStream stdoutStream, PrintStream stderrStream) {
        this.stdoutStream = stdoutStream;
        this.stderrStream = stderrStream;
        this.keywordMapping = new YWKeywords();
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
    public DefaultExtractor sourceReader(Reader reader) {
        this.sourceReader = new BufferedReader(reader);
        return this;
    }

    @Override
    public DefaultExtractor sourcePath(String path) {
        this.sourcePath = path;
        return this;
    }

    @Override
    public DefaultExtractor databasePath(String path) {
        this.databasePath = path;
        return this;
    }

    @Override
    public DefaultExtractor extract() throws Exception {

        if (sourceReader == null) {
            sourceReader = getFileReaderForPath(sourcePath);
        }

        extractLines();
        extractComments();
        
        if (comments.isEmpty()) {
            stderrStream.println("WARNING: No YW comments found in source code.");
        }
        
        return this;
    }

    @Override
    public List<String> getLines() {
        return ywCommentLines;
    }

    @Override
    public List<Comment> getComments() {
        return comments;
    }

    private void extractLines() throws IOException {
        
        // extract all comments from script using the language model
        CommentMatcher commentMatcher = new CommentMatcher(languageModel);
        List<String> allCommentLines = commentMatcher.getCommentsAsLines(sourceReader);
        
        // select only the comments that contain YW keywords,
        // trimming characters preceding the first YW keyword in each
        KeywordMatcher ywCommentMatcher = new KeywordMatcher(keywordMapping.getKeywords());
        ywCommentLines = ywCommentMatcher.match(allCommentLines, true);
    }

    private void extractComments() throws Exception {

        comments = new LinkedList<Comment>();

        for (String commentLine : ywCommentLines) {

            String keyword = extractKeyword(commentLine);
            Tag tag = keywordMapping.getTag(keyword);
            
            if (tag == null) {
                throw new YWMarkupException("ERROR: Comment keyword " + keyword + " is not supported");                
            }
            
            Comment comment = null;
            switch(tag) {
                case BEGIN: comment = new BeginComment(commentLine);    break;
                case END:   comment = new EndComment(commentLine);      break;
                case IN:    comment = new InComment(commentLine);       break;
                case OUT:   comment = new OutComment(commentLine);      break;
                case AS:    break;
            }

            comments.add(comment);
        }
    }
 
    private String extractKeyword(String commentLine) {

        int tagEndIndex = commentLine.indexOf(' ');
        if (tagEndIndex == -1) tagEndIndex = commentLine.indexOf('\t');

        if (tagEndIndex == -1) {
            return commentLine;
        } else {
            return commentLine.substring(0, tagEndIndex);
        }
    }


    private BufferedReader getFileReaderForPath(String path) throws YWToolUsageException {

        if (sourcePath == null) throw new YWToolUsageException("No source path provided to extractor");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            throw new YWToolUsageException("ERROR: Input source file not found: " + path);
        }

        return reader;
    }
    
    @Override
    public Language getLanguage() {
        return languageModel.getLanguage();
    }
}
