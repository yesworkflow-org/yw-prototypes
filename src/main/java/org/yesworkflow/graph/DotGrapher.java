package org.yesworkflow.graph;

import java.io.FileWriter;
import java.io.Writer;

import org.yesworkflow.model.Channel;
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
    
    @Override
	public String getGraph() throws Exception {
        return graphText;
    }
    
    @Override
    public DotGrapher graph() {
        
        switch(graphType) {
        
            case DATA_FLOW_GRAPH:
                this.graphText = createDataflowGraph();
                break;
            
            case DATA_DEPENDENCY_GRAPH:
                break;
            
            case ABSTRACT_PROVENANCE_GRAPH:
                break;
        }
        
        return this;
    }
    
    private String createDataflowGraph() {

	    DotBuilder dot = new DotBuilder();
		
		dot.begin();

		// draw an ellipse for each program
		for (Program p : workflow.programs) {
			dot.node(p.beginComment.programName, "box", 1);
		}

		// create an edge between each program pair with connected out port and in port
		for (Channel c : workflow.channels) {
		    
			dot.edge(c.sourceProgram.beginComment.programName,
			         c.sinkProgram.beginComment.programName,
			         c.sourcePort.comment.binding());
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

