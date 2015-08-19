package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.YWKeywords.Tag;

public class Out extends Flow {
    
    public Out(Long id,Long sourceId, Long lineNumber, String comment) throws Exception {        
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.OUT);
    }

    public Out(Long id, Long sourceId, Long lineNumber, String comment, Tag tag) throws Exception {
        super(id, sourceId, lineNumber, comment, tag);
    }
}
