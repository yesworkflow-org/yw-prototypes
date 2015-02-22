package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class As extends Qualification {
    
    public As(String comment) throws Exception {
        super(comment, YWKeywords.STANDARD_AS_KEYWORD);
    }
}

