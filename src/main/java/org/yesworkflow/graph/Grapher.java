package org.yesworkflow.graph;

import org.yesworkflow.YWStage;
import org.yesworkflow.model.Workflow;

public interface Grapher extends YWStage {    
    Grapher workflow(Workflow workflow);
    Grapher graph() throws Exception;
    Grapher view(GraphView type);
    Grapher format(GraphFormat format);
    DotGrapher enableComments(boolean state);
}
