package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class Return extends Out {
    
    public Return(Integer id, SourceLine line, String comment) throws Exception {        
        super(id, line, comment, YWKeywords.Tag.RETURN);
    }  
}
