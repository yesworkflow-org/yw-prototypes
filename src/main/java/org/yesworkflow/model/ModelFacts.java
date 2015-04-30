package org.yesworkflow.model;

import java.util.HashSet;
import java.util.Set;

import org.yesworkflow.query.FactsBuilder;

public class ModelFacts {

    private final Model model;
    private String factsString = null;
    private Integer nextUriVariableId = 1;
    
    private FactsBuilder programFacts  = new FactsBuilder("program", "program_id", "program_name", "begin_annotation_id", "end_annotation_id");
//    private FactsBuilder qualifiedProgramNameFacts  = new FactsBuilder("qualified_program_name", "program_id", "qualified_program_name");
    private FactsBuilder workflowFacts = new FactsBuilder("workflow", "program_id");
    private FactsBuilder functionFacts = new FactsBuilder("function", "program_id");
    private FactsBuilder subprogramFacts = new FactsBuilder("has_sub_program", "program_id", "subprogram_id");
    private FactsBuilder portFacts = new FactsBuilder("port", "port_id", "port_type", "port_name", "port_annotation_id");
    private FactsBuilder portAliasFacts = new FactsBuilder("port_alias", "port_id", "alias");
    private FactsBuilder portUriFacts = new FactsBuilder("port_uri", "port_id", "uri");
    private FactsBuilder hasInPortFacts = new FactsBuilder("has_in_port", "block_id", "port_id");
    private FactsBuilder hasOutPortFacts = new FactsBuilder("has_out_port", "block_id", "port_id");
    private FactsBuilder channelFacts = new FactsBuilder("channel", "channel_id", "binding");
    private FactsBuilder portConnectionFacts = new FactsBuilder("port_connects_to_channel", "port_id", "channel_id");
    private FactsBuilder portUriVariableFacts = new FactsBuilder("uri_variable", "uri_variable_id", "variable_name", "port_id");

    public ModelFacts(Model model) {
        this.model = model;
    }

    public ModelFacts build() {
        
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

        for (Port port : ports) {

            String variableName = port.flowAnnotation.name;
            String portType = port.flowAnnotation.tag.substring(1);            
            portFacts.add(port.id, portType, variableName, port.flowAnnotation.id);

            String portAlias = port.flowAnnotation.alias();
            if (portAlias != null) {
                portAliasFacts.add(port.id, portAlias);
            }
            
            if (port.uriTemplate != null) {
                portUriFacts.add(port.id, port.uriTemplate.toString());
                Set<String> uniqueVariableNames = new HashSet<String>();
                for (String name : port.uriTemplate.getVariableNames()) {
                    uniqueVariableNames.add(name);
                }
                for (String uriVariableName : uniqueVariableNames) {
                    if (! uriVariableName.trim().isEmpty()) {
                        portUriVariableFacts.add(nextUriVariableId++, uriVariableName, port.id);
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
