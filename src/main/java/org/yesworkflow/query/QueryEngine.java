package org.yesworkflow.query;

public enum QueryEngine {
    DLV,
    IRIS,
    SWIPL,
    XSB;

    public static QueryEngine toQueryEngine(Object engine) throws Exception {
        
        if (engine instanceof QueryEngine) return (QueryEngine)engine;
        
        if (engine instanceof String) {
            String engineString = (String)engine; 
            if (engineString.equalsIgnoreCase("DLV"))   return QueryEngine.DLV;
            if (engineString.equalsIgnoreCase("IRIS"))  return QueryEngine.IRIS;
            if (engineString.equalsIgnoreCase("SWIPL")) return QueryEngine.SWIPL;
            if (engineString.equalsIgnoreCase("XSB"))   return QueryEngine.XSB;
        }
        
        throw new Exception("Unrecognized logic engine: " + engine);
    }

}