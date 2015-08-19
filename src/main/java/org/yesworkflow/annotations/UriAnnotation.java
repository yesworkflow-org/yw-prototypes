package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.YWKeywords.Tag;

public class UriAnnotation extends Qualification {
    
    public UriAnnotation(Long id, Long sourceId, Long lineNumber,String comment, Annotation primaryAnnotation) throws Exception {
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.URI, primaryAnnotation);
    }

    protected UriAnnotation(Long id, Long sourceId, Long lineNumber,String comment, Tag tag, Annotation primaryAnnotation) throws Exception {
        super(id, sourceId, lineNumber,comment, tag, primaryAnnotation);
    }
    
    public String toString() {
        return name;
    }
}

