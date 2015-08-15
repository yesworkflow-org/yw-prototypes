package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.Comment;

public class Param extends In {

    public Param(Long id, Comment line, String comment) throws Exception {        
        super(id, line, comment, YWKeywords.Tag.PARAM);
    }
}
