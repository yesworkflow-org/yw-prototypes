package org.yesworkflow.comments;

import java.util.StringTokenizer;

public abstract class PortComment extends Comment {

    public final String tag;
    public final String data;
    public final String label;
    public final String description;
    
    public PortComment(String tag, String data, String label, String description) {
        this.tag = tag;
        this.data = data;
        this.label = label;        
        this.description = description;
    }
    
    public String binding() {
        return (label != null) ? label : data;
    }
    
    public PortComment(String commentLine, String expectedTag) throws Exception {
        
        StringTokenizer commentTokens = new StringTokenizer(commentLine);
        
        tag = commentTokens.nextToken();
        if (!tag.equalsIgnoreCase(expectedTag)) {
            throw new Exception("Wrong tag for " + expectedTag + " comment: " + tag);        
        }
    
        String token = commentTokens.nextToken();
        this.data = token;
        
        if (commentTokens.hasMoreTokens()) {
            token = commentTokens.nextToken();
        } else {
            this.label = null;
            this.description = null;
            return;
        }
        
        if (token.equalsIgnoreCase("@as")) {
            this.label = commentTokens.nextToken();
            this.description = buildDescriptionFromTokens(null, commentTokens);
            return;
        } else {
            this.label = null;
            this.description = buildDescriptionFromTokens(token, commentTokens);
        }
    }
}
