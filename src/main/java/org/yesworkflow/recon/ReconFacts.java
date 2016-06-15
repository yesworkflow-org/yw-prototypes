package org.yesworkflow.recon;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yesworkflow.annotations.In;
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
    
    private DataExportBuilder resourceFacts;
    private DataExportBuilder dataResourceFacts;
    private DataExportBuilder uriVariableValueFacts;
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
    
    private List<Resource> findResourcesForPort(Port port) throws IOException {
        
        List<Resource> foundResources = new LinkedList<Resource>();
        
        ResourceFinder.ResourceRole role = (port.flowAnnotation instanceof In) ? ResourceRole.INPUT : ResourceRole.OUTPUT;
        
        if (port.uriTemplate != null) {
            Collection<String> matchingResourceURIs = resourceFinder.findMatchingResources(run.runDirectoryBase.toString(), port.uriTemplate, role);
            for (String uri : matchingResourceURIs) {
                Resource resource = addResource(port.data, uri);
                foundResources.add(resource);
            }
        }
        
        return foundResources;
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
