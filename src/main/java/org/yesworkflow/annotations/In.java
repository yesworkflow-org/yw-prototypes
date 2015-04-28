package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class In extends Flow {

    public In(SourceLine line, String comment) throws Exception {
        super(line, comment, YWKeywords.STANDARD_IN_KEYWORD);
    }

    public In(SourceLine line, String comment, String expectedKeyword) throws Exception {
        super(line, comment, expectedKeyword);
    }    
}
