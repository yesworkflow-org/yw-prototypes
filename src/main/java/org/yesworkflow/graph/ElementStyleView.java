package org.yesworkflow.graph;

public enum ElementStyleView {
    
    ON,
    OFF;
    
    public static ElementStyleView toElementStyleView(Object es) throws Exception {
        
        if (es instanceof ElementStyleView) return (ElementStyleView)es;
        
        if (es instanceof String) {
            String esstring = (String)es; 
            if (esstring.equalsIgnoreCase("on")) return ElementStyleView.ON;
            if (esstring.equalsIgnoreCase("off")) return ElementStyleView.OFF;
        }

        throw new Exception("Unrecognized ElementStyleView: " + es);
    }
}