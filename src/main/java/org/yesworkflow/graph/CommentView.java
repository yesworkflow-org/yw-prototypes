package org.yesworkflow.graph;

public enum CommentView {
    
    SHOW,
    HIDE;
    
    public static CommentView get(String cv) throws Exception {
        if (cv.equalsIgnoreCase("show")) return CommentView.SHOW;
        if (cv.equalsIgnoreCase("hide")) return CommentView.HIDE;
        throw new Exception("Unrecognized CommentView: " + cv);
    }
}