package org.yesworkflow.annotations;

import java.util.StringTokenizer;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.data.LogEntryTemplate;

public class Log extends Qualification {
    
    public final LogEntryTemplate entryTemplate;
    
    public Log(Long id, Long sourceId, Long lineNumber, String comment, Out primaryAnnotation) throws Exception {
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.LOG, primaryAnnotation);
        StringTokenizer commentTokens = new StringTokenizer(comment);
        commentTokens.nextToken();
        value = buildTemplateString(commentTokens);
        entryTemplate = new LogEntryTemplate(value);
    }

    private String buildTemplateString(StringTokenizer commentTokens) {
        StringBuilder templateBuilder = new StringBuilder();
        while (commentTokens.hasMoreTokens()) {
            templateBuilder.append(' ').append(commentTokens.nextToken());
        }
        String template = templateBuilder.toString().trim();
        return (template.length() > 0) ? template : null;
    }

    public String toString() {
        return value;
    }
}

