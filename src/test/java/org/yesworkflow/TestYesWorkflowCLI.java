package org.yesworkflow;

/* This file is an adaptation of TestKuratorAkka.java in the org.kurator.akka
 * package as of 18Dec2014.
 */

import java.util.List;

import org.yesworkflow.util.YesWorkflowTestCase;

public class TestYesWorkflowCLI extends YesWorkflowTestCase {

    private static String EXPECTED_HELP_OUTPUT =
        ""                                                              + EOL +
        "Option                     Description                      "  + EOL +
        "------                     -----------                      "  + EOL +
        "-c, --command <command>    command to YesWorkflow           "  + EOL +
        "-d, --database <database>  path to database file for storing"  + EOL +
        "                             extracted workflow graph       "  + EOL +
        "-h, --help                 display help                     "  + EOL +
        "-s, --source <script>      path to source file to analyze   "  + EOL;
    
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
            "Usage error: Option s/source requires an argument"      + EOL +
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_Extract_DefaultExtractor_NoSourceOption() throws Exception {
        String[] args = {"-c", "extract"};
        int returnValue = new YesWorkflowCLI(stdoutStream, stderrStream).runForArgs(args);
        assertEquals(YesWorkflowCLI.YW_CLI_USAGE_ERROR, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            "Usage error: No source path provided to extractor"      + EOL +
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
    
    private static class MockExtractor implements Extractor {

        public String sourcePath = null;
        public String databasePath = null;
        public boolean extracted = false;
        
        public Extractor commentCharacter(char c) { return this; }
        public Extractor sourcePath(String path) { this.sourcePath = path; return this; }
        public Extractor databasePath(String path) { this.databasePath = path; return this; }
        public void extract() throws Exception { this.extracted = true; }
        public List<String> getLines() { return null; }
    }
}
