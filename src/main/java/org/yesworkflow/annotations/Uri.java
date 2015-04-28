package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.extract.SourceLine;

public class Uri extends Qualification {
    
    public Uri(SourceLine line, String comment) throws Exception {
        super(line, comment, YWKeywords.STANDARD_URI_KEYWORD);
    }
    
    public String toString() {
        return name;
    }
}

