package org.yesworkflow;

import org.yesworkflow.exceptions.UsageException;

public class DefaultExtractor implements Extractor {

    private String sourcePath = null;
    private String databasePath = null;
    
    public DefaultExtractor sourcePath(String path) {
        this.sourcePath = path;
        return this;
    }

    public DefaultExtractor databasePath(String path) {
        this.databasePath = path;
        return this;
    }
    
    public void extract() throws Exception {
        if (sourcePath == null) throw new UsageException("No source path provided to extractor");
    }
    
}
