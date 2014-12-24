package org.yesworkflow;

/* This file is an adaptation of KuratorAkka.java in the org.kurator.akka
 * package as of 18Dec2014.
 */

import static java.util.Arrays.asList;

import java.io.PrintStream;

import org.yesworkflow.exceptions.UsageException;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;


public class YesWorkflowCLI {

    public static int YW_CLI_SUCCESS = 0;
    public static int YW_CLI_USAGE_ERROR = -1;
    public static int YW_CLI_UNCAUGHT_EXCEPTION = -2;

    public static void main(String[] args) throws Exception {
        
        Integer returnValue = null;

        try {
            returnValue = new YesWorkflowCLI().runForArgs(args);
        } catch (Exception e) {
            e.printStackTrace();
            returnValue = YW_CLI_UNCAUGHT_EXCEPTION;
        }
        
        System.exit(returnValue);
    }
    
    private PrintStream outStream;
    private PrintStream errStream;
    private OptionParser parser = null;
    private OptionSet options = null;
    private String command = null;
    private String sourceFilePath = null;
    private String databaseFilePath = null;
    
    public YesWorkflowCLI() throws Exception {
        this(System.out, System.err);
    }

    public YesWorkflowCLI(PrintStream outStream, PrintStream errStream) throws Exception {
        this.outStream = outStream;
        this.errStream = errStream;
        this.parser = createOptionsParser();
    }
    
    private void initialize() {
        options = null;
        command = null;
        sourceFilePath = null;
        databaseFilePath = null;
    }
    
    
    public int runForArgs(String[] args) throws Exception {
        
        initialize();
        
        try {

            // parse the command line arguments and options
            try {
                options = parser.parse(args);
            } catch (OptionException exception) {
                throw new UsageException(exception.getMessage());
            }
            
            // print help and exit if requested
            if (options.has("h")) {
                errStream.println();
                parser.printHelpOn(errStream);
                return YW_CLI_SUCCESS;            
            }
            
            // extract mandatory YesWorkflow command from arguments
            extractCommandFromOptions();                            
            if (command == null) {
                throw new UsageException("No command provided to YesWorkflow");
            }
        
            // extract remaining arguments
            extractSourcePathFromOptions();
            extractDatabasePathFromOptions();

            // run extractor and exit if extract command given
            if (command.equals("extract")) {
                new Extractor()
                    .inputScriptPath(sourceFilePath)
                    .outputDbPath(databaseFilePath)
                    .extract();
                return YW_CLI_SUCCESS;
            }
            
        } catch (UsageException ue) {
            errStream.print("Usage error: ");
            errStream.println(ue.getMessage());
            errStream.println();
            parser.printHelpOn(errStream);
            return YW_CLI_USAGE_ERROR;
        }
        
        return YW_CLI_SUCCESS;
    }
    
    
    
    private void extractCommandFromOptions() {

        if (options.nonOptionArguments().size() == 1) {
            
            // if there is only one non-option argument assume this is the command to YesWorkflow
            command = (String) options.nonOptionArguments().get(0);

        } else if (options.hasArgument("c")) {

            // otherwise use the argument to the -c option
            command = (String) options.valueOf("c");
        }
    }

    private void extractDatabasePathFromOptions() {
        if (options.hasArgument("d")) {
            databaseFilePath = (String) options.valueOf("d");
        }
    }

    private void extractSourcePathFromOptions() {
        if (options.hasArgument("s")) {
            sourceFilePath = (String) options.valueOf("s");
        }
    }
    
    private OptionParser createOptionsParser() throws Exception {

        OptionParser parser = null;
        
        parser = new OptionParser() {{
            
            acceptsAll(asList("c", "command"), "command to YesWorkflow")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("command");

            acceptsAll(asList("s", "source"), "path to source file to analyze")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("script");

            acceptsAll(asList("d", "database"), "path to database file for storing extracted workflow graph")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("database");
            
            acceptsAll(asList("h", "help"), "display help");

        }};
            
        return parser;
    }
}