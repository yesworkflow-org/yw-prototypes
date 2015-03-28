package org.yesworkflow;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/** 
 * Class that models the programming language of the script analyzed by YesWorkflow.

 * <p>Associates comment-delimiter strings with each language, and facilitates
 * inference of programming language from source file extension.</p>
 * 
 * <p>Supports strings indicating the start of single-line comments 
 * (e.g., lines starting with "#" in Python, Bash and R) as well as pairs of
 * strings that bracket comments (e.g., "%{" and "%}" in MATLAB). Allows
 * multiple strings of both kinds for each language (needed, e.g., for SAS).</p>
 * 
 * <p>Provides methods for comparing potential comment delimiter strings 
 * with those defined for the language.  These methods determine if there is 
 * a partial or full match with any of them.
 */
public class LanguageModel {

    /** Programming language represented by this model. */
    private final Language language;
    
    /** Backing for collection of single-line comment delimiters. */
    private List<String> singleCommentDelimiters = new LinkedList<String>();

    /** Backing for collection of paired comment delimiters. */
    private Map<String,String> pairedCommentDelimiters = new LinkedHashMap<String,String>();
    
    /** Mapping from recognized source file extensions to programming languages.*/
    private static Map<String,Language> languageForExtension;
    static {
        languageForExtension = new HashMap<String,Language>();
        languageForExtension.put("sh", Language.BASH);
        languageForExtension.put("c", Language.C);
        languageForExtension.put("h", Language.C);
        languageForExtension.put("cpp", Language.CPLUSPLUS);
        languageForExtension.put("java", Language.JAVA);
        languageForExtension.put("m", Language.MATLAB);
        languageForExtension.put("py", Language.PYTHON);
        languageForExtension.put("r", Language.R);
        languageForExtension.put("sas", Language.SAS);
    }
    
    /** Utility method for looking up the programming language
     * associated with the file extension of the provided file name.
     * @param fileName The name of the file from which to infer the language.
     * @return The inferred programming language, or 
     * {@link org.yesworkflow.Language Language}.GENERIC
     * if the extension is not recognized.
     */
    public static Language languageForFileName(String fileName) {

        Language language = null;
        
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            String extension = fileName.substring(i+1);
            language = languageForExtension.get(extension.toLowerCase());
        }
        
        if (language == null) language = Language.GENERIC;
        
        return language;
    }

    /** Constructor for models of languages not explicitly supported by 
     *  YesWorkflow. Comment delimiter strings can be assigned using 
     *  the {@link #singleDelimiter(String) delimiter()} and 
     *  {@link #delimiterPair(String, String) delimiterPair()} methods.
     */    
    public LanguageModel() {
        this.language = Language.GENERIC;
    }
    
    /** Constructor that builds a model for the given language.
     *  @param language The programming language to model.
     */
    public LanguageModel(Language language) {
        this.language = language;
        assignCommentDelimiters();
    }
            
    /** Constructor that builds a model for the language inferred
     *  from the extension of the provided filename.
     *  @param fileName The file name from which to infer the programming language.
     */
    public LanguageModel(String fileName) {
        this(languageForFileName(fileName));
    }
    
    /** Provides access to the collection of strings
     *  that signal the beginning of single-line comments.
     *  @return The list of single-line comment delimiters.
     */
    public List<String> getSingleCommentDelimiters() {
        return new ArrayList<String>(singleCommentDelimiters);
    }

    /** Provides access to the collection of pairs of strings
     *  used to bracket delimited, possibly multi-line comments.
     *  @return The list of comment delimiters pairs.
     */
    public Map<String,String> getPairedCommentDelimiters() {
        return new HashMap<String,String>(pairedCommentDelimiters);
    }
    
    /** Provides access to the language modeled by this instance.
     * @return The programming language.
     */
    public Language getLanguage() {
        return language;
    }
    
    /** Returns the name of the programming language modeled
     * by this instance.
     * @return The name of the programming language.
     */
    public String toString() {
        return language.toString();
    }
    
    /** Adds a single-line comment delimiter string to the model.
     * @param start A string indicating the start of a one-line comment.
     */
    public void singleDelimiter(String start) {
        singleCommentDelimiters.add(start);
    }

    /** Adds a pair of comment-delimiting strings to the model.
     * @param start A string indicating the start of a delimited, possibly multi-line comment.
     * @param end The corresponding string indicating the end of the comment.
     */
    public void delimiterPair(String start, String end) {
        pairedCommentDelimiters.put(start, end);
    }
    
    /** Enumeration of match conditions returned from comment delimiter matching methods.
     *  Enables match methods to distinguish between matches to the two kinds of start delimiters,
     *  and between full and prefix matches. 
     */
    public enum MatchExtent {
        NO_MATCH,
        PREFIX_MATCH,
        FULL_MATCH_SINGLE,
        FULL_MATCH_PAIRED,
        FULL_MATCH_SINGLE_PREFIX_MATCH_PAIRED,
        FULL_MATCH_PAIRED_PREFIX_MATCH_SINGLE
    }
    
    /**
     * Determines if the passed string matches any of the comment start delimiters
     * defined for the language.  Tries to match against the single delimiters
     * used to start one-line comments, as well as the start delimiters of 
     * delimiter pairs used to define partial-line or multi-line comments.  The
     * return value distinguishes between matches to the two kinds of start delimiters,
     * and between full and prefix matches.
     * 
     * @param s The potential comment start delimiter to be tested.
     * @return The extent of the match found.
     */
    public MatchExtent commentStartMatches(String s) {
        
        int length = s.length();
        
        // look for a match with single-line comment start delimiter
        MatchExtent singleMatchExtent = MatchExtent.NO_MATCH;
        for (String singleCommentDelimiter : singleCommentDelimiters) {
            if (singleCommentDelimiter.startsWith(s)) {
                singleMatchExtent = (length == singleCommentDelimiter.length()) ? 
                        MatchExtent.FULL_MATCH_SINGLE : MatchExtent.PREFIX_MATCH;
                break;
            }
        }
        
        // look for a match with partial-line/multi-line comment start delimiters
        MatchExtent pairedMatchExtent = MatchExtent.NO_MATCH;
        for (String startCommentDelimiter : pairedCommentDelimiters.keySet()) {
            if (startCommentDelimiter.startsWith(s)) {
                pairedMatchExtent = (length == startCommentDelimiter.length()) ? 
                        MatchExtent.FULL_MATCH_PAIRED : MatchExtent.PREFIX_MATCH;
                break;
            }
        }

        switch(singleMatchExtent) {
                            
            case PREFIX_MATCH:
                
                switch(pairedMatchExtent) {
                
                    case FULL_MATCH_PAIRED:
                        return MatchExtent.FULL_MATCH_PAIRED_PREFIX_MATCH_SINGLE;
                        
                    default:
                        return MatchExtent.PREFIX_MATCH;
                }
                
            case FULL_MATCH_SINGLE:
                
                switch(pairedMatchExtent) {
                
                    case PREFIX_MATCH:
                        return MatchExtent.FULL_MATCH_SINGLE_PREFIX_MATCH_PAIRED;
                        
                    default:
                        return MatchExtent.FULL_MATCH_SINGLE;
                }
                
            default:
                return pairedMatchExtent;
        }
    }
    
    /**
     * Determines if the passed string matches the comment end delimiter
     * corresponding to the provided comment start delimiter. The
     * return value distinguishes between full and prefix matches.
     * 
     * @param s The potential comment end delimiter to be tested.
     * @param startDelimiter The comment start delimiter corresponding to the expected
     *                       end delimiter.
     * @return The extent of the match found.
     */
    public MatchExtent commentEndMatches(String s, String startDelimiter) {
        
        String endCommentDelimiter = pairedCommentDelimiters.get(startDelimiter);
        if (endCommentDelimiter.startsWith(s)) {
            if (s.length() == endCommentDelimiter.length()) { 
                return MatchExtent.FULL_MATCH_PAIRED;
            } else {
                return MatchExtent.PREFIX_MATCH;
            }        
        } else {
            return MatchExtent.NO_MATCH;
        }
    }  
    
    /** Assigns comment delimiter strings to the model according to the 
     *  language the model represents.
     */
    private void assignCommentDelimiters() {
        
        if (language != null) {
            
            switch(language) {
            
            case BASH:
                singleDelimiter("#");
                break;
                
            case C:
                singleDelimiter("//");
                delimiterPair("/*", "*/");
                break;
    
            case CPLUSPLUS:
                singleDelimiter("//");
                delimiterPair("/*", "*/");
                break;
            
            case GENERIC:
                break;

            case JAVA:
                singleDelimiter("//");
                delimiterPair("/*", "*/");
                break;
            
            case MATLAB:
                singleDelimiter("%");
                delimiterPair("%{", "%}");
                delimiterPair("...", "...");
                break;
    
            case PYTHON:
                singleDelimiter("#");
                delimiterPair("\"\"\"", "\"\"\"");
                delimiterPair("'''", "'''");
                break;
            
            case R:
                singleDelimiter("#");
                break;
            
            case SAS:
                delimiterPair("*", ";");
                delimiterPair("/*", "*/");
                break;
            }
        }
    }
}
