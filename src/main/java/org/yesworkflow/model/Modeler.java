package org.yesworkflow.model;

import java.util.List;

import org.yesworkflow.YWStage;
import org.yesworkflow.comments.Comment;

public interface Modeler extends YWStage {
    Modeler comments(List<Comment> comments);
    Modeler model() throws Exception;
    Program getModel();
}
