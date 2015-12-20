package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class FileUri extends UriAnnotation {
    
    public FileUri(Long id, Long sourceId, Long lineNumber, String comment, Annotation primaryAnnotation) throws Exception {
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.FILE, primaryAnnotation);
    }
    
    public String toString() {
        return value;
    }
}

