package org.yesworkflow.query;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

public class CSVExportBuilder extends DataExportBuilder {
	
    private char fieldSeparator = ',';
    private char quote = '"';
    private CSVPrinter csvPrinter = null;
    private StringWriter writer = new StringWriter();
    
    public CSVExportBuilder(String name, String... fields) throws IOException {
	    super(null, name, fields);
	    createCsvPrinter(fields);
	}
    
    private void createCsvPrinter(String fields[]) throws IOException {
            
        CSVFormat csvFormat = CSVFormat.newFormat(fieldSeparator)
                .withQuoteMode(QuoteMode.MINIMAL)
                .withQuote(quote)
                .withRecordSeparator(System.getProperty("line.separator"))
                .withSkipHeaderRecord(false)
                .withHeader(fields);
        
        csvPrinter = new CSVPrinter(writer, csvFormat);
    }    

    @Override
    public DataExportBuilder addHeader(String... headers) {
	    return this;
    }
	
	public DataExportBuilder addRow(Object values[]) throws IOException {
	    csvPrinter.printRecord(values);
	    return this;
	}
    
	public String toString() {
		return writer.toString();
	}

    @Override
    public DataExportBuilder comment(String c) {
        return this;
    }
}