package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class Return extends Out {
    
    public Return(SourceLine line, String comment) throws Exception {        
        super(line, comment, YWKeywords.STANDARD_RETURN_KEYWORD);
    }  
}
