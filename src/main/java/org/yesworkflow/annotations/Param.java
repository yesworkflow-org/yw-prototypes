package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class Param extends In {

    public Param(Integer id, SourceLine line, String comment) throws Exception {        
        super(id, line, comment, YWKeywords.Tag.PARAM);
    }
}
