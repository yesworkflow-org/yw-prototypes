package org.yesworkflow.model;

import java.util.List;
import java.util.Map;

import org.yesworkflow.YWStage;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.config.Configurable;

public interface Modeler extends YWStage, Configurable {
    Modeler configure(String key, Object value) throws Exception;
    Modeler configure(Map<String, Object> config) throws Exception;
    Modeler annotations(List<Annotation> annotations);
    Modeler model() throws Exception;
    Model getModel();
    Map<String, String> getFacts();
}
