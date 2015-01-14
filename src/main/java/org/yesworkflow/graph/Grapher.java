package org.yesworkflow.graph;

import org.yesworkflow.model.Workflow;

public interface Grapher {
    
    Grapher workflow(Workflow workflow);
    Grapher graph() throws Exception;
    Grapher view(GraphView type);
    Grapher format(GraphFormat format);
}
