package org.yesworkflow.query;

public class FactsBuilder {
	
    public static final String EOL = System.getProperty("line.separator");

    private StringBuilder _buffer = new StringBuilder();
    private final String name;
    private final int fieldCount;
    
	public FactsBuilder(String name, String... fields) {
	    
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
	    
	    _buffer.append(    name            )
	           .append(    "("             )
	           .append(    q(values[0])    );
	    
	    for (int i = 1; i < fieldCount; ++i) {
	        _buffer.append(    ", "            )
                   .append(    q(values[i])    );
	    }

	    _buffer.append(    ")."    )
	           .append(    EOL     );
	}
	
    public FactsBuilder comment(String c) {
        
        _buffer.append(     EOL     )
               .append(     "% "    )
               .append(     c       )
               .append(     EOL     );
        
        return this;
    }

    private String q(Object value) {
        if (value instanceof Integer) {
            return value.toString();
        } else {
            return "'" + value.toString() + "'";
        }
    }  
    
	public String toString() {
		return _buffer.toString();
	}
}