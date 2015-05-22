package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class UriAnnotation extends Qualification {
    
    public UriAnnotation(Integer id, SourceLine line, String comment, Annotation primaryAnnotation) throws Exception {
        super(id, line, comment, YWKeywords.STANDARD_URI_KEYWORD, primaryAnnotation);
    }

    protected UriAnnotation(Integer id, SourceLine line, String comment, String expectedTag, Annotation primaryAnnotation) throws Exception {
        super(id, line, comment, expectedTag, primaryAnnotation);
    }
    
    public String toString() {
        return name;
    }
}

