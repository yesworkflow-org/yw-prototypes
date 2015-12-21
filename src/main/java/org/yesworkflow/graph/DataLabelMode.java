package org.yesworkflow.graph;

public enum DataLabelMode {
    
    NAME,
    URI,
    BOTH;
    
    public static DataLabelMode toDataLabelMode(Object dlm) throws Exception {
                
        if (dlm instanceof DataLabelMode) return (DataLabelMode)dlm;
        
        if (dlm instanceof String) {
            String dlmstring = (String)dlm;
            if (dlmstring.equalsIgnoreCase("name")) return DataLabelMode.NAME;
            if (dlmstring.equalsIgnoreCase("uri")) return DataLabelMode.URI;
            if (dlmstring.equalsIgnoreCase("both")) return DataLabelMode.BOTH;
        }
        
        throw new Exception("Unrecognized DataLabelMode: " + dlm);
    }
}