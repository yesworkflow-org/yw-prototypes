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

    public static void main(String[] args) throws Exception {
        
        int returnValue = -1;
        
        try {
         returnValue = runForArgs(args, System.out, System.err);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.exit(returnValue);
    }

    public static int runForArgs(String[] args, PrintStream outStream, PrintStream errStream) throws Exception {
        
        OptionParser parser = createOptionsParser();
        
        try {

            // parse the command line arguments and options
            OptionSet options = null;
            try {
                options = parser.parse(args);
            } catch (OptionException exception) {
                throw new UsageException(exception.getMessage());
            }
            
            // print help and exit if requested
            if (options.has("h")) {
                errStream.println();
                parser.printHelpOn(errStream);
                return 0;            
            }
            
            // extract mandatory YesWorkflow command from arguments
            String command = extractCommandFromOptions(options);                            
            if (command == null) {
                throw new UsageException("No command provided to YesWorkflow");
            }
        
            // extract remaining arguments
            String sourceFilePath = extractSourcePathFromOptions(options);
            String databaseFilePath = extractDatabasePathFromOptions(options);

            // run extractor and exit if extract command given
            if (command.equals("extract")) {
                new Extractor()
                    .inputScriptPath(sourceFilePath)
                    .outputDbPath(databaseFilePath)
                    .extract();
                return 0;
            }
            
        } catch (UsageException ue) {
            errStream.print("Usage error: ");
            errStream.println(ue.getMessage());
            errStream.println();
            parser.printHelpOn(errStream);
            return -1;
        }
        
        return 0;
    }
    
    private static String extractCommandFromOptions(OptionSet options) {

        String command = null;
        
        if (options.nonOptionArguments().size() == 1) {
            
            // if there is only one non-option argument assume this is the command to YesWorkflow
            command = (String) options.nonOptionArguments().get(0);

        } else if (options.hasArgument("c")) {

            // otherwise use the argument to the -c option
            command = (String) options.valueOf("c");
        }
        
        return command;
    }

    private static String extractDatabasePathFromOptions(OptionSet options) {
        
        String path = null;

        if (options.hasArgument("d")) {
            path = (String) options.valueOf("d");
        }

        return path; 
    }

    private static String extractSourcePathFromOptions(OptionSet options) {
        
        String scriptFilePath = null;

        if (options.hasArgument("s")) {
            scriptFilePath = (String) options.valueOf("s");
        }

        return scriptFilePath; 
    }
    
    private static OptionParser createOptionsParser() throws Exception {

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
