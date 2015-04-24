package org.yesworkflow.query;

public class FactsBuilder {
	
    public static final String EOL = System.getProperty("line.separator");

    private StringBuilder _buffer = new StringBuilder();
    private final String name;
    private final int fieldCount;
    private int nextId = 1;
    
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
	
	public Integer nextId() {
	       Integer id = nextId++;
	       return id;
	}
	
	public void fact(String... values) {
	    
	    _buffer.append(    name        )
	           .append(    "("         )
	           .append(    values[0]   );
	    
	    for (int i = 1; i < fieldCount; ++i) {
	        _buffer.append(    ", "           )
                   .append(    values[i]      );
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
    
	public String toString() {
		return _buffer.toString();
	}
}