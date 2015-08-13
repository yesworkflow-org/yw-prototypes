package org.yesworkflow.extract;

import java.io.BufferedReader;
import java.io.StringReader;

import org.yesworkflow.Language;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.query.QueryEngine;
import org.yesworkflow.query.QueryEngineModel;
import org.yesworkflow.YesWorkflowTestCase;

public class TestExtractFacts extends YesWorkflowTestCase {

    static private QueryEngine DEFAULT_QUERY_ENGINE = QueryEngine.SWIPL;

    private QueryEngine queryEngine = DEFAULT_QUERY_ENGINE;
    private YesWorkflowDB ywdb = null;
    private DefaultExtractor extractor = null;
    QueryEngineModel queryEngineModel = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.ywdb = YesWorkflowDB.createInMemoryDB();
        extractor = new DefaultExtractor(this.ywdb, super.stdoutStream, super.stderrStream);
        this.extractor.configure("language", Language.PYTHON);
        this.queryEngineModel = new QueryEngineModel(queryEngine);
        queryEngineModel.showComments = false;
    }
    
    public void testExtractFacts_BlankLine() throws Exception {        
        String source = "  " + EOL;
        BufferedReader reader = new BufferedReader(new StringReader(source));
        extractor.reader(reader).extract();
        assertEquals(
            "extract_source(1, '')."    + EOL, 
            extractor.getFacts(queryEngineModel));
    }   
}