package org.yesworkflow.graph;

public enum CommentVisibility {
    
    ON,
    OFF;
    
    public static CommentVisibility toCommentVisibility(Object cv) throws Exception {
        
        if (cv instanceof CommentVisibility) return (CommentVisibility)cv;
        
        if (cv instanceof String) {
            String cvstring = (String)cv; 
            if (cvstring.equalsIgnoreCase("on")) return CommentVisibility.ON;
            if (cvstring.equalsIgnoreCase("off")) return CommentVisibility.OFF;
        }

        throw new Exception("Unrecognized CommentView: " + cv);
    }
}