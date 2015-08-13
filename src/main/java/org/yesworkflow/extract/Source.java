package org.yesworkflow.extract;

import org.yesworkflow.db.YesWorkflowDB;

public class Source {


    public final Integer id;
    public final String path;
    
    public static Source newSource(YesWorkflowDB ywdb, String path) {
        int id = ywdb.insertSourceFile(path);
        Source source = new Source(id, path);
        return source;
    }
    
    public Source(Integer id, String path) {
        this.id = id;
        this.path = path;
    }
}
