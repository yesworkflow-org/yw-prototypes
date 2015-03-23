package org.yesworkflow.graph;

public enum ParamVisibility { 
    HIDDEN,
    LOW,
    SAME;
    
    public static ParamVisibility get(String pv) throws Exception {
        if (pv.equalsIgnoreCase("hidden")) return ParamVisibility.HIDDEN;
        if (pv.equalsIgnoreCase("low")) return ParamVisibility.LOW;
        if (pv.equalsIgnoreCase("same")) return ParamVisibility.SAME;
        throw new Exception("Unrecognized ParamVisibility: " + pv);
    }
}