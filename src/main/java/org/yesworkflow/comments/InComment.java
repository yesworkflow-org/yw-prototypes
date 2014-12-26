package org.yesworkflow.comments;

public class InComment extends PortComment {

    public InComment(String tag, String binding, String portLabel, String description) throws Exception { 
        super(tag, binding, portLabel, description);
    }
    
    public InComment(String commentLine) throws Exception {        
        super(commentLine, "@in");
    }    
}
