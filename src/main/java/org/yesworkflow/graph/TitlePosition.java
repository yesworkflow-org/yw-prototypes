package org.yesworkflow.graph;

public enum TitlePosition {
    
    HIDE,
    TOP_CENTER;
    
    public static TitlePosition toTitlePosition(Object tp) throws Exception {
                
        if (tp instanceof TitlePosition) return (TitlePosition)tp;
        
        if (tp instanceof String) {
            String wtmstring = (String)tp; 
            if (wtmstring.equalsIgnoreCase("hide")) return TitlePosition.HIDE;
            if (wtmstring.equalsIgnoreCase("topcenter")) return TitlePosition.TOP_CENTER;
        }
        
        throw new Exception("Unrecognized TitlePosition: " + tp);
    }
}