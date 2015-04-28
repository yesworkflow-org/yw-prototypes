package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class End extends Delimiter {

    public End(SourceLine line, String comment) throws Exception {
        super(line, comment, YWKeywords.STANDARD_END_KEYWORD);
    }
}