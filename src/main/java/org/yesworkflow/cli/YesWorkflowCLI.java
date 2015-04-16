package org.yesworkflow.cli;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.config.YWConfiguration;
import org.yesworkflow.exceptions.YWMarkupException;
import org.yesworkflow.exceptions.YWToolUsageException;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.graph.DotGrapher;
import org.yesworkflow.graph.Grapher;
import org.yesworkflow.model.DefaultModeler;
import org.yesworkflow.model.Modeler;
import org.yesworkflow.model.Workflow;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/** 
 * Class that provides the default command-line interface (CLI) for YesWorkflow.
 * The CLI takes one argument (or option) representing the operation to 
 * be carried out (currently <i>extract</i>, <i>model</i>, or <i>graph</i>),
 * along with additional options that specify desired outputs and formats.  
 * Each operation implies and automatically runs the operations that logically 
 * precede it, i.e. the <i>graph</i> command implies the <i>extract</i> and 
 * <i>model</i> operations.</p>
 *
 * <p>The static {@link #main(String[]) main()} method instantiates this class
 * and passes the command line arguments it receives from the OS to 
 * the {@link #runForArgs(String[]) runForArgs()} method.
 * It then uses the {@link ExitCode} returned by 
 * {@link #runForArgs(String[]) runForArgs()} as the process exit code. 
 * 
 * <p>The CLI can be invoked programmatically by instantiating this class and
 * calling {@link #runForArgs(String[]) runForArgs()}. This function takes an argument 
 * of array of String representing command line arguments and options.
 * The {@link org.yesworkflow.extract.Extractor Extractor}, 
 * {@link org.yesworkflow.model.Modeler Modeler}, 
 * and {@link org.yesworkflow.graph.Grapher Grapher} used by the instance may be injected 
 * using the {@link #extractor(Extractor) extractor()}, {@link #modeler(Modeler) modeler()},
 * and {@link #grapher(Grapher) grapher()} methods before calling 
 * {@link #runForArgs(String[]) runForArgs()}.  A 
 * {@link #YesWorkflowCLI(PrintStream, PrintStream) non-default constructor} allows 
 * the output streams used by YesWorkflow to be assigned.</p>
 */
public class YesWorkflowCLI {

    public static final String EOL = System.getProperty("line.separator");
    private static final String PROPERTY_FILE_NAME = "yw.properties";
    private static final String YAML_FILE_NAME = "yw.yaml";
       
    private PrintStream errStream;
    private PrintStream outStream;    
    private OptionSet options = null;
    private Extractor extractor = null;
    private Modeler modeler = null;
    private Grapher grapher = null;
    private List<String> sourceFiles = null;
    private List<Annotation> annotations;
    private Workflow workflow = null;
    private YWConfiguration config = null;
    
    /** Method invoked first when the YesWorkflow CLI is run from the 
     * command line. Creates an instance of {@link YesWorkflowCLI},
     * passes the command line arguments to {@link #runForArgs(String[]) runForArgs()}, 
     * and uses the integer value associated with the {@link ExitCode} 
     * returned by {@link #runForArgs(String[]) runForArgs()} as the process
     * exit code.
     * 
     * @param args Arguments provided to the CLI on the command line.
     */
    public static void main(String[] args) {

        ExitCode exitCode;

        try {
            exitCode = new YesWorkflowCLI().runForArgs(args);
        } catch (Exception e) {
            e.printStackTrace();
            exitCode = ExitCode.UNCAUGHT_ERROR;
        }

        System.exit(exitCode.value());
    }

    /** 
     * Default constructor.  Used when YesWorkflow should use the
     * system-provided System.out and System.err streams.
     */
    public YesWorkflowCLI() {
        this(System.out, System.err);
    }

    /** 
     * Constructor that injects custom output streams. Used when 
     * YesWorkflow should use the streams provided as parameters instead 
     * of System.out and System.err.
     * @param outStream The PrintStream to use instead of System.out.
     * @param errStream The PrintStream to use instead of System.err.
     */
    public YesWorkflowCLI(PrintStream outStream, PrintStream errStream) {
        this.outStream = outStream;
        this.errStream = errStream;
    }

    public YesWorkflowCLI config(YWConfiguration config) {
        this.config = config;
        return this;
    }
    
    /** Method used to inject the 
     * {@link org.yesworkflow.extract.Extractor Extractor} to be used.
     * @param extractor A configured {@link org.yesworkflow.extract.Extractor Extractor} to use.
     * @return This instance.
     */
    public YesWorkflowCLI extractor(Extractor extractor) {
        this.extractor = extractor;
        return this;
    }

    /** Method used to inject the 
     * {@link org.yesworkflow.model.Modeler Modeler} to be used.
     * @param modeler A configured {@link org.yesworkflow.model.Modeler Modeler} to use.
     * @return This instance.
     */
    public YesWorkflowCLI modeler(Modeler modeler) {
        this.modeler = modeler;
        return this;
    }

    /** Method used to inject the 
     * {@link org.yesworkflow.graph.Grapher Grapher} to be used.
     * @param grapher A configured {@link org.yesworkflow.graph.Grapher Grapher} to use.
     * @return This instance.
     */
    public YesWorkflowCLI grapher(Grapher grapher) {
        this.grapher = grapher;
        return this;
    }

    /** 
     * Method that parses the provided command line arguments and executes the 
     * sequence of YesWorkflow operations requested by them.
     * @param args The command line arguments to parse.
     * @return An {@link ExitCode} indicating either that YesWorkflow 
     * ran successfully, or that an error of the indicated type occurred.
     * @throws Exception if an exception other than 
     * {@link org.yesworkflow.exceptions.YWToolUsageException YWToolUsageException}
     * or {@link org.yesworkflow.exceptions.YWMarkupException YWMarkupException}
     * is thrown while parsing the command line options or executing the YesWorkflow
     * operations. 
     */
    public ExitCode runForArgs(String[] args) throws Exception {

        OptionParser parser = createOptionsParser();

        try {

            // parse the command line arguments and options
            try {
                options = parser.parse(args);
            } catch (OptionException exception) {
                throw new YWToolUsageException("ERROR: " + exception.getMessage());
            }

            // load the configuration file
            if (config == null) {
                config = YWConfiguration.fromYamlFile(YAML_FILE_NAME);
                config.applyPropertyFile(PROPERTY_FILE_NAME);
            }
            
            // apply command-line overrides of config file
            config.applyConfigOptions(options.valuesOf("c"));
                    
            // print help and exit if requested
            if (options.has("h")) {
                printCLIHelp(parser);
                return ExitCode.SUCCESS;
            }

            // extract YesWorkflow command from arguments
            String commandString = extractCommandFromFirstNonOptionArgument();
            if (commandString == null) {
                throw new YWToolUsageException("ERROR: No command provided to YesWorkflow");
            }
            
            YWCommand command = null;
            try {
                command = YWCommand.toYWCommand(commandString);
            } catch(Exception e) {
                throw new YWToolUsageException("ERROR: Unrecognized YW command: " + commandString);
            }

            sourceFiles = extractSourceFileNamesFromRemainingArguments();
            if (sourceFiles.isEmpty()) {
                String sourceFile = config.getConfigOptionValue("source.file");
                if (sourceFile != null && !sourceFile.isEmpty()) {
                    sourceFiles.add(sourceFile);
                }                
            }
            
            switch(command) {
            
                case EXTRACT:
                    extract();
                    return ExitCode.SUCCESS;
    
                case MODEL:
                    extract();
                    model();
                    return ExitCode.SUCCESS;
                    
                case GRAPH:
                    extract();
                    model();
                    graph();
                    return ExitCode.SUCCESS;
            }
            
        } catch (YWToolUsageException e) {
            printToolUsageErrors(e.getMessage());
            return ExitCode.CLI_USAGE_ERROR;
        } catch (YWMarkupException e) {
            printMarkupErrors(e.getMessage());
            return ExitCode.MARKUP_ERROR;
        } 

        return ExitCode.SUCCESS;
    }
    
    private void printMarkupErrors(String message) {
        errStream.println("******************* YESWORKFLOW MARKUP ERRORS **************************");
        errStream.print(message);
        errStream.println();
        errStream.println("------------------------------------------------------------------------");
    }

    private void printToolUsageErrors(String message) {
        errStream.println();
        errStream.println(message);
        errStream.println();
        errStream.println("Use the -h option to display help for the YW command-line interface.");
    }
    
    public static final String YW_CLI_USAGE_HELP = 
            "usage: yw <command> [source file(s)] [option(s)]"                              + EOL;
    
    public static final String YW_CLI_COMMAND_HELP = 
        "Command                    Function"                                               + EOL +
        "-------                    --------"                                               + EOL +
        "extract                    Identify YW comments in script source file(s)"          + EOL +
        "model                      Build workflow model from YW comments in script"        + EOL +
        "graph                      Graphically render workflow model of script"            + EOL;

    public static final String YW_CLI_CONFIG_HELP = 
        "Configuration Name         Value"                                                  + EOL +
        "------------------         -----"                                                  + EOL +
        "extract.comment            Single-line comment delimiter in source files"          + EOL +
        "extract.language           Language used in source files"                          + EOL +
        "extract.listing            File for storing list of extracted comments"            + EOL +
        ""                                                                                  + EOL +
        "graph.datalabel            Info to display in data nodes: NAME, URI, or BOTH"      + EOL +
        "graph.dotcomments          SHOW or HIDE comments in dot files"                     + EOL +
        "graph.layout               Direction of graph layout: TB, LR, RL, or BT"           + EOL +
        "graph.portlayout           Layout mode for workflow ports: HIDE, RELAX or GROUP"   + EOL +
        "graph.view                 Workflow view to render: PROCESS, DATA or COMBINED"     + EOL +
        "graph.workflowbox          SHOW or HIDE box around nodes internal to workflow"     + EOL;
    
    public static final String YW_CLI_EXAMPLES_HELP = 
        "Examples"                                                                          + EOL +
        "--------"                                                                          + EOL +
        "$ yw graph myscript.py -config graph.view=combined -config graph.datalabel=uri"    + EOL +
        "$ yw extract myscript -c extract.comment='#' -c extract.listing=comments.txt"      + EOL;
    
    private void printCLIHelp(OptionParser parser) throws IOException {
        errStream.println();
        errStream.println(YW_CLI_USAGE_HELP);
        errStream.println(YW_CLI_COMMAND_HELP);
        parser.printHelpOn(errStream);
        errStream.println();
        errStream.println(YW_CLI_CONFIG_HELP);
        errStream.println(YW_CLI_EXAMPLES_HELP);
    }
    
    private String extractCommandFromFirstNonOptionArgument() throws YWToolUsageException {
        if (options.nonOptionArguments().size() == 0) {
            throw new YWToolUsageException("ERROR: Command must be first non-option argument to YesWorkflow");
        }
        return (String) options.nonOptionArguments().get(0);
    }

    private List<String> extractSourceFileNamesFromRemainingArguments() throws YWToolUsageException {
        List<String> sourceFilePaths = new LinkedList<String>();
        for (int i = 1; i < options.nonOptionArguments().size(); ++i) {
            sourceFilePaths.add((String) options.nonOptionArguments().get(i));
        }
        return sourceFilePaths;
    }
    
    private OptionParser createOptionsParser() throws Exception {
        
        OptionParser parser = null;

        parser = new OptionParser() {{
            acceptsAll(asList("c", "config"), "Assign configuration value")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("configuration")
                .describedAs("name=value");

            acceptsAll(asList("h", "help"), "Display this help");
        }};

        return parser;
    }

    private void extract() throws Exception {
    	
        if (extractor == null) {
            extractor =  new DefaultExtractor(this.outStream, this.errStream);
        }

        extractor.configure(config.getSection("extract"))
	             .configure("sources", sourceFiles)
                 .extract();
        
        annotations = extractor.getAnnotations();
    }

    private void model() throws Exception {
        
        if (modeler == null) {
            modeler = new DefaultModeler(this.outStream, this.errStream);
         }

        workflow =  modeler.configure(config.getSection("model"))
                           .annotations(annotations)
                           .model()
                           .getWorkflow();
    }

    private void graph() throws Exception {

        if (grapher == null) {
            grapher = new DotGrapher(this.outStream, this.errStream);
         }
        
        String graph = grapher.configure(config.getSection("graph"))
                              .workflow(workflow)
                              .graph()
                              .toString();

        writeTextToConfigNamedFile("graph.dotfile", graph);
    }

    private void writeTextToConfigNamedFile(String configuration, String text) throws IOException {        
        String path = config.getConfigOptionValue(configuration);
        writeTextToFileOrStdout(path, text);
    }

    
    private void writeTextToFileOrStdout(String path, String text) throws IOException {        
        PrintStream stream = (path == null || path.equals(YWConfiguration.EMPTY_VALUE) || path.equals("-")) ?
                             outStream : new PrintStream(path);
        stream.print(text);
        if (stream != outStream) {
            stream.close();
        }
    }
}
