package org.yesworkflow;

/* This file is an adaptation of TestKuratorAkka.java in the org.kurator.akka
 * package as of 18Dec2014.
 */

import java.io.Reader;
import java.util.List;

import org.yesworkflow.LanguageModel.Language;
import org.yesworkflow.comments.Comment;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestYesWorkflowCLI extends YesWorkflowTestCase {
    
    static final String TEST_RESOURCE_DIR = "org/yesworkflow/";

    private static String EXPECTED_HELP_OUTPUT =
        ""                                                                              + EOL +
        "---------------------- YesWorkflow usage summary -----------------------"      + EOL +
        ""                                                                              + EOL +
        "Option                              Description                           "    + EOL +
        "------                              -----------                           "    + EOL +
        "-c, --command <command>             command to YesWorkflow                "    + EOL +
        "-d, --database <database>           path to database file for storing     "    + EOL +
        "                                      extracted workflow graph            "    + EOL +
        "-g, --graph [dot file]              path to graphviz dot file for storing "    + EOL +
        "                                      rendered workflow graph (default: -)"    + EOL +
        "-h, --help                          display help                          "    + EOL +
        "-l, --lines [lines file]            path to file for saving extracted     "    + EOL +
        "                                      comment lines (default: -)          "    + EOL +
        "-s, --source [script]               path to source file to analyze        "    + EOL +
        "                                      (default: -)                        "    + EOL +
        "-v, --view <process|data|combined>  view of model to render as a graph    "    + EOL +
        "                                      (default: process)                  "    + EOL +
        "-x, --commchar [comment]            comment character                     "    + EOL +
        ""                                                                              + EOL +
        "------------------------------------------------------------------------"      + EOL;

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testYesWorkflowCLI_NoArgs() throws Exception {
        String[] args = {};
        new YesWorkflowCLI(stdoutStream, stderrStream).runForArgs(args);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            ""                                                                          + EOL +
            "****************** YESWORKFLOW TOOL USAGE ERRORS ***********************"  + EOL +
            ""                                                                          + EOL +
            "ERROR: No command provided to YesWorkflow"   + EOL +
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_HelpOption() throws Exception {
        String[] args = {"--help"};
        int returnValue = new YesWorkflowCLI(stdoutStream, stderrStream).runForArgs(args);
        assertEquals(YesWorkflowCLI.YW_CLI_SUCCESS, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_HelpOption_Abbreviation() throws Exception {
        String[] args = {"-h"};
        int returnValue = new YesWorkflowCLI(stdoutStream, stderrStream).runForArgs(args);
        assertEquals(YesWorkflowCLI.YW_CLI_SUCCESS, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_CommandOption_NoArgument() throws Exception {
        String[] args = {"-c"};
        int returnValue = new YesWorkflowCLI(stdoutStream, stderrStream).runForArgs(args);
        assertEquals(YesWorkflowCLI.YW_CLI_USAGE_EXCEPTION, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            ""                                                                          + EOL +
            "****************** YESWORKFLOW TOOL USAGE ERRORS ***********************"  + EOL +
            ""                                                                          + EOL +
            "ERROR: Option c/command requires an argument"                              + EOL +
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_SourceOption_NoArgument() throws Exception {
        String[] args = {"-s"};
        int returnValue = new YesWorkflowCLI(stdoutStream, stderrStream).runForArgs(args);
        assertEquals(YesWorkflowCLI.YW_CLI_USAGE_EXCEPTION, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            ""                                                                          + EOL +
            "****************** YESWORKFLOW TOOL USAGE ERRORS ***********************"  + EOL +
            ""                                                                          + EOL +
            "ERROR: No command provided to YesWorkflow"                                 + EOL +
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_Extract_DefaultExtractor_MissingSourceFile() throws Exception {

        String[] args = {"-c", "extract", "-s", "no_such_script.py"};
        new YesWorkflowCLI(stdoutStream, stderrStream).runForArgs(args);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
                ""                                                                          + EOL +
                "****************** YESWORKFLOW TOOL USAGE ERRORS ***********************"  + EOL +
                ""                                                                          + EOL +
                "ERROR: Input source file not found: no_such_script.py"      + EOL +
                EXPECTED_HELP_OUTPUT,
                stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_Extract_InjectedExtractor_SourceOnly() throws Exception {

        String[] args = {"-c", "extract", "-s", "script.py"};
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        MockExtractor extractor = new MockExtractor();
        cli.extractor(extractor);

        assertNull(extractor.sourcePath);
        assertNull(extractor.databasePath);
        assertFalse(extractor.extracted);

        int returnValue = cli.runForArgs(args);

        assertEquals(YesWorkflowCLI.YW_CLI_SUCCESS, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals("", stderrBuffer.toString());

        assertEquals("script.py", extractor.sourcePath);
        assertNull(extractor.databasePath);
        assertTrue(extractor.extracted);
    }

    public void testYesWorkflow_CommentCharacters_Python_ExplicitOption() throws Exception{
        
        Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.extractor(extractor);
        
        cli.runForArgs(new String[] {"-c", "extract", "-x", "#", "-s", "src/main/resources/example.py"});
        
        assertNull(extractor.getLanguage());
        assertEquals('#', extractor.getCommentCharacter());
    }
    
    public void testYesWorkflow_CommentCharacters_PythonLowercase() throws Exception{
        
        Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.extractor(extractor);
    
        cli.runForArgs(new String[] {"-c", "extract", "-s", "src/main/resources/example.py"});

        assertEquals(Language.PYTHON, extractor.getLanguage());
        assertEquals('#', extractor.getCommentCharacter());
    }
    
    public void testYesWorkflow_CommentCharacters_PythonUppercase() throws Exception{
        
        Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.extractor(extractor);

        cli.runForArgs(new String[] {"-c", "extract", "-s", "src/main/resources/example.PY"});

        assertEquals(Language.PYTHON, extractor.getLanguage());
        assertEquals('#', extractor.getCommentCharacter());
    }
    
    public void testYesWorkflow_CommentCharacters_RUppercase() throws Exception{
        
        Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.extractor(extractor);

        cli.runForArgs(new String[] {"-c", "extract", "-s", "incoming/drain_dem.R"});

        assertEquals(Language.R, extractor.getLanguage());
        assertEquals('#', extractor.getCommentCharacter());
    }
    
    public void testYesWorkflow_CommentCharacters_JavaLowercase() throws Exception{
        
        Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);

        cli.extractor(extractor);

        cli.runForArgs(new String[] {"-c", "extract", "-s", "src/test/resources/testJavaScript.java"});
        assertEquals(Language.JAVA, extractor.getLanguage());
        assertEquals('/', extractor.getCommentCharacter());
    }
    
    
    public void testYesWorkflow_CommentCharacters_MatlabLowercase() throws Exception{
        
        Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.extractor(extractor);

        cli.runForArgs(new String[] {"-c", "extract", "-s", "src/test/resources/testMatlab.m"});
        
        assertEquals(Language.MATLAB, extractor.getLanguage());
        assertEquals('%', extractor.getCommentCharacter());
    }

    public void testYesWorkflow_CommentCharacters_MatlabUpperCase() throws Exception{
        
        Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        
        cli.extractor(extractor);

        cli.runForArgs(new String[] {"-c", "extract", "-s", "src/test/resources/testMatlab.M"});
        assertEquals(Language.MATLAB, extractor.getLanguage());
        assertEquals('%', extractor.getCommentCharacter());
    }
    
    public void testYesWorkflow_CommentCharacters_MatlabLowercase_ExplicitOption() throws Exception{
        
        Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.extractor(extractor);
    
        cli.runForArgs(new String[] {"-c", "extract", "-x", "%", "-s", "src/test/resources/testMatlab.m"});

        assertNull(extractor.getLanguage());
        assertEquals('%', extractor.getCommentCharacter());
    }

    public void testYesWorkflow_CommentCharacters_NoExtension() throws Exception{
        
        Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.extractor(extractor);

        cli.runForArgs(new String[] {"-c", "extract", "-s", "src/test/resources/testfileNoExtension"});
        
        assertNull(extractor.getLanguage());
        assertEquals('#', extractor.getCommentCharacter());
    }
    
//    public void testYesWorkflowCLI_Graph() throws Exception {
//
//        String[] args = {
//                "-c", "graph",
//                "-s", "src/main/resources/example.py"
//        };
//
//        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
//
//        int returnValue = cli.runForArgs(args);
//
//    }

    public void testYesWorkflowCLI_Extract_InjectedExtractor_SourceAndDatabase() throws Exception {

        String[] args = {"-c", "extract", "-s", "script.py", "-d", "wf.tdb"};
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        MockExtractor extractor = new MockExtractor();
        cli.extractor(extractor);

        assertNull(extractor.sourcePath);
        assertNull(extractor.databasePath);
        assertFalse(extractor.extracted);

        int returnValue = cli.runForArgs(args);

        assertEquals(YesWorkflowCLI.YW_CLI_SUCCESS, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals("", stderrBuffer.toString());

        assertEquals("script.py", extractor.sourcePath);
        assertEquals("wf.tdb", extractor.databasePath);
        assertTrue(extractor.extracted);
    }

    public void testYesWorkflowCLI_Extract_ExamplePy_OutputLines() throws Exception {

        String[] args = {"-c", "extract", "-s", "src/main/resources/example.py", "-l"};
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.runForArgs(args);

        assertEquals(
            "@begin main"                                                   + EOL +
            "@in LandWaterMask_Global_CRUNCEP.nc @as input_mask_file"       + EOL +
            "@in NEE_first_year.nc @as input_data_file"                     + EOL +
            "@out result_simple.pdf @as result_NEE_pdf"                     + EOL +
            "@begin fetch_mask"                                             + EOL +
            "@in \"LandWaterMask_Global_CRUNCEP.nc\" @as input_mask_file"   + EOL +
            "@out mask @as land_water_mask"                                 + EOL +
            "@end fetch_mask"                                               + EOL +
            "@begin load_data"                                              + EOL +
            "@in \"CLM4_BG1_V1_Monthly_NEE.nc4\" @as input_data_file"       + EOL +
            "@out data @as NEE_data"                                        + EOL +
            "@end load_data"                                                + EOL +
            "@begin standardize_with_mask"                                  + EOL +
            "@in data @as NEE_data"                                         + EOL +
            "@in mask @as land_water_mask"                                  + EOL +
            "@out data @as standardized_NEE_data"                           + EOL +
            "@end standardize_mask"                                         + EOL +
            "@begin simple_diagnose"                                        + EOL +
            "@in np @as standardized_NEE_data"                              + EOL +
            "@out pp @as result_NEE_pdf"                                    + EOL +
            "@end simple_diagnose"                                          + EOL +
            "@end main"                                                     + EOL,
            stdoutBuffer.toString());
    }

    public void testYesWorkflowCLI_ExamplePy_OutputLines_WithCommentChar() throws Exception{
    	String[] args = {"-c", "extract", "-x", "#", "-s", "src/main/resources/example.py", "-l"};
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.runForArgs(args);

        assertEquals(
            "@begin main"                                                   + EOL +
            "@in LandWaterMask_Global_CRUNCEP.nc @as input_mask_file"       + EOL +
            "@in NEE_first_year.nc @as input_data_file"                     + EOL +
            "@out result_simple.pdf @as result_NEE_pdf"                     + EOL +
            "@begin fetch_mask"                                             + EOL +
            "@in \"LandWaterMask_Global_CRUNCEP.nc\" @as input_mask_file"   + EOL +
            "@out mask @as land_water_mask"                                 + EOL +
            "@end fetch_mask"                                               + EOL +
            "@begin load_data"                                              + EOL +
            "@in \"CLM4_BG1_V1_Monthly_NEE.nc4\" @as input_data_file"       + EOL +
            "@out data @as NEE_data"                                        + EOL +
            "@end load_data"                                                + EOL +
            "@begin standardize_with_mask"                                  + EOL +
            "@in data @as NEE_data"                                         + EOL +
            "@in mask @as land_water_mask"                                  + EOL +
            "@out data @as standardized_NEE_data"                           + EOL +
            "@end standardize_mask"                                         + EOL +
            "@begin simple_diagnose"                                        + EOL +
            "@in np @as standardized_NEE_data"                              + EOL +
            "@out pp @as result_NEE_pdf"                                    + EOL +
            "@end simple_diagnose"                                          + EOL +
            "@end main"                                                     + EOL,
            stdoutBuffer.toString());
    }

    public void testYesWorkflowCLI_Graph_ExamplePy_ProcessGraph() throws Exception {

        String[] args = {"-c", "graph", "-s", "src/main/resources/example.py", "-g"};
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.runForArgs(args);

        assertEquals(
            readTextFileOnClasspath(TEST_RESOURCE_DIR + "testYesWorkflowCLI_Graph_ExamplePy_ProcessGraph.gv"),
            stdoutBuffer.toString()
         );
    }

    private static class MockExtractor implements Extractor {

        public String sourcePath = null;
        public String databasePath = null;
        public boolean extracted = false;

        public Extractor languageModel(LanguageModel language) { return this; };
		public Extractor commentCharacter(char c) { return this; }
		public Extractor sourceReader(Reader reader) { return this; }
		public Extractor sourcePath(String path) { this.sourcePath = path; return this; }
		public Extractor databasePath(String path) { this.databasePath = path; return this; }
		public List<String> getLines() { return null; }
		public List<Comment> getComments() { return null; }
		public char getCommentCharacter() { return 0; }
        public Extractor extract() throws Exception { this.extracted = true; return null; }
        public Language getLanguage() { return null; }
    }
}
