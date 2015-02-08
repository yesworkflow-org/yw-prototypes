package org.yesworkflow.comments;

import org.yesworkflow.YWKeywords;

public class OutComment extends PortComment {

    public OutComment(String tag, String name, String alias, String description) throws Exception { 
        super(tag, name, alias, description);
    }
    
    public OutComment(String commentLine) throws Exception {        
        super(commentLine, YWKeywords.STANDARD_OUT_KEYWORD);
    }  
}
