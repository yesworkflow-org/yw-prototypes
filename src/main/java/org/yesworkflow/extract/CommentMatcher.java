package org.yesworkflow.extract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.yesworkflow.LanguageModel;


/** Class for matching and retrieving comments from source code implemented
 *  in a particular programming language.  Uses a simple finite state machine
 *  and the comment delimiter strings defined for the language to identify
 *  the portions of the source code that are contained in comments.
 */
public class CommentMatcher {

    private static final String EOL = System.getProperty("line.separator");
    
    private LanguageModel languageModel;
    private State currentState;
    private String commentStartToken;
    private StringBuffer buffer = new StringBuffer();
    
    /**
     * Constructs a CommentMatcher for the given programming language model.
     * @param lm The programming language model for the source code to be analyzed.
     */
    public CommentMatcher(LanguageModel lm) {
        languageModel = lm;
        currentState = State.IN_CODE;
        commentStartToken = null;
        buffer = new StringBuffer();
    }
    
    /** Extracts the contents of all comments found in the provided source code,
     *  and returns each line of each comment as a string.  Comments that span multiple lines
     *  in the source are represented as multiple strings in the return value.
     * 
     * @param source A String containing the entire source code to analyze.
     * @return  A List of Strings representing the comments in the source code.
     * @throws IOException 
     */
    public List<String> getCommentsAsLines(String source) throws IOException {
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        String line;
        List<String> commentLines = new LinkedList<String>();
        
        while ((line = reader.readLine()) != null) {
            StringBuffer commentLine = new StringBuffer();            
            for (int i = 0; i < line.length(); ++i) {
                int c = line.charAt(i);
                String newCommentChars = processNextChar((char)c);
                commentLine.append(newCommentChars);
                if (newCommentChars.equals(EOL)) {
                    String trimmedCommentLine = commentLine.toString().trim();
                    if (trimmedCommentLine.length() > 0) {
                        commentLines.add(trimmedCommentLine);
                    }
                    commentLine = new StringBuffer();            
                }
            }
            commentLine.append(processNextChar('\n'));
            String trimmedCommentLine = commentLine.toString().trim();
            if (trimmedCommentLine.length() > 0) {
                commentLines.add(trimmedCommentLine);
            }
        }
        
        return commentLines;
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
    public String getCommentsAsString(String source) throws IOException {
        
        StringBuffer comments = new StringBuffer();
        for (String cl : getCommentsAsLines(source)) {
            comments.append(cl);
            comments.append(EOL);
        }
        
        return comments.toString();
    }
    
    private enum State {
        IN_CODE,
        IN_SINGLE_LINE_COMMENT,
        IN_MULTI_LINE_COMMENT,
    }
    
    @SuppressWarnings("incomplete-switch")
    private String processNextChar(int c) {
        
        String newCommentCharacters = "";
        State nextState = currentState;
        buffer.append((char)c);
        
        switch(currentState) {
        
        case IN_CODE:
            
            switch(languageModel.commentStartMatches(buffer.toString())) {

                case PREFIX_MATCH:
                    break;
                
                case FULL_MATCH_SINGLE:
                    nextState = State.IN_SINGLE_LINE_COMMENT;
                    clearBuffer();
                    break;
                
                case FULL_MATCH_PAIRED:
                    nextState = State.IN_MULTI_LINE_COMMENT;
                    commentStartToken = buffer.toString();
                    clearBuffer();
                    break;
                
                default:
                    nextState = State.IN_CODE;
                    clearBuffer();
            }
            
            break;
            
        case IN_SINGLE_LINE_COMMENT:
            
            if (c == '\r' || c == '\n') {
                nextState = State.IN_CODE;
                newCommentCharacters = EOL;
            } else {
                newCommentCharacters = buffer.toString();
            }
            clearBuffer();

            break;
            
        case IN_MULTI_LINE_COMMENT:
            
            switch(languageModel.commentEndMatches(buffer.toString(), commentStartToken)) {
            
                case NO_MATCH:
                    newCommentCharacters = buffer.toString();
                    clearBuffer();
                    break;
                
                case PREFIX_MATCH:
                    break;
                
                case FULL_MATCH_PAIRED:
                    nextState = State.IN_CODE;
                    newCommentCharacters = EOL;
                    clearBuffer();
                    break;
            }
            
            break;
        }
        
        currentState = nextState;
        
        return newCommentCharacters;
    }

    private void clearBuffer() {
        buffer.delete(0, buffer.length());        
    }    
}
