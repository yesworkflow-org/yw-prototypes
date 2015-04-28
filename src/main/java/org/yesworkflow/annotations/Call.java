package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class Call extends Annotation {

    public Call(SourceLine line, String comment, String expectedTag) throws Exception {
    	super(line, comment, expectedTag);    	
    }

    public Call(SourceLine line, String comment) throws Exception {
        super(line, comment, YWKeywords.STANDARD_CALL_KEYWORD);
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

