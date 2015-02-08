package org.yesworkflow;

import java.util.ArrayList;
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
 * multiple strings of both kinds for each language.</p>
 */
public class LanguageModel {

    /** Enumeration of programming languages recognized by YesWorkflow. */
    public enum Language {
        BASH,
        C,
        CPLUSPLUS,
        JAVA,
        MATLAB,
        PYTHON,
        R,
        SAS
    }

    /** Class representing a pair of strings used to bracket a comment
     *  spanning one or more lines. */
    static public class DelimiterPair {
        
        /** The comment-start delimiter string. */
        public final String start;          

        /** The comment-end delimiter string. */
        public final String end;
        
        /** Constructor that initializes both fields.
         *  @param start Comment-start delimiter string.
         *  @param end Comment-end delimiter string.  
         */ 
        DelimiterPair(String start, String end) {
            this.start = start;
            this.end = end;
        }
    }
    
    /** Programming language represented by this model. */
    private Language language;
    
    /** Backing for collection of single-line comment delimiters. */
    private List<String> singleLineCommentDelimiters = new LinkedList<String>();

    /** Backing for collection of paired comment delimiters. */
    private List<DelimiterPair> delimitedCommentDelimiters = new LinkedList<DelimiterPair>();
    
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
     * @return The inferred programming language, or null if the extension is not recognized.
     */
    public static Language languageForFileName(String fileName) {
        int i = fileName.lastIndexOf(".");
        if (i == -1) return null;        
        String extension = fileName.substring(i+1);
        return languageForExtension.get(extension.toLowerCase());
    }

    /** Constructor for models of languages not explicitly supported by 
     *  YesWorkflow. Comment delimiter strings can be assigned using 
     *  the {@link #delimiter(String) delimiter()} and 
     *  {@link #delimiterPair(String, String) delimiterPair()} methods.
     *  @param language The programming language to model.
     */    
    public LanguageModel() {}
    
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
    public List<String> getSingleLineCommentDelimiters() {
        return new ArrayList<String>(singleLineCommentDelimiters);
    }

    /** Provides access to the collection of pairs of strings
     *  used to bracket delimited, possibly multi-line comments.
     *  @return The list of comment delimiters pairs.
     */
    public List<DelimiterPair> getDelimitedCommentDelimiters() {
        return new ArrayList<DelimiterPair>(delimitedCommentDelimiters);
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
    public void delimiter(String start) {
        singleLineCommentDelimiters.add(start);
    }

    /** Adds a pair of comment-delimiting strings to the model.
     * @param start A string indicating the start of a delimited, possibly multi-line comment.
     * @param end The corresponding string indicating the end of the comment.
     */
    public void delimiterPair(String start, String end) {
        delimitedCommentDelimiters.add(new DelimiterPair(start, end));
    }
    
    /** Assigns comment delimiter strings to the model according to the 
     *  language the model represents.
     */
    private void assignCommentDelimiters() {
        
        if (language != null) {
            
            switch(language) {
            
            case BASH:
                delimiter("#");
                break;
                
            case C:
                delimiter("//");
                delimiterPair("/*", "*/");
                break;
    
            case CPLUSPLUS:
                delimiter("//");
                delimiterPair("/*", "*/");
                break;
            
            case JAVA:
                delimiter("//");
                delimiterPair("/*", "*/");
                break;
            
            case MATLAB:
                delimiter("%");
                delimiterPair("%{", "%}");
                break;
    
            case PYTHON:
                delimiter("#");
                delimiterPair("\"\"\"", "\"\"\"");
                break;
            
            case R:
                delimiter("#");
                break;
            
            case SAS:
                delimiterPair("*", ";");
                delimiterPair("/*", "*/");
                break;
            }
        }
    }
    
}
