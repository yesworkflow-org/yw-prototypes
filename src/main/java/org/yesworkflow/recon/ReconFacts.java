package org.yesworkflow.recon;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yesworkflow.data.UriTemplate;
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
    private Map<String, Integer> resourceIdForUri = new HashMap<String,Integer>();

    public ReconFacts(LogicLanguage logicLanguage, Run run) {
        if (logicLanguage == null) throw new IllegalArgumentException("Null logicLanguage argument passed to ModelFacts constructor.");
        if (run == null) throw new IllegalArgumentException("Null run argument passed to RunFacts constructor.");
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
        
        StringBuilder sb = new StringBuilder();
        sb.append(resourceFacts)
          .append(uriVariableValueFacts);

        factsString = sb.toString();
     
        return this;
    }

   private void buildReconFactsRecursively(Program program) {
        
        if (program == null) throw new IllegalArgumentException("Null program argument.");
        if (program.channels == null) throw new IllegalArgumentException("Null channels field in program argument.");
        if (program.programs == null) throw new IllegalArgumentException("Null programs field in program argument.");
        if (program.functions == null) throw new IllegalArgumentException("Null functions field in program argument.");

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
                resourcesWithVariables.addAll(addMatchingResources(port.uriTemplate));
            }
        }
        
        return resourcesWithVariables;
    }
    
    private void addResource(String uri) {
        if (resourceIdForUri.get(uri) == null) {
            Integer id = nextResourceId++;
            resourceIdForUri.put(uri, id);
            resourceFacts.add(id, uri);
        }
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
        // align resource.uri to fixed parts of uriTemplate
        // for each named variable in template extract corresponding value from resource.uri
        //   if variable occurs early in template make sure new value matches last one
        //   save valid variable values to uriVariableValueFacts
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
              addResource(file.toString());
          }
          return FileVisitResult.CONTINUE;
        }
        
        @Override  public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes aAttrs) throws IOException {
          return FileVisitResult.CONTINUE;
        }
    }
}
