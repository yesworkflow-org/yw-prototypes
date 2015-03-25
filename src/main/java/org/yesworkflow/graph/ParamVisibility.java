package org.yesworkflow.graph;

public enum ParamVisibility {
    
    HIDDEN,
    LOW,
    SAME;
    
    public static ParamVisibility toParamVisibility(Object pv) throws Exception {
                
        if (pv instanceof ParamVisibility) return (ParamVisibility)pv;
        
        if (pv instanceof String) {
            String pvstring = (String)pv; 
            if (pvstring.equalsIgnoreCase("hidden")) return ParamVisibility.HIDDEN;
            if (pvstring.equalsIgnoreCase("low")) return ParamVisibility.LOW;
            if (pvstring.equalsIgnoreCase("same")) return ParamVisibility.SAME;
        }
        
        throw new Exception("Unrecognized ParamVisibility: " + pv);
    }
}