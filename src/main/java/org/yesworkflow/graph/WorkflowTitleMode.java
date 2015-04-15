package org.yesworkflow.graph;

public enum WorkflowTitleMode {
    
    HIDE,
    SHOW;
    
    public static WorkflowTitleMode toWorkflowTitleMode(Object wtm) throws Exception {
                
        if (wtm instanceof WorkflowTitleMode) return (WorkflowTitleMode)wtm;
        
        if (wtm instanceof String) {
            String wtmstring = (String)wtm; 
            if (wtmstring.equalsIgnoreCase("hide")) return WorkflowTitleMode.HIDE;
            if (wtmstring.equalsIgnoreCase("show")) return WorkflowTitleMode.SHOW;
        }
        
        throw new Exception("Unrecognized WorkflowTitleMode: " + wtm);
    }
}