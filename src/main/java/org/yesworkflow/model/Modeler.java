package org.yesworkflow.model;

import java.util.List;
import java.util.Map;

import org.yesworkflow.YWStage;
import org.yesworkflow.annotations.Annotation;

public interface Modeler extends YWStage {
    Modeler annotations(List<Annotation> annotations);
    public DefaultModeler configure(String key, Object value) throws Exception;
    Modeler configure(Map<String, Object> config) throws Exception;
    Modeler model() throws Exception;
    Model getModel();
    Workflow getWorkflow();
}
