package org.yesworkflow.graph;

import java.util.LinkedList;
import java.util.List;

import org.yesworkflow.model.Channel;
import org.yesworkflow.model.Port;
import org.yesworkflow.model.Program;
import org.yesworkflow.model.Workflow;

public class DotGrapher implements Grapher  {

	final static String EOL = System.getProperty("line.separator");
	
    private Workflow workflow = null;
    private GraphView graphView = null;
    private String graphText = null;

    @SuppressWarnings("unused")
    private GraphFormat graphFormat = null;
    
    @Override
    public DotGrapher workflow(Workflow workflow) {
        this.workflow = workflow;
        return this;
    }

    @Override
    public DotGrapher view(GraphView graphView) {
        this.graphView = graphView;
        return this;
    }
    
    @Override
    public Grapher format(GraphFormat format) {
       this.graphFormat = format;
       return this;
    }
    
	public String toString() {
        return graphText;
    }
    
    @Override
    public DotGrapher graph() {
        
        switch(graphView) {
        
            case PROCESS_CENTRIC_VIEW:
                this.graphText = renderProcessCentricView();
                break;
            
            case DATA_CENTRIC_VIEW:
                this.graphText = renderDataCentricView();
                break;
            
            case COMBINED_VIEW:
                this.graphText = renderCombinedView();
                break;
        }
        
        return this;
    }
    
    private String renderProcessCentricView() {

	    DotBuilder dot = new DotBuilder();
		
		dot.begin();

		// draw a box for each program in the workflow
		dot.shape("box").fillcolor("#CCFFCC");		
		for (Program p : workflow.programs) dot.node(p.beginComment.programName);
	
		// draw a small circle for each outward facing in and out port
		dot.shape("circle").width(0.1).fillcolor("#FFFFFF");
		
		for (Port p : workflow.inPorts) {
		    String binding = p.portComment.binding(); 
		    if (workflowHasChannelForBinding(binding)) {
		        dot.node(binding, "");
		    }
		}
		
        for (Port p : workflow.outPorts) {
            String binding = p.portComment.binding(); 
            if (workflowHasChannelForBinding(binding)) {
                dot.node(binding, "");
            }
        }
		
		for (Channel c : workflow.channels) {
		    
		    Program sourceProgram = c.sourceProgram;
		    Program sinkProgram = c.sinkProgram;
		    
		    // draw edges for channels between workflow in ports and programs in workflow
		    if (sourceProgram == null) {
		        
                dot.edge(c.sinkPort.portComment.binding(),
                         c.sinkProgram.beginComment.programName,
                         c.sinkPort.portComment.binding());
		        
            // draw edges for channels between programs in workflow and workflow out ports
		    } else if (sinkProgram == null) {
		        
                dot.edge(c.sourceProgram.beginComment.programName,
                         c.sourcePort.portComment.binding(),
                         c.sourcePort.portComment.binding());
		        
            // draw edges for channels between programs within workflow
		    } else {
		    
    			dot.edge(c.sourceProgram.beginComment.programName,
    			         c.sinkProgram.beginComment.programName,
    			         c.sourcePort.portComment.binding());
		    }
		}

		dot.end();
		
		return dot.toString();
	}
    
    private boolean workflowHasChannelForBinding(String binding) {
        for (Channel c : workflow.channels) {
            if (binding.equals(c.sourcePort.portComment.binding())) {
                return true;
            }
        }
        return false;
    }
    
    private String renderDataCentricView() {

        DotBuilder dot = new DotBuilder();
        
        dot.begin();

        // draw a box for each channel in the workflow
        dot.shape("box").fillcolor("#FFFFCC").style("rounded,filled");

        List<String> channelBindings = new LinkedList<String>();

        for (Channel c : workflow.channels) {
            String binding = c.sourcePort.portComment.binding();
            channelBindings.add(binding);
            dot.node(binding);
        }

        // draw an edge for each pairing of out port and in port for each program
        for (Program p : workflow.programs) {
            for (Port out : p.outPorts) {
                for (Port in : p.inPorts) {
                    
                    if (channelBindings.contains(in.portComment.binding()) && channelBindings.contains(out.portComment.binding())) {
                        dot.edge(
                            in.portComment.binding(), 
                            out.portComment.binding(), 
                            p.beginComment.programName
                        );
                    }
                }
            }
        }

        dot.end();

        return dot.toString();
    }
    
    private String renderCombinedView() {

        DotBuilder dot = new DotBuilder();
        
        dot.begin();

        // draw a box for each program in the workflow
        dot.shape("box").fillcolor("#CCFFCC");
        for (Program p : workflow.programs) dot.node(p.beginComment.programName);

        
        List<String> channelBindings = new LinkedList<String>();
        
        // draw a box for each channel in the workflow
        dot.shape("box").fillcolor("#FFFFCC").style("rounded,filled");
        for (Channel c : workflow.channels) {
            String binding = c.sourcePort.portComment.binding(); 
            channelBindings.add(binding);
            dot.node(binding);
        }

        // draw an edge for each pairing of out port and in port for each program
        for (Program p : workflow.programs) {

            for (Port out : p.outPorts) {
                
                String binding = out.portComment.binding();
                if (channelBindings.contains(binding)) {
                    dot.edge(
                        p.beginComment.programName,
                        binding
                    );
                }
            }

            for (Port in : p.inPorts) {
                String binding = in.portComment.binding();
                if (channelBindings.contains(binding)) {                
                    dot.edge(
                        binding,
                        p.beginComment.programName
                    );
                }
            }
        }

        dot.end();
        
        return dot.toString();
    }
}

