package org.yesworkflow.model;

import java.util.HashSet;
import java.util.Set;

import org.yesworkflow.annotations.In;
import org.yesworkflow.data.UriVariable;
import org.yesworkflow.query.FactsBuilder;
import org.yesworkflow.query.LogicLanguage;
import org.yesworkflow.query.LogicLanguageModel;

public class ModelFacts {

    private final Model model;
    private String factsString = null;
    
    
    private FactsBuilder programFacts ;
    private FactsBuilder workflowFacts;
    private FactsBuilder functionFacts;
    private FactsBuilder subprogramFacts;
    private FactsBuilder portFacts;
    private FactsBuilder portAliasFacts;
    private FactsBuilder portUriFacts;
    private FactsBuilder hasInPortFacts;
    private FactsBuilder hasOutPortFacts;
    private FactsBuilder channelFacts;
    private FactsBuilder dataFacts;
    private FactsBuilder portConnectionFacts;
    private FactsBuilder inflowConnectionFacts;
    private FactsBuilder outflowConnectionFacts;
    private FactsBuilder portUriVariableFacts;

    public ModelFacts(LogicLanguage logicLanguage, Model model) {

        if (logicLanguage == null) throw new IllegalArgumentException("Null logicLanguage argument passed to ModelFacts constructor.");
        if (model == null) throw new IllegalArgumentException("Null model argument passed to ModelFacts constructor.");
        if (model.program == null) throw new IllegalArgumentException("Null program field in model argument passed to ModelFacts constructor.");
        
        this.model = model;

        LogicLanguageModel logicLanguageModel = new LogicLanguageModel(logicLanguage);

        this.programFacts  = new FactsBuilder(logicLanguageModel, "program", "program_id", "program_name", "qualified_program_name", "begin_annotation_id", "end_annotation_id");
        this.workflowFacts = new FactsBuilder(logicLanguageModel, "workflow", "program_id");
        this.functionFacts = new FactsBuilder(logicLanguageModel, "function", "program_id");
        this.subprogramFacts = new FactsBuilder(logicLanguageModel, "has_subprogram", "program_id", "subprogram_id");
        this.portFacts = new FactsBuilder(logicLanguageModel, "port", "port_id", "port_type", "port_name", "qualified_port_name", "port_annotation_id", "data_id");
        this.portAliasFacts = new FactsBuilder(logicLanguageModel, "port_alias", "port_id", "alias");
        this.portUriFacts = new FactsBuilder(logicLanguageModel, "port_uri_template", "port_id", "uri");
        this.hasInPortFacts = new FactsBuilder(logicLanguageModel, "has_in_port", "block_id", "port_id");
        this.hasOutPortFacts = new FactsBuilder(logicLanguageModel, "has_out_port", "block_id", "port_id");
        this.channelFacts = new FactsBuilder(logicLanguageModel, "channel", "channel_id", "data_id");
        this.dataFacts = new FactsBuilder(logicLanguageModel, "data", "data_id", "data_name", "qualified_data_name");
        this.portConnectionFacts = new FactsBuilder(logicLanguageModel, "port_connects_to_channel", "port_id", "channel_id");
        this.inflowConnectionFacts = new FactsBuilder(logicLanguageModel, "inflow_connects_to_channel", "port_id", "channel_id");
        this.outflowConnectionFacts = new FactsBuilder(logicLanguageModel, "outflow_connects_to_channel", "port_id", "channel_id");
        this.portUriVariableFacts = new FactsBuilder(logicLanguageModel, "uri_variable", "uri_variable_id", "variable_name", "port_id");
    }

    public ModelFacts build() {
        
        if (model.program == null) throw new NullPointerException("Null program field in ModelFacts.model.");
        if (model.functions == null) throw new NullPointerException("Null functions field in ModelFacts.model.");

        for (Data data : model.data) {
            String qualifiedDataName = qualifiedName("", "[", data.name, "]");
            dataFacts.add(data.id, data.name, qualifiedDataName);
        }

        buildProgramFactsRecursively(model.program, null, null);
        
        for (Function function : model.functions) {
            buildProgramFactsRecursively(function, null, null);
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(programFacts)
          .append(workflowFacts)
          .append(functionFacts)
          .append(subprogramFacts)
          .append(portFacts)
          .append(portAliasFacts)
          .append(portUriFacts)
          .append(hasInPortFacts)
          .append(hasOutPortFacts)
          .append(dataFacts)
          .append(channelFacts)
          .append(portConnectionFacts)
          .append(inflowConnectionFacts)
          .append(outflowConnectionFacts)
          .append(portUriVariableFacts);

        factsString = sb.toString();
        
        return this;
    }

    private void buildProgramFactsRecursively(Program program, String parentName, Integer parentId) {

        if (program == null) throw new IllegalArgumentException("Null program argument.");
        if (program.channels == null) throw new IllegalArgumentException("Null channels field in program argument.");
        if (program.programs == null) throw new IllegalArgumentException("Null programs field in program argument.");
        if (program.functions == null) throw new IllegalArgumentException("Null functions field in program argument.");
        
        String qualifiedProgramName = qualifiedName(parentName, ".", program.beginAnnotation.name, "");
        programFacts.add(program.id, program.beginAnnotation.name, qualifiedProgramName, program.beginAnnotation.id, program.endAnnotation.id);
        
        if (program.channels.length > 0) {
            workflowFacts.add(program.id);
        }
        
        if (program instanceof Function) {            
            functionFacts.add(program.id);
        }
        
        if (parentId != null) {
            subprogramFacts.add(parentId, program.id);
        }
        
        for (Data data : program.data) {
            String qualifiedDataName = qualifiedName(qualifiedProgramName, "[", data.name, "]");
            dataFacts.add(data.id, data.name, qualifiedDataName);
        }
        
        buildPortFacts(program.inPorts, program, program.id, qualifiedProgramName);
        buildPortFacts(program.outPorts, program, program.id, qualifiedProgramName);
        
        for (Channel channel : program.channels) {
            
            if (channel.sourcePort == null) throw new NullPointerException("Null sourcePort field in channel.");
            if (channel.sinkPort == null) throw new NullPointerException("Null sinkPort field in channel.");
            if (channel.sourcePort.flowAnnotation == null) throw new NullPointerException("Null flowAnnotation field in sourcePort.");

            channelFacts.add(channel.id, channel.data.id);
            
            if (channel.sourceProgram == null) {
                inflowConnectionFacts.add(channel.sourcePort.id, channel.id);
            } else {
                portConnectionFacts.add(channel.sourcePort.id, channel.id);
            }
            
            
            if (channel.sinkProgram == null) {
                outflowConnectionFacts.add(channel.sinkPort.id, channel.id);
            } else {
                portConnectionFacts.add(channel.sinkPort.id, channel.id);
            }
        }
        
        for (Program childProgram : program.programs) {
            buildProgramFactsRecursively(childProgram, qualifiedProgramName, program.id);
        }
        
        for (Program childFunction : program.functions) {
            buildProgramFactsRecursively(childFunction, qualifiedProgramName, program.id);
        }
    }

    private String qualifiedName(String parentName, String infix, String name, String suffix) {
        return (parentName == null) ? name : parentName + infix + name + suffix;
    }
    
    private void buildPortFacts(Port[] ports, Program block, Integer blockId, String programName) {
        
        if (ports == null) throw new IllegalArgumentException("Null ports argument.");
        if (block == null) throw new IllegalArgumentException("Null block argument.");
        if (blockId == null) throw new IllegalArgumentException("Null blockId argument.");

        for (Port port : ports) {

            if (port.flowAnnotation == null) throw new NullPointerException("Null flowAnnotation field in port.");
            if (port.flowAnnotation.keyword == null) throw new NullPointerException("Null tag field in port.flowAnnotation.");
            
            String portName = port.flowAnnotation.name;
            String portType = port.flowAnnotation.keyword.substring(1);
            String infix = (port.flowAnnotation instanceof In) ? "<-" : "->";
            String qualifiedPortName = qualifiedName(programName, infix, portName, "");
            portFacts.add(port.id, portType, portName, qualifiedPortName, port.flowAnnotation.id, port.data.id);

            String portAlias = port.flowAnnotation.alias();
            if (portAlias != null) {
                portAliasFacts.add(port.id, portAlias);
            }
            
            if (port.uriTemplate != null) {
                portUriFacts.add(port.id, port.uriTemplate.toString());
                Set<String> uniqueVariableNames = new HashSet<String>();
                for (UriVariable variable : port.uriTemplate.variables) {
                    if (! variable.name.trim().isEmpty()) {
                        if (!uniqueVariableNames.contains(variable.name)) {
                            uniqueVariableNames.add(variable.name);
                            portUriVariableFacts.add(variable.id, variable.name, port.id);
                        }
                    }
                }
            }
            
            if (portType.equals("in") || portType.equals("param")) {
                hasInPortFacts.add(blockId, port.id);
            } else {
                hasOutPortFacts.add(blockId, port.id);
            }
        }
    }

    public String toString() {
        return factsString;
    }
}
