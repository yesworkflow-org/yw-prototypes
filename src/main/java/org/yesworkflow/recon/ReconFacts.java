package org.yesworkflow.recon;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yesworkflow.data.UriTemplate;
import org.yesworkflow.data.UriVariable;
import org.yesworkflow.model.Function;
import org.yesworkflow.model.Port;
import org.yesworkflow.model.Program;
import org.yesworkflow.query.FactsBuilder;
import org.yesworkflow.query.LogicLanguage;
import org.yesworkflow.query.LogicLanguageModel;

public class ReconFacts {

    private final Run run;
    private String factsString = null;
    private Integer nextResourceId = 1;
    
    private FactsBuilder resourceFacts;
    private FactsBuilder uriVariableValueFacts;
    private Map<String,Resource> resourceForUri = new HashMap<String,Resource>();

    public ReconFacts(LogicLanguage logicLanguage, Run run) {
        if (logicLanguage == null) throw new IllegalArgumentException("Null logicLanguage argument passed to ReconFacts constructor.");
        if (run == null) throw new IllegalArgumentException("Null run argument passed to ReconFacts constructor.");
        if (run.model == null) throw new IllegalArgumentException("Null model field in run argument to passed to ReconFacts constructor.");
        this.run = run;
        LogicLanguageModel logicLanguageModel = new LogicLanguageModel(logicLanguage);

        this.resourceFacts  = new FactsBuilder(logicLanguageModel, "resource", "resource_id", "resource_uri");
        this.uriVariableValueFacts  = new FactsBuilder(logicLanguageModel, "uri_variable_value", "resource_id", "uri_variable_id", "uri_variable_value");
    }

    public ReconFacts build() {

        buildReconFactsRecursively(run.model.program);
        
        for (Function function : run.model.functions) {
            buildReconFactsRecursively(function);            
        }
        
        factsString = new StringBuilder()
          .append(resourceFacts)
          .append(uriVariableValueFacts)
          .toString();

        return this;
    }

   private void buildReconFactsRecursively(Program program) {
        
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
   
    private void buildFactsForPortResources(Port[] ports) {        
        for (Port port: ports) {
            List<Resource> resourcesWithVariables = findResourcesForPort(port);
            for (Resource resource : resourcesWithVariables) {
                buildUriVariableValueFacts(port.uriTemplate, resource);
            }
        }
    }   
    
    private List<Resource> findResourcesForPort(Port port) {
        
        List<Resource> resourcesWithVariables = new LinkedList<Resource>();
        
        UriTemplate template = port.uriTemplate;
        if (template != null) {

            if (Files.isRegularFile(port.uriTemplate.leadingPath)) {
                addResource(port.uriTemplate.leadingPath.toString());
            } else {
                List<Resource> matchingResources = addMatchingResources(port.uriTemplate);
                resourcesWithVariables.addAll(matchingResources);
            }
        }
        
        return resourcesWithVariables;
    }
    
    private Resource addResource(String uri) {
        Resource resource = resourceForUri.get(uri);
        if (resource == null) {
            Integer id = nextResourceId++;
            resource = new Resource(id, uri);
            resourceForUri.put(uri, resource);
            resourceFacts.add(id, uri);
        }
        return resource;
    }

    private List<Resource> addMatchingResources(UriTemplate template) {
        
        FileVisitor<Path> resourceFinder = new FileResourceFinder(template);
        
        try {
            Files.walkFileTree(template.leadingPath, resourceFinder);
        } catch(Exception e) {
            System.out.println(e.getStackTrace());
        }
        return ((FileResourceFinder)resourceFinder).resources;
    }
    
    private void buildUriVariableValueFacts(UriTemplate uriTemplate, Resource resource) {
        Map<String,String> variableValues = uriTemplate.extractValuesFromPath(resource.uri);
        for (UriVariable variable : uriTemplate.variables) {
            String variableValue = variableValues.get(variable.name);
            uriVariableValueFacts.add(resource.id, variable.id, variableValue);
        }
    }
    
    public String toString() {
        return factsString;
    }
    
    private final class FileResourceFinder extends SimpleFileVisitor<Path> {
        
        private final PathMatcher matcher;
        public final List<Resource> resources = new LinkedList<Resource>();
        
        public FileResourceFinder(UriTemplate template) {
            super();
            FileSystem fs = FileSystems.getDefault();
            matcher = fs.getPathMatcher("glob:" + template.getGlobPattern());
        }
        
        @Override public FileVisitResult visitFile(Path file, BasicFileAttributes aAttrs) throws IOException {
          if (matcher.matches(file)) {
              Resource r = addResource(file.toString());
              resources.add(r);
          }
          return FileVisitResult.CONTINUE;
        }
        
        @Override  public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes aAttrs) throws IOException {
          // TODO: Improve performance by detecting when directory cannot lead to a match for the template
          return FileVisitResult.CONTINUE;
        }
    }
}
