package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords.Tag;
import org.yesworkflow.extract.CommentLine;

public abstract class Delimiter extends Annotation {

    public Delimiter(Long id, CommentLine line, String comment, Tag tag) throws Exception {
    	super(id, line, comment, tag);    	
    }
    
    @Override
    public String toString() {
        
        StringBuffer sb = new StringBuffer();
        
        sb.append(keyword)
          .append("{name=")
          .append(name);

        if (description != null) {
          sb.append(",description=")
            .append(description);
        }
        
        sb.append("}");
        
        return sb.toString();
    }
}

