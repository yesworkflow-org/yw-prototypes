package org.yesworkflow;

import org.yesworkflow.exceptions.UsageException;

public class Extractor {

    private String sourcePath = null;
    private String databasePath = null;
    
    public Extractor inputScriptPath(String path) {
        this.sourcePath = path;
        return this;
    }

    public Extractor outputDbPath(String path) {
        this.databasePath = path;
        return this;
    }
    
    public void extract() throws Exception {
        if (sourcePath == null) throw new UsageException("No source path provided to extractor");
    }
    
}
