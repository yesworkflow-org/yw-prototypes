package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class Begin extends Delimiter {
    
    public Begin(SourceLine line, String comment) throws Exception {
        super(line, comment, YWKeywords.STANDARD_BEGIN_KEYWORD);
    }
}

