package org.yesworkflow.annotations;

import java.util.StringTokenizer;
import org.yesworkflow.YWKeywords;

public class Desc extends Qualification {
    
    protected String value = null;
    
    public Desc(Long id, Long sourceId, Long lineNumber,String comment, Annotation primaryAnnotation) throws Exception {
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.DESC, primaryAnnotation);
        StringTokenizer commentTokens = new StringTokenizer(comment);
        commentTokens.nextToken();
        value = buildDescription(commentTokens);
        primaryAnnotation.qualifyWith(this);
    }

    public String description() {
        return value;
    }
    
    private String buildDescription(StringTokenizer commentTokens) {
        StringBuilder descriptionBuilder = new StringBuilder();
        while (commentTokens.hasMoreTokens()) {
            descriptionBuilder.append(' ').append(commentTokens.nextToken());
        }
        String description = descriptionBuilder.toString().trim();
        return (description.length() > 0) ? description : null;
    }
    
    public String toString() {
        return name;
    }
}

