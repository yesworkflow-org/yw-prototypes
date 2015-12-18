package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.YWKeywords.Tag;

public class Desc extends Qualification {
    
    public Desc(Long id, Long sourceId, Long lineNumber,String comment, Annotation primaryAnnotation) throws Exception {
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.DESC, primaryAnnotation);
    }

    protected Desc(Long id, Long sourceId, Long lineNumber,String comment, Tag tag, Annotation primaryAnnotation) throws Exception {
        super(id, sourceId, lineNumber,comment, tag, primaryAnnotation);
    }
    
    public String toString() {
        return name;
    }
}

