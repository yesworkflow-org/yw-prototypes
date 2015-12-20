package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.YWKeywords.Tag;

public class Call extends Annotation {

    public Call(Long id, Long sourceId, Long lineNumber, String comment, Tag expectedTag) throws Exception {
    	super(id, sourceId, lineNumber,comment, expectedTag);    	
    }

    public Call(Long id, Long sourceId, Long lineNumber, String comment) throws Exception {
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.CALL);
    }

    @Override
    public String toString() {
        
        StringBuffer sb = new StringBuffer();
        
        sb.append(keyword)
          .append("{name=")
          .append(name);

        if (description() != null) {
          sb.append(",description=")
            .append(description());
        }
        
        sb.append("}");
        
        return sb.toString();
    }
}

