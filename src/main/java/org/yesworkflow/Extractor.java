package org.yesworkflow;

public interface Extractor {
    public Extractor sourcePath(String path);
    public Extractor databasePath(String path);
    public void extract() throws Exception;
}
