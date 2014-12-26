package org.yesworkflow.comments;

public class OutComment extends PortComment {

    public OutComment(String tag, String binding, String portLabel, String description) throws Exception { 
        super(tag, binding, portLabel, description);
    }
    
    public OutComment(String commentLine) throws Exception {        
        super(commentLine, "@out");
    }  
}
