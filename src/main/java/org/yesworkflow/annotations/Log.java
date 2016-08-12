package org.yesworkflow.annotations;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.data.LogEntryTemplate;
import org.yesworkflow.data.TemplateVariable;

public class Log extends Qualification {

    public final LogEntryTemplate entryTemplate;

    private Map<String,Long> templateVariables = new LinkedHashMap<String,Long>();
    
    public Log(Long id, Long sourceId, Long lineNumber, String comment, Out primaryAnnotation) throws Exception {
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.LOG, primaryAnnotation);
        StringTokenizer commentTokens = new StringTokenizer(comment);
        commentTokens.nextToken();
        value = buildTemplateString(commentTokens);
        entryTemplate = new LogEntryTemplate(value);
        identifyTemplateVariables();
    }

    private String buildTemplateString(StringTokenizer commentTokens) {
        StringBuilder templateBuilder = new StringBuilder();
        while (commentTokens.hasMoreTokens()) {
            templateBuilder.append(' ').append(commentTokens.nextToken());
        }
        String template = templateBuilder.toString().trim();
        return (template.length() > 0) ? template : null;
    }

    private void identifyTemplateVariables() {
        for (TemplateVariable variable : entryTemplate.variables) {
            templateVariables.put(variable.name, variable.id);
        }
    }
    
    public Long variableId(String name) {
        return templateVariables.get(name);
    }
    
    public String toString() {
        return value;
    }
}

