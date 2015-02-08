package org.yesworkflow.comments;

import org.yesworkflow.YWKeywords;

public class InComment extends PortComment {

    public InComment(String tag, String name, String alias, String description) throws Exception { 
        super(tag, name, alias, description);
    }
    
    public InComment(String commentLine) throws Exception {        
        super(commentLine, YWKeywords.STANDARD_IN_KEYWORD);
    }    
}
