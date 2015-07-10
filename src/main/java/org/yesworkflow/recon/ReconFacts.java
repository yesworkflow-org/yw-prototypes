package org.yesworkflow.recon;

import org.yesworkflow.query.FactsBuilder;
import org.yesworkflow.query.LogicLanguage;
import org.yesworkflow.query.LogicLanguageModel;

public class ReconFacts {

    private final Run run;
    private String factsString = null;
    
    private FactsBuilder resourceFacts;
    private FactsBuilder resourceChannelFacts;
    private FactsBuilder uriVariableValueFacts;

    public ReconFacts(LogicLanguage logicLanguage, Run run) {
        if (logicLanguage == null) throw new IllegalArgumentException("Null logicLanguage argument passed to ModelFacts constructor.");
        if (run == null) throw new IllegalArgumentException("Null run argument passed to RunFacts constructor.");
        this.run = run;
        LogicLanguageModel logicLanguageModel = new LogicLanguageModel(logicLanguage);

        this.resourceFacts  = new FactsBuilder(logicLanguageModel, "resource", "resource_id", "resource_uri");
        this.resourceChannelFacts  = new FactsBuilder(logicLanguageModel, "resource_channel", "resource_id", "channel_id");
        this.uriVariableValueFacts  = new FactsBuilder(logicLanguageModel, "uri_variable_value", "resource_id", "variable_id", "variable_value");
    }

    public ReconFacts build() {
        
        StringBuilder sb = new StringBuilder();
        sb.append(resourceFacts)
          .append(resourceChannelFacts)
          .append(uriVariableValueFacts);

        factsString = sb.toString();
     
        return this;
    }

    public String toString() {
        return factsString;
    }
}
