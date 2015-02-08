package org.yesworkflow.comments;

import org.yesworkflow.YWKeywords;

public class BeginComment extends DelimiterComment {

    public BeginComment(String tag, String name, String description) {
        super(tag, name, description);
    }
    
    public BeginComment(String commentLine) throws Exception {
        super(commentLine, YWKeywords.STANDARD_BEGIN_KEYWORD);
    }
}

