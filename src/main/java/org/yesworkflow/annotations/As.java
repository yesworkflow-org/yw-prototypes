package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.CommentLine;

public class As extends Qualification {
    
    public As(Long id, CommentLine line, String comment, Annotation primaryAnnotation) throws Exception {
        super(id, line, comment, YWKeywords.Tag.AS, primaryAnnotation);
    }
}

