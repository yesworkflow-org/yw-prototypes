package org.yesworkflow.graph;

public enum WorkflowBoxMode {
    
    HIDE,
    SHOW;
    
    public static WorkflowBoxMode toWorkflowBoxMode(Object wbm) throws Exception {
                
        if (wbm instanceof WorkflowBoxMode) return (WorkflowBoxMode)wbm;
        
        if (wbm instanceof String) {
            String wbmstring = (String)wbm; 
            if (wbmstring.equalsIgnoreCase("hide")) return WorkflowBoxMode.HIDE;
            if (wbmstring.equalsIgnoreCase("show")) return WorkflowBoxMode.SHOW;
        }
        
        throw new Exception("Unrecognized LayoutDirection: " + wbm);
    }
}