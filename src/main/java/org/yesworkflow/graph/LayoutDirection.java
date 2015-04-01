package org.yesworkflow.graph;

public enum LayoutDirection {
    
    LEFT_TO_RIGHT,
    TOP_TO_BOTTOM;
    
    public static LayoutDirection toLayoutDirection(Object pv) throws Exception {
                
        if (pv instanceof LayoutDirection) return (LayoutDirection)pv;
        
        if (pv instanceof String) {
            String pvstring = (String)pv; 
            if (pvstring.equalsIgnoreCase("lr")) return LayoutDirection.LEFT_TO_RIGHT;
            if (pvstring.equalsIgnoreCase("horizontal")) return LayoutDirection.LEFT_TO_RIGHT;
            if (pvstring.equalsIgnoreCase("tb")) return LayoutDirection.TOP_TO_BOTTOM;
            if (pvstring.equalsIgnoreCase("vertical")) return LayoutDirection.TOP_TO_BOTTOM;
        }
        
        throw new Exception("Unrecognized LayoutDirection: " + pv);
    }
}