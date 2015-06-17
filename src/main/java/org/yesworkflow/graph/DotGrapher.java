package org.yesworkflow.graph;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yesworkflow.config.YWConfiguration;
import org.yesworkflow.data.UriTemplate;
import org.yesworkflow.exceptions.YWToolUsageException;
import org.yesworkflow.model.Channel;
import org.yesworkflow.model.Model;
import org.yesworkflow.model.Port;
import org.yesworkflow.model.Program;

public class DotGrapher implements Grapher  {

    public static GraphView DEFAULT_GRAPH_VIEW = GraphView.PROCESS_CENTRIC_VIEW;
    public static CommentVisibility DEFAULT_COMMENT_VISIBILITY = CommentVisibility.OFF;
    public static ElementStyleView DEFAULT_ELEMENT_STYLE_VIEW = ElementStyleView.ON;
    public static LayoutClusterView DEFAULT_LAYOUT_CLUSTER_VIEW = LayoutClusterView.ON;
    public static ParamVisibility DEFAULT_PARAM_VISIBILITY = ParamVisibility.REDUCE;
    public static LayoutDirection DEFAULT_LAYOUT_DIRECTION = LayoutDirection.LR;
    public static WorkflowBoxMode DEFAULT_WORKFLOW_BOX_MODE = WorkflowBoxMode.SHOW;
    public static PortLayout DEFAULT_PORT_LAYOUT = PortLayout.GROUP;
    public static DataLabelMode DEFAULT_URI_DISPLAY_MODE = DataLabelMode.BOTH;
    public static EdgeLabelMode DEFAULT_EDGE_LABEL_MODE = EdgeLabelMode.SHOW;
    public static TitlePosition DEFAULT_TITLE_POSITION = TitlePosition.TOP;
    public static String  DEFAULT_PROGRAM_SHAPE = "box";
    public static String  DEFAULT_PROGRAM_STYLE = "filled";
    public static String  DEFAULT_PROGRAM_FILL_COLOR = "#CCFFCC";
    public static String  DEFAULT_PROGRAM_FONT = "Courier";
    public static Integer DEFAULT_PROGRAM_PERIPHERIES = 1;
    public static String  DEFAULT_SUBWORKFLOW_SHAPE = "box";
    public static String  DEFAULT_SUBWORKFLOW_STYLE = "filled";
    public static String  DEFAULT_SUBWORKFLOW_FILL_COLOR = "#CCFFCC";
    public static Integer DEFAULT_SUBWORKFLOW_PERIPHERIES = 2;
    public static String  DEFAULT_DATA_SHAPE = "box";
    public static String  DEFAULT_DATA_STYLE = "rounded,filled";
    public static String  DEFAULT_DATA_FONT = "Helvetica";
    public static String  DEFAULT_DATA_FILL_COLOR = "#FFFFCC";
    public static Integer DEFAULT_DATA_PERIPHERIES = 1;
    public static String  DEFAULT_REDUCED_PARAM_FILL_COLOR = "#FCFCFC";
    public static String  DEFAULT_PORT_SHAPE = "circle";
    public static Double  DEFAULT_PORT_SIZE = 0.2;
    public static String  DEFAULT_PORT_FILL_COLOR = "#FFFFFF";
    public static Integer DEFAULT_PORT_PERIPHERIES = 1;
    
    private Program topWorkflow = null;
    private Program workflow = null;
    private GraphView graphView = DEFAULT_GRAPH_VIEW;    
    private CommentVisibility commentView = DEFAULT_COMMENT_VISIBILITY;
    private ElementStyleView elementStyleView = DEFAULT_ELEMENT_STYLE_VIEW;
    private LayoutClusterView layoutClusterView = DEFAULT_LAYOUT_CLUSTER_VIEW;
    private ParamVisibility paramVisibility = DEFAULT_PARAM_VISIBILITY;
    private LayoutDirection layoutDirection = DEFAULT_LAYOUT_DIRECTION;
    private WorkflowBoxMode workflowBoxMode = DEFAULT_WORKFLOW_BOX_MODE;
    private PortLayout portLayout = DEFAULT_PORT_LAYOUT;
    private DataLabelMode uriDisplayMode = DEFAULT_URI_DISPLAY_MODE;
    private EdgeLabelMode edgeLabelMode = DEFAULT_EDGE_LABEL_MODE;
    private TitlePosition titlePosition = DEFAULT_TITLE_POSITION;
    private String programFont = DEFAULT_PROGRAM_FONT;
    private String dataFont = DEFAULT_DATA_FONT;
    private String programShape = DEFAULT_PROGRAM_SHAPE;
    private String programStyle = DEFAULT_PROGRAM_STYLE;
    private String programFillColor = DEFAULT_PROGRAM_FILL_COLOR;
    private Integer programPeripheries = DEFAULT_PROGRAM_PERIPHERIES;
    private String subworkflowShape = DEFAULT_SUBWORKFLOW_SHAPE;
    private String subworkflowStyle = DEFAULT_SUBWORKFLOW_STYLE;
    private String subworkflowFillColor = DEFAULT_SUBWORKFLOW_FILL_COLOR;
    private Integer subworkflowPeripheries = DEFAULT_SUBWORKFLOW_PERIPHERIES;
    private String dataShape = DEFAULT_DATA_SHAPE;
    private String dataStyle = DEFAULT_DATA_STYLE;
    private String portShape = DEFAULT_PORT_SHAPE;
    private Double portSize = DEFAULT_PORT_SIZE;
    private Integer portPeripheries = DEFAULT_PORT_PERIPHERIES;
    private String dataFillColor = DEFAULT_DATA_FILL_COLOR;
    private Integer dataPeripheries = DEFAULT_DATA_PERIPHERIES;
    private String portFillColor = DEFAULT_PORT_FILL_COLOR;
    private String reducedParamFillColor = DEFAULT_REDUCED_PARAM_FILL_COLOR;
    
    private String title = null;
    private String subworkflowName = null;
    private String graphText = null;
    private String outputDotFile = null;
    private PrintStream stdoutStream = null;
    private List<String> channelBindings = new LinkedList<String>();        
    private DotBuilder dot = null;
    
    @SuppressWarnings("unused")
    private PrintStream stderrStream = null;
    
    public DotGrapher(PrintStream stdoutStream, PrintStream stderrStream) {
        this.stdoutStream = stdoutStream;
        this.stderrStream = stderrStream;
    }

    @Override
    public DotGrapher model(Model model) {        
        if (model == null) throw new IllegalArgumentException("Null model passed to DotGrapher.");
        if (model.program == null) throw new IllegalArgumentException("Model with null program passed to DotGrapher.");
        this.topWorkflow = model.program;
        return this;
    }

    @Override
    public DotGrapher workflow(Program workflow) {
        if (workflow == null) throw new IllegalArgumentException("Null workflow passed to DotGrapher.");
        this.topWorkflow = workflow;
        return this;
    }
    
    @Override
    public DotGrapher configure(Map<String,Object> config) throws Exception {
        if (config != null) {
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                configure(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }
    
    public DotGrapher configure(String key, Object value) throws Exception {
        if (key.equalsIgnoreCase("view")) { 
            graphView = GraphView.toGraphView(value);
        } else if (key.equalsIgnoreCase("dotcomments")) {
            commentView = CommentVisibility.toCommentVisibility(value);
        } else if (key.equalsIgnoreCase("elementstyles")) {
            elementStyleView = ElementStyleView.toElementStyleView(value);
        } else if (key.equalsIgnoreCase("layoutclusters")) {
            layoutClusterView = LayoutClusterView.toLayoutClusterView(value);
        } else if (key.equalsIgnoreCase("params")) {
            paramVisibility = ParamVisibility.toParamVisibility(value);
        } else if (key.equalsIgnoreCase("layout")) {
            layoutDirection = LayoutDirection.toLayoutDirection(value);
        } else if (key.equalsIgnoreCase("workflowbox")) {
            workflowBoxMode = WorkflowBoxMode.toWorkflowBoxMode(value);
        } else if (key.equalsIgnoreCase("portlayout")) {
            portLayout = PortLayout.toPortLayout(value);
        } else if (key.equalsIgnoreCase("datalabel")) {
            uriDisplayMode = DataLabelMode.toUriDisplayMode(value);
        } else if (key.equalsIgnoreCase("edgelabels")) {
            edgeLabelMode = EdgeLabelMode.toEdgeLabelMode(value);
        } else if (key.equalsIgnoreCase("subworkflow")) {
            subworkflowName = (String)value;
        } else if (key.equalsIgnoreCase("programshape")) {
            programShape = (String)value;
        } else if (key.equalsIgnoreCase("datashape")) {
            dataShape = (String)value;
        } else if (key.equalsIgnoreCase("datastyle")) {
            dataStyle = (String)value;
        } else if (key.equalsIgnoreCase("subworkflowshape")) {
            subworkflowShape = (String)value;
        } else if (key.equalsIgnoreCase("title")) {
            title = (String)value;
        } else if (key.equalsIgnoreCase("titleposition")) {
            titlePosition = TitlePosition.toTitlePosition(value);
        } else if (key.equalsIgnoreCase("dotfile")) {
            outputDotFile = (String)value;
        }
        
        return this;
    }
    
	public String toString() {
        return graphText;
    }
    
    @Override
    public DotGrapher graph() throws Exception {
        
        if (topWorkflow == null) throw new YWToolUsageException("Workflow not identified to DotGrapher.");
        workflow = (subworkflowName == null) ? topWorkflow : topWorkflow.getSubprogram(subworkflowName);
        if (workflow == null) throw new YWToolUsageException("Subworkflow named " + subworkflowName + " not found.");
        
        switch(graphView) {        
            case PROCESS_CENTRIC_VIEW: renderProcessView();  break;
            case DATA_CENTRIC_VIEW:    renderDataView();     break;
            case COMBINED_VIEW:        renderCombinedView(); break;
        }

        writeTextToFileOrStdout(outputDotFile, this.graphText);
        return this;
    }
    
    private void renderProcessView() {
        beginDotRendering();
        drawWorkflowTitle();
        beginWorkflowBox();
        drawAtomicProgramsAsNodes();
        drawCompositeProgramsAsNodes();
        drawChannelEdgesBetweenPrograms();
        endWorkflowBox();
        drawWorkflowInputPortsAsNodes();
        drawWorkflowOutputPortsAsNodes();
        drawChannelEdgesBetweenProgramsAndInputPorts();
        drawChannelEdgesBetweenProgramsAndOutputPorts();
        endDotRendering();
    }

    private void renderDataView() {
        beginDotRendering();
        drawWorkflowTitle();
        beginWorkflowBox();
        drawChannelsAsNodes();
        drawProgramEdgesBetweenChannels();
        endWorkflowBox();
        drawWorkflowInputPortsAsNodes();
        drawWorkflowOutputPortsAsNodes();
        drawUnlabeledEdgesBetweenChannelsAndInputPorts();
        drawUnlabeledEdgesBetweenChannelsAndOutputPorts();
        endDotRendering();
    }

    private void renderCombinedView() {
        beginDotRendering();
        drawWorkflowTitle();
        beginWorkflowBox();
        drawAtomicProgramsAsNodes();
        drawCompositeProgramsAsNodes();
        drawChannelsAsNodes();
        drawUnlabeledEdgesBetweenProgramsAndChannels();
        endWorkflowBox();
        drawWorkflowInputPortsAsNodes();
        drawWorkflowOutputPortsAsNodes();
        drawUnlabeledEdgesBetweenChannelsAndInputPorts();
        drawUnlabeledEdgesBetweenChannelsAndOutputPorts();
        endDotRendering();
    }
    
    private void writeTextToFileOrStdout(String path, String text) throws IOException {        
        PrintStream stream = (path == null || path.equals(YWConfiguration.EMPTY_VALUE) || path.equals("-")) ?
                             this.stdoutStream : new PrintStream(path);
        stream.print(text);
        if (stream != this.stdoutStream) {
            stream.close();
        }
    }
    
    private void beginDotRendering() {
        dot = new DotBuilder()
                .enableComments(commentView == CommentVisibility.ON)
                .comment("Start of top-level graph")
                .beginGraph()
                .rankDir(layoutDirection.toString());
    }
    
    private void endDotRendering() {
        this.graphText = dot.comment("End of top-level graph")
                            .endGraph()
                            .toString();
    }
    
    private void drawWorkflowTitle() {
        if (titlePosition == TitlePosition.HIDE) return;
        dot.comment("Title for graph")
           .title( (title == null) ? workflow.toString() : title,
                   programFont, 
                   (titlePosition == TitlePosition.TOP) ? "t" : "b");
    }
    
    private void beginWorkflowBox() {
        if (layoutClusterView == LayoutClusterView.ON) {
            dot.comment("Start of double cluster for drawing box around nodes in workflow")
               .beginSubgraph("workflow_box", workflowBoxMode == WorkflowBoxMode.SHOW);
        }
    }
    
    private void endWorkflowBox() {
        if (layoutClusterView == LayoutClusterView.ON) {
            dot.comment("End of double cluster for drawing box around nodes in workflow")
               .endSubgraph();
        }
    }
    
    private void drawAtomicProgramsAsNodes() {

        if (elementStyleView == ElementStyleView.ON) {
            dot.comment("Style for nodes representing atomic programs in workflow")
               .nodeFont(programFont)
               .nodeShape(programShape)
               .nodeStyle(programStyle)
               .nodeFillcolor(programFillColor)
               .nodePeripheries(programPeripheries)
               .flushNodeStyle();
        }
        
        dot.comment("Nodes representing atomic programs in workflow");
        for (Program p : workflow.programs) {
            if (! (p.isWorkflow())) {
                dot.node(p.beginAnnotation.name);
                if (paramVisibility != ParamVisibility.HIDE) {
                    channelBindings.addAll(p.outerBindings());
                } else {
                    channelBindings.addAll(p.outerDataBindings());
                }
            }
        }
    }

    private void drawCompositeProgramsAsNodes() {

        if (workflow.subworkflowCount() == 0) return;
        
        if (elementStyleView == ElementStyleView.ON) {
            dot.comment("Style for nodes representing composite programs (sub-workflows) in workflow")
               .nodeShape(subworkflowShape)
               .nodeStyle(subworkflowStyle)
               .nodeFillcolor(subworkflowFillColor)
               .nodePeripheries(subworkflowPeripheries)
               .flushNodeStyle();
        }
        
        dot.comment("Nodes representing composite programs (sub-workflows) in workflow");
        for (Program p : workflow.programs) {
            if (p.isWorkflow()) {
                dot.node(p.beginAnnotation.name);
                if (paramVisibility == ParamVisibility.SHOW) {
                    channelBindings.addAll(p.outerBindings());
                } else {
                    channelBindings.addAll(p.outerDataBindings());
                }
            }
        }
    }
    
    private void drawChannelsAsNodes() {

        if (elementStyleView == ElementStyleView.ON) {
            dot.nodeFont(dataFont)
               .nodeShape(dataShape)
               .nodeStyle(dataStyle)
               .nodeFillcolor(dataFillColor)
               .nodePeripheries(dataPeripheries);
        }
        
        switch(paramVisibility) {

            case SHOW:
                
                if (elementStyleView == ElementStyleView.ON) {
                    dot.comment("Style for nodes representing parameter and non-parameter data channels in workflow")
                       .flushNodeStyle();
                }
                
                dot.comment("Nodes representing parameter and non-parameter data channels in workflow");
                for (Channel c : workflow.innerChannels()) drawChannelNode(c);
                
                break;
                
            case REDUCE:
                
                if (elementStyleView == ElementStyleView.ON) {
                    dot.comment("Style for nodes representing non-parameter data channels in workflow")
                       .flushNodeStyle();
                }
                
                dot.comment("Nodes for non-parameter data channels in workflow");
                for (Channel c : workflow.innerDataChannels()) drawChannelNode(c);
                
                if (elementStyleView == ElementStyleView.ON) {
                    dot.comment("Style for nodes representing parameter channels in workflow")
                       .nodeFillcolor(reducedParamFillColor)
                       .flushNodeStyle();
                }
                
                dot.comment("Nodes representing parameter channels in workflow");
                for (Channel c : workflow.innerParamChannels()) drawChannelNode(c);

                break;
                
            case HIDE:
                
                if (elementStyleView == ElementStyleView.ON) {
                    dot.comment("Style for nodes representing non-parameter data channels in workflow")
                       .flushNodeStyle();
                }
                
                dot.comment("Nodes representing non-parameter data channels in workflow");
                for (Channel c : workflow.innerDataChannels()) drawChannelNode(c);
                
                break;
        }
    }
    
    private void drawChannelNode(Channel c) {
        
        String binding = c.sourcePort.flowAnnotation.binding();
        channelBindings.add(binding);
        UriTemplate uri = c.sourcePort.uriTemplate;
        
        if (uri == null) {
            dot.node(binding);
        } else {
            String uriLabel = uri.toString();
            switch(uriDisplayMode) {
                case NAME: dot.node(binding);                          break;
                case URI:  dot.node(binding, uriLabel);                break;
                case BOTH: dot.recordNode(binding, binding, uriLabel); break;
            }
            dot.node(binding);
        }
    }

    private void drawWorkflowInputPortsAsNodes() {
        
        if (portLayout == PortLayout.HIDE) return;
        if (workflow.inPorts.length == 0) return;

        if (portLayout == PortLayout.GROUP && layoutClusterView == LayoutClusterView.ON) {
            dot.comment("Hidden double-cluster for grouping workflow input ports")
               .beginSubgraph("input_ports_group", false);
        }

        if (elementStyleView == ElementStyleView.ON) {
            dot.comment("Style for nodes representing workflow input ports")
               .nodeShape(portShape)
               .nodePeripheries(portPeripheries)
               .nodeWidth(portSize)
               .nodeFillcolor(portFillColor)
               .flushNodeStyle();
        }
        
        dot.comment("Nodes representing workflow input ports");
        for (Port p : workflow.inPorts) {
            String binding = p.flowAnnotation.binding();
            if (channelBindings.contains(binding)) {
                dot.node(binding + "_input_port", "");
            }
        }

        if (portLayout == PortLayout.GROUP && layoutClusterView == LayoutClusterView.ON) {
            dot.comment("End of double-cluster for grouping workflow input ports")
               .endSubgraph();
        }
    }

    private void drawWorkflowOutputPortsAsNodes() {
        
        if (portLayout == PortLayout.HIDE) return;
        if (workflow.outPorts.length == 0) return;

        if (portLayout == PortLayout.GROUP && layoutClusterView == LayoutClusterView.ON) {
            dot.comment("Hidden double-cluster for grouping workflow output ports")
               .beginSubgraph("output_ports_group", false);
        }

        if (elementStyleView == ElementStyleView.ON) {
            dot.comment("Style for nodes representing workflow output ports")
               .nodeShape(portShape)
               .nodePeripheries(portPeripheries)
               .nodeWidth(portSize)
               .nodeFillcolor(portFillColor)
               .flushNodeStyle();
        }
        
        dot.comment("Nodes representing workflow output ports");
        for (Port p : workflow.outPorts) {
            String binding = p.flowAnnotation.binding();
            if (channelBindings.contains(binding)) {
                dot.node(binding + "_output_port", "");
            }
        }
        
        if (portLayout == PortLayout.GROUP && layoutClusterView == LayoutClusterView.ON) {
            dot.comment("End of double-cluster for grouping workflow output ports")
               .endSubgraph();
        }
    }
    
    private String edgeLabel(String label) {
        return (edgeLabelMode == EdgeLabelMode.SHOW) ? label : "";
    }
    
    private void drawUnlabeledEdgesBetweenChannelsAndInputPorts() {
        if (portLayout == PortLayout.HIDE) return;
        if (workflow.inPorts.length == 0) return;
        dot.comment("Edges from input ports to channels");
        for (Port p : workflow.inPorts) {
            String binding = p.flowAnnotation.binding();
            if (channelBindings.contains(binding)) {
                dot.edge(binding + "_input_port", binding);
            }
        }
    }

    private void drawUnlabeledEdgesBetweenChannelsAndOutputPorts() {
        if (portLayout == PortLayout.HIDE) return;
        if (workflow.outPorts.length == 0) return;
        dot.comment("Edges from channels to output ports");
        for (Port p : workflow.outPorts) {
            String binding = p.flowAnnotation.binding();
            if (workflow.hasChannelForBinding(binding)) {
                dot.edge(binding, binding + "_output_port");
            }
        }
    }
    
    private void drawProgramEdgesBetweenChannels() {

        if (elementStyleView == ElementStyleView.ON) {    
            dot.comment("Style for edges representing programs connecting data channels in workflow")
               .edgeFont(programFont)
               .flushEdgeStyle();
        }
        
        dot.comment("Edges representing programs connecting data channels in workflow");
        for (Program p : workflow.programs) {
            for (Port out : p.outPorts) {
                for (Port in : p.inPorts) {
                    if (channelBindings.contains(in.flowAnnotation.binding()) 
                            && channelBindings.contains(out.flowAnnotation.binding())) {
                        dot.edge(
                            in.flowAnnotation.binding(), 
                            out.flowAnnotation.binding(), 
                            edgeLabel(p.beginAnnotation.name)
                        );
                    }
                }
            }
        }
    }
    
    private void drawChannelEdgesBetweenPrograms() {

        if (workflow.channels.length == 0) return;

        if (elementStyleView == ElementStyleView.ON) {
            dot.comment("Style for edges representing channels between programs in workflow")
               .edgeFont(dataFont)
               .flushEdgeStyle();
        }
        
        dot.comment("Edges representing channels between programs in workflow");
        for (Channel c : workflow.channels) {
            
            // skip this channel if it's a parameter and parameters are hidden
            if (c.isParam && paramVisibility == ParamVisibility.HIDE) continue;
            
            // draw edge for the channel if both source and sink are progams (not ports)
            if (c.sourceProgram != null && c.sinkProgram != null) {
                dot.edge(c.sourceProgram.beginAnnotation.name,
                         c.sinkProgram.beginAnnotation.name,
                         edgeLabel(c.sourcePort.flowAnnotation.binding()));
            }
        }
    }
            
    private void drawChannelEdgesBetweenProgramsAndInputPorts() {

        if (portLayout == PortLayout.HIDE) return;
        if (workflow.inPorts.length == 0) return;

        if (elementStyleView == ElementStyleView.ON) {
            dot.comment("Style for edges representing channels between programs and workflow input ports")
               .edgeFont(dataFont)
               .flushEdgeStyle();
        }
        
        dot.comment("Edges representing channels between programs and workflow input ports");
        for (Channel c : workflow.channels) {

            // skip this channel if it's a parameter and parameters are hidden
            if (c.isParam && paramVisibility == ParamVisibility.HIDE) continue;
            
            // draw edge from input port to sink program if source is not a program
            if (c.sourceProgram == null) {
                if (portLayout != PortLayout.HIDE) {
                    dot.edge(c.sinkPort.flowAnnotation.binding() + "_input_port",
                             c.sinkProgram.beginAnnotation.name,
                             edgeLabel(c.sinkPort.flowAnnotation.binding()));
                }
            }             
        }
    }
    
    private void drawChannelEdgesBetweenProgramsAndOutputPorts() {

        if (portLayout == PortLayout.HIDE) return;
        if (workflow.outPorts.length == 0) return;

        if (elementStyleView == ElementStyleView.ON) {
            dot.comment("Style for edges representing channels between programs and workflow output ports")
               .edgeFont(dataFont)
               .flushEdgeStyle();
        }
        
        dot.comment("Edges representing channels between programs and workflow output ports");
        for (Channel c : workflow.channels) {

            // skip this channel if it's a parameter and parameters are hidden
            if (c.isParam && paramVisibility == ParamVisibility.HIDE) continue;

            // draw edge from source program to output port if sink is not a program
            if (c.sinkProgram == null) {
                if (portLayout != PortLayout.HIDE) {
                    dot.edge(c.sourceProgram.beginAnnotation.name,
                         c.sourcePort.flowAnnotation.binding() + "_output_port",
                         edgeLabel(c.sourcePort.flowAnnotation.binding()));
                }
            }
        }
    }
    
    private void drawUnlabeledEdgesBetweenProgramsAndChannels() {
        
        if (workflow.channels.length == 0) return;
        dot.comment("Edges representing connections between programs and channels");
        
        for (Program p : workflow.programs) {

            for (Port out : p.outPorts) {
                String binding = out.flowAnnotation.binding();
                if (channelBindings.contains(binding)) {
                    dot.edge(p.beginAnnotation.name, binding);
                }
            }

            for (Port in : p.inPorts) {
                String binding = in.flowAnnotation.binding();
                if (channelBindings.contains(binding)) {
                    dot.edge(binding, p.beginAnnotation.name);
                }
            }
        }
    }
}