package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class Return extends Out {
    
    public Return(Long id, Long sourceId, Long lineNumber, String comment) throws Exception {        
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.RETURN);
    }  
}
