package org.yesworkflow.graph;

public enum GraphView {
    
    PROCESS_CENTRIC_VIEW,
    DATA_CENTRIC_VIEW, 
    COMBINED_VIEW;
    
    public static GraphView toGraphView(Object gv) throws Exception {
        
        if (gv instanceof GraphView) return (GraphView)gv;
        
        if (gv instanceof String) {
            String gvstring = (String)gv; 
            if (gvstring.equalsIgnoreCase("process")) return GraphView.PROCESS_CENTRIC_VIEW;
            if (gvstring.equalsIgnoreCase("data")) return GraphView.DATA_CENTRIC_VIEW;
            if (gvstring.equalsIgnoreCase("combined")) return GraphView.COMBINED_VIEW;
        }
        
        throw new Exception("Unrecognized GraphView: " + gv);
    }
}