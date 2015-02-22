package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class Begin extends Delimiter {
    
    public Begin(String comment) throws Exception {
        super(comment, YWKeywords.STANDARD_BEGIN_KEYWORD);
    }
}

