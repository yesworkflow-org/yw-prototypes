package org.yesworkflow.extract;

import java.util.List;

import org.yesworkflow.comments.Comment;
import org.yesworkflow.model.Program;

public interface Extractor {
    Extractor commentCharacter(char c);
    Extractor sourcePath(String path);
    Extractor databasePath(String path);
    void extract() throws Exception;
    List<String> getLines();
    List<Comment> getComments();
	Program getModel();
}
