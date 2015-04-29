package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class Begin extends Delimiter {
    
    public Begin(Integer id, SourceLine line, String comment) throws Exception {
        super(id, line, comment, YWKeywords.STANDARD_BEGIN_KEYWORD);
    }
}

