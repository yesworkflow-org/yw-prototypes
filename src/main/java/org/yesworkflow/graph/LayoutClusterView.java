package org.yesworkflow.graph;

public enum LayoutClusterView {
    
    ON,
    OFF;
    
    public static LayoutClusterView toLayoutClusterView(Object lc) throws Exception {
        
        if (lc instanceof LayoutClusterView) return (LayoutClusterView)lc;
        
        if (lc instanceof String) {
            String lcstring = (String)lc; 
            if (lcstring.equalsIgnoreCase("on")) return LayoutClusterView.ON;
            if (lcstring.equalsIgnoreCase("off")) return LayoutClusterView.OFF;
        }

        throw new Exception("Unrecognized LayoutClusterView: " + lc);
    }
}