package org.yesworkflow.query;

import java.io.IOException;

public abstract class DataExportBuilder {
	
    protected static final String EOL = System.getProperty("line.separator");
    public final String name;
    protected final int fieldCount;
    protected StringBuilder _buffer = new StringBuilder();
    protected QueryEngineModel queryEngineModel;
    
	public DataExportBuilder(QueryEngineModel queryEngineModel, String name, String... fields) {
	    this.name = name;
	    this.queryEngineModel = queryEngineModel;
	    this.fieldCount = fields.length;
	}

    public abstract DataExportBuilder addHeader(String... headers);
	public abstract DataExportBuilder addRow(Object... values) throws IOException;
    public abstract DataExportBuilder comment(String c);
    
    public static DataExportBuilder create(QueryEngine engine, String name, String... fields) throws IOException {

        if (engine == null) {
            engine = QueryEngine.DEFAULT;
        }
        
        QueryEngineModel qem = null;
        
        switch(engine) {
            
            case CSV:
                return new CSVExportBuilder(name, fields);                
                
            case DLV:
                qem = new QueryEngineModel()
                          .showComments(true)
                          .commentStart("% ")
                          .quote("\"");
                return new FactsExportBuilder(qem, name, fields);
            
            case IRIS:
                qem = new QueryEngineModel()
                          .showComments(false)
                          .quote("\'");
                return new FactsExportBuilder(qem, name, fields);
                
            default:
            case XSB:
            case SWIPL:
            case DEFAULT:
                qem = new QueryEngineModel()
                          .showComments(true)
                          .commentStart("% ")
                          .quote("'");
                return new FactsExportBuilder(qem, name, fields);
        }
    }
    
    public QueryEngineModel queryEngineModel() {
        return queryEngineModel;
    }
    
	public String toString() {
		return _buffer.toString();
	}
}