package org.yesworkflow.data;

/**
 * <p> This class represents the fields and methods shared between
 * Uri and UriTemplate. 
 *
 * All fields of a UriBase instance are final and immutable, and the class
 * is thread safe. </p>
 * 
 * This file is derived from UriBase.java in the org.restflow.data package
 * as of 28Apr2015.
 */
public abstract class UriBase {

	///////////////////////////////////////////////////////////////////
	////                  protected data fields                    ////

	protected final String		expression;		// The full URI expression
	protected final String 		path;			// The path portion of the URI
	protected final String		name;			// The portion of the path following the last slash
	protected final String 		scheme;			// The scheme portion of the URI

	///////////////////////////////////////////////////////////////////
	////                  public static constants                  ////
	
	/** Value returned by {@link #getScheme()} when no scheme is included in the full template. */
	public static final String NO_SCHEME = "";

	
	/**
	 * Initializes the core fields of a Uri or UriTemplate object.
	 * Extracts the scheme and path portions of the passed expression
	 * and optionally trims the terminal slash from the path.
	 * 
	 * @param expression			A string representation of the full 
	 *                           	URI or URI template to construct.
	 * @param trimTerminalSlash		True to remove the terminal slash of expression
	 *                              prior to further processing.
	 * 
	 */
	public UriBase(String expression, boolean trimTerminalSlash) {

		if (trimTerminalSlash) {
			this.expression = trimTerminalSlash(expression);
		} else {
			this.expression = expression;
		}
		
		// extract scheme and path from uri and store each
		SchemePathPair spp = extractSchemeAndPath(this.expression);
		scheme = spp.scheme;
		path = spp.path;
		
		// extract the name portion of the path
		name = extractPathName(path);
	}	

	/**
	 * Initializes the core fields of a Uri or UriTemplate object.
	 * Extracts the scheme and path portions of the passed expression.
	 * Does not trim the terminal slash from the path.
	 * 
	 * @param expression			A string representation of the full 
	 *                           	URI or URI template to construct.
	 */
	public UriBase(String uriString) {
		this(uriString, false);
	}
	
	/** @return The full URI represented by the instance. */
	public String getExpression() {
		return expression;
	}
	
	/** @return The name portion of the URI. */
	public String getName() {
		return name;
	}

	/** @return The path portion of the URI. */
	public String getPath() {
		return path;
	}
	
	/** @return	The scheme portion of the URI if one is defined, otherwise {@link #NO_SCHEME} */
	public String getScheme() { 
		return scheme; 
	}
	
	/** 
	 * @return String representation of the full URI.
	 */
	public String toString() {
		return getExpression();
	}
	
	/**
	 * Returns the portion of a path that follows the final slash, or the entire path
	 * if there is no slash. The empty string is returned if the path ends with a slash.
	 *  
	 * @param path A path expression contanining zero or more slashes.
	 * @return The name (terminal text) portion of the path.
	 */
	public static String extractPathName(String path) {
		
		// locate the final slash in the path
		int finalSlashIndex = path.lastIndexOf('/');

		String name;
		
		// the name is the entire path if no slash was found
		if (finalSlashIndex == -1) {
			name = path;
		
		// the name is the empty string if the final slash is the last character in ther
		} else if (finalSlashIndex == path.length() - 1) {
			name = "";
			
		// otherwise the name is the text following the final slash
		} else {
			name = path.substring(finalSlashIndex + 1);
		}
		
		return name;
	}
	
	
	/**
	 * Splits a URI string into scheme and path portions.  Assumes that the first
	 * colon in the string delimits the scheme and path portion of the URI.
	 *  
	 * @param uriString A string representation of a URI optionally including 
	 *        a scheme portion.
	 * @return A custom object comprising the scheme and path portions of 
	 * 		   a URI as two public fields.
	 */
	public static SchemePathPair extractSchemeAndPath(String uriString) {

		// create a SchemePathPair to hold the two return values
		SchemePathPair spp = new SchemePathPair();
		
		// locate the scheme-path delimiter
		int colonIndex = uriString.indexOf(':');
		
		// if there is no delimiter there is no scheme
		if (colonIndex == -1) {
			
			// indicate that no scheme was given
			spp.scheme = ConcreteUri.NO_SCHEME;
			
			// take the entire uri string as the path
			spp.path = uriString;
		
		// otherwise there is both a scheme and a path
		} else {
			
			// take the portion of the uri string before the delimiter as the scheme
			spp.scheme = uriString.substring(0, colonIndex);
			
			// take the portion following the delimiter as the path
			spp.path = uriString.substring(spp.scheme.length() + 1);
		}
		
		// reduce any double-slashes in path to single slashes
		spp.path = spp.path.replaceAll("//", "/");
		
		// return the scheme-path pair as a single return value
		return spp;
	}

	
	/**
	 * Removes the terminal slash from a URI string if present.  The slash
	 * is not removed if it comprises the entire path portion of the URI string.
	 *  
	 * @param uriString A String representing a URI that may end with a slash.
	 * @return A string representing the input URI with terminal slash removed.
	 *
	 */
	public static String trimTerminalSlash(String uriString) {
		
		// get the length of the uri
		int uriLength = uriString.length();
		
		// make sure uri is longer than one character and ends in slash
		if (uriLength > 1 && uriString.endsWith("/")) {
			
			// find the end of the uri scheme if any
			int colonIndex = uriString.indexOf(':');
			
			// strip off the terminal slash if there is no scheme or 
			// there are more than two characters following the colon
			if (colonIndex == -1 || uriLength - colonIndex > 3) {
				
				// return the uri string minus the final slash character
				return uriString.substring(0, uriLength -1);
			}
		}
		
		// in all other cases return the original uri string
		return uriString;
	}
	
	public static String encodeString(String unescapedString) {
		
		StringBuffer escapedString = new StringBuffer(unescapedString);
		
		for (int index = 0; index < escapedString.length(); index++) {
			Character c = escapedString.charAt(index);
			if ((!Character.isLetterOrDigit(c)) && (c != '-') && (c != '.') && (c != '_') && (c != '~')) {
				String hexCode = String.format("%02x", (int)c);
				escapedString.replace(index, index+1, "%" + hexCode);
				index += hexCode.length();
			}
		}
		
		return escapedString.toString();
	}

	/**
	 * Returns the scheme and path portion of uri, up to the final element.
	 *  
	 * @param path A path expression contanining zero or more slashes.
	 * @return The parent portion of the path.
	 */
	public static String extractParent(String path) {
		if (path.endsWith("/")) return "";
		
		// locate the final slash in the path
		int finalSlashIndex = path.lastIndexOf('/');

		if (finalSlashIndex == -1) {
			int colonIndex = path.indexOf(':');
			
			if (colonIndex == -1) return "";
			return path.substring(0,colonIndex+1);
		}
		
		return path.substring(0,finalSlashIndex+1);
	}
	

	///////////////////////////////////////////////////////////////////
	////                  public inner classes                     ////
	
	/**
	 * This class is used by extractSchemeAndPath() to return a pair of values,
	 * one representing the scheme portion of a URI, the other the path portion of
	 * the template.  The class provides two public fields for storing and accessing 
	 * these values.
	 */
	public static class SchemePathPair {
		public String scheme;
		public String path;		
	}
}
