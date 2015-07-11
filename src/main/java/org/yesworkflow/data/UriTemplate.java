package org.yesworkflow.data;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

/**
 * <p> This class represents a URI template that can be matched
 * directly against other URI templates without expanding 
 * either template to a concrete URI.  Two URI templates match
 * if the path portions are identical other than the names
 * given to variables in the template.  I.e., for two templates to match,
 * the <i>positions</i> of the variables in the templates must match, 
 * but the <i>names</i> of the 
 * variables need not match.  Variable names also may be completely left 
 * out the templates, with variable positions represented by empty 
 * pairs of curly braces. </p>
 * 
 * <p> Instances of this classes can be expanded to concrete URIs by supplying
 * a mapping of variable names to values.  Template expansion also yields an
 * array of values corresponding to the variables in the template in the order
 * in which the value appear in the expanded URI.
 *
 * Instances of MatchableUriTemplate are immutable and thus thread safe. </p>
 * 
 * This file is derived from UriTemplate.java in the org.restflow.data package
 * as of 28Apr2015.
 */
public class UriTemplate extends UriBase {
	
	///////////////////////////////////////////////////////////////////
	////                    private data fields                    ////

	public final String    reducedPath;	    // Fully reduced, directly matchable representation of the URI template
	public final Path      leadingPath;     // Portion of path preceding any path element that contains variables
	private final String[] _variableNames;	// Array of variables named in the URI template in position order
	private final String[]	_pathFragments; // Array of strings representing non-variable portions of the template path
	
	///////////////////////////////////////////////////////////////////
	////                     public constructors                   ////

	/**
	 * Creates a new MatchableUriTemplate object.
	 * 
	 * @param template	A string representation of the full URI template to construct.
	 */
	public UriTemplate(String template) {

		super(template, true);

		// store a reduced version of the path with the variable names deleted
		// and extract the variable names and fixed portions of the template path
		List<String> variableNames = new LinkedList<String>();
		List<String> constantFragments = new LinkedList<String>();
		reducedPath = reduceTemplateAndExtractVariables(path, variableNames, constantFragments);
		
		// store the variable names as an array
		_variableNames = variableNames.toArray(new String[] {});

		// store the fixed portions of the template path as an array
		_pathFragments = constantFragments.toArray(new String[] {});
		
		String leadingPathString;
		if (_pathFragments.length == 0) {
		    leadingPathString = "";
		} else if (_variableNames.length == 0) {
		    leadingPathString = _pathFragments[0];
		} else {
		    int lastSlashInFirstFragment = _pathFragments[0].lastIndexOf('/');
		    if (lastSlashInFirstFragment == -1) {
		        leadingPathString = "";
		    } else {
		        leadingPathString = path.substring(0, lastSlashInFirstFragment);		        
		    }
		}
		
		leadingPath = Paths.get(leadingPathString);
	}	
	

	//TODO Add support for double slashes following scheme delimiter.
	
	///////////////////////////////////////////////////////////////////
	////                 public instance methods                   ////

	public String getGlobPattern() {
        StringBuilder globPatternBuilder = new StringBuilder(_pathFragments[0]);
        for (int i = 0; i < _variableNames.length; i++) {
            globPatternBuilder.append("*");
            globPatternBuilder.append(_pathFragments[i+1]);
        }
        return globPatternBuilder.toString();
    }	    
	
	/**
	 * Expands a URI template to a concrete URI given an array of variable name-value pairs.
	 * Also returns the values of the variables used in the template as an array ordered by
	 * appearance of the variables in the template.  Variables values will appear multiple times
	 * in the array if the corresponding variables are used more than once in the template.
	 * 
	 * @param nameValueMap	Mapping of variable names to values to be used
	 * 						when expanding the template.
	 * @param valueArray	Reference used to return an array of the values of the 
	 * 						template variables expanded, in order of appearance
	 * 						in the template.
	 * @param pathPrefix	A string to insert between the scheme (if present)
	 * 						and path portions of the uri template following expansion.
	 * @param pathSuffix	A string to append to the end of the uri template following
	 * 						expansion.
	 * @return				Concrete URI representing the requested expansion of the template.
	 * @throws				Exception if template has missing variables, i.e. empty braces
	 * 						and thus cannot be expanded.
	 */
	public ConcreteUri getExpandedUri(Map<String,Object> nameValueMap, Object[] valueArray, 
			  String pathPrefix, String pathSuffix) throws Exception {

		// create a string builder for assembling the uri
		StringBuilder uriBuilder = new StringBuilder();
		
		// start with the scheme and a colon if a scheme is defined
		if (scheme != ConcreteUri.NO_SCHEME) {
			uriBuilder.append(scheme);
			uriBuilder.append(':');
		}
		
		
		String expandedPath = getExpandedPath(nameValueMap, valueArray);

		// place the provided path prefix between the scheme and the expanded path
		if (pathPrefix.length() > 0) {
			uriBuilder.append(pathPrefix);
			if (!pathPrefix.endsWith("/") && !expandedPath.startsWith("/")) {
				uriBuilder.append("/");
			}
		}

		// expand the template path and append to the uri
		uriBuilder.append(expandedPath);
		
		// follow the expanded path with the provided suffix
		uriBuilder.append(pathSuffix);
		
		// create a URI object from the assembled uri string and return
		String uriString = uriBuilder.toString();
		ConcreteUri uri = new ConcreteUri(uriString);
		return uri;
	}

	/** @return The reduced, directly matchable representation of the URI template. */
	public String getReducedPath() {
		return reducedPath;
	}

	/** @return The number of variable positions in the URI template */
	public int getVariableCount() {
		return _variableNames.length;
	}
	
	/** @return A list of variables named in the URI template in order of their position */
	public String[] getVariableNames() {
		return Arrays.copyOf(_variableNames, _variableNames.length);
	}

	/** 
	 * @return true if the reduced paths of the two templates are identical, otherwise false
	 */
	public boolean matches(UriTemplate otherTemplate) {
		return this.reducedPath.equals(otherTemplate.reducedPath);
	}

	
	/**
	 * Expands the path portion of a URI template given an array of variable name-value pairs.
	 * Also returns the values of the variables used in the template as an array ordered by
	 * appearance of the variables in the template.  Variables values will appear multiple times
	 * in the array if the corresponding variables are used more than once in the template.
	 * 
	 * @param nameValueMap	Mapping of variable names to values to be used
	 * 						when expanding the template.
	 * @param valueArray	Reference used to return an array of the values of the 
	 * 						template variables expanded, in order of appearance
	 * 						in the template.
	 * @return				String containing the requested expansion of the path portion of the URI template.
	 * @throws				Exception if template has missing variables, i.e. empty braces
	 * 						and thus cannot be expanded.
	 */
	public String getExpandedPath(Map<String,Object> nameValueMap, Object[] valueArray) throws Exception {
		
		// create a string builder for assembling the expanded template path
		// starting with the first constant fragment of the template path
		StringBuilder expandedPathBuilder = new StringBuilder(_pathFragments[0]);
		
		// iterate over template variable indices
		for (int i = 0; i < _variableNames.length; i++) {
			
			// get the next variable name and make sure it isn't an empty string
			String name = _variableNames[i];
			if (name.isEmpty()) {
				throw new Exception("Cannot expand a URI template with missing variable names: " 
						+ expression);
			}
			
			// look up the variable value in the value map and make sure there is one
			Object value = nameValueMap.get(name);
			if (value == null) {
				throw new Exception ("Could not expand template. No value for variable '" + name + "'.");
			}
			
			// hex-encode any characters not safe to use in file system patha
			String encodedValue = encodeString(value.toString());
			
			// append the value of the current variable to the expanded path
			expandedPathBuilder.append(encodedValue);
			
			// append the next constant fragment of the template path
			expandedPathBuilder.append(_pathFragments[i+1]);

			// save the value of the current variable in the value array
			valueArray[i] = value;
		}
		
		// return the expanded path
		return expandedPathBuilder.toString();
	}

	///////////////////////////////////////////////////////////////////
	////                  public class methods                     ////
	
	/**
	 * Extract variables names from URI template.
	 *  
	 * @param fullTemplate A URI template including variable names.
	 * @param variables A list in which the method stores the names of the variables extracted from the full template.
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
			fragments.add(fullTemplate.substring(closingBracePosition + fragmentStartOffset, 
					openingBracePosition));
			
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
		fragments.add(fullTemplate.substring(closingBracePosition + fragmentStartOffset, 
				fullTemplate.length()));
		
		// add to the reduced template the portion of the full template that follows the final closing brace
		reducedPathBuffer.append(fullTemplate.substring(closingBracePosition, fullTemplate.length()));
		
		// return the reduced path
		return reducedPathBuffer.toString();
	}	
}
