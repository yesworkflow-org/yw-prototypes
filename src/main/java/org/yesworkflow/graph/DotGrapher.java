package org.yesworkflow.graph;

import java.io.FileWriter;
import java.io.Writer;

import org.yesworkflow.model.Channel;
import org.yesworkflow.model.Port;
import org.yesworkflow.model.Program;
import org.yesworkflow.model.Workflow;

public class DotGrapher implements Grapher  {

	final static String EOL = System.getProperty("line.separator");
	
    private Workflow workflow = null;
    private GraphType graphType = null;
    private String graphText = null;
    private String filePath = null;
    private Writer writer = null;

    @SuppressWarnings("unused")
    private GraphFormat graphFormat = null;
    
    @Override
    public DotGrapher workflow(Workflow workflow) {
        this.workflow = workflow;
        return this;
    }

    @Override
    public DotGrapher type(GraphType graphType) {
        this.graphType = graphType;
        return this;
    }
    
    @Override
    public DotGrapher filePath(String dotFilePath) {
        this.filePath = dotFilePath;
        return this;
    }

    @Override
    public Grapher format(GraphFormat format) {
       this.graphFormat = format;
       return this;
    }
    
    @Override
    public Grapher writer(Writer writer) {
        this.writer = writer;
        return this;
    }
    
	public String toString() {
        return graphText;
    }
    
    @Override
    public DotGrapher graph() {
        
        switch(graphType) {
        
            case DATA_FLOW_GRAPH:
                this.graphText = renderWorkflowGraph();
                break;
            
            case DATA_DEPENDENCY_GRAPH:
                break;
            
            case ABSTRACT_PROVENANCE_GRAPH:
                break;
        }
        
        return this;
    }
    
    private String renderWorkflowGraph() {

	    DotBuilder dot = new DotBuilder();
		
		dot.begin();

		// draw a 3D box for each program in the workflow
		dot.shape("box").fillcolor("#CCFFCC");		
		for (Program p : workflow.programs) dot.node(p.beginComment.programName);
	
		// draw a tiny circle for each outward facing @in and @out port
		dot.shape("circle").width(0.1).fillcolor("#FFFFFF");
		for (Port p : workflow.inPorts) dot.node(p.comment.binding(), false);
        for (Port p : workflow.outPorts) dot.node(p.comment.binding(), false);
		
		// create an edge between each program pair with connected out port and in port
		for (Channel c : workflow.channels) {
		    
		    Program sourceProgram = c.sourceProgram;
		    Program sinkProgram = c.sinkProgram;
		    
		    if (sourceProgram == null) {
		        
                dot.edge(c.sinkPort.comment.binding(),
                         c.sinkProgram.beginComment.programName,
                         c.sinkPort.comment.binding());
		        
		    } else if (sinkProgram == null) {
		        
                dot.edge(c.sourceProgram.beginComment.programName,
                         c.sourcePort.comment.binding(),
                         c.sourcePort.comment.binding());
		        
		    } else {
		    
    			dot.edge(c.sourceProgram.beginComment.programName,
    			         c.sinkProgram.beginComment.programName,
    			         c.sourcePort.comment.binding());
		    }
		}
				
		dot.end();
		
		return dot.toString();
	}


    @Override
    public DotGrapher write() throws Exception {
        
        if (writer != null) {

            writer.write(graphText);
            
        } else {
            
            if (filePath == null) {
                throw new Exception("No path for dot file provided.");
            }
        
            writer = new FileWriter(filePath, false);
            writer.write(graphText);
            writer.close();
            writer = null;
        } 

        return this;
    }
}

