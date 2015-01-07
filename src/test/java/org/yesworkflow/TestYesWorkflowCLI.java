package org.yesworkflow;

/* This file is an adaptation of TestKuratorAkka.java in the org.kurator.akka
 * package as of 18Dec2014.
 */

import java.io.Reader;
import java.util.List;

import org.yesworkflow.comments.Comment;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.model.Program;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestYesWorkflowCLI extends YesWorkflowTestCase {

    private static String EXPECTED_HELP_OUTPUT =
        ""                                                                      + EOL +
        "Option                     Description                           "     + EOL +
        "------                     -----------                           "     + EOL +
        "-c, --command <command>    command to YesWorkflow                "     + EOL +
        "-d, --database <database>  path to database file for storing     "     + EOL +
        "                             extracted workflow graph            "     + EOL +
        "-g, --graph [dot file]     path to graphviz dot file for storing "     + EOL +
        "                             rendered workflow graph (default: -)"     + EOL +
        "-h, --help                 display help                          "     + EOL +
        "-l, --lines [lines file]   path to file for saving extracted     "     + EOL +
        "                             comment lines (default: -)          "     + EOL +
        "-s, --source [script]      path to source file to analyze        "     + EOL +
        "                             (default: -)                        "     + EOL +
        "-x, --commchar [comment]   comment character                     "     + EOL;

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testYesWorkflowCLI_NoArgs() throws Exception {
        String[] args = {};
        new YesWorkflowCLI(stdoutStream, stderrStream).runForArgs(args);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            "Usage error: No command provided to YesWorkflow"   + EOL +
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
        assertEquals(YesWorkflowCLI.YW_CLI_USAGE_ERROR, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            "Usage error: Option c/command requires an argument"     + EOL +
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_SourceOption_NoArgument() throws Exception {
        String[] args = {"-s"};
        int returnValue = new YesWorkflowCLI(stdoutStream, stderrStream).runForArgs(args);
        assertEquals(YesWorkflowCLI.YW_CLI_USAGE_ERROR, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            "Usage error: No command provided to YesWorkflow"      + EOL +
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_Extract_DefaultExtractor_MissingSourceFile() throws Exception {

        String[] args = {"-c", "extract", "-s", "no_such_script.py"};
        new YesWorkflowCLI(stdoutStream, stderrStream).runForArgs(args);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
                "Usage error: Input source file not found: no_such_script.py"      + EOL +
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

    public void testYesWorkflow_CommentCharacters() throws Exception{

    	String[] args1 = {"-c", "extract", "-x", "#", "-s", "src/main/resources/example.py"};
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.runForArgs(args1);
        assertEquals('#', cli.getExtractor().getCommentCharacter());

        String[] args2 = {"-c", "extract", "-s", "src/main/resources/example.py"};
        cli.runForArgs(args2);
        assertEquals('#', cli.getExtractor().getCommentCharacter());

        String[] args3 = {"-c", "extract", "-s", "src/main/resources/example.PY"};
        cli.runForArgs(args3);
        assertEquals('#', cli.getExtractor().getCommentCharacter());

        String[] args4 = {"-c", "extract", "-s", "incoming/drain_dem.R"};
        cli.runForArgs(args4);
        assertEquals('#', cli.getExtractor().getCommentCharacter());

        String[] args5 = {"-c", "extract", "-s", "incoming/tesetJavaScript.java"};
        cli.runForArgs(args5);
        assertEquals('/', cli.getExtractor().getCommentCharacter());

        String[] args6 = {"-c", "extract", "-s", "incoming/tesetMatlab.m"};
        cli.runForArgs(args6);
        assertEquals('%', cli.getExtractor().getCommentCharacter());

        String[] args7 = {"-c", "extract", "-s", "incoming/tesetMatlab.M"};
        cli.runForArgs(args7);
        assertEquals('%', cli.getExtractor().getCommentCharacter());

        String[] args8 = {"-c", "extract", "-x", "%", "-s", "incoming/tesetMatlab.m"};
        cli.runForArgs(args8);
        assertEquals('%', cli.getExtractor().getCommentCharacter());

        String[] args9 = {"-c", "extract", "-s", "incoming/tesetfileNoExtension"}; // default '#' when file extension is not available
        cli.runForArgs(args9);
        assertEquals('#', cli.getExtractor().getCommentCharacter());
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

    public void testYesWorkflowCLI_Graph_ExamplePy_OutputGraph() throws Exception {

        String[] args = {"-c", "graph", "-s", "src/main/resources/example.py", "-g"};
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.runForArgs(args);

        assertEquals(
            "digraph Workflow {"                                                                + EOL +
            "rankdir=LR"                                                                        + EOL +
            "node[shape=box style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1]"              + EOL +
            "node1 [label=\"fetch_mask\"];"                                                     + EOL +
            "node2 [label=\"load_data\"];"                                                      + EOL +
            "node3 [label=\"standardize_with_mask\"];"                                          + EOL +
            "node4 [label=\"simple_diagnose\"];"                                                + EOL +
            "node[shape=circle style=\"filled\" fillcolor=\"#FFFFFF\" peripheries=1 width=0.1]" + EOL +
            "node5 [label=\"\"];"                                                               + EOL +
            "node6 [label=\"\"];"                                                               + EOL +
            "node7 [label=\"\"];"                                                               + EOL +
            "node4 -> node7 [label=\"result_NEE_pdf\"];"                                        + EOL +
            "node5 -> node1 [label=\"input_mask_file\"];"                                       + EOL +
            "node6 -> node2 [label=\"input_data_file\"];"                                       + EOL +
            "node2 -> node3 [label=\"NEE_data\"];"        						                + EOL +
            "node1 -> node3 [label=\"land_water_mask\"];"        				                + EOL +
            "node3 -> node4 [label=\"standardized_NEE_data\"];"                                 + EOL +
            "}"                                                                                 + EOL,
            stdoutBuffer.toString());
    }

    private static class MockExtractor implements Extractor {

        public String sourcePath = null;
        public String databasePath = null;
        public boolean extracted = false;

        @Override
		public Extractor commentCharacter(char c) { return this; }
		@Override
		public Extractor sourceReader(Reader reader) { return null; }
        @Override
		public Extractor sourcePath(String path) { this.sourcePath = path; return this; }
        @Override
		public Extractor databasePath(String path) { this.databasePath = path; return this; }
        @Override
		public void extract() throws Exception { this.extracted = true; }
        @Override
		public List<String> getLines() { return null; }
        @Override
		public List<Comment> getComments() { return null; }
		@Override
		public Program getProgram() { return null; }
		@Override
		public char getCommentCharacter() { return 0; }
    }
}
