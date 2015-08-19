package org.yesworkflow.extract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.jooq.Record;
import org.jooq.Result;
import org.yesworkflow.LanguageModel;
import org.yesworkflow.db.Table;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.db.Column.SOURCE;

import static org.yesworkflow.db.Column.*;

/** Class for matching and retrieving comments from source code implemented
 *  in a particular programming language.  Uses a simple finite state machine
 *  and the comment delimiter strings defined for the language to identify
 *  the portions of the source code that are contained in comments.
 */
public class CommentMatcher {

    private static final String EOL = System.getProperty("line.separator");
    
    private YesWorkflowDB ywdb;
    private long sourceId;
    private LanguageModel languageModel;
    private State currentState;
    private String commentStartToken;
    private KeywordMatcher keywordMatcher;
    private String lastFullMatch;
    private boolean lastFullMatchWasSingle;
    private StringBuffer buffer = new StringBuffer();
    
    /**
     * Constructs a CommentMatcher for the given programming language model.
     * @param languageModel The programming language model for the source code to be analyzed.
     */
    public CommentMatcher(YesWorkflowDB ywdb, KeywordMatcher keywordMatcher, long sourceId, LanguageModel languageModel) {
        this.ywdb = ywdb;
        this.keywordMatcher = keywordMatcher;
        this.sourceId = sourceId;
        this.languageModel = languageModel;
        this.currentState = State.IN_CODE;
        this.commentStartToken = null;
        this.buffer = new StringBuffer();
    }
    
    /** Extracts the contents of all comments found in the source code provided via
     *  a {@link java.io.BufferedReader BufferedReader}
     *  and returns each line of each comment as a string.  Comments that span multiple lines
     *  in the source are represented as multiple strings in the return value.
     * 
     * @param reader The BufferedReader used to read the source file.
     * @return  A List of Strings representing the comments in the source code.
     * @throws IOException 
     */
    public void extractCommentsFromLines(BufferedReader reader) throws IOException {

        String line;
        Long lineNumber = 1L;
        lastFullMatch = null;
        
        while ((line = reader.readLine()) != null) {
            ywdb.insertCode(sourceId, lineNumber, line);
            StringBuffer commentText = new StringBuffer();
            Long rank = 1L;
            for (int i = 0; i < line.length(); ++i) {
                int c = line.charAt(i);
                String newCommentChars = processNextChar((char)c);
                commentText.append(newCommentChars);
                if (newCommentChars.equals(EOL)) {
                    addCommentToDB(commentText.toString(), lineNumber, rank);
                    commentText = new StringBuffer();            
                }
            }
            commentText.append(processNextChar('\n'));
            addCommentToDB(commentText.toString(), lineNumber++, rank);
        }
    }
        
    /** Extracts the contents of all comments found in the provided source code,
     *  and returns all of the comments as a single string.  The comments are separated
     *  by end-of-line characters in the returned String. Comments that span multiple 
     *  lines in the source are represented as multiple lines.
     * 
     * @param source A String containing the entire source code to analyze.
     * @return  A Strings containing all the comments in the source code.
     * @throws IOException 
     */
    @SuppressWarnings("unchecked")
    public String getCommentsAsString(String source) throws IOException {
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        StringBuffer comments = new StringBuffer();
        extractCommentsFromLines(reader);
        
        Result<Record> rows = ywdb.jooq().select(ID, SOURCE_ID, LINE_NUMBER, RANK, TEXT)
                                         .from(Table.COMMENT)
                                         .orderBy(ID, LINE_NUMBER, RANK)
                                         .fetch();
        
        for (Record row : rows) {
            comments.append(row.getValue(TEXT));
            comments.append(EOL);
        }
        
        return comments.toString();
    }
    
    /** Helper method for accumulating non-blank comment lines. */
    private void addCommentToDB(String commentText, Long lineNumber, Long rank) {
        String trimmedCommentText = commentText.toString().trim();
        if (trimmedCommentText.length() > 0) {
            long firstKeywordIndex = keywordMatcher.findKeyword(trimmedCommentText);
            Long keywordStart = (firstKeywordIndex != -1) ? firstKeywordIndex : null;
            ywdb.insertComment(sourceId, lineNumber, rank++, trimmedCommentText, keywordStart);
        }
    }
    
    /** Enumeration defining the three states of the comment-matching finite state machine */
    private enum State {
        IN_CODE,
        IN_PREFIX,
        IN_SINGLE_LINE_COMMENT,
        IN_MULTI_LINE_COMMENT,
    }
    
    /** Updates the state of the comment-matching finite state machine. */
    @SuppressWarnings("incomplete-switch")
    private String processNextChar(int c) {
        
        String newCommentCharacters = "";
        State nextState = currentState;
        buffer.append((char)c);
        
        switch(currentState) {
        
        case IN_CODE:
            
            lastFullMatch = null;
            
            switch(languageModel.commentStartMatches(buffer.toString())) {
                
                case FULL_MATCH_SINGLE:
                    nextState = State.IN_SINGLE_LINE_COMMENT;
                    buffer.setLength(0);
                    break;
                
                case FULL_MATCH_PAIRED:
                    nextState = State.IN_MULTI_LINE_COMMENT;
                    commentStartToken = buffer.toString();
                    buffer.setLength(0);
                    break;

                case PREFIX_MATCH:
                    nextState = State.IN_PREFIX;
                    break;

                case FULL_MATCH_SINGLE_PREFIX_MATCH_PAIRED:
                    lastFullMatch = buffer.toString();
                    lastFullMatchWasSingle = true;
                    nextState = State.IN_PREFIX;
                    break;                    

                case FULL_MATCH_PAIRED_PREFIX_MATCH_SINGLE:
                    lastFullMatch = buffer.toString();
                    lastFullMatchWasSingle = false;
                    nextState = State.IN_PREFIX;
                    break;                    
                    
                default:
                    nextState = State.IN_CODE;
                    buffer.setLength(0);
            }
            
            break;

        case IN_PREFIX:
            
            switch(languageModel.commentStartMatches(buffer.toString())) {
                
                case FULL_MATCH_SINGLE:
                    nextState = State.IN_SINGLE_LINE_COMMENT;
                    buffer.setLength(0);
                    break;
                
                case FULL_MATCH_PAIRED:
                    nextState = State.IN_MULTI_LINE_COMMENT;
                    commentStartToken = buffer.toString();
                    buffer.setLength(0);
                    break;

                case NO_MATCH:
                    if (lastFullMatch == null) {
                        nextState = State.IN_CODE;                    
                    } else {
                        commentStartToken = lastFullMatch;
                        nextState = (lastFullMatchWasSingle) ? 
                                State.IN_SINGLE_LINE_COMMENT :
                                State.IN_MULTI_LINE_COMMENT;
                    }
                    buffer.setLength(0);
                    buffer.append((char)c);
                    break;
                    
                case PREFIX_MATCH:
                    nextState = State.IN_PREFIX;
                    break;

                case FULL_MATCH_SINGLE_PREFIX_MATCH_PAIRED:
                case FULL_MATCH_PAIRED_PREFIX_MATCH_SINGLE:
                    lastFullMatch = buffer.toString();
                    nextState = State.IN_PREFIX;
                    break;
                    
                default:
                    nextState = State.IN_CODE;
                    buffer.setLength(0);
            }
            
            break;
            
            
        case IN_SINGLE_LINE_COMMENT:
            
            if (c == '\r' || c == '\n') {
                nextState = State.IN_CODE;
                newCommentCharacters = EOL;
            } else {
                newCommentCharacters = buffer.toString();
            }
            buffer.setLength(0);

            break;
            
        case IN_MULTI_LINE_COMMENT:
            
            switch(languageModel.commentEndMatches(buffer.toString(), commentStartToken)) {
            
                case NO_MATCH:
                    newCommentCharacters = buffer.toString();
                    buffer.setLength(0);
                    break;
                
                case FULL_MATCH_PAIRED:
                    nextState = State.IN_CODE;
                    newCommentCharacters = EOL;
                    buffer.setLength(0);
                    break;
            }
            
            break;
        }
        
        currentState = nextState;
        
        return newCommentCharacters;
    }
}
