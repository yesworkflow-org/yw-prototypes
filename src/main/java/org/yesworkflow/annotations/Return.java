package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class Return extends Out {
    
    public Return(String comment) throws Exception {        
        super(comment, YWKeywords.STANDARD_RETURN_KEYWORD);
    }  
}
