package org.yesworkflow.graph;

public enum PortLayout {
    
    HIDE,
    RELAX,
    GROUP;
    
    public static PortLayout toPortLayout(Object pl) throws Exception {
                
        if (pl instanceof PortLayout) return (PortLayout)pl;
        
        if (pl instanceof String) {
            String wbmstring = (String)pl; 
            if (wbmstring.equalsIgnoreCase("hide")) return PortLayout.HIDE;
            if (wbmstring.equalsIgnoreCase("relax")) return PortLayout.RELAX;
            if (wbmstring.equalsIgnoreCase("group")) return PortLayout.GROUP;
        }
        
        throw new Exception("Unrecognized PortLayout: " + pl);
    }
}