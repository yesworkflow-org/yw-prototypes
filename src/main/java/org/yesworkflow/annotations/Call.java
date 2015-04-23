package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class Call extends Annotation {

    public Call(String comment, String expectedTag) throws Exception {
    	super(comment, expectedTag);    	
    }

    public Call(String comment) throws Exception {
        super(comment, YWKeywords.STANDARD_CALL_KEYWORD);
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

