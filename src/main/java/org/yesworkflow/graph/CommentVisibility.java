package org.yesworkflow.graph;

public enum CommentVisibility {
    
    SHOW,
    HIDE;
    
    public static CommentVisibility toCommentVisibility(Object cv) throws Exception {
        
        if (cv instanceof CommentVisibility) return (CommentVisibility)cv;
        
        if (cv instanceof String) {
            String cvstring = (String)cv; 
            if (cvstring.equalsIgnoreCase("show")) return CommentVisibility.SHOW;
            if (cvstring.equalsIgnoreCase("hide")) return CommentVisibility.HIDE;
        }

        throw new Exception("Unrecognized CommentView: " + cv);
    }
}