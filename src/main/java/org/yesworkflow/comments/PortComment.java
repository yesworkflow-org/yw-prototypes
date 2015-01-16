package org.yesworkflow.comments;

import java.util.StringTokenizer;

public abstract class PortComment extends Comment {

    public final String tag;
    public final String name;
    public final String alias;
    public final String description;
    
    public PortComment(String tag, String name, String alias, String description) {
        this.tag = tag;
        this.name = name;
        this.alias = alias;
        this.description = description;
    }
    
    public String binding() {
        return (alias != null) ? alias : name;
    }
    
    public PortComment(String commentLine, String expectedTag) throws Exception {
        
        StringTokenizer commentTokens = new StringTokenizer(commentLine);
        
        tag = commentTokens.nextToken();
        if (!tag.equalsIgnoreCase(expectedTag)) {
            throw new Exception("Wrong tag for " + expectedTag + " comment: " + tag);        
        }
    
        String token = commentTokens.nextToken();
        this.name = token;
        
        if (commentTokens.hasMoreTokens()) {
            token = commentTokens.nextToken();
        } else {
            this.alias = null;
            this.description = null;
            return;
        }
        
        if (token.equalsIgnoreCase("@as")) {
            this.alias = commentTokens.nextToken();
            this.description = buildDescriptionFromTokens(null, commentTokens);
            return;
        } else {
            this.alias = null;
            this.description = buildDescriptionFromTokens(token, commentTokens);
        }
    }
    
//        return String.format("%s{name=%s,alias=%s,description=%s}", this.tag, this.name, this.alias, this.description);
    
    @Override
    public String toString() {
        
        StringBuffer sb = new StringBuffer();
        
        sb.append(this.tag)
          .append("{name=")
          .append(this.name);

        if (this.alias != null) {
            sb.append(",alias=")
              .append(this.alias);
        }
        
        if (this.description != null) {
          sb.append(",description=")
            .append(this.description);
        }
        
        sb.append("}");
        
        return sb.toString();
    }
}
