package org.yesworkflow.recon;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yesworkflow.data.UriTemplate;
import org.yesworkflow.data.UriVariable;
import org.yesworkflow.model.Data;
import org.yesworkflow.model.Function;
import org.yesworkflow.model.Port;
import org.yesworkflow.model.Program;
import org.yesworkflow.query.FactsBuilder;
import org.yesworkflow.query.QueryEngine;
import org.yesworkflow.query.QueryEngineModel;
import org.yesworkflow.util.FileIO;

public class ReconFacts {

    private final Run run;
    private String factsString = null;
    private Integer nextResourceId = 1;
    
    private FactsBuilder resourceFacts;
    private FactsBuilder dataResourceFacts;
    private FactsBuilder uriVariableValueFacts;
    private Map<String,Resource> resourceForUri = new HashMap<String,Resource>();

    public ReconFacts(QueryEngine queryEngine, Run run) {
        if (queryEngine == null) throw new IllegalArgumentException("Null logicLanguage argument passed to ReconFacts constructor.");
        if (run == null) throw new IllegalArgumentException("Null run argument passed to ReconFacts constructor.");
        if (run.model == null) throw new IllegalArgumentException("Null model field in run argument to passed to ReconFacts constructor.");
        this.run = run;
        QueryEngineModel queryEngineModel = new QueryEngineModel(queryEngine);

        this.resourceFacts  = new FactsBuilder(queryEngineModel, "resource", "resource_id", "resource_uri");
        this.dataResourceFacts = new FactsBuilder(queryEngineModel, "data_resource", "data_id", "resource_id");
        this.uriVariableValueFacts  = new FactsBuilder(queryEngineModel, "uri_variable_value", "resource_id", "uri_variable_id", "uri_variable_value");
    }

    public ReconFacts build() throws Exception {

        buildReconFactsRecursively(run.model.program);
        
        for (Function function : run.model.functions) {
            buildReconFactsRecursively(function);
        }
        
        factsString = new StringBuilder()
          .append(resourceFacts)
          .append(dataResourceFacts)
          .append(uriVariableValueFacts)
          .toString();

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
            List<Resource> resourcesWithVariables = findResourcesForPort(port);
            for (Resource resource : resourcesWithVariables) {
                buildUriVariableValueFacts(port.uriTemplate, resource);
            }
        }
    }
    
    private List<Resource> findResourcesForPort(Port port) {
        List<Resource> resourcesWithVariables = new LinkedList<Resource>();
        if (port.uriTemplate != null) {
            Path resourcePath = run.runDirectoryBase.resolve(port.uriTemplate.leadingPath);
            if (Files.isRegularFile(resourcePath)) {
                addResource(port.data, run.runDirectoryBase.relativize(resourcePath));
            } else {
                List<Resource> matchingResources = addMatchingResources(port);
                resourcesWithVariables.addAll(matchingResources);
            }
        }
        
        return resourcesWithVariables;
    }
    
    private Resource addResource(Data data, Path path) {
        String uri = path.toString();
        Resource resource = resourceForUri.get(uri);
        if (resource == null) {
            resource = new Resource(nextResourceId++, path.toString());
            resourceForUri.put(uri, resource);
            resourceFacts.add(resource.id, FileIO.normalizePathSeparator(uri));
        }
        dataResourceFacts.add(data.id, resource.id);
        return resource;
    }

    private List<Resource> addMatchingResources(Port port) {
        
        Path resourceSearchBase = run.runDirectoryBase.resolve(port.uriTemplate.leadingPath);
        FileResourceFinder resourceFinder = new FileResourceFinder(port.uriTemplate, run.runDirectoryBase);

        try {
            Files.walkFileTree(resourceSearchBase, resourceFinder);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        
        List<Path> resourcePaths = resourceFinder.getResourcePaths();
        List<Resource> resources = new LinkedList<Resource>();
        for (Path path : resourcePaths) {
            resources.add(addResource(port.data, path));
        }
        return resources;
    }
    
    private void buildUriVariableValueFacts(UriTemplate uriTemplate, Resource resource) throws Exception {
        Map<String,String> variableValues = uriTemplate.extractValuesFromPath(resource.uri);
        for (UriVariable variable : uriTemplate.variables) {
            String variableValue = variableValues.get(variable.name);
            uriVariableValueFacts.add(resource.id, variable.id, variableValue);
        }
    }
    
    public String toString() {
        return factsString;
    }
    
}
