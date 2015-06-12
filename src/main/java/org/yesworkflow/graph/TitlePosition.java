package org.yesworkflow.graph;

public enum TitlePosition {
    
    HIDE,
    TOP,
    BOTTOM;
    
    public static TitlePosition toTitlePosition(Object tp) throws Exception {
                
        if (tp instanceof TitlePosition) return (TitlePosition)tp;
        
        if (tp instanceof String) {
            String tpstring = (String)tp; 
            if (tpstring.equalsIgnoreCase("hide")) return TitlePosition.HIDE;
            if (tpstring.equalsIgnoreCase("t")) return TitlePosition.TOP;
            if (tpstring.equalsIgnoreCase("top")) return TitlePosition.TOP;
            if (tpstring.equalsIgnoreCase("b")) return TitlePosition.BOTTOM;
            if (tpstring.equalsIgnoreCase("bottom")) return TitlePosition.BOTTOM;
        }
        
        throw new Exception("Unrecognized TitlePosition: " + tp);
    }
}