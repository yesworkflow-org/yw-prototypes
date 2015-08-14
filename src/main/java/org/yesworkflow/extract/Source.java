package org.yesworkflow.extract;

import org.yesworkflow.db.YesWorkflowDB;

public class Source {

    public final Long id;
    public final String path;
    
    public static Source newSource(YesWorkflowDB ywdb, String path) {
        Long id = ywdb.insertSourceFile(path);
        Source source = new Source(id, path);
        return source;
    }
    
    public Source(Long id, String path) {
        this.id = id;
        this.path = path;
    }
}
