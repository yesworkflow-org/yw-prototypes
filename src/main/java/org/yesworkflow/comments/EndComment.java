package org.yesworkflow.comments;

import org.yesworkflow.YWKeywords;

public class EndComment extends DelimiterComment {

    public EndComment(String tag, String name, String description) {
        super(tag, name, description);
    }
    
    public EndComment(String commentLine) throws Exception {
        super(commentLine, YWKeywords.STANDARD_END_KEYWORD);
    }
}