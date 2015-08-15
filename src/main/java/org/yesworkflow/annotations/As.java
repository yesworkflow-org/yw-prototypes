package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.Comment;

public class As extends Qualification {
    
    public As(Long id, Comment line, String comment, Annotation primaryAnnotation) throws Exception {
        super(id, line, comment, YWKeywords.Tag.AS, primaryAnnotation);
    }
}

