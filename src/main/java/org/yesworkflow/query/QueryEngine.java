package org.yesworkflow.query;

public enum QueryEngine {
    CSV,
    DLV,
    IRIS,
    SWIPL,
    XSB, 
    DEFAULT;

    public static QueryEngine toQueryEngine(Object engine) throws Exception {
        
        if (engine instanceof QueryEngine) return (QueryEngine)engine;
        
        if (engine instanceof String) {
            String engineString = (String)engine;
            if (engineString.equalsIgnoreCase("CSV"))   return QueryEngine.CSV;
            if (engineString.equalsIgnoreCase("DLV"))   return QueryEngine.DLV;
            if (engineString.equalsIgnoreCase("IRIS"))  return QueryEngine.IRIS;
            if (engineString.equalsIgnoreCase("SWIPL")) return QueryEngine.SWIPL;
            if (engineString.equalsIgnoreCase("XSB"))   return QueryEngine.XSB;
            if (engineString.equalsIgnoreCase("DEFAULT")) return QueryEngine.DEFAULT;
        }
        
        throw new Exception("Unrecognized logic engine: " + engine);
    }

}