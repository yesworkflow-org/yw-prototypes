package org.yesworkflow.graph;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import org.yesworkflow.model.Channel;
import org.yesworkflow.model.Port;
import org.yesworkflow.model.Program;
import org.yesworkflow.model.Workflow;

public class DotGrapher implements Grapher  {

    private Workflow workflow = null;
    private GraphView graphView = null;
    private String graphText = null;
    private boolean commentsEnabled = true;

    @SuppressWarnings("unused")
    private GraphFormat graphFormat = null;
    
    @SuppressWarnings("unused")
    private PrintStream stdoutStream = null;
    
    @SuppressWarnings("unused")
    private PrintStream stderrStream = null;
    
    public DotGrapher(PrintStream stdoutStream, PrintStream stderrStream) {
        this.stdoutStream = stdoutStream;
        this.stderrStream = stderrStream;
    }
    
    @Override
    public DotGrapher enableComments(boolean state) {
        commentsEnabled = state;
        return this;
    }
    
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
		
		dot.beginGraph()
		   .enableComments(commentsEnabled);
		
        dot.comment("Use serif font for process labels and sans serif font for data labels");
		dot.graphFont("Courier")
           .edgeFont("Helvetica")
           .nodeFont("Courier");
		
		renderProgramAsProcess(this.workflow, dot, 1);
		
		dot.endGraph();
		
		return dot.toString();
	}
    
    private void renderProgramAsProcess(Workflow workflow, DotBuilder dot, int depth) {

        // draw a small circle for each outward facing in and out port
        dot.comment("Set node style for input and output ports");
        dot.shape("circle").peripheries(1).width(0.1).fillcolor("#FFFFFF");
        dot.flushNodeStyle();       
        
        dot.comment("Nodes representing workflow input ports");
        for (Port p : workflow.inPorts) {
            String binding = p.flowAnnotation.binding(); 
            if (workflowHasChannelForBinding(binding)) {
                dot.node(binding, null);
            }
        }
        
        dot.comment("Nodes representing workflow output ports");
        for (Port p : workflow.outPorts) {
            String binding = p.flowAnnotation.binding(); 
            if (workflowHasChannelForBinding(binding)) {
                dot.node(binding, null);
            }
        }
        
        dot.comment("Start of cluster for drawing box around programs in workflow");
        dot.beginSubgraph(workflow.toString());
    
        dot.comment("Set node style for programs in workflow");
        dot.shape("box3d").peripheries(1).fillcolor("#CCFFCC");      
        dot.flushNodeStyle();
        
        dot.comment("Nodes representing programs in workflow");
        for (Program p : workflow.programs) {
            if (! (p instanceof Workflow)) {
                dot.node(p.beginAnnotation.name);
            }
        }

        dot.comment("Set node style for subworkflows in workflow");
        dot.shape("box").peripheries(2).fillcolor("#CCFFCC");      
        dot.flushNodeStyle();

        dot.comment("Nodes representing subworkflows in workflow");
        dot.shape("box").peripheries(depth+1).fillcolor("#CCFFCC");   
        for (Program p : workflow.programs) {
            if (p instanceof Workflow) {
                dot.node(p.beginAnnotation.name);
            }
        }

        dot.comment("End of cluster for drawing box around programs in workflow");
        dot.endSubraph();

        
        dot.comment("Directed edges for each channel in workflow");
        for (Channel c : workflow.channels) {
            
            Program sourceProgram = c.sourceProgram;
            Program sinkProgram = c.sinkProgram;
            
            // draw edges for channels between workflow in ports and programs in workflow
            if (sourceProgram == null) {
                
                dot.edge(c.sinkPort.flowAnnotation.binding(),
                         c.sinkProgram.beginAnnotation.name,
                         c.sinkPort.flowAnnotation.binding());
                
            // draw edges for channels between programs in workflow and workflow out ports
            } else if (sinkProgram == null) {
                
                dot.edge(c.sourceProgram.beginAnnotation.name,
                         c.sourcePort.flowAnnotation.binding(),
                         c.sourcePort.flowAnnotation.binding());
                
            // draw edges for channels between programs within workflow
            } else {
            
                dot.edge(c.sourceProgram.beginAnnotation.name,
                         c.sinkProgram.beginAnnotation.name,
                         c.sourcePort.flowAnnotation.binding());
            }
        }
        

        // render subworkflows
        for (Program p : workflow.programs) {
            if (p instanceof Workflow) {
                renderProgramAsProcess((Workflow)p, dot, depth + 1);
            }
        }
    }
    
    
    
    private boolean workflowHasChannelForBinding(String binding) {
        for (Channel c : workflow.channels) {
            if (binding.equals(c.sourcePort.flowAnnotation.binding())) {
                return true;
            }
        }
        return false;
    }
    
    private String renderDataCentricView() {

        DotBuilder dot = new DotBuilder();
        
        dot.beginGraph()
           .enableComments(commentsEnabled);

        dot.comment("Use serif font for process labels and sans serif font for data labels");
        dot.graphFont("Courier")
           .edgeFont("Courier")
           .nodeFont("Helvetica");
        
        // draw a box for each channel in the workflow
        dot.shape("box").fillcolor("#FFFFCC").style("rounded,filled");

        List<String> channelBindings = new LinkedList<String>();

        for (Channel c : workflow.channels) {
            String binding = c.sourcePort.flowAnnotation.binding();
            channelBindings.add(binding);
            dot.node(binding);
        }

        // draw an edge for each pairing of out port and in port for each program
        for (Program p : workflow.programs) {
            for (Port out : p.outPorts) {
                for (Port in : p.inPorts) {
                    
                    if (channelBindings.contains(in.flowAnnotation.binding()) && channelBindings.contains(out.flowAnnotation.binding())) {
                        dot.edge(
                            in.flowAnnotation.binding(), 
                            out.flowAnnotation.binding(), 
                            p.beginAnnotation.name
                        );
                    }
                }
            }
        }

        dot.endGraph();

        return dot.toString();
    }
    
    private String renderCombinedView() {

        DotBuilder dot = new DotBuilder();
        
        dot.beginGraph()
           .enableComments(commentsEnabled);

        dot.comment("Use serif font for process labels");
        dot.graphFont("Courier")
           .nodeFont("Courier");
        
        // draw a box for each program in the workflow
        dot.shape("box3d").fillcolor("#CCFFCC");
        for (Program p : workflow.programs) dot.node(p.beginAnnotation.name);

        
        List<String> channelBindings = new LinkedList<String>();
        
        dot.comment("Use sans serif font for data labels");
        dot.nodeFont("Helvetica");

        // draw a box for each channel in the workflow
        dot.shape("box").fillcolor("#FFFFCC").style("rounded,filled");
        for (Channel c : workflow.channels) {
            String binding = c.sourcePort.flowAnnotation.binding(); 
            channelBindings.add(binding);
            dot.node(binding);
        }

        // draw an edge for each pairing of out port and in port for each program
        for (Program p : workflow.programs) {

            for (Port out : p.outPorts) {
                
                String binding = out.flowAnnotation.binding();
                if (channelBindings.contains(binding)) {
                    dot.edge(
                        p.beginAnnotation.name,
                        binding
                    );
                }
            }

            for (Port in : p.inPorts) {
                String binding = in.flowAnnotation.binding();
                if (channelBindings.contains(binding)) {                
                    dot.edge(
                        binding,
                        p.beginAnnotation.name
                    );
                }
            }
        }

        dot.endGraph();
        
        return dot.toString();
    }
}

