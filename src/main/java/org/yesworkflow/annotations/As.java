package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class As extends Qualification {
    
    public As(SourceLine line, String comment) throws Exception {
        super(line, comment, YWKeywords.STANDARD_AS_KEYWORD);
    }
}

