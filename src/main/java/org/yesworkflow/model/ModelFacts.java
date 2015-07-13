package org.yesworkflow.model;

import java.util.HashSet;
import java.util.Set;

import org.yesworkflow.data.UriVariable;
import org.yesworkflow.query.FactsBuilder;
import org.yesworkflow.query.LogicLanguage;
import org.yesworkflow.query.LogicLanguageModel;

public class ModelFacts {

    private final Model model;
    private String factsString = null;
    
    private FactsBuilder programFacts ;
//    private FactsBuilder qualifiedProgramNameFacts;
    private FactsBuilder workflowFacts;
    private FactsBuilder functionFacts;
    private FactsBuilder subprogramFacts;
    private FactsBuilder portFacts;
    private FactsBuilder portAliasFacts;
    private FactsBuilder portUriFacts;
    private FactsBuilder hasInPortFacts;
    private FactsBuilder hasOutPortFacts;
    private FactsBuilder channelFacts;
    private FactsBuilder portConnectionFacts;
    private FactsBuilder portUriVariableFacts;

    public ModelFacts(LogicLanguage logicLanguage, Model model) {

        if (logicLanguage == null) throw new IllegalArgumentException("Null logicLanguage argument passed to ModelFacts constructor.");
        if (model == null) throw new IllegalArgumentException("Null model argument passed to ModelFacts constructor.");
        if (model.program == null) throw new IllegalArgumentException("Null program field in model argument passed to ModelFacts constructor.");
        
        this.model = model;

        LogicLanguageModel logicLanguageModel = new LogicLanguageModel(logicLanguage);

        this.programFacts  = new FactsBuilder(logicLanguageModel, "program", "program_id", "program_name", "begin_annotation_id", "end_annotation_id");
        //this.qualifiedProgramNameFacts  = new FactsBuilder("qualified_program_name", "program_id", "qualified_program_name");
        this.workflowFacts = new FactsBuilder(logicLanguageModel, "workflow", "program_id");
        this.functionFacts = new FactsBuilder(logicLanguageModel, "function", "program_id");
        this.subprogramFacts = new FactsBuilder(logicLanguageModel, "has_sub_program", "program_id", "subprogram_id");
        this.portFacts = new FactsBuilder(logicLanguageModel, "port", "port_id", "port_type", "port_name", "port_annotation_id");
        this.portAliasFacts = new FactsBuilder(logicLanguageModel, "port_alias", "port_id", "alias");
        this.portUriFacts = new FactsBuilder(logicLanguageModel, "port_uri", "port_id", "uri");
        this.hasInPortFacts = new FactsBuilder(logicLanguageModel, "has_in_port", "block_id", "port_id");
        this.hasOutPortFacts = new FactsBuilder(logicLanguageModel, "has_out_port", "block_id", "port_id");
        this.channelFacts = new FactsBuilder(logicLanguageModel, "channel", "channel_id", "binding");
        this.portConnectionFacts = new FactsBuilder(logicLanguageModel, "port_connects_to_channel", "port_id", "channel_id");
        this.portUriVariableFacts = new FactsBuilder(logicLanguageModel, "uri_variable", "uri_variable_id", "variable_name", "port_id");

    }

    public ModelFacts build() {
        
        if (model.program == null) throw new NullPointerException("Null program field in ModelFacts.model.");
        if (model.functions == null) throw new NullPointerException("Null functions field in ModelFacts.model.");

        buildProgramFactsRecursively(model.program, null);
        
        for (Function function : model.functions) {
            buildProgramFactsRecursively(function, null);            
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
          .append(channelFacts)
          .append(portConnectionFacts)
          .append(portUriVariableFacts);

        factsString = sb.toString();
        
        return this;
    }

    private void buildProgramFactsRecursively(Program program, Integer parentId) {
        
        if (program == null) throw new IllegalArgumentException("Null program argument.");
        if (program.channels == null) throw new IllegalArgumentException("Null channels field in program argument.");
        if (program.programs == null) throw new IllegalArgumentException("Null programs field in program argument.");
        if (program.functions == null) throw new IllegalArgumentException("Null functions field in program argument.");
        
        programFacts.add(program.id, program.beginAnnotation.name, program.beginAnnotation.id, program.endAnnotation.id);
        
        if (program.channels.length > 0) {
            workflowFacts.add(program.id);
        }
        
        if (program instanceof Function) {            
            functionFacts.add(program.id);
        }
        
        if (parentId != null) {
            subprogramFacts.add(parentId, program.id);
        }
        
        buildPortFacts(program.inPorts, program, program.id);
        buildPortFacts(program.outPorts, program, program.id);
        
        for (Channel channel : program.channels) {
            
            if (channel.sourcePort == null) throw new NullPointerException("Null sourcePort field in channel.");
            if (channel.sinkPort == null) throw new NullPointerException("Null sinkPort field in channel.");
            if (channel.sourcePort.flowAnnotation == null) throw new NullPointerException("Null flowAnnotation field in sourcePort.");
            
            String binding = channel.sourcePort.flowAnnotation.binding();
            channelFacts.add(channel.id, binding);
            portConnectionFacts.add(channel.sourcePort.id, channel.id);
            portConnectionFacts.add(channel.sinkPort.id, channel.id);
        }
        
        for (Program childProgram : program.programs) {
            buildProgramFactsRecursively(childProgram, program.id);
        }
        
        for (Program childFunction : program.functions) {
            buildProgramFactsRecursively(childFunction, program.id);
        }
    }

    private void buildPortFacts(Port[] ports, Program block, Integer blockId) {
        
        if (ports == null) throw new IllegalArgumentException("Null ports argument.");
        if (block == null) throw new IllegalArgumentException("Null block argument.");
        if (blockId == null) throw new IllegalArgumentException("Null blockId argument.");

        for (Port port : ports) {

            if (port.flowAnnotation == null) throw new NullPointerException("Null flowAnnotation field in port.");
            if (port.flowAnnotation.keyword == null) throw new NullPointerException("Null tag field in port.flowAnnotation.");
            
            String variableName = port.flowAnnotation.name;
            String portType = port.flowAnnotation.keyword.substring(1);            
            portFacts.add(port.id, portType, variableName, port.flowAnnotation.id);

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
