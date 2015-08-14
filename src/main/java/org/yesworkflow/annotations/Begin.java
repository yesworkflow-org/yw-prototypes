package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.CommentLine;

public class Begin extends Delimiter {
    
    public Begin(Long id, CommentLine line, String comment) throws Exception {
        super(id, line, comment, YWKeywords.Tag.BEGIN);
    }
}

