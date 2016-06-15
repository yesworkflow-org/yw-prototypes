package org.yesworkflow.extract;

import java.io.BufferedReader;
import java.io.StringReader;

import org.yesworkflow.Language;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.YesWorkflowTestCase;

public class TestExtractFacts extends YesWorkflowTestCase {

    private YesWorkflowDB ywdb;
    private DefaultExtractor extractor;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.ywdb = YesWorkflowDB.createInMemoryDB();
        this.extractor = new DefaultExtractor(this.ywdb, super.stdoutStream, super.stderrStream)
                        .configure("language", Language.PYTHON);
    }
    
    public void testExtractFacts_BlankLine() throws Exception {        
//        String source = "  " + EOL;
//        BufferedReader reader = new BufferedReader(new StringReader(source));
//        extractor.reader(reader).extract();
//        assertEquals(
//            "extract_source(1, '')."    + EOL,
//            extractor.getFacts());
    }   
}