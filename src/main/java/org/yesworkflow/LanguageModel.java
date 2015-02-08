package org.yesworkflow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class LanguageModel {

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

    static public class DelimiterPair {
        public final String start;
        public final String end;
        DelimiterPair(String start, String end) {
            this.start = start;
            this.end = end;
        }
    }
    
    private Language language;
    private List<String> singleLineCommentDelimiters = new LinkedList<String>();
    private List<DelimiterPair> delimitedCommentDelimiters = new LinkedList<DelimiterPair>();
    
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
    
    public LanguageModel(Language language) {
        
        this.language = language;
        
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
    
    public LanguageModel(String fileName) {
        this(languageForFileName(fileName));
    }
    
    public String toString() {
        return language.toString();
    }
    
    private static Language languageForFileName(String fileName) {
        int i = fileName.lastIndexOf(".");
        if (i == -1) return null;        
        String extension = fileName.substring(i+1);
        return languageForExtension.get(extension.toLowerCase());
    }
    
    private void delimiter(String start) {
        singleLineCommentDelimiters.add(start);
    }

    private void delimiterPair(String start, String end) {
        delimitedCommentDelimiters.add(new DelimiterPair(start, end));
    }
    
    public List<String> getSingleLineCommentDelimiters() {
        return new ArrayList<String>(singleLineCommentDelimiters);
    }

    public List<DelimiterPair> getDelimitedCommentDelimiters() {
        return new ArrayList<DelimiterPair>(delimitedCommentDelimiters);
    }
    
    public Language getLanguage() {
        return language;
    }
}
