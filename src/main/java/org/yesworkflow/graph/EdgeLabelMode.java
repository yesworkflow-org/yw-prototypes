package org.yesworkflow.graph;

public enum EdgeLabelMode {
    
    HIDE,
    SHOW;
    
    public static EdgeLabelMode toEdgeLabelMode(Object elm) throws Exception {
                
        if (elm instanceof EdgeLabelMode) return (EdgeLabelMode)elm;
        
        if (elm instanceof String) {
            String elmstring = (String)elm; 
            if (elmstring.equalsIgnoreCase("hide")) return EdgeLabelMode.HIDE;
            if (elmstring.equalsIgnoreCase("show")) return EdgeLabelMode.SHOW;
        }
        
        throw new Exception("Unrecognized EdgeLabelMode: " + elm);
    }
}