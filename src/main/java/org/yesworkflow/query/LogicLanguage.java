package org.yesworkflow.query;

public enum LogicLanguage {
    PROLOG,
    DATALOG_DLV,
    DATALOG_IRIS;

    public static LogicLanguage toLogicLanguage(Object lang) throws Exception {
        
        if (lang instanceof LogicLanguage) return (LogicLanguage)lang;
        
        if (lang instanceof String) {
            String langString = (String)lang; 
            if (langString.equalsIgnoreCase("PROLOG")) return LogicLanguage.PROLOG;
            if (langString.equalsIgnoreCase("DLV"))    return LogicLanguage.DATALOG_DLV;
            if (langString.equalsIgnoreCase("IRIS"))   return LogicLanguage.DATALOG_IRIS;
        }
        
        throw new Exception("Unrecognized logic language: " + lang);
    }

}