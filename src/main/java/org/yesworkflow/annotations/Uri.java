package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;

public class Uri extends Qualification {
    
    public Uri(String comment) throws Exception {
        super(comment, YWKeywords.STANDARD_URI_KEYWORD);
    }
    
    public String toString() {
        return name;
    }
}

