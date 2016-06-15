package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.YWKeywords.Tag;

public class Log extends Qualification {
    
    public Log(Long id, Long sourceId, Long lineNumber, String comment, Out primaryAnnotation) throws Exception {
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.LOG, primaryAnnotation);
    }

    protected Log(Long id, Long sourceId, Long lineNumber,String comment, Tag tag, Out primaryAnnotation) throws Exception {
        super(id, sourceId, lineNumber,comment, tag, primaryAnnotation);
    }
    
    public String toString() {
        return value;
    }
}

