package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class Param extends In {

    public Param(String comment) throws Exception {        
        super(comment, YWKeywords.STANDARD_PARAM_KEYWORD);
    }
}
