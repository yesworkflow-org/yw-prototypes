package org.yesworkflow.model;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.yesworkflow.annotations.In;
import org.yesworkflow.data.UriVariable;
import org.yesworkflow.query.DataExportBuilder;
import org.yesworkflow.query.QueryEngine;

public class ModelFacts {

    private final Model model;
    private Map<String,String> facts = new LinkedHashMap<String,String>();
    
    private DataExportBuilder programFacts ;
    private DataExportBuilder workflowFacts;
    private DataExportBuilder functionFacts;
    private DataExportBuilder subprogramFacts;
    private DataExportBuilder portFacts;
    private DataExportBuilder portAliasFacts;
    private DataExportBuilder portUriFacts;
    private DataExportBuilder hasInPortFacts;
    private DataExportBuilder hasOutPortFacts;
    private DataExportBuilder channelFacts;
    private DataExportBuilder dataFacts;
    private DataExportBuilder portConnectionFacts;
    private DataExportBuilder inflowConnectionFacts;
    private DataExportBuilder outflowConnectionFacts;
    private DataExportBuilder portUriVariableFacts;

    public ModelFacts(QueryEngine queryEngine, Model model) {

        if (queryEngine == null) throw new IllegalArgumentException("Null logicLanguage argument passed to ModelFacts constructor.");
        if (model == null) throw new IllegalArgumentException("Null model argument passed to ModelFacts constructor.");
        if (model.program == null) throw new IllegalArgumentException("Null program field in model argument passed to ModelFacts constructor.");
        
        this.model = model;

        this.programFacts  = DataExportBuilder.create(queryEngine, "program", "program_id", "program_name", "qualified_program_name", "begin_annotation_id", "end_annotation_id");
        this.workflowFacts = DataExportBuilder.create(queryEngine, "workflow", "program_id");
        this.functionFacts = DataExportBuilder.create(queryEngine, "function", "program_id");
        this.subprogramFacts = DataExportBuilder.create(queryEngine, "has_subprogram", "program_id", "subprogram_id");
        this.portFacts = DataExportBuilder.create(queryEngine, "port", "port_id", "port_type", "port_name", "qualified_port_name", "port_annotation_id", "data_id");
        this.portAliasFacts = DataExportBuilder.create(queryEngine, "port_alias", "port_id", "alias");
        this.portUriFacts = DataExportBuilder.create(queryEngine, "port_uri_template", "port_id", "uri");
        this.hasInPortFacts = DataExportBuilder.create(queryEngine, "has_in_port", "block_id", "port_id");
        this.hasOutPortFacts = DataExportBuilder.create(queryEngine, "has_out_port", "block_id", "port_id");
        this.channelFacts = DataExportBuilder.create(queryEngine, "channel", "channel_id", "data_id");
        this.dataFacts = DataExportBuilder.create(queryEngine, "data", "data_id", "data_name", "qualified_data_name");
        this.portConnectionFacts = DataExportBuilder.create(queryEngine, "port_connects_to_channel", "port_id", "channel_id");
        this.inflowConnectionFacts = DataExportBuilder.create(queryEngine, "inflow_connects_to_channel", "port_id", "channel_id");
        this.outflowConnectionFacts = DataExportBuilder.create(queryEngine, "outflow_connects_to_channel", "port_id", "channel_id");
        this.portUriVariableFacts = DataExportBuilder.create(queryEngine, "uri_variable", "uri_variable_id", "variable_name", "port_id");
    }

    public ModelFacts build() {
        
        if (model.program == null) throw new NullPointerException("Null program field in ModelFacts.model.");
        if (model.functions == null) throw new NullPointerException("Null functions field in ModelFacts.model.");

        for (Data data : model.data) {
            String qualifiedDataName = qualifiedName("", "[", data.name, "]");
            dataFacts.addRow(data.id, data.name, qualifiedDataName);
        }

        buildProgramFactsRecursively(model.program, null, null);
        
        for (Function function : model.functions) {
            buildProgramFactsRecursively(function, null, null);
        }
        
        facts.put(programFacts.name, programFacts.toString());
        facts.put(workflowFacts.name, workflowFacts.toString());
        facts.put(functionFacts.name, functionFacts.toString());
        facts.put(subprogramFacts.name, subprogramFacts.toString());
        facts.put(portFacts.name, portFacts.toString());
        facts.put(portAliasFacts.name, portAliasFacts.toString());
        facts.put(portUriFacts.name, portUriFacts.toString());
        facts.put(hasInPortFacts.name, hasInPortFacts.toString());
        facts.put(hasOutPortFacts.name, hasOutPortFacts.toString());
        facts.put(dataFacts.name, dataFacts.toString());
        facts.put(channelFacts.name, channelFacts.toString());
        facts.put(portConnectionFacts.name, portConnectionFacts.toString());
        facts.put(inflowConnectionFacts.name, inflowConnectionFacts.toString());
        facts.put(outflowConnectionFacts.name, outflowConnectionFacts.toString());
        facts.put(portUriVariableFacts.name, portUriVariableFacts.toString());
        
        return this;
    }

    private void buildProgramFactsRecursively(Program program, String parentName, Long parentId) {

        if (program == null) throw new IllegalArgumentException("Null program argument.");
        if (program.channels == null) throw new IllegalArgumentException("Null channels field in program argument.");
        if (program.programs == null) throw new IllegalArgumentException("Null programs field in program argument.");
        if (program.functions == null) throw new IllegalArgumentException("Null functions field in program argument.");
        
        String qualifiedProgramName = qualifiedName(parentName, ".", program.beginAnnotation.value(), "");
        programFacts.addRow(program.id, program.beginAnnotation.value(), qualifiedProgramName, program.beginAnnotation.id, program.endAnnotation.id);
        
        if (program.channels.length > 0) {
            workflowFacts.addRow(program.id);
        }
        
        if (program instanceof Function) {            
            functionFacts.addRow(program.id);
        }
        
        if (parentId != null) {
            subprogramFacts.addRow(parentId, program.id);
        }
        
        for (Data data : program.data) {
            String qualifiedDataName = qualifiedName(qualifiedProgramName, "[", data.name, "]");
            dataFacts.addRow(data.id, data.name, qualifiedDataName);
        }
        
        buildPortFacts(program.inPorts, program, program.id, qualifiedProgramName);
        buildPortFacts(program.outPorts, program, program.id, qualifiedProgramName);
        
        for (Channel channel : program.channels) {
            
            if (channel.sourcePort == null) throw new NullPointerException("Null sourcePort field in channel.");
            if (channel.sinkPort == null) throw new NullPointerException("Null sinkPort field in channel.");
            if (channel.sourcePort.flowAnnotation == null) throw new NullPointerException("Null flowAnnotation field in sourcePort.");

            channelFacts.addRow(channel.id, channel.data.id);
            
            if (channel.sourceProgram == null) {
                inflowConnectionFacts.addRow(channel.sourcePort.id, channel.id);
            } else {
                portConnectionFacts.addRow(channel.sourcePort.id, channel.id);
            }
            
            
            if (channel.sinkProgram == null) {
                outflowConnectionFacts.addRow(channel.sinkPort.id, channel.id);
            } else {
                portConnectionFacts.addRow(channel.sinkPort.id, channel.id);
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
    
    private void buildPortFacts(Port[] ports, Program block, Long blockId, String programName) {
        
        if (ports == null) throw new IllegalArgumentException("Null ports argument.");
        if (block == null) throw new IllegalArgumentException("Null block argument.");
        if (blockId == null) throw new IllegalArgumentException("Null blockId argument.");

        for (Port port : ports) {

            if (port.flowAnnotation == null) throw new NullPointerException("Null flowAnnotation field in port.");
            if (port.flowAnnotation.keyword == null) throw new NullPointerException("Null tag field in port.flowAnnotation.");
            
            String portName = port.flowAnnotation.value();
            String portType = port.flowAnnotation.keyword.substring(1);
            String infix = (port.flowAnnotation instanceof In) ? "<-" : "->";
            String qualifiedPortName = qualifiedName(programName, infix, portName, "");
            portFacts.addRow(port.id, portType, portName, qualifiedPortName, port.flowAnnotation.id, port.data.id);

            String portAlias = port.flowAnnotation.alias();
            if (portAlias != null) {
                portAliasFacts.addRow(port.id, portAlias);
            }
            
            if (port.uriTemplate != null) {
                portUriFacts.addRow(port.id, port.uriTemplate.toString());
                Set<String> uniqueVariableNames = new HashSet<String>();
                for (UriVariable variable : port.uriTemplate.variables) {
                    if (! variable.name.trim().isEmpty()) {
                        if (!uniqueVariableNames.contains(variable.name)) {
                            uniqueVariableNames.add(variable.name);
                            portUriVariableFacts.addRow(variable.id, variable.name, port.id);
                        }
                    }
                }
            }
            
            if (portType.equalsIgnoreCase("in") || portType.equalsIgnoreCase("param")) {
                hasInPortFacts.addRow(blockId, port.id);
            } else {
                hasOutPortFacts.addRow(blockId, port.id);
            }
        }
    }

    public Map<String,String> facts() {
        return facts;
    }
}
