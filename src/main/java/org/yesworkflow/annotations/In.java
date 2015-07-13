package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.YWKeywords.Tag;
import org.yesworkflow.extract.SourceLine;

public class In extends Flow {

    public In(Integer id, SourceLine line, String comment) throws Exception {
        super(id, line, comment, YWKeywords.Tag.IN);
    }

    public In(Integer id, SourceLine line, String comment, Tag tag) throws Exception {
        super(id, line, comment, tag);
    }    
}
