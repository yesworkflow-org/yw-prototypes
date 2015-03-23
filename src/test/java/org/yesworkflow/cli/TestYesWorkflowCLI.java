package org.yesworkflow.cli;

/* This file is an adaptation of TestKuratorAkka.java in the org.kurator.akka
 * package as of 18Dec2014.
 */


import java.io.Reader;
import java.util.List;

import org.yesworkflow.LanguageModel;
import org.yesworkflow.LanguageModel.Language;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.cli.ExitCode;
import org.yesworkflow.cli.YesWorkflowCLI;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.YesWorkflowTestCase;

public class TestYesWorkflowCLI extends YesWorkflowTestCase {
    
    static final String TEST_RESOURCE_DIR = "src/test/resources/org/yesworkflow/testYesWorkflowCLI/";

    private static String EXPECTED_HELP_OUTPUT =
        ""                                                                              + EOL +
        "---------------------- YesWorkflow usage summary -----------------------"      + EOL +
        ""                                                                              + EOL +
        "Option                              Description                           "    + EOL +
        "------                              -----------                           "    + EOL +
        "-c, --command <command>             command to YesWorkflow                "    + EOL +
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
        ExitCode returnValue = new YesWorkflowCLI(stdoutStream, stderrStream).runForArgs(args);
        assertEquals(ExitCode.SUCCESS, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_HelpOption_Abbreviation() throws Exception {
        String[] args = {"-h"};
        ExitCode returnValue = new YesWorkflowCLI(stdoutStream, stderrStream).runForArgs(args);
        assertEquals(ExitCode.SUCCESS, returnValue);
        assertEquals("", stdoutBuffer.toString());
        assertEquals(
            EXPECTED_HELP_OUTPUT,
            stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_CommandOption_NoArgument() throws Exception {
        String[] args = {"-c"};
        ExitCode returnValue = new YesWorkflowCLI(stdoutStream, stderrStream).runForArgs(args);
        assertEquals(ExitCode.CLI_USAGE_ERROR, returnValue);
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
        ExitCode returnValue = new YesWorkflowCLI(stdoutStream, stderrStream).runForArgs(args);
        assertEquals(ExitCode.CLI_USAGE_ERROR, returnValue);
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
                "ERROR: Input file not found: no_such_script.py"      + EOL +
                EXPECTED_HELP_OUTPUT,
                stderrBuffer.toString());
    }

    public void testYesWorkflowCLI_Extract_InjectedExtractor_SourceOnly() throws Exception {

        String[] args = {"-c", "extract", "-s", TEST_RESOURCE_DIR + "pythonFileLowercase.py"};
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
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
        
    	Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.extractor(extractor);        
        cli.runForArgs(new String[] {"-c", "extract", "-x", "#", "-s", TEST_RESOURCE_DIR + "pythonFileLowercase.py"});        
        assertEquals(Language.GENERIC, extractor.getLanguage());
    }
    
    
    public void testYesWorkflow_CommentCharacters_PythonLowercase() throws Exception{
        
    	Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.extractor(extractor);
        cli.runForArgs(new String[] {"-c", "extract", "-s", TEST_RESOURCE_DIR + "pythonFileLowercase.py"});
        assertEquals(Language.PYTHON, extractor.getLanguage());
    }
    
    public void testYesWorkflow_CommentCharacters_PythonUppercase() throws Exception{
        
    	Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.extractor(extractor);
        cli.runForArgs(new String[] {"-c", "extract", "-s", TEST_RESOURCE_DIR + "pythonFileUppercase.PY"});
        assertEquals(Language.PYTHON, extractor.getLanguage());
    }
    
    public void testYesWorkflow_CommentCharacters_RUppercase() throws Exception{
    	Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.extractor(extractor);
        cli.runForArgs(new String[] {"-c", "extract", "-s", TEST_RESOURCE_DIR + "rFileUppercaseExtension.R"});
        assertEquals(Language.R, extractor.getLanguage());
    }
    
    public void testYesWorkflow_CommentCharacters_JavaLowercase() throws Exception{
        
    	Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);

        cli.extractor(extractor);

        cli.runForArgs(new String[] {"-c", "extract", "-s", TEST_RESOURCE_DIR + "javaFile.java"});
        assertEquals(Language.JAVA, extractor.getLanguage());
    }
    
    
    public void testYesWorkflow_CommentCharacters_MatlabLowercase() throws Exception{
    	Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.extractor(extractor);
        cli.runForArgs(new String[] {"-c", "extract", "-s", TEST_RESOURCE_DIR + "matlabFileLowercaseExtension.m"});
        assertEquals(Language.MATLAB, extractor.getLanguage());
    }

    public void testYesWorkflow_CommentCharacters_MatlabUpperCase() throws Exception{
        
    	Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        
        cli.extractor(extractor);

        cli.runForArgs(new String[] {"-c", "extract", "-s", TEST_RESOURCE_DIR + "matlabFileUppercaseExtension.M"});
        assertEquals(Language.MATLAB, extractor.getLanguage());
    }
    
    public void testYesWorkflow_CommentCharacters_MatlabLowercase_ExplicitOption() throws Exception{
        
    	Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.extractor(extractor);
    
        cli.runForArgs(new String[] {"-c", "extract", "-x", "%", "-s", TEST_RESOURCE_DIR + "matlabFileLowercaseExtension.m"});
        
        assertEquals(Language.GENERIC, extractor.getLanguage());
    }

    public void testYesWorkflow_CommentCharacters_NoExtension() throws Exception{
        
    	Extractor extractor = new DefaultExtractor(stderrStream, stderrStream);
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
        cli.extractor(extractor);
        cli.runForArgs(new String[] {"-c", "extract", "-s", TEST_RESOURCE_DIR + "extensionlessSource"});        
        assertEquals(Language.GENERIC, extractor.getLanguage());
    }

    public void testYesWorkflowCLI_Extract_InjectedExtractor_SourceOption() throws Exception {

        String[] args = {"-c", "extract", "-s", "src/test/resources/simpleExample.py"};
        YesWorkflowCLI cli = new YesWorkflowCLI(stdoutStream, stderrStream);
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
            readTextFile(TEST_RESOURCE_DIR + "graph_ExamplePy_ProcessGraph.gv"),
            stdoutBuffer.toString()
         );
    }

    private static class MockExtractor implements Extractor {

        public boolean extracted = false;
 
        @Override public Extractor languageModel(LanguageModel language) { return this; };
        @Override public Extractor commentDelimiter(String c) { return this; }
        @Override public Extractor source(Reader reader) { return this; }
        @Override public List<String> getLines() { return null; }
        @Override public List<String> getComments() { return null; }
        @Override public Extractor extract() throws Exception { this.extracted = true; return null; }
        @Override public Language getLanguage() { return null; }
		@Override public List<Annotation> getAnnotations() { return null; }
    }
}
