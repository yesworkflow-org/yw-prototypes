package org.yesworkflow.graph;

import java.util.Map;

import org.yesworkflow.YWStage;
import org.yesworkflow.model.Workflow;

public interface Grapher extends YWStage {    
    Grapher workflow(Workflow workflow);
    DotGrapher config(Map<String, Object> config) throws Exception;
    Grapher graph() throws Exception;
}
