package org.yesworkflow.comments;

public class EndComment extends DelimiterComment {

    public EndComment(String tag, String name, String description) {
        super(tag, name, description);
    }
    
    public EndComment(String commentLine) throws Exception {
        super(commentLine, "@end");
    }
}