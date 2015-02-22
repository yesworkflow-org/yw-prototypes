package org.yesworkflow.model;

import java.util.List;

import org.yesworkflow.YWStage;
import org.yesworkflow.annotations.Annotation;

public interface Modeler extends YWStage {
    Modeler annotations(List<Annotation> annotations);
    Modeler model() throws Exception;
    Program getModel();
}
