package org.yesworkflow.recon;

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
            List<Resource> resources = findResourcesForPort(port);
            for (Resource resource : resources) {
                buildUriVariableValueFacts(port.uriTemplate, resource);
            }
        }
    }   
    
    private List<Resource> findResourcesForPort(Port port) {
        List<Resource> resources = new LinkedList<Resource>();
        // search file system for expansions of port.uriTemplate
        // each match corresponds to a resource with concrete uri
        // use Resource in resourceIdForUri if concrete uri has been seen before
        // otherwise create new Resource for each match and add new resources to 
        // resourceIdForUri and resourceFacts.
        return resources;
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
}
