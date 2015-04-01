package org.yesworkflow.graph;

public enum LayoutDirection {
    
    LR,
    RL,
    TB,
    BT;
    
    public static LayoutDirection toLayoutDirection(Object pv) throws Exception {
                
        if (pv instanceof LayoutDirection) return (LayoutDirection)pv;
        
        if (pv instanceof String) {
            
            String pvstring = (String)pv; 
            
            if (pvstring.equalsIgnoreCase("lr") ||pvstring.equalsIgnoreCase("lefttoright"))
                return LayoutDirection.LR;
            
            if (pvstring.equalsIgnoreCase("rl") ||pvstring.equalsIgnoreCase("righttoleft"))
                return LayoutDirection.RL;

            if (pvstring.equalsIgnoreCase("tb") || pvstring.equalsIgnoreCase("toptobottom"))
                return LayoutDirection.TB;
            
            if (pvstring.equalsIgnoreCase("bt") || pvstring.equalsIgnoreCase("bottomtotop"))
                return LayoutDirection.BT;
        }
        
        throw new Exception("Unrecognized LayoutDirection: " + pv);
    }
}