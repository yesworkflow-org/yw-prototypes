package org.yesworkflow;

/* This file is an adaptation of KuratorAkka.java in the org.kurator.akka
 * package as of 18Dec2014.
 */

import static java.util.Arrays.asList;

import java.io.PrintStream;

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
        
        OptionParser parser = null;        
        try {
            parser = createOptionsParser();
        }
        catch (OptionException exception) {
            errStream.print("Error:  Option definition invalid");
            errStream.println(exception.getMessage());
            return -1;
        }
        
        OptionSet options = null;
        
        try {

            options = parser.parse(args);

        } catch (OptionException exception) {
            errStream.println("Error: Unable to parse command-line options");
            errStream.println(exception.getMessage());
            errStream.println();
            parser.printHelpOn(errStream);
            return -1;
        }
                
        if (options.has("h")) {
            errStream.println();
            parser.printHelpOn(errStream);
            return 0;            
        }
        
        String command = extractCommandFromOptions(options);        
        if (command == null) {
            errStream.println("Error: No command provided to YesWorkflow");
            errStream.println();
            parser.printHelpOn(errStream);
        }
        
        String scriptFilePath = extractScriptPathFromOptions(options);
        
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

    private static String extractScriptPathFromOptions(OptionSet options) {
        
        String scriptFilePath = null;

        if (options.hasArgument("f")) {
            scriptFilePath = (String) options.valueOf("f");
        }

        return scriptFilePath; 
    }
    
    private static OptionParser createOptionsParser() throws Exception {

        OptionParser parser = null;
        
        parser = new OptionParser() {{
            
            acceptsAll(asList("f", "file"), "path to script to analyze")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("definition");

            acceptsAll(asList("c", "command"), "command to YesWorkflow")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("command");
            
            acceptsAll(asList("h", "help"), "display help");

        }};
            
        return parser;
    }    
}
