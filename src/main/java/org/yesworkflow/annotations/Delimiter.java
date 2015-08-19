package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords.Tag;

public abstract class Delimiter extends Annotation {

    public Delimiter(Long id, Long sourceId, Long lineNumber, String comment, Tag tag) throws Exception {
    	super(id, sourceId, lineNumber, comment, tag);    	
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

