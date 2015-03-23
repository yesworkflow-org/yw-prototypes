package org.yesworkflow.graph;

public enum GraphView {
    
    PROCESS_CENTRIC_VIEW,
    DATA_CENTRIC_VIEW, 
    COMBINED_VIEW;
    
    public static GraphView get(String gv) throws Exception {
        if (gv.equalsIgnoreCase("process")) return GraphView.PROCESS_CENTRIC_VIEW;
        if (gv.equalsIgnoreCase("data")) return GraphView.DATA_CENTRIC_VIEW;
        if (gv.equalsIgnoreCase("combined")) return GraphView.COMBINED_VIEW;
        throw new Exception("Unrecognized GraphView: " + gv);
    }
}