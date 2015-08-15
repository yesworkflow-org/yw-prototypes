package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.Comment;

public class FileUri extends UriAnnotation {
    
    public FileUri(Long id, Comment line, String comment, Annotation primaryAnnotation) throws Exception {
        super(id, line, comment, YWKeywords.Tag.FILE, primaryAnnotation);
    }
    
    public String toString() {
        return name;
    }
}

