package org.yesworkflow.query;

public class CSVExportBuilder extends DataExportBuilder {
	
    private String fieldSeparator = ",";
    private String quote = "'";
    
    public CSVExportBuilder(String name, String... fields) {
	    super(null, name, fields);
	    addHeader(fields);
	}

    @Override
    public DataExportBuilder addHeader(String... headers) {

        _buffer.append(  headers[0]  );
	    
	    for (int i = 1; i < fieldCount; ++i) {
	        _buffer.append(  fieldSeparator  )
	               .append(  headers[i]      );
	    }
	    
        _buffer.append(EOL);
	    
	    return this;
    }
	
	public DataExportBuilder addRow(Object... values) {
	    
	    _buffer.append(    quote(values[0])    );
	    
	    for (int i = 1; i < fieldCount; ++i) {
	        _buffer.append(    fieldSeparator      )
                   .append(    quote(values[i])    );
	    }

	    _buffer.append(EOL);
	    
	    return this;
	}
	
    public CSVExportBuilder comment(String c) {      
        return this;
    }

    private String quote(Object value) {
        if (value instanceof Number) {
            return value.toString();
        } else {
            return quote + value.toString() + quote;
        }
    }  
    
	public String toString() {
		return _buffer.toString();
	}
}