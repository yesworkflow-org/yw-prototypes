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

    private void buildFactsForCodeBlockAndChildren(FactsBuilder blockFacts, Program block, Integer parentId) {
        
        String blockName = block.beginAnnotation.name;

        Integer blockId = blockFacts.nextId();
        blockFacts.fact(blockId.toString(), sq(blockName));
        
        if (block.channels.length > 0) {
            workflows.fact(blockId.toString());
        }
        
        if (block instanceof Function) {            
            functions.fact(blockId.toString());
        }
        
        if (parentId != null) {
            subprograms.fact(parentId.toString(), blockId.toString());
        }
        
        buildPortFacts(block.inPorts, block, blockId);
        buildPortFacts(block.outPorts, block, blockId);
        
        for (Channel channel : block.channels) {
            Integer channelId = channels.nextId();
            String binding = channel.sourcePort.flowAnnotation.binding();
            Integer sourcePortId = getIdForPort(channel.sourcePort);
            Integer sinkPortId = getIdForPort(channel.sinkPort);
            channels.fact(channelId.toString(), sq(binding));
            portConnections.fact(sourcePortId.toString(), channelId.toString());
            portConnections.fact(sinkPortId.toString(), channelId.toString());
        }
        
        for (Program childProgram : block.programs) {
            buildFactsForCodeBlockAndChildren(programs, childProgram, blockId);
        }
        
        for (Program childFunction : block.functions) {
            buildFactsForCodeBlockAndChildren(programs, childFunction, blockId);
        }
    }

    private void buildPortFacts(Port[] portss, Program block, Integer blockId) {

        for (Port port : portss) {

            Integer portId = getIdForPort(port);
            String variableName = port.flowAnnotation.name;
            String portType = port.flowAnnotation.tag.substring(1);            
            ports.fact(portId.toString(), sq(portType), sq(variableName));

            String portAlias = port.flowAnnotation.alias();
            if (portAlias != null) {
                portAliases.fact(portId.toString(), sq(portAlias));
            }
            
            Uri portUri = port.flowAnnotation.uri();
            if (portUri != null) {
                portUris.fact(portId.toString(), sq(portUri.toString()));
            }
            
            if (portType.equals("in") || portType.equals("param")) {
                hasInPort.fact(blockId.toString(), portId.toString());
            } else {
                hasOutPort.fact(blockId.toString(), portId.toString());
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
}
