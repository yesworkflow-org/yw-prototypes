package org.yesworkflow.extract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.yesworkflow.LanguageModel;
import org.yesworkflow.db.YesWorkflowDB;

/** Class for matching and retrieving comments from source code implemented
 *  in a particular programming language.  Uses a simple finite state machine
 *  and the comment delimiter strings defined for the language to identify
 *  the portions of the source code that are contained in comments.
 */
public class CommentMatcher {

    static final String EOL = System.getProperty("line.separator");
    
    private YesWorkflowDB ywdb;
    private LanguageModel languageModel;
    private State currentState;
    private String commentStartToken;
    private String lastFullMatch;
    private boolean lastFullMatchWasSingle;
    private StringBuffer buffer = new StringBuffer();
    
    /**
     * Constructs a CommentMatcher for the given programming language model.
     * @param languageModel The programming language model for the source code to be analyzed.
     */
    public CommentMatcher(YesWorkflowDB ywdb, LanguageModel languageModel) {
        this.ywdb = ywdb;
        this.languageModel = languageModel;
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
    public void extractComments(Long sourceId, BufferedReader reader) throws IOException {

        String line;
        Long lineNumber = 1L;
        lastFullMatch = null;
        this.currentState = State.IN_CODE;
        this.commentStartToken = null;
        this.buffer = new StringBuffer();
        
        if (sourceId == null) sourceId = ywdb.insertSource(null);
        
        while ((line = reader.readLine()) != null) {
            ywdb.insertCode(sourceId, lineNumber, line);
            StringBuffer commentText = new StringBuffer();
            Long rank = 1L;
            for (int i = 0; i < line.length(); ++i) {
                int c = line.charAt(i);
                String newCommentChars = processNextChar((char)c);
                commentText.append(newCommentChars);
                if (newCommentChars.equals(EOL)) {
                    rank = insertTrimmedComment(sourceId, lineNumber, rank, commentText.toString());
                    commentText = new StringBuffer();            
                }
            }
            commentText.append(processNextChar('\n'));
            insertTrimmedComment(sourceId, lineNumber++, rank, commentText.toString());
        }
    }
        
    public void extractComments(String code) throws IOException {
        extractComments(null, new BufferedReader(new StringReader(code)));
    }
        
    /** Helper method for inserting non-blank comments in YesWorkflow DB */
    private Long insertTrimmedComment(Long sourceId, Long lineNumber, Long rank, String commentText) {
        String trimmedCommentText = commentText.toString().trim();
        if (trimmedCommentText.length() > 0) {
            ywdb.insertComment(sourceId, lineNumber, rank++, trimmedCommentText);
        }
        return rank;
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
