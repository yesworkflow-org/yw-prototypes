package org.yesworkflow.model;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.End;
import org.yesworkflow.annotations.In;
import org.yesworkflow.annotations.Out;
import org.yesworkflow.annotations.Return;
import org.yesworkflow.annotations.Uri;
import org.yesworkflow.config.YWConfiguration;
import org.yesworkflow.exceptions.YWMarkupException;

public class DefaultModeler implements Modeler {

    private List<Annotation> annotations;
    private Model model;
    private String topWorkflowName;
    private PrintStream stdoutStream = null;
    private PrintStream stderrStream = null;
    private String factsFile = null;
    private String facts = null;
    private Map<String,Integer> idForPort = new HashMap<String,Integer>();
    
    public DefaultModeler() {
        this(System.out, System.err);
    }

    public DefaultModeler(PrintStream stdoutStream, PrintStream stderrStream) {
        this.stdoutStream = stdoutStream;
        this.stderrStream = stderrStream;
    }
    
    @Override
    public DefaultModeler configure(Map<String,Object> config) throws Exception {
        if (config != null) {
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                configure(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    @Override
    public DefaultModeler configure(String key, Object value) throws Exception {
        if (key.equalsIgnoreCase("workflow")) {
            topWorkflowName = (String)value;
        } else if (key.equalsIgnoreCase("factsfile")) {
           factsFile = (String)value;
        }
        return this;
    }  
    
    @Override
    public Modeler annotations(List<Annotation> annotations) {
        this.annotations = annotations;
        return this;
    }

    @Override
    public Modeler model() throws Exception {	
    	buildModel();
    	if (factsFile != null) {
    	    facts = getFacts();
    	    writeTextToFileOrStdout(factsFile, facts);
    	}
    	return this;
    }
    
    @Override
    public Model getModel() {
        return this.model;
    }

    @Override
    public Workflow getWorkflow() {
        return this.model.workflow;
    }

    @Override
    public String getFacts() {

        if (facts == null) {
            facts = buildFacts();
        }
        return facts;
    }

    private static class FactsBuilders {
        public FactsBuilder programs  = new FactsBuilder("program", "program_id", "program_name");
        public FactsBuilder workflows = new FactsBuilder("workflow", "program_id");
        public FactsBuilder functions = new FactsBuilder("function", "program_id");
        public FactsBuilder subprograms = new FactsBuilder("has_sub_program", "program_id", "subprogram_id");
        public FactsBuilder ports = new FactsBuilder("port", "port_id", "port_type", "variable_name");
        public FactsBuilder portAliases = new FactsBuilder("port_alias", "port_id", "alias");
        public FactsBuilder portUris = new FactsBuilder("port_uri", "port_id", "uri");
        public FactsBuilder hasInPort = new FactsBuilder("has_in_port", "block_id", "port_id");
        public FactsBuilder hasOutPort = new FactsBuilder("has_out_port", "block_id", "port_id");
        public FactsBuilder channels = new FactsBuilder("channel", "channel_id", "binding");
        public FactsBuilder portConnections = new FactsBuilder("port_connects_to_channel", "port_id", "channel_id");
    }

    private String buildFacts() {
        
        FactsBuilders modelFacts = new FactsBuilders();
        buildFactsForCodeBlockAndChildren(modelFacts, modelFacts.programs, model.workflow, null);
        for (Function function : model.functions) {
            buildFactsForCodeBlockAndChildren(modelFacts, modelFacts.programs, function, null);            
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(modelFacts.programs)
          .append(modelFacts.workflows)
          .append(modelFacts.functions)
          .append(modelFacts.subprograms)
          .append(modelFacts.ports)
          .append(modelFacts.portAliases)
          .append(modelFacts.portUris)
          .append(modelFacts.hasInPort)
          .append(modelFacts.hasOutPort)
          .append(modelFacts.channels)
          .append(modelFacts.portConnections);
        
        return sb.toString();
    }

    private void buildFactsForCodeBlockAndChildren(FactsBuilders modelFacts, FactsBuilder blockFacts, Program block, Integer parentId) {
        
        String blockName = block.beginAnnotation.name;

        Integer blockId = blockFacts.nextId();
        blockFacts.fact(blockId.toString(), sq(blockName));
        
        if (block.type().equals("workflow")) {
            modelFacts.workflows.fact(blockId.toString());
        } else if (block.type().equals("function")) {            
            modelFacts.functions.fact(blockId.toString());
        }
        
        if (parentId != null) {
            modelFacts.subprograms.fact(parentId.toString(), blockId.toString());
        }
        
        buildPortFacts(block.inPorts, modelFacts, block, blockId);
        buildPortFacts(block.outPorts, modelFacts, block, blockId);
        
        for (Channel channel : block.channels) {
            Integer channelId = modelFacts.channels.nextId();
            String binding = channel.sourcePort.flowAnnotation.binding();
            Integer sourcePortId = getIdForPort(channel.sourcePort);
            Integer sinkPortId = getIdForPort(channel.sinkPort);
            modelFacts.channels.fact(channelId.toString(), sq(binding));
            modelFacts.portConnections.fact(sourcePortId.toString(), channelId.toString());
            modelFacts.portConnections.fact(sinkPortId.toString(), channelId.toString());
        }
        
        for (Program childProgram : block.programs) {
            buildFactsForCodeBlockAndChildren(modelFacts, modelFacts.programs, childProgram, blockId);
        }
        
        for (Program childFunction : block.functions) {
            buildFactsForCodeBlockAndChildren(modelFacts, modelFacts.programs, childFunction, blockId);
        }
    }

    private void buildPortFacts(Port[] ports, FactsBuilders facts, Program block, Integer blockId) {

        for (Port port : ports) {

            Integer portId = getIdForPort(port);
            String variableName = port.flowAnnotation.name;
            String portType = port.flowAnnotation.tag.substring(1);            
            facts.ports.fact(portId.toString(), sq(portType), sq(variableName));

            String portAlias = port.flowAnnotation.alias();
            if (portAlias != null) {
                facts.portAliases.fact(portId.toString(), sq(portAlias));
            }
            
            Uri portUri = port.flowAnnotation.uri();
            if (portUri != null) {
                facts.portUris.fact(portId.toString(), sq(portUri.toString()));
            }
            
            if (portType.equals("in") || portType.equals("param")) {
                facts.hasInPort.fact(blockId.toString(), portId.toString());
            } else {
                facts.hasOutPort.fact(blockId.toString(), portId.toString());
            }
        }
    }
    
    private static Integer nextPortId = 1;
    
    private Integer getIdForPort(Port port) {        
        String portName = port.beginAnnotation.name;
        portName += "_" + port.flowAnnotation.tag + "_";
        portName += port.flowAnnotation.binding();
        Integer id = idForPort.get(portName);
        if (id == null) {
            id = nextPortId++;
            idForPort.put(portName, id);
        }
        return id;
    }

    private String sq(String text) {
        return "'" + text + "'";
    }   
    
    private void writeTextToFileOrStdout(String path, String text) throws IOException {  
        PrintStream stream = (path.equals(YWConfiguration.EMPTY_VALUE) || path.equals("-")) ?
                             this.stdoutStream : new PrintStream(path);
        stream.print(text);
        if (stream != this.stdoutStream) {
            stream.close();
        }
    }

    private void buildModel() throws Exception {

        WorkflowBuilder workflowBuilder = null;
        WorkflowBuilder topWorkflowBuilder = null;
        Workflow topWorkflow = null;
        WorkflowBuilder parentBuilder = null;
        Stack<WorkflowBuilder> parentWorkflowBuilders = new Stack<WorkflowBuilder>();
        List<Function> functions = new LinkedList<Function>();

        for (Annotation annotation : annotations) {

            if (annotation instanceof Begin) {

                if (workflowBuilder != null) {
                    parentWorkflowBuilders.push(workflowBuilder);
                    parentBuilder = workflowBuilder;
                }

                workflowBuilder = new WorkflowBuilder(this.stdoutStream, this.stderrStream)
                    .begin((Begin)annotation);
                
                if (topWorkflowBuilder == null) { 
                    String blockName = ((Begin)annotation).name;
                    if (topWorkflowName == null || topWorkflowName.equals(blockName)) {
                        topWorkflowBuilder = workflowBuilder;
                    }
                }

            } else if (annotation instanceof Return) {
                
                workflowBuilder.returnPort((Return)annotation);

            } else if (annotation instanceof Out) {
                
                Port outPort = workflowBuilder.outPort((Out)annotation);
                if (parentBuilder != null) {
                    parentBuilder.nestedOutPort(outPort);
                }

            } else if (annotation instanceof In) {
                
                Port inPort = workflowBuilder.inPort((In)annotation);
                if (parentBuilder != null) {
                    parentBuilder.nestedInPort(inPort);
                }

            } else if (annotation instanceof End) {

                workflowBuilder.end((End)annotation);
                    
                if (parentWorkflowBuilders.isEmpty()) {
                    
                    if (workflowBuilder == topWorkflowBuilder) {
                        if (workflowBuilder.hasReturnPort()) {
                            topWorkflow = workflowBuilder.buildFunction();
                        } else {
                            topWorkflow = workflowBuilder.buildWorkflow();
                        }
                    } else {
                        functions.add(workflowBuilder.buildFunction());
                    }

                    workflowBuilder = null;
                    
                } else {

                    if (workflowBuilder.hasReturnPort()) {
                        Function function = workflowBuilder.buildFunction();
                        parentBuilder.nestedFunction(function);
                    } else if (workflowBuilder.hasNestedPrograms()) {
                        Workflow workflow = workflowBuilder.buildWorkflow();
                        parentBuilder.nestedProgram(workflow);
                  } else {
                        Program program = workflowBuilder.buildProgram();
                        parentBuilder.nestedProgram(program);
                    }
                    
                    workflowBuilder = parentWorkflowBuilders.pop();
                }
                
                if (!parentWorkflowBuilders.isEmpty()) {
                    parentBuilder = parentWorkflowBuilders.peek();
                }
            }
        }
        
        if (workflowBuilder != null) {
            // throw exception if missing any paired end comments
            StringBuilder messageBuilder = new StringBuilder();
            do {
                messageBuilder.append("ERROR: No @end comment paired with '@begin ");
                messageBuilder.append(workflowBuilder.getProgramName());
                messageBuilder.append("'");
                messageBuilder.append(EOL);
                workflowBuilder = parentWorkflowBuilders.isEmpty() ? null : parentWorkflowBuilders.pop();
            } while (workflowBuilder != null);        
            throw new YWMarkupException(messageBuilder.toString());
        }
        
        model = new Model(topWorkflow, functions);
    }
}
