package org.yesworkflow.annotations;

import org.yesworkflow.extract.SourceLine;

public abstract class Delimiter extends Annotation {

    public Delimiter(Integer id, SourceLine line, String comment, String expectedTag) throws Exception {
    	super(id, line, comment, expectedTag);    	
    }
    
    @Override
    public String toString() {
        
        StringBuffer sb = new StringBuffer();
        
        sb.append(tag)
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

