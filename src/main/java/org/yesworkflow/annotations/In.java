package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class In extends Flow {

    public In(String comment) throws Exception {        
        super(comment, YWKeywords.STANDARD_IN_KEYWORD);
    }    
}
