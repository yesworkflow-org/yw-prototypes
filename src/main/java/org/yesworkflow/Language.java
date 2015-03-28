package org.yesworkflow;

/** Enumeration of programming languages recognized by YesWorkflow. */
public enum Language {
    
    BASH,
    C,
    CPLUSPLUS,
    GENERIC,
    JAVA,
    MATLAB,
    PYTHON,
    R,
    SAS;
    
    public static Language toLanguage(Object lang) throws Exception {
        
        if (lang instanceof Language) return (Language)lang;
        
        if (lang instanceof String) {
            String langString = (String)lang; 
            if (langString.equalsIgnoreCase("BASH"))        return Language.BASH;
            if (langString.equalsIgnoreCase("C"))           return Language.C;
            if (langString.equalsIgnoreCase("CPLUSPLUS"))   return Language.CPLUSPLUS;
            if (langString.equalsIgnoreCase("GENERIC"))     return Language.GENERIC;
            if (langString.equalsIgnoreCase("JAVA"))        return Language.JAVA;
            if (langString.equalsIgnoreCase("MATLAB"))      return Language.MATLAB;
            if (langString.equalsIgnoreCase("PYTHON"))      return Language.PYTHON;
            if (langString.equalsIgnoreCase("R"))           return Language.R;
            if (langString.equalsIgnoreCase("SAS"))         return Language.SAS;
        }
        
        throw new Exception("Unrecognized scripting language: " + lang);
    }
}