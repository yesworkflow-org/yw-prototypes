package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class End extends Delimiter {

    public End(String comment) throws Exception {
        super(comment, YWKeywords.STANDARD_END_KEYWORD);
    }
}