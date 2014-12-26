package org.yesworkflow.comments;

import java.util.StringTokenizer;

public abstract class Comment {

    protected String buildDescriptionFromTokens(String firstToken, StringTokenizer commentTokens) {
        
        StringBuilder descriptionBuilder = new StringBuilder();
        
        if (firstToken != null) {
            descriptionBuilder.append(firstToken);
        }
        
        while (commentTokens.hasMoreTokens()) {
            descriptionBuilder.append(' ').append(commentTokens.nextToken());
        }
        
        String description = descriptionBuilder.toString().trim();
        
        return (description.length() > 0) ? description : null;
    }
}
