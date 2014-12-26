package org.yesworkflow.comments;

import java.util.StringTokenizer;

public abstract class DelimiterComment extends Comment {

    public final String tag;
    public final String programName;
    public final String description;
    
    public DelimiterComment(String tag, String name, String description) {
        this.tag = tag;
        this.programName = name;
        this.description = description;
    }
    
    public DelimiterComment(String commentLine, String expectedTag) throws Exception {
        
        StringTokenizer commentTokens = new StringTokenizer(commentLine);
        
        this.tag = commentTokens.nextToken();
        if (!tag.equalsIgnoreCase(expectedTag)) {
            throw new Exception("Wrong tag for " + expectedTag + " comment: " + tag);        
        }
       
        this.programName = commentTokens.nextToken();
        
        if (commentTokens.hasMoreTokens()) {
            this.description = buildDescriptionFromTokens(null, commentTokens);
        } else  {
            this.description = null;
        }
    }
}

