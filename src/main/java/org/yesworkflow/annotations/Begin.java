package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class Begin extends Delimiter {
    
    public Begin(Long id, Long sourceId, Long lineNumber, String comment) throws Exception {
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.BEGIN);
    }
}

