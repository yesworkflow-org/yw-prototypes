package org.yesworkflow.query;

public class FactsBuilder {
	
    public static final String EOL = System.getProperty("line.separator");
    public final String name;
    public final int fieldCount;
    public final QueryEngineModel queryEngineModel;

    private StringBuilder _buffer = new StringBuilder();
    
	public FactsBuilder(QueryEngineModel queryEngineModel, String name, String... fields) {
	    
        this.queryEngineModel = queryEngineModel;
	    this.name = name;
	    this.fieldCount = fields.length;
	    
	    StringBuilder signature = new StringBuilder();
	    
	    signature.append(  "FACT: "    )
	             .append(  name        )
	             .append(  "("         )
	             .append(  fields[0]   );
	    
	    for (int i = 1; i < fieldCount; ++i) {
	        
	        signature.append(  ", "        )
	                 .append(  fields[i]   );
	    }
	    
	    signature.append(  ")."    );

	    this.comment(signature.toString());
	}
	
	public void add(Object... values) {
	    
	    _buffer.append(    name                )
	           .append(    "("                 )
	           .append(    quote(values[0])    );
	    
	    for (int i = 1; i < fieldCount; ++i) {
	        _buffer.append(    ", "                )
                   .append(    quote(values[i])    );
	    }

	    _buffer.append(    ")."    )
	           .append(    EOL     );
	}
	
    public FactsBuilder comment(String c) {
        if (queryEngineModel.showComments) {
            _buffer.append(     EOL                             )
                   .append(     queryEngineModel.commentStart )
                   .append(     c                               )
                   .append(     EOL                             );
        }        
        return this;
    }

    // TODO Apply quotes only when required by facts file format
    private String quote(Object value) {
        if (value instanceof Integer) {
            return value.toString();
        } else {
            return queryEngineModel.quote + value.toString() + queryEngineModel.quote;
        }
    }  
    
	public String toString() {
		return _buffer.toString();
	}
}