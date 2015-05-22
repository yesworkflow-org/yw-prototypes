package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class FileUri extends UriAnnotation {
    
    public FileUri(Integer id, SourceLine line, String comment, Annotation primaryAnnotation) throws Exception {
        super(id, line, comment, YWKeywords.STANDARD_FILE_KEYWORD, primaryAnnotation);
    }
    
    public String toString() {
        return name;
    }
}

