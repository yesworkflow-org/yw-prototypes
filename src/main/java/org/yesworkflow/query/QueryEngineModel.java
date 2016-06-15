package org.yesworkflow.query;

public class QueryEngineModel {

    public String commentStart = "% ";
    public String quote = "'";
    public boolean showComments = true;

    public QueryEngineModel() {}
    
    public QueryEngineModel commentStart(String commentStart) {
        this.commentStart = commentStart;
        return this;
    }

    public QueryEngineModel showComments(boolean showComments) {
        this.showComments = showComments;
        return this;
    }

    public QueryEngineModel quote(String quote) {
        this.quote = quote;
        return this;
    }
}
