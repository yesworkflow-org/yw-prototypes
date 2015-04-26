package org.yesworkflow.model;

import java.util.HashMap;
import java.util.Map;

import org.yesworkflow.annotations.Uri;
import org.yesworkflow.query.FactsBuilder;

public class ModelFacts {

    private static Integer nextPortId = 1;

    private final Model model;
    private Map<String,Integer> idForPort = new HashMap<String,Integer>();
    private String factsString = null;
    
    public ModelFacts(Model model) {
        this.model = model;
    }

    public ModelFacts build() {
        
        FactsBuilders modelFacts = new FactsBuilders();
        buildFactsForCodeBlockAndChildren(modelFacts, modelFacts.programs, model.program, null);
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

        factsString = sb.toString();
        
        return this;
    }
    
    public String toString() {
        return factsString;
    }

    private void buildFactsForCodeBlockAndChildren(FactsBuilders modelFacts, FactsBuilder blockFacts, Program block, Integer parentId) {
        
        String blockName = block.beginAnnotation.name;

        Integer blockId = blockFacts.nextId();
        blockFacts.fact(blockId.toString(), sq(blockName));
        
        if (block.channels.length > 0) {
            modelFacts.workflows.fact(blockId.toString());
        }
        
        if (block instanceof Function) {            
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
}
