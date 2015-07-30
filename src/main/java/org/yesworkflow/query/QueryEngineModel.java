package org.yesworkflow.query;

public class QueryEngineModel {

    public final QueryEngine engine;
    public final boolean showComments;
    public final String commentStart;
    public final String quote;

    public QueryEngineModel(QueryEngine engine) {
        
        this.engine = engine;
        
        switch(engine) {
            
            case DLV:
                this.showComments = true;
                this.commentStart = "% ";
                this.quote = "\"";
                break;
            
            case IRIS:
                this.showComments = false;
                this.commentStart = null;
                this.quote = "\'";
                break;
                
            case SWIPL:
                this.showComments = true;
                this.commentStart = "% ";
                this.quote = "'";
                break;

            default:
            case XSB:
                this.showComments = true;
                this.commentStart = "% ";
                this.quote = "'";
                break;
        }
    }
}
