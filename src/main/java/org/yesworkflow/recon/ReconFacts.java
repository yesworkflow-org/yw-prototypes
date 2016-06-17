package org.yesworkflow.recon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yesworkflow.annotations.In;
import org.yesworkflow.annotations.Log;
import org.yesworkflow.annotations.Out;
import org.yesworkflow.data.LogEntryTemplate;
import org.yesworkflow.data.UriTemplate;
import org.yesworkflow.data.TemplateVariable;
import org.yesworkflow.model.Data;
import org.yesworkflow.model.Function;
import org.yesworkflow.model.Port;
import org.yesworkflow.model.Program;
import org.yesworkflow.query.DataExportBuilder;
import org.yesworkflow.query.QueryEngine;
import org.yesworkflow.recon.ResourceFinder.ResourceRole;
import org.yesworkflow.util.FileIO;

public class ReconFacts {

    private final Run run;
    private Map<String,String> facts = new LinkedHashMap<String,String>();
    private Integer nextResourceId = 1;
    private Long nextLogEntryId = 1L;
    
    private DataExportBuilder resourceFacts;
    private DataExportBuilder dataResourceFacts;
    private DataExportBuilder uriVariableValueFacts;
    private DataExportBuilder logVariableValueFacts;

    private Map<String,Resource> resourceForUri = new HashMap<String,Resource>();

    private ResourceFinder resourceFinder;
    
    public ReconFacts(QueryEngine queryEngine, Run run, ResourceFinder resourceFinder) throws IOException {
        
        if (queryEngine == null) throw new IllegalArgumentException("Null logicLanguage argument passed to ReconFacts constructor.");
        if (run == null) throw new IllegalArgumentException("Null run argument passed to ReconFacts constructor.");
        if (run.model == null) throw new IllegalArgumentException("Null model field in run argument to passed to ReconFacts constructor.");
        this.run = run;

        this.resourceFacts  = DataExportBuilder.create(queryEngine, "resource", "resource_id", "resource_uri");
        this.dataResourceFacts = DataExportBuilder.create(queryEngine, "data_resource", "data_id", "resource_id");
        this.uriVariableValueFacts = DataExportBuilder.create(queryEngine, "uri_variable_value", "resource_id", "uri_variable_id", "uri_variable_value");
        this.logVariableValueFacts = DataExportBuilder.create(queryEngine, "log_variable_value", "resource_id", "log_entry_id", "log_variable_id", "log_variable_value");

        this.resourceFinder = resourceFinder;
    }

    public ReconFacts build() throws Exception {

        buildReconFactsRecursively(run.model.program);
        
        for (Function function : run.model.functions) {
            buildReconFactsRecursively(function);
        }
        
        facts.put(resourceFacts.name, resourceFacts.toString());
        facts.put(dataResourceFacts.name, dataResourceFacts.toString());
        facts.put(uriVariableValueFacts.name, uriVariableValueFacts.toString());
        facts.put(logVariableValueFacts.name, logVariableValueFacts.toString());
 
        return this;
    }

   private void buildReconFactsRecursively(Program program) throws Exception {
        
        if (program == null) throw new IllegalArgumentException("Null program argument.");
        if (program.programs == null) throw new IllegalArgumentException("Null programs field in program argument.");
        if (program.functions == null) throw new IllegalArgumentException("Null functions field in program argument.");
        if (program.inPorts == null) throw new IllegalArgumentException("Null inPorts field in program argument.");
        if (program.outPorts == null) throw new IllegalArgumentException("Null outPorts field in program argument.");

        buildFactsForPortResources(program.inPorts);
        buildFactsForPortResources(program.outPorts);
        
        for (Program childProgram : program.programs) {
            buildReconFactsRecursively(childProgram);
        }
        
        for (Program childFunction : program.functions) {
            buildReconFactsRecursively(childFunction);
        }
    }

    private void buildFactsForPortResources(Port[] ports) throws Exception {
        for (Port port: ports) {
            List<Resource> resources = findResourcesForPort(port);
            for (Resource resource : resources) {
                buildUriVariableValueFacts(port.uriTemplate, resource);
            }
        }
    }
    
    private List<Resource> findResourcesForPort(Port port) throws Exception {
        
        List<Resource> foundResources = new LinkedList<Resource>();
        
        ResourceFinder.ResourceRole role = (port.flowAnnotation instanceof In) ? ResourceRole.INPUT : ResourceRole.OUTPUT;
        
        if (port.uriTemplate != null) {
            
            Collection<String> matchingResourceURIs = resourceFinder.findMatchingResources(run.runDirectoryBase.toString(), port.uriTemplate, role);
            for (String uri : matchingResourceURIs) {
                Resource resource = addResource(port.data, uri);
                foundResources.add(resource);
            }
            
            if (port.flowAnnotation instanceof Out && !foundResources.isEmpty()) {
                Out outPort = (Out)port.flowAnnotation;
                if (!outPort.logAnnotations().isEmpty()) {
                    for (Resource resource : foundResources) {
                        findLogEntries(outPort.logAnnotations(), resource);
                    }
                }
            }
        }
        
        return foundResources;
    }
    
    private void findLogEntries(List<Log> logAnnotations, Resource resource) throws Exception {
        File logFile = new File(run.runDirectoryBase.toString() + "/" + resource.uri);
        FileReader fileReader = new FileReader(logFile);
        BufferedReader br = new BufferedReader(fileReader);
        String entry = null;
        while ((entry = br.readLine()) != null) {
            findLogEntryVariableValues(logAnnotations, resource, entry);
        }
        br.close();
    }
    
    private void findLogEntryVariableValues(List<Log> logAnnotations, Resource resource, String entry) throws Exception {
        // "resource_id", "log_entry_id", "log_variable_id", "log_variable_value"
        for (Log logAnnotation : logAnnotations) {
            LogEntryTemplate template = logAnnotation.entryTemplate;
            Map<String,String> variableValues = template.extractValuesFromLogEntry(entry);
            if (variableValues != null && !variableValues.isEmpty()) {
                System.out.println(variableValues);
                Long logEntryId = nextLogEntryId++;
                for (Map.Entry<String, String> e : variableValues.entrySet()) {
                    String variableName =  e.getKey();
                    Long variableId = logAnnotation.variableId(variableName);
                    logVariableValueFacts.addRow(resource.id, logEntryId, variableId, e.getValue());
                }
                return;
            }
        }
    }
    
    private Resource addResource(Data data, String uri) throws IOException {
        Resource resource = resourceForUri.get(uri);
        if (resource == null) {
            resource = new Resource(nextResourceId++, uri.toString());
            resourceForUri.put(uri, resource);
            resourceFacts.addRow(resource.id, FileIO.normalizePathSeparator(uri));
        }
        dataResourceFacts.addRow(data.id, resource.id);
        return resource;
    }

    private void buildUriVariableValueFacts(UriTemplate uriTemplate, Resource resource) throws Exception {
        Map<String,String> variableValues = uriTemplate.extractValuesFromPath(resource.uri);
        for (TemplateVariable variable : uriTemplate.variables) {
            String variableValue = variableValues.get(variable.name);
            uriVariableValueFacts.addRow(resource.id, variable.id, variableValue);
        }
    }
    
    public Map<String,String> facts() {
        return facts;
    }
}
