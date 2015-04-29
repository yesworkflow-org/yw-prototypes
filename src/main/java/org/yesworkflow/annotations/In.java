package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class In extends Flow {

    public In(Integer id, SourceLine line, String comment) throws Exception {
        super(id, line, comment, YWKeywords.STANDARD_IN_KEYWORD);
    }

    public In(Integer id, SourceLine line, String comment, String expectedKeyword) throws Exception {
        super(id, line, comment, expectedKeyword);
    }    
}
