package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.YWKeywords.Tag;
import org.yesworkflow.extract.CommentLine;

public class UriAnnotation extends Qualification {
    
    public UriAnnotation(Long id, CommentLine line, String comment, Annotation primaryAnnotation) throws Exception {
        super(id, line, comment, YWKeywords.Tag.URI, primaryAnnotation);
    }

    protected UriAnnotation(Long id, CommentLine line, String comment, Tag tag, Annotation primaryAnnotation) throws Exception {
        super(id, line, comment, tag, primaryAnnotation);
    }
    
    public String toString() {
        return name;
    }
}

