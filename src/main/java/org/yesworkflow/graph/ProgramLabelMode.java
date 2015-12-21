package org.yesworkflow.graph;

public enum ProgramLabelMode {
    
    NAME,
    DESCRIPTION,
    BOTH;
    
    public static ProgramLabelMode toProgramLabelMode(Object plm) throws Exception {
                
        if (plm instanceof ProgramLabelMode) return (ProgramLabelMode)plm;
        
        if (plm instanceof String) {
            String plmstring = (String)plm;
            if (plmstring.equalsIgnoreCase("name")) return ProgramLabelMode.NAME;
            if (plmstring.equalsIgnoreCase("description")) return ProgramLabelMode.DESCRIPTION;
            if (plmstring.equalsIgnoreCase("both")) return ProgramLabelMode.BOTH;
        }
        
        throw new Exception("Unrecognized ProgramLabelMode: " + plm);
    }
}