package org.yesworkflow.cli;

public enum YWCommand {
    
    NOOP,
    EXTRACT,
    MODEL,
    GRAPH,
    RECON,
    SAVE;
    
    public static YWCommand toYWCommand(Object ywc) throws Exception {
                
        if (ywc instanceof YWCommand) return (YWCommand)ywc;
        
        if (ywc instanceof String) {
            String ywcstring = (String)ywc; 
            if (ywcstring.equalsIgnoreCase("noop")) return YWCommand.NOOP;
            if (ywcstring.equalsIgnoreCase("extract")) return YWCommand.EXTRACT;
            if (ywcstring.equalsIgnoreCase("model")) return YWCommand.MODEL;
            if (ywcstring.equalsIgnoreCase("graph")) return YWCommand.GRAPH;
            if (ywcstring.equalsIgnoreCase("recon")) return YWCommand.RECON;
            if (ywcstring.equalsIgnoreCase("save")) return YWCommand.SAVE;
        }
        
        throw new Exception("Unrecognized YW command: " + ywc);
    }
}