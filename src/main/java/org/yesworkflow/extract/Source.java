package org.yesworkflow.extract;

import org.yesworkflow.db.YesWorkflowDB;

public class Source {

    private static Integer nextSourceId = 1;

    public final Integer id;
    public final String path;
    
    public static Source newSource(YesWorkflowDB ywdb, String path) {
        Source source = new Source(nextSourceId++, "path");
        ywdb.insertSourceFile(source.id, source.path);
        return source;
    }
    
    public Source(Integer id, String path) {
        this.id = id;
        this.path = path;
    }
}
