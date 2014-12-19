package org.yesworkflow;

/* This file is an adaptation of TestKuratorAkka.java in the org.kurator.akka
 * package as of 18Dec2014.
 */

import org.yesworkflow.util.YesWorkflowTestCase;

public class TestYesWorkflowCLI extends YesWorkflowTestCase {

    private static String EXPECTED_HELP_OUTPUT =
        ""                                                      + EOL +
        "Option                   Description              "    + EOL +
        "------                   -----------              "    + EOL +
        "-c, --command <command>  command to YesWorkflow   "    + EOL +
        "-f, --file <definition>  path to script to analyze"    + EOL +
        "-h, --help               display help             "    + EOL;
    
    @Override
    public void setUp() {
        super.setUp();
    }
    
    public void testYesWorkflowCLI_NoArgs() throws Exception {
        String[] args = {};
        YesWorkflowCLI.runForArgs(args, stdoutStream, stderrStream);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            "Error: No command provided to YesWorkflow"   + EOL +
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_HelpOption() throws Exception {
        String[] args = {"--help"};
        YesWorkflowCLI.runForArgs(args, stdoutStream, stderrStream);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_HelpOption_Abbreviation() throws Exception {
        String[] args = {"-h"};
        YesWorkflowCLI.runForArgs(args, stdoutStream, stderrStream);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_CommandOption_NoArgument() throws Exception {
        String[] args = {"-c"};
        YesWorkflowCLI.runForArgs(args, stdoutStream, stderrStream);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            "Error: Unable to parse command-line options"   + EOL +
            "Option c/command requires an argument"            + EOL +
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }
    
    public void testYesWorkflowCLI_FileOption_NoArgument() throws Exception {
        String[] args = {"-f"};
        YesWorkflowCLI.runForArgs(args, stdoutStream, stderrStream);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            "Error: Unable to parse command-line options"   + EOL +
            "Option f/file requires an argument"            + EOL +
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

}
