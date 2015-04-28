package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class Param extends In {

    public Param(SourceLine line, String comment) throws Exception {        
        super(line, comment, YWKeywords.STANDARD_PARAM_KEYWORD);
    }
}
