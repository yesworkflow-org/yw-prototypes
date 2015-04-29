package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class Out extends Flow {
    
    public Out(Integer id, SourceLine line, String comment) throws Exception {        
        super(id, line, comment, YWKeywords.STANDARD_OUT_KEYWORD);
    }

    public Out(Integer id, SourceLine line, String comment, String expectedKeyword) throws Exception {
        super(id, line, comment, expectedKeyword);
    }
}
