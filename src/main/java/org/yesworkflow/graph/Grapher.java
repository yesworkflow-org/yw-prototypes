package org.yesworkflow.graph;

import java.io.Writer;

import org.yesworkflow.model.Workflow;

public interface Grapher {
    
    Grapher workflow(Workflow workflow);
    Grapher graph() throws Exception;
    Grapher type(GraphType type);
    Grapher format(GraphFormat format);
}
