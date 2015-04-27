package org.yesworkflow.model;

import org.yesworkflow.annotations.Uri;
import org.yesworkflow.query.FactsBuilder;

public class ModelFacts {

    private final Model model;
    private String factsString = null;
    
    private FactsBuilder programs  = new FactsBuilder("program", "program_id", "program_name");
    private FactsBuilder workflows = new FactsBuilder("workflow", "program_id");
    private FactsBuilder functions = new FactsBuilder("function", "program_id");
    private FactsBuilder subprograms = new FactsBuilder("has_sub_program", "program_id", "subprogram_id");
    private FactsBuilder ports = new FactsBuilder("port", "port_id", "port_type", "variable_name");
    private FactsBuilder portAliases = new FactsBuilder("port_alias", "port_id", "alias");
    private FactsBuilder portUris = new FactsBuilder("port_uri", "port_id", "uri");
    private FactsBuilder hasInPort = new FactsBuilder("has_in_port", "block_id", "port_id");
    private FactsBuilder hasOutPort = new FactsBuilder("has_out_port", "block_id", "port_id");
    private FactsBuilder channels = new FactsBuilder("channel", "channel_id", "binding");
    private FactsBuilder portConnections = new FactsBuilder("port_connects_to_channel", "port_id", "channel_id");

    public ModelFacts(Model model) {
        this.model = model;
    }

    public ModelFacts build() {
        
        buildFactsForCodeBlockAndChildren(programs, model.program, null);
        for (Function function : model.functions) {
            buildFactsForCodeBlockAndChildren(programs, function, null);            
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(programs)
          .append(workflows)
          .append(functions)
          .append(subprograms)
          .append(ports)
          .append(portAliases)
          .append(portUris)
          .append(hasInPort)
          .append(hasOutPort)
          .append(channels)
          .append(portConnections);

        factsString = sb.toString();
        
        return this;
    }
    
    public String toString() {
        return factsString;
    }

    private void buildFactsForCodeBlockAndChildren(FactsBuilder blockFacts, Program program, Integer parentId) {
        
        blockFacts.fact(program.id.toString(), sq(program.beginAnnotation.name));
        
        if (program.channels.length > 0) {
            workflows.fact(program.id.toString());
        }
        
        if (program instanceof Function) {            
            functions.fact(program.id.toString());
        }
        
        if (parentId != null) {
            subprograms.fact(parentId.toString(), program.id.toString());
        }
        
        buildPortFacts(program.inPorts, program, program.id);
        buildPortFacts(program.outPorts, program, program.id);
        
        for (Channel channel : program.channels) {
            String binding = channel.sourcePort.flowAnnotation.binding();
            channels.fact(channel.id.toString(), sq(binding));
            portConnections.fact(channel.sourcePort.id.toString(), channel.id.toString());
            portConnections.fact(channel.sinkPort.id.toString(), channel.id.toString());
        }
        
        for (Program childProgram : program.programs) {
            buildFactsForCodeBlockAndChildren(programs, childProgram, program.id);
        }
        
        for (Program childFunction : program.functions) {
            buildFactsForCodeBlockAndChildren(programs, childFunction, program.id);
        }
    }

    private void buildPortFacts(Port[] portss, Program block, Integer blockId) {

        for (Port port : portss) {

            String variableName = port.flowAnnotation.name;
            String portType = port.flowAnnotation.tag.substring(1);            
            ports.fact(port.id.toString(), sq(portType), sq(variableName));

            String portAlias = port.flowAnnotation.alias();
            if (portAlias != null) {
                portAliases.fact(port.id.toString(), sq(portAlias));
            }
            
            Uri portUri = port.flowAnnotation.uri();
            if (portUri != null) {
                portUris.fact(port.id.toString(), sq(portUri.toString()));
            }
            
            if (portType.equals("in") || portType.equals("param")) {
                hasInPort.fact(blockId.toString(), port.id.toString());
            } else {
                hasOutPort.fact(blockId.toString(), port.id.toString());
            }
        }
    }

    private String sq(String text) {
        return "'" + text + "'";
    }    
}
