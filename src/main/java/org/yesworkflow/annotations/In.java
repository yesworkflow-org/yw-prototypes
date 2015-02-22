package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class In extends Flow {

    public In(String commentLine) throws Exception {        
        super(commentLine, YWKeywords.STANDARD_IN_KEYWORD);
    }    
}
