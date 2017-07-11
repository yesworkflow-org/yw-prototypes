package org.yesworkflow.data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

public class LogEntryTemplate {

    private static Long nextVariableId = 1L;
    
    public static void resetIds() {
        nextVariableId = 1L;
    }
    
    public final String template;
    public final String reducedTemplate;       // Fully reduced, directly matchable representation of the template
	public final TemplateVariable[] variables; // Array of template variables in order of their first appearnce
    public final TemplateVariable[] instances; // Array of references to variables in order of each occurrence in the template
	public final String[] fragments;           // Array of strings representing non-variable portions of the template path
	
	/**
	 * Creates a new MatchableTemplate object.
	 * 
	 * @param template	A string representation of the full template to construct.
	 */
	public LogEntryTemplate(String template) {

	    this.template = template;
	    
		// store a reduced version of the path with the variable names deleted
		// and extract the variable names and fixed portions of the template path
		List<String> variableNames = new LinkedList<String>();
		List<String> constantFragments = new LinkedList<String>();
		reducedTemplate = reduceTemplateAndExtractVariables(template, variableNames, constantFragments);
		instances = new TemplateVariable[variableNames.size()];
		Map<String,TemplateVariable> templateVariableForName = new LinkedHashMap<String,TemplateVariable>();
		int position = 0;
		for (String name : variableNames) {
		    TemplateVariable templateVariable = templateVariableForName.get(name);
		    if (!name.isEmpty()) {
    		    if (templateVariable == null) {
    		        templateVariable = new TemplateVariable(nextVariableId++, name);
    		        templateVariableForName.put(name, templateVariable);
    		    }
		    }
		    instances[position++] = templateVariable;
		}
		
		variables = new TemplateVariable[templateVariableForName.size()];
		int i = 0;
		for (Map.Entry<String, TemplateVariable> entry : templateVariableForName.entrySet()) {
		    variables[i++] = entry.getValue();
		}

		// store the fixed portions of the template path as an array
		fragments = constantFragments.toArray(new String[] {});
	}

	public String getGlobPattern() {
        StringBuilder globPatternBuilder = new StringBuilder("glob:");
        globPatternBuilder.append(fragments[0]);
        for (int i = 0; i < fragments.length - 1; i++) {
            globPatternBuilder.append("*");
            globPatternBuilder.append(fragments[i+1]);
        }
        return globPatternBuilder.toString();
    }

	public Pattern getRegexpPattern() {
	    StringBuilder regexpPatternBuilder = new StringBuilder("");
        regexpPatternBuilder.append(fragments[0]);
        for (int i = 0; i < fragments.length - 1; i++) {
            regexpPatternBuilder.append("[\\S]+");
            regexpPatternBuilder.append(fragments[i+1]);
        }
        Pattern pattern = Pattern.compile(regexpPatternBuilder.toString());
        return pattern;
    }
	
    public Map<String,String> extractValuesFromLogEntry(String entry) throws Exception {

        Map<String,String> variableValues = new LinkedHashMap<String,String>();

        if (entry.length() == 0) return variableValues;
        
        if (fragments.length == 0) {
            variableValues.put(variables[0].name, entry);
            return variableValues;
        }
        
        int start = 0;
        int valueStart = 0;
        int valueEnd = 0;
        int i;
        for (i = 0; i < fragments.length - 1; ++i) {
            valueStart = start + fragments[i].length();
            valueEnd = entry.indexOf(fragments[i+1], valueStart);
            if (valueEnd == -1) return null;
            TemplateVariable variable = instances[i];
            // TODO make sure values in concrete log entry match for multiple instances of a variable
            if (variable != null && variableValues.get(variable.name) == null) {
                String value = entry.substring(valueStart, valueEnd);
                variableValues.put(variable.name, value);
                if (!variable.name.isEmpty()) {
                    variableValues.put(variable.name, value);
                }
            }
            start = valueEnd;
        }
       
        if (i > 0 && instances.length == i+1) {
            TemplateVariable variable = instances[i];
            String value = entry.substring(start + fragments[i].length());
            variableValues.put(variable.name, value);
        }

        return variableValues;
    }

	/** @return The reduced, directly matchable representation of the leg entry template. */
	public String getReducedTemplate() {
		return reducedTemplate;
	}
	
	/**
	 * Extract variables names from log entry template.
	 *  
	 * @param fullTemplate A log entry template including variable names.
	 * @param variables A list in which the method stores the names of the variables extracted from the full template.
	 * @param fragments A list the constant fragments of the log entry template.
	 * @return A reduced template with variable names removed.
	 */
	public static String reduceTemplateAndExtractVariables(String fullTemplate, List<String> variables, 
			List<String> fragments) {
		
		// create a buffer for composing the reduced template
		StringBuffer reducedPathBuffer = new StringBuffer();

		// iterate over paired braces in full template
		int openingBracePosition = 0;
		int closingBracePosition = 0;
		int fragmentStartOffset = 0;
		
		while ((openingBracePosition = fullTemplate.indexOf('{', closingBracePosition)) != -1) {
			
			// store substring between last closing brace and next opening brace as fixed path fragment
		    if (openingBracePosition == 0) {
		        fragments.add("");
		    } else {
		        fragments.add(fullTemplate.substring(closingBracePosition + fragmentStartOffset, 
		                openingBracePosition));
		    }
		    
			// add to reduced template substring starting at last closing brace through the next opening brace in full template
			reducedPathBuffer.append(fullTemplate.substring(closingBracePosition, openingBracePosition + 1));
			
			// find next closing brace in full template
			closingBracePosition = fullTemplate.indexOf('}', openingBracePosition);
			
			// save the variable name between the current pair of braces
			String variableName = fullTemplate.substring(openingBracePosition + 1, closingBracePosition);
			variables.add(variableName);
			
			// constant fragments begin one character after last closing brace after first pass through loop
			fragmentStartOffset = 1;
		}

		// store the final fixed path fragment
		if (closingBracePosition + fragmentStartOffset < fullTemplate.length()) {
    		fragments.add(fullTemplate.substring(closingBracePosition + fragmentStartOffset, 
    				fullTemplate.length()));
		}
		
		// add to the reduced template the portion of the full template that follows the final closing brace
		reducedPathBuffer.append(fullTemplate.substring(closingBracePosition, fullTemplate.length()));
		
		// return the reduced path
		return reducedPathBuffer.toString();
	}
}
