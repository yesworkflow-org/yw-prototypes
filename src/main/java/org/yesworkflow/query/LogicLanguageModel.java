package org.yesworkflow.query;

public class LogicLanguageModel {

    public final LogicLanguage logicLanguage;
    public final boolean showComments;
    public final String commentStart;
    public final String quote;

    public LogicLanguageModel(LogicLanguage logicLanguage) {
        
        this.logicLanguage = logicLanguage;
        
        switch(logicLanguage) {
            
        case DATALOG_DLV:
                this.showComments = true;
                this.commentStart = "% ";
                this.quote = "\"";
                break;
            
            case DATALOG_IRIS:
                this.showComments = false;
                this.commentStart = null;
                this.quote = "\'";
                break;
                
            default:
            case PROLOG:
                this.showComments = true;
                this.commentStart = "% ";
                this.quote = "'";
                break;
        
        }
    }
}
