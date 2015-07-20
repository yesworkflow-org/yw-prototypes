package org.yesworkflow.query;

public class FactsBuilder {
	
    public static final String EOL = System.getProperty("line.separator");
    public final String name;
    public final int fieldCount;
    public final LogicLanguageModel logicLanguageModel;

    private StringBuilder _buffer = new StringBuilder();
    
	public FactsBuilder(LogicLanguageModel logicLanguageModel, String name, String... fields) {
	    
        this.logicLanguageModel = logicLanguageModel;
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
        if (logicLanguageModel.showComments) {
            _buffer.append(     EOL                             )
                   .append(     logicLanguageModel.commentStart )
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
            return logicLanguageModel.quote + value.toString() + logicLanguageModel.quote;
        }
    }  
    
	public String toString() {
		return _buffer.toString();
	}
}