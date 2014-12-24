package org.yesworkflow;

import java.util.List;

public interface Extractor {
    Extractor commentCharacter(char c);
    Extractor sourcePath(String path);
    Extractor databasePath(String path);
    void extract() throws Exception;
    List<String> getLines();
}
