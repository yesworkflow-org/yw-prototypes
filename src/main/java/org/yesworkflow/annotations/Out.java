package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class Out extends Flow {
    
    public Out(String commentLine) throws Exception {        
        super(commentLine, YWKeywords.STANDARD_OUT_KEYWORD);
    }  
}
