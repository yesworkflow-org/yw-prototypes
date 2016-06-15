package org.yesworkflow.cli;

/* This file is an adaptation of TestKuratorAkka.java in the org.kurator.akka
 * package as of 18Dec2014.
 */

import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.yesworkflow.Language;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.cli.ExitCode;
import org.yesworkflow.cli.YesWorkflowCLI;
import org.yesworkflow.config.YWConfiguration;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.query.QueryEngine;
import org.yesworkflow.YesWorkflowTestCase;

public class TestYesWorkflowCLI extends YesWorkflowTestCase {
    
    static final String TEST_RESOURCE_DIR = "src/test/resources/org/yesworkflow/testYesWorkflowCLI/";

    private YesWorkflowDB ywdb;
    
    private static String EXPECTED_HELP_OUTPUT =
            ""                                                                      + EOL +
            YesWorkflowCLI.YW_CLI_USAGE_HELP                                        + EOL +
            YesWorkflowCLI.YW_CLI_COMMAND_HELP                                      + EOL +
            "Option                     Description                         "       + EOL +
            "------                     -----------                         "       + EOL +
            "-c, --config <name=value>  Assign value to configuration option"       + EOL +
            "-h, --help                 Display this help                   "       + EOL +
            ""                                                                      + EOL +
             YesWorkflowCLI.YW_CLI_CONFIG_HELP                                      + EOL +
             YesWorkflowCLI.YW_CLI_EXAMPLES_HELP                                    + EOL;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.ywdb = YesWorkflowDB.createInMemoryDB();
    }

    public void testYesWorkflowCLI_NoArgs() throws Exception {
        String[] args = {};
        new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream).runForArgs(args);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            ""                                                                          + EOL +
            "ERROR: Command must be first non-option argument to YesWorkflow"           + EOL +
            ""                                                                          + EOL +
            "Use the -h option to display help for the YW command-line interface."      + EOL,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_HelpOption() throws Exception {
        String[] args = {"--help"};
        ExitCode returnValue = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream).runForArgs(args);
        assertEquals(ExitCode.SUCCESS, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_HelpOption_Abbreviation() throws Exception {
        String[] args = {"-h"};
        ExitCode returnValue = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream).runForArgs(args);
        assertEquals(ExitCode.SUCCESS, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_NoArgument() throws Exception {
        String[] args = new String[]{};
        ExitCode returnValue = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream).runForArgs(args);
        assertEquals(ExitCode.CLI_USAGE_ERROR, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            ""                                                                          + EOL +
            "ERROR: Command must be first non-option argument to YesWorkflow"           + EOL +
            ""                                                                          + EOL +
            "Use the -h option to display help for the YW command-line interface."      + EOL,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_NoCommand() throws Exception {
        String[] args = new String[]{"example.py"};
        ExitCode returnValue = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream).runForArgs(args);
        assertEquals(ExitCode.CLI_USAGE_ERROR, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            ""                                                                          + EOL +
            "ERROR: Unrecognized YW command: example.py"                                + EOL +
            ""                                                                          + EOL +
            "Use the -h option to display help for the YW command-line interface."      + EOL,
            stderrBuffer.toString());
    }

    
    public void testYesWorkflowCLI_Extract_DefaultExtractor_MissingSourceFile() throws Exception {

        String[] args = {"extract", "no_such_script.py"};
        new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream).runForArgs(args);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
                ""                                                                      + EOL +
                "ERROR: Input file not found: no_such_script.py"                        + EOL +
                ""                                                                      + EOL +
                "Use the -h option to display help for the YW command-line interface."  + EOL,
                stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_SingleConfigureOption_TopLevel() throws Exception {

        String[] args = { "noop", "-c", "conf0=val0"};
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
        
        ExitCode returnValue = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream)
                                   .config(config)
                                   .runForArgs(args);

        assertEquals(ExitCode.SUCCESS, returnValue);
        assertEquals(1, config.settingCount());
        assertEquals("val0", config.get("conf0"));
    }

    @SuppressWarnings("unchecked")
    public void testYesWorkflowCLI_SingleConfigureOption_TwoLevels() throws Exception {

        String[] args = { "noop", "-c", "table1.conf1=val1"};
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
        
        ExitCode returnValue = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream)
                                   .config(config)
                                   .runForArgs(args);

        assertEquals(ExitCode.SUCCESS, returnValue);
        assertEquals(1, config.settingCount());
        Map<String,Object> table1 = (Map<String,Object>)(config.get("table1"));
        assertEquals(1, table1.size());
        String value1 = (String)(table1.get("conf1"));
        assertEquals("val1", value1);
    }

    @SuppressWarnings("unchecked")
    public void testYesWorkflowCLI_SingleConfigureOption_ThreeLevels() throws Exception {

        String[] args = { "noop", "-c", "table1.table2.conf2=val2"};
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
        
        ExitCode returnValue = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream)
                                   .config(config)
                                   .runForArgs(args);

        assertEquals(ExitCode.SUCCESS, returnValue);
        assertEquals(1, config.settingCount());
        Map<String,Object> table1 = (Map<String,Object>)(config.get("table1"));
        assertEquals(1, table1.size());
        Map<String,Object> table2 = (Map<String,Object>)(table1.get("table2"));
        assertEquals(1, table2.size());
        String value2 = (String)(table2.get("conf2"));
        assertEquals("val2", value2);
    }
    
    @SuppressWarnings("unchecked")
    public void testYesWorkflowCLI_ThreeConfigureOptions() throws Exception {

        String[] args = {
                "noop",
                "-c", "conf0=val0",
                "-c", "table1.conf1=val1",
                "-c", "table1.table2.conf2=val2"};
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
        
        ExitCode returnValue = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream)
                                   .config(config)
                                   .runForArgs(args);

        assertEquals(ExitCode.SUCCESS, returnValue);

        assertEquals(3, config.settingCount());        
        assertEquals("val0", config.get("conf0"));

        Map<String,Object> table1 = (Map<String,Object>)(config.get("table1"));
        assertEquals(2, table1.size());
        String value1 = (String)(table1.get("conf1"));
        assertEquals("val1", value1);
    
        Map<String,Object> table2 = (Map<String,Object>)(table1.get("table2"));
        assertEquals(1, table2.size());
        String value2 = (String)(table2.get("conf2"));
        assertEquals("val2", value2);
    }    

    
    public void testYesWorkflowCLI_Extract_InjectedExtractor_SourceOnly() throws Exception {

        String[] args = {"extract", TEST_RESOURCE_DIR + "pythonFileLowercase.py"};
        YesWorkflowCLI cli = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream);
        MockExtractor extractor = new MockExtractor();
        cli.extractor(extractor);

        assertFalse(extractor.extracted);

        ExitCode returnValue = cli.runForArgs(args);

        assertEquals(ExitCode.SUCCESS, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals("", stderrBuffer.toString());

        assertTrue(extractor.extracted);
    }

    public void testYesWorkflow_CommentCharacters_Python_ExplicitOption() throws Exception{
        
    	Extractor extractor = new DefaultExtractor(this.ywdb, stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream);
        cli.extractor(extractor);        
        cli.runForArgs(new String[] {"extract", "-c", "extract.comment=#", TEST_RESOURCE_DIR + "pythonFileLowercase.py"});        
        assertEquals(Language.GENERIC, extractor.getLanguage());
    }
    
    
    public void testYesWorkflow_CommentCharacters_PythonLowercase() throws Exception{
        
    	Extractor extractor = new DefaultExtractor(this.ywdb, stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream);
        cli.extractor(extractor);
        cli.runForArgs(new String[] {"extract", TEST_RESOURCE_DIR + "pythonFileLowercase.py"});
        assertEquals(Language.PYTHON, extractor.getLanguage());
    }
    
    public void testYesWorkflow_CommentCharacters_PythonUppercase() throws Exception{
        
    	Extractor extractor = new DefaultExtractor(this.ywdb, stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream);
        cli.extractor(extractor);
        cli.runForArgs(new String[] {"extract", TEST_RESOURCE_DIR + "pythonFileUppercase.PY"});
        assertEquals(Language.PYTHON, extractor.getLanguage());
    }
    
    public void testYesWorkflow_CommentCharacters_RUppercase() throws Exception{
    	Extractor extractor = new DefaultExtractor(this.ywdb, stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream);
        cli.extractor(extractor);
        cli.runForArgs(new String[] {"extract", TEST_RESOURCE_DIR + "rFileUppercaseExtension.R"});
        assertEquals(Language.R, extractor.getLanguage());
    }
    
    public void testYesWorkflow_CommentCharacters_JavaLowercase() throws Exception{
        
    	Extractor extractor = new DefaultExtractor(this.ywdb, stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream);

        cli.extractor(extractor);

        cli.runForArgs(new String[] {"extract", TEST_RESOURCE_DIR + "javaFile.java"});
        assertEquals(Language.JAVA, extractor.getLanguage());
    }
    
    
    public void testYesWorkflow_CommentCharacters_MatlabLowercase() throws Exception{
    	Extractor extractor = new DefaultExtractor(this.ywdb, stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream);
        cli.extractor(extractor);
        cli.runForArgs(new String[] {"extract", TEST_RESOURCE_DIR + "matlabFileLowercaseExtension.m"});
        assertEquals(Language.MATLAB, extractor.getLanguage());
    }

    public void testYesWorkflow_CommentCharacters_MatlabUpperCase() throws Exception{
        
    	Extractor extractor = new DefaultExtractor(this.ywdb, stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream);
        
        cli.extractor(extractor);

        cli.runForArgs(new String[] {"extract", TEST_RESOURCE_DIR + "matlabFileUppercaseExtension.M"});
        assertEquals(Language.MATLAB, extractor.getLanguage());
    }
    
    public void testYesWorkflow_CommentCharacters_MatlabLowercase_ExplicitOption() throws Exception{
        
    	Extractor extractor = new DefaultExtractor(this.ywdb, stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream);
        cli.extractor(extractor);
    
        cli.runForArgs(new String[] {"extract", "-c", "extract.comment=%", TEST_RESOURCE_DIR + "matlabFileLowercaseExtension.m"});
        
        assertEquals(Language.GENERIC, extractor.getLanguage());
    }

    public void testYesWorkflow_CommentCharacters_NoExtension() throws Exception{
        
    	Extractor extractor = new DefaultExtractor(this.ywdb, stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream);
        cli.extractor(extractor);
        cli.runForArgs(new String[] {"extract", TEST_RESOURCE_DIR + "extensionlessSource"});        
        assertEquals(Language.GENERIC, extractor.getLanguage());
    }

    public void testYesWorkflowCLI_Extract_InjectedExtractor_SourceOption() throws Exception {

        String[] args = {"extract", "src/test/resources/simpleExample.py"};
        YesWorkflowCLI cli = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream);
        MockExtractor extractor = new MockExtractor();
        cli.extractor(extractor);

        assertFalse(extractor.extracted);

        ExitCode returnValue = cli.runForArgs(args);

        assertEquals(ExitCode.SUCCESS, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals("", stderrBuffer.toString());

        assertTrue(extractor.extracted);
    }

    public void testYesWorkflowCLI_Extract_ExamplePy_OutputLines() throws Exception {

        String[] args = {"extract", "src/main/resources/example.py", 
                         "-c", "extract.listfile"};
        YesWorkflowCLI cli = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream);
        cli.runForArgs(args);

        assertEquals(
                "@BEGIN main"                                                                                       + EOL +
                "@PARAM db_pth"                                                                                     + EOL +
                "@PARAM fmodel"                                                                                     + EOL +
                "@IN input_mask_file  @URI file:{db_pth}/land_water_mask/LandWaterMask_Global_CRUNCEP.nc"           + EOL +
                "@IN input_data_file  @URI file:{db_pth}/NEE_first_year.nc"                                         + EOL +
                "@OUT result_NEE_pdf  @URI file:result_NEE.pdf"                                                     + EOL +
                "@BEGIN fetch_mask"                                                                                 + EOL +
                "@PARAM db_pth"                                                                                     + EOL +
                "@IN g  @AS input_mask_file  @URI file:{db_pth}/land_water_mask/LandWaterMask_Global_CRUNCEP.nc"    + EOL +
                "@OUT mask  @AS land_water_mask"                                                                    + EOL +
                "@END fetch_mask"                                                                                   + EOL +
                "@BEGIN load_data"                                                                                  + EOL +
                "@PARAM db_pth"                                                                                     + EOL +
                "@IN input_data_file  @URI file:{db_pth}/NEE_first_year.nc"                                         + EOL +
                "@OUT data  @AS NEE_data"                                                                           + EOL +
                "@END load_data"                                                                                    + EOL +
                "@BEGIN standardize_with_mask"                                                                      + EOL +
                "@IN data @AS NEE_data"                                                                             + EOL +
                "@IN mask @AS land_water_mask"                                                                      + EOL +
                "@OUT data @AS standardized_NEE_data"                                                               + EOL +
                "@END standardize_with_mask"                                                                        + EOL +
                "@BEGIN simple_diagnose"                                                                            + EOL +
                "@PARAM fmodel"                                                                                     + EOL +
                "@IN data @AS standardized_NEE_data"                                                                + EOL +
                "@OUT pp  @AS result_NEE_pdf  @URI file:result_NEE.pdf"                                             + EOL +
                "@END simple_diagnose"                                                                              + EOL +
                "@END main"                                                                                         + EOL,
            stdoutBuffer.toString());
    }

    public void testYesWorkflowCLI_ExamplePy_OutputLines_WithCommentChar() throws Exception{
    	String[] args = {"extract", "-c", "extract.comment=#", "src/main/resources/example.py", 
    	                 "-c", "extract.listfile"};
        YesWorkflowCLI cli = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream);
        cli.runForArgs(args);

        assertEquals(
            "@BEGIN main"                                                                                       + EOL +
            "@PARAM db_pth"                                                                                     + EOL +
            "@PARAM fmodel"                                                                                     + EOL +
            "@IN input_mask_file  @URI file:{db_pth}/land_water_mask/LandWaterMask_Global_CRUNCEP.nc"           + EOL +
            "@IN input_data_file  @URI file:{db_pth}/NEE_first_year.nc"                                         + EOL +
            "@OUT result_NEE_pdf  @URI file:result_NEE.pdf"                                                     + EOL +
            "@BEGIN fetch_mask"                                                                                 + EOL +
            "@PARAM db_pth"                                                                                     + EOL +
            "@IN g  @AS input_mask_file  @URI file:{db_pth}/land_water_mask/LandWaterMask_Global_CRUNCEP.nc"    + EOL +
            "@OUT mask  @AS land_water_mask"                                                                    + EOL +
            "@END fetch_mask"                                                                                   + EOL +
            "@BEGIN load_data"                                                                                  + EOL +
            "@PARAM db_pth"                                                                                     + EOL +
            "@IN input_data_file  @URI file:{db_pth}/NEE_first_year.nc"                                         + EOL +
            "@OUT data  @AS NEE_data"                                                                           + EOL +
            "@END load_data"                                                                                    + EOL +
            "@BEGIN standardize_with_mask"                                                                      + EOL +
            "@IN data @AS NEE_data"                                                                             + EOL +
            "@IN mask @AS land_water_mask"                                                                      + EOL +
            "@OUT data @AS standardized_NEE_data"                                                               + EOL +
            "@END standardize_with_mask"                                                                        + EOL +
            "@BEGIN simple_diagnose"                                                                            + EOL +
            "@PARAM fmodel"                                                                                     + EOL +
            "@IN data @AS standardized_NEE_data"                                                                + EOL +
            "@OUT pp  @AS result_NEE_pdf  @URI file:result_NEE.pdf"                                             + EOL +
            "@END simple_diagnose"                                                                              + EOL +
            "@END main"                                                                                         + EOL,
            stdoutBuffer.toString());
    }

    public void testYesWorkflowCLI_Graph_ExamplePy_ProcessGraph() throws Exception {

        String[] args = {"graph", "src/main/resources/example.py", 
                         "-c", "graph.params=hide",
                         "-c", "graph.dotcomments=on",
                         "-c", "graph.portlayout=relax",
                         "-c", "graph.titleposition=hide"};
        YesWorkflowCLI cli = new YesWorkflowCLI(this.ywdb, stdoutStream, stderrStream);
        cli.runForArgs(args);

        assertEquals(
            readTextFile(TEST_RESOURCE_DIR + "graph_ExamplePy_ProcessGraph.gv"),
            stdoutBuffer.toString()
         );
    }

    private static class MockExtractor implements Extractor {

        public boolean extracted = false;
 
        @Override public Extractor extract() throws Exception { this.extracted = true; return this; }
        @Override public Language getLanguage() { return null; }
		@Override public List<Annotation> getAnnotations() { return null; }
        @Override public Extractor configure(Map<String, Object> config) throws Exception { return this; }
        @Override public Extractor configure(String key, Object value) throws Exception { return this; }
        @Override public Extractor reader(Reader reader) { return this; }
        @Override public String getFacts() { return null; }
        @Override public String getSkeleton() { return null; }
        public Extractor queryEngine(QueryEngine ge) { return null; }
    }
}
