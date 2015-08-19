package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class End extends Delimiter {

    public End(Long id, Long sourceId, Long lineNumber, String comment) throws Exception {
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.END);
    }
}