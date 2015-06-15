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
    public static CommentVisibility DEFAULT_COMMENT_VISIBILITY = CommentVisibility.HIDE;
    public static ParamVisibility DEFAULT_PARAM_VISIBILITY = ParamVisibility.HIDE;
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
    private ParamVisibility paramVisibility = DEFAULT_PARAM_VISIBILITY;
    private CommentVisibility commentView = DEFAULT_COMMENT_VISIBILITY;
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
        
        case PROCESS_CENTRIC_VIEW:
            this.graphText = new ProcessRendering().render();
            break;
        
        case DATA_CENTRIC_VIEW:
            this.graphText = new DataRendering().render();
            break;
        
        case COMBINED_VIEW:
            this.graphText = new CombinedRendering().render();
            break;
        }
        
        writeTextToFileOrStdout(outputDotFile, this.graphText);
        return this;
    }
    
    private void writeTextToFileOrStdout(String path, String text) throws IOException {        
        PrintStream stream = (path == null || path.equals(YWConfiguration.EMPTY_VALUE) || path.equals("-")) ?
                             this.stdoutStream : new PrintStream(path);
        stream.print(text);
        if (stream != this.stdoutStream) {
            stream.close();
        }
    }
        
    private abstract class GraphRendering {
        
        private DotBuilder dot = new DotBuilder()
                                .beginGraph()
                                .rankDir(layoutDirection.toString())
                                .enableComments(commentView == CommentVisibility.SHOW);
        
        public abstract String render();

        protected String endDotRendering() {
            return dot.endGraph().toString();
        }
        
        protected void drawWorkflowTitle() {
            
            if (titlePosition == TitlePosition.HIDE) return;

            dot.comment("Title for graph");
            dot.title( (title == null) ? workflow.toString() : title,
                       programFont, 
                       (titlePosition == TitlePosition.TOP) ? "t" : "b");
        }
        
        protected void startWorkflowBox() {
            dot.comment("Start of double cluster for drawing box around nodes in workflow")
               .beginSubgraph(workflowBoxMode == WorkflowBoxMode.HIDE);
        }
        
        protected void endWorkflowBox() {
            dot.comment("End of double cluster for drawing box around nodes in workflow")
               .endSubgraph();
        }

        private String edgeLabel(String label) {
            return (edgeLabelMode == EdgeLabelMode.SHOW) ? label : "";
        }
        
        protected void drawInputAndOutputPorts() {
            
            if (portLayout == PortLayout.HIDE) return;

            dot.comment("Nodes representing workflow ports")
               .nodeShape(portShape)
               .nodePeripheries(portPeripheries)
               .width(portSize)
               .nodeFillcolor(portFillColor)
               .flushNodeStyle();

            if (portLayout == PortLayout.GROUP) dot.beginHiddenSubgraph();
            for (Port p : workflow.inPorts) {
                String binding = p.flowAnnotation.binding();
                if (channelBindings.contains(binding)) {
                    dot.node(binding + "_inport", "");
                }
            }
            if (portLayout == PortLayout.GROUP) dot.endSubgraph();
            
            if (portLayout == PortLayout.GROUP) dot.beginHiddenSubgraph();
            for (Port p : workflow.outPorts) {
                String binding = p.flowAnnotation.binding();
                if (channelBindings.contains(binding)) {
                    dot.node(binding + "_outport", "");
                }
            }
            if (portLayout == PortLayout.GROUP) dot.endSubgraph();
        }        

        protected void drawProgramsAsNodes() {

            dot.comment("Nodes representing programs in workflow")
               .nodeFont(programFont)
               .nodeShape(programShape)
               .nodeStyle(programStyle)
               .nodeFillcolor(programFillColor)
               .nodePeripheries(programPeripheries)
               .flushNodeStyle();
            
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

            dot.comment("Nodes representing subworkflows in workflow")
               .nodeShape(subworkflowShape)
               .nodeStyle(subworkflowStyle)
               .nodeFillcolor(subworkflowFillColor)
               .nodePeripheries(subworkflowPeripheries)
               .flushNodeStyle();

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
        
        protected void drawChannelsAsNodes() {

            dot.comment("Nodes for data channels in workflow")
               .nodeFont(dataFont)
               .nodeShape(dataShape)
               .nodeStyle(dataStyle)
               .nodeFillcolor(dataFillColor)
               .nodePeripheries(dataPeripheries)
               .flushNodeStyle();

            switch(paramVisibility) {

            case SHOW:
                for (Channel c : workflow.innerChannels()) {
                    drawChannelNode(c);
                }        
            case REDUCE:
                for (Channel c : workflow.innerDataChannels()) {
                    drawChannelNode(c);
                }
                dot.nodeFillcolor(reducedParamFillColor);
                for (Channel c : workflow.innerParamChannels()) {
                    drawChannelNode(c);
                }
            case HIDE:
                for (Channel c : workflow.innerDataChannels()) {
                    drawChannelNode(c);
                }
            }
        }
        
        private void drawChannelNode(Channel c) {
            
            String binding = c.sourcePort.flowAnnotation.binding();
            channelBindings.add(binding);
            UriTemplate uri = c.sourcePort.uriTemplate;
            
            if (uri == null) {
                dot.node(binding);
            } else {
                String uriLabel = uri.toString().replace("{", "\\{").replace("}", "\\}");
                switch(uriDisplayMode) {
                    case NAME: 
                        dot.node(binding);
                        break;
                    case URI:
                        dot.node(binding, uriLabel);
                        break;
                    case BOTH:
                        dot.recordNode(binding, binding, uriLabel);
                        break;
                }
                dot.node(binding);
            }
        }
        
        protected void drawUnlabeledEdgesBetweenChannelsAndPorts() {
            
            dot.comment("Edges between channels and ports");

            if (portLayout != PortLayout.HIDE) {
                
                for (Port p : workflow.inPorts) {
                    String binding = p.flowAnnotation.binding();
                    if (channelBindings.contains(binding)) {
                        dot.edge(binding + "_inport", binding);
                    }
                }
                
                for (Port p : workflow.outPorts) {
                    String binding = p.flowAnnotation.binding();
                    if (workflow.hasChannelForBinding(binding)) {
                        dot.edge(binding, binding + "_outport");
                    }
                }
            }
        }
        
        protected void drawProgramEdgesBetweenChannels() {

            dot.comment("Edges between channels in workflow")
               .edgeFont(programFont);
            
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
        
        protected void drawChannelEdgesBetweenPrograms() {
            
            dot.comment("Edges for channels between programs in workflow")
               .edgeFont(dataFont);
            
            for (Channel c : workflow.channels) {
                
                if (c.isParam && paramVisibility == ParamVisibility.HIDE) continue;
                
                Program sourceProgram = c.sourceProgram;
                Program sinkProgram = c.sinkProgram;
                
                if (sourceProgram != null && sinkProgram != null) {
                    dot.edge(c.sourceProgram.beginAnnotation.name,
                             c.sinkProgram.beginAnnotation.name,
                             edgeLabel(c.sourcePort.flowAnnotation.binding()));
                }
            }
        }
                
        protected void drawChannelEdgesBetweenProgramsAndPorts() {
            
            dot.comment("Edges for channels between programs and ports")
               .edgeFont(dataFont);
            
            for (Channel c : workflow.channels) {
                
                if (c.isParam && paramVisibility == ParamVisibility.HIDE) continue;
                
                Program sourceProgram = c.sourceProgram;
                Program sinkProgram = c.sinkProgram;
                
                if (sourceProgram == null) {
                    if (portLayout != PortLayout.HIDE) {
                        dot.edge(c.sinkPort.flowAnnotation.binding() + "_inport",
                                 c.sinkProgram.beginAnnotation.name,
                                 edgeLabel(c.sinkPort.flowAnnotation.binding()));
                    }
                    
                } else if (sinkProgram == null) {
                    if (portLayout != PortLayout.HIDE) {
                        dot.edge(c.sourceProgram.beginAnnotation.name,
                             c.sourcePort.flowAnnotation.binding() + "_outport",
                             edgeLabel(c.sourcePort.flowAnnotation.binding()));
                    }
                }
            }
        }
        
        protected void drawUnlabeledEdgesBetweenProgramsAndChannels() {
            
            dot.comment("Edges for channels between programs and channels");
            
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
    
    private class ProcessRendering extends GraphRendering {

        @Override
        public String render() {
            drawWorkflowTitle();
            startWorkflowBox();
            drawProgramsAsNodes();
            drawChannelEdgesBetweenPrograms();
            endWorkflowBox();
            drawInputAndOutputPorts();
            drawChannelEdgesBetweenProgramsAndPorts();
            return endDotRendering();
        }
    }
    
    private class DataRendering extends GraphRendering {

        @Override
        public String render() {
            drawWorkflowTitle();
            startWorkflowBox();
            drawChannelsAsNodes();
            drawProgramEdgesBetweenChannels();
            endWorkflowBox();
            drawInputAndOutputPorts();
            drawUnlabeledEdgesBetweenChannelsAndPorts();
            return endDotRendering();
        }
    }
    
    private class CombinedRendering extends GraphRendering {
        
        @Override
        public String render() {
            drawWorkflowTitle();
            startWorkflowBox();
            drawProgramsAsNodes();
            drawChannelsAsNodes();
            drawUnlabeledEdgesBetweenProgramsAndChannels();
            endWorkflowBox();
            drawInputAndOutputPorts();
            drawUnlabeledEdgesBetweenChannelsAndPorts();
            return endDotRendering();
        }
    }    
}

