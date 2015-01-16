package org.yesworkflow.comments;

public class InComment extends PortComment {

    public InComment(String tag, String name, String alias, String description) throws Exception { 
        super(tag, name, alias, description);
    }
    
    public InComment(String commentLine) throws Exception {        
        super(commentLine, "@in");
    }    
}
