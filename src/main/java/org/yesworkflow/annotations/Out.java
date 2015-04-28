package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class Out extends Flow {
    
    public Out(SourceLine line, String comment) throws Exception {        
        super(line, comment, YWKeywords.STANDARD_OUT_KEYWORD);
    }

    public Out(SourceLine line, String comment, String expectedKeyword) throws Exception {
        super(line, comment, expectedKeyword);
    }
}
