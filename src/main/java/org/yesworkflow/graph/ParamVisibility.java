package org.yesworkflow.graph;

public enum ParamVisibility {
    
    HIDE,
    REDUCE,
    SHOW;
    
    public static ParamVisibility toParamVisibility(Object pv) throws Exception {
                
        if (pv instanceof ParamVisibility) return (ParamVisibility)pv;
        
        if (pv instanceof String) {
            String pvstring = (String)pv; 
            if (pvstring.equalsIgnoreCase("hide")) return ParamVisibility.HIDE;
            if (pvstring.equalsIgnoreCase("reduce")) return ParamVisibility.REDUCE;
            if (pvstring.equalsIgnoreCase("show")) return ParamVisibility.SHOW;
        }
        
        throw new Exception("Unrecognized ParamVisibility: " + pv);
    }
}