package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class In extends Flow {

    public In(String comment) throws Exception {
        super(comment, YWKeywords.STANDARD_IN_KEYWORD);
    }

    public In(String comment, String expectedKeyword) throws Exception {
        super(comment, expectedKeyword);
    }    
}
