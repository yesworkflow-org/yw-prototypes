package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.YWKeywords.Tag;
import org.yesworkflow.extract.Comment;

public class Call extends Annotation {

    public Call(Long id, Comment line, String comment, Tag expectedTag) throws Exception {
    	super(id, line, comment, expectedTag);    	
    }

    public Call(Long id, Comment line, String comment) throws Exception {
        super(id, line, comment, YWKeywords.Tag.CALL);
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

