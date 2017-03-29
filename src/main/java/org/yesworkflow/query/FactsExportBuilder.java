package org.yesworkflow.query;

public class FactsExportBuilder extends DataExportBuilder {
	
    public FactsExportBuilder(QueryEngineModel queryEngineModel, String name, String... fields) {
	    super(queryEngineModel, name, fields);
	    addHeader(fields);
	}

    @Override
    public DataExportBuilder addHeader(String... headers) {

	    StringBuilder signature = new StringBuilder();
	    
	    signature.append(  "FACT: "    )
	             .append(  name        )
	             .append(  "("         )
	             .append(  headers[0]  );
	    
	    for (int i = 1; i < fieldCount; ++i) {
	        signature.append(  ", "        )
	                 .append(  headers[i]  );
	    }
	    
	    signature.append(  ")."    );

	    this.comment(signature.toString());
	    
	    return this;
    }
	
	public DataExportBuilder addRow(Object... values) {
	    
	    _buffer.append(    name                )
	           .append(    "("                 )
	           .append(    quote(values[0])    );
	    
	    for (int i = 1; i < fieldCount; ++i) {
	        _buffer.append(    ", "                )
                   .append(    quote(values[i])    );
	    }

	    _buffer.append(    ")."    )
	           .append(    EOL     );
	    
	    return this;
	}
	
    public FactsExportBuilder comment(String c) {
        if (queryEngineModel.showComments) {
            _buffer.append(     queryEngineModel.commentStart   )
                   .append(     c                               )
                   .append(     EOL                             );
        }        
        return this;
    }

    // TODO Apply quotes only when required by facts file format
    private String quote(Object value) {
        if (value == null) {
            return "nil";
        }
        if (value instanceof Number) {
            return value.toString();
        } else {
            return queryEngineModel.quote + value.toString() + queryEngineModel.quote;
        }
    }  
    
	public String toString() {
		return _buffer.toString();
	}
}