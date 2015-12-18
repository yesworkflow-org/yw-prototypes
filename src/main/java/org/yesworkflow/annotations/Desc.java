package org.yesworkflow.annotations;

import java.util.StringTokenizer;
import org.yesworkflow.YWKeywords;

public class Desc extends Qualification {
    
    protected String description = null;
    
    public Desc(Long id, Long sourceId, Long lineNumber,String comment, Annotation primaryAnnotation) throws Exception {
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.DESC, primaryAnnotation);
        StringTokenizer commentTokens = new StringTokenizer(comment);
        commentTokens.nextToken();
        primaryAnnotation.description = buildDescription(commentTokens);
    }

    public void appendDescription(String extraDescription) {
        if (extraDescription != null) {
            if (description == null) {
                description = extraDescription;
            } else {
                description += " " + extraDescription;
            }
        }
    }
    
    public String description() {
        return description;
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

