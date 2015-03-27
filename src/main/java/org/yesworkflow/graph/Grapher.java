package org.yesworkflow.graph;

import java.util.Map;

import org.yesworkflow.YWStage;
import org.yesworkflow.model.Program;

public interface Grapher extends YWStage {    
    Grapher workflow(Program workflow);
    DotGrapher config(Map<String, Object> config) throws Exception;
    Grapher graph() throws Exception;
}