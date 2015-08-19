package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class As extends Qualification {
    
    public As(Long id, Long sourceId, Long lineNumber, String comment, Annotation primaryAnnotation) throws Exception {
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.AS, primaryAnnotation);
    }
}

