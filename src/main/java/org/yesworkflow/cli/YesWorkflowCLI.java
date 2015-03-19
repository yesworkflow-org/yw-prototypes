package org.yesworkflow.cli;

import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

import org.yesworkflow.LanguageModel;
import org.yesworkflow.LanguageModel.Language;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.exceptions.YWMarkupException;
import org.yesworkflow.exceptions.YWToolUsageException;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.graph.DotGrapher;
import org.yesworkflow.graph.GraphView;
import org.yesworkflow.graph.Grapher;
import org.yesworkflow.model.DefaultModeler;
import org.yesworkflow.model.Modeler;
import org.yesworkflow.model.Program;
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
 * The {@link org.yesworkflow.extract.Extractor}, {@link org.yesworkflow.model.Modeler}, 
 * and {@link org.yesworkflow.graph.Grapher} used by the instance may be injected 
 * using the {@link #extractor(Extractor) extractor()}, {@link #modeler(Modeler) modeler()},
 * and {@link #grapher(Grapher) grapher()} methods before calling 
 * {@link #runForArgs(String[]) runForArgs()}.  A 
 * {@link #YesWorkflowCLI(PrintStream, PrintStream) non-default constructor} allows 
 * the output streams used by YesWorkflow to be assigned.</p>
 */
public class YesWorkflowCLI {
    
    private PrintStream errStream;
    private PrintStream outStream;    
    private OptionSet options = null;
    private Extractor injectedExtractor = null;
    private Modeler modeler = null;
    private Grapher grapher = null;
    private List<Annotation> annotations;
    private Program model = null;
    
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


    public YesWorkflowCLI() throws Exception {
        this(System.out, System.err);
    }

    public YesWorkflowCLI(PrintStream outStream, PrintStream errStream) throws Exception {
        this.outStream = outStream;
        this.errStream = errStream;
    }

    public YesWorkflowCLI extractor(Extractor extractor) {
        this.injectedExtractor = extractor;
        return this;
    }

    public YesWorkflowCLI modeler(Modeler modeler) {
        this.modeler = modeler;
        return this;
    }

    public YesWorkflowCLI grapher(Grapher grapher) {
        this.grapher = grapher;
        return this;
    }

    public ExitCode runForArgs(String[] args) throws Exception {

        OptionParser parser = createOptionsParser();

        try {

            // parse the command line arguments and options
            try {
                options = parser.parse(args);
            } catch (OptionException exception) {
                throw new YWToolUsageException("ERROR: " + exception.getMessage());
            }

            // print help and exit if requested
            if (options.has("h")) {
                printCLIHelp(parser);
                return ExitCode.SUCCESS;
            }

            // extract YesWorkflow command from arguments
            String command = extractCommandFromOptions();
            if (command == null) {
                throw new YWToolUsageException("ERROR: No command provided to YesWorkflow");
            }

            // run extractor and exit if extract command given
            if (command.equals("extract")) {
                extract();
                return ExitCode.SUCCESS;
            }

            if (command.equals("graph")) {
                extract();
                model();
                graph();
                return ExitCode.SUCCESS;
            }

        } catch (YWToolUsageException e) {
            printToolUsageErrors(e.getMessage());
            printCLIHelp(parser);
            return ExitCode.CLI_USAGE_ERROR;
        } catch (YWMarkupException e) {
            printMarkupErrors(e.getMessage());
            return ExitCode.MARKUP_ERROR;
        } 

        return ExitCode.SUCCESS;
    }
    
    private void printMarkupErrors(String message) {
        errStream.println();
        errStream.println("******************* YESWORKFLOW MARKUP ERRORS **************************");
        errStream.println();
        errStream.print(message);
        errStream.println();
        errStream.println("------------------------------------------------------------------------");
    }

    private void printToolUsageErrors(String message) {
        errStream.println();
        errStream.println("****************** YESWORKFLOW TOOL USAGE ERRORS ***********************");
        errStream.println();
        errStream.println(message);
    }
    
    private void printCLIHelp(OptionParser parser) throws IOException {
        errStream.println();
        errStream.println("---------------------- YesWorkflow usage summary -----------------------");
        errStream.println();
        parser.printHelpOn(errStream);
        errStream.println();
        errStream.println("------------------------------------------------------------------------");
    }
    
    private String extractCommandFromOptions() {

        if (options.nonOptionArguments().size() == 1) {

            // if there is only one non-option argument assume this is the command to YesWorkflow
           return (String) options.nonOptionArguments().get(0);

        } else if (options.hasArgument("c")) {

            // otherwise use the argument to the -c option if present
            return (String) options.valueOf("c");

        } else {
            
            // and return null if no command given at all
            return null;
        }
    }

    private GraphView extractGraphView() throws YWToolUsageException {
        
        String viewString = (String) options.valueOf("v");
        
        if (viewString.equalsIgnoreCase("process"))     return GraphView.PROCESS_CENTRIC_VIEW;
        if (viewString.equalsIgnoreCase("data"))        return GraphView.DATA_CENTRIC_VIEW;
        if (viewString.equalsIgnoreCase("combined"))    return GraphView.COMBINED_VIEW;
        
        throw new YWToolUsageException("Unsupported graph view: " + viewString);
    }

    private OptionParser createOptionsParser() throws Exception {

        OptionParser parser = null;

        parser = new OptionParser() {{

            acceptsAll(asList("c", "command"), "command to YesWorkflow")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("command");

            acceptsAll(asList("x", "commchar"), "comment character")
        		.withOptionalArg()
        		.ofType(String.class)
        		.describedAs("comment");

            acceptsAll(asList("s", "source"), "path to source file to analyze")
                .withOptionalArg()
                .defaultsTo("-")
                .ofType(String.class)
                .describedAs("script");

            acceptsAll(asList("g", "graph"), "path to graphviz dot file for storing rendered workflow graph")
                .withOptionalArg()
                .defaultsTo("-")
                .ofType(String.class)
                .describedAs("dot file");

            acceptsAll(asList("v", "view"), "view of model to render as a graph")
                .withRequiredArg()
                .ofType(String.class)
                .defaultsTo("process")
                .describedAs("process|data|combined");

            acceptsAll(asList("l", "lines"), "path to file for saving extracted comment lines")
                .withOptionalArg()
                .defaultsTo("-")
                .ofType(String.class)
                .describedAs("lines file");

            acceptsAll(asList("h", "help"), "display help");

        }};

        return parser;
    }

    private BufferedReader getFileReaderForPath(String path) throws YWToolUsageException {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            throw new YWToolUsageException("ERROR: Input file not found: " + path);
        }

        return reader;
    }

    
    private void extract() throws Exception {
    	
    	Extractor extractor;

    	String sourceFilePath = (String) options.valueOf("s");    	
        if (sourceFilePath.equals("-")) {
        	extractor = getStdinExtractor();
        } else {
        	extractor = getSingleFileExtractor(sourceFilePath);
        }
        
        if (options.hasArgument("x")) {
        	extractor.commentDelimiter((String)options.valueOf("x"));
        } else {
            Language language = LanguageModel.languageForFileName(sourceFilePath);
            if (language != null) {
            	extractor.languageModel(new LanguageModel(language));
            } else {
                throw new YWToolUsageException("Cannot identify language of source file.  Please specify a comment character.");
            }
        }
        
        extractor.extract();

        if (options.has("l")) {

            StringBuffer linesBuffer = new StringBuffer();
            for (String line : extractor.getLines()) {
                linesBuffer.append(line);
                linesBuffer.append(System.getProperty("line.separator"));
            }

            writeTextToOptionNamedFile("l", linesBuffer.toString());
        }
        
        @SuppressWarnings("unused")
        List<String> comments = extractor.getComments();
        annotations = extractor.getAnnotations();
    }
    
    private Extractor getStdinExtractor() {
    	
    	Extractor extractor;
        if (injectedExtractor != null) {
        	extractor = injectedExtractor;
        } else {
           extractor = new DefaultExtractor(this.outStream, this.errStream);
        }
    	extractor.source(new InputStreamReader(System.in));
    	
    	return extractor;
    }
    

    private Extractor getSingleFileExtractor(String sourcePath) throws YWToolUsageException {

    	Extractor extractor;
        if (injectedExtractor != null) {
        	extractor = injectedExtractor;
        } else {
           extractor = new DefaultExtractor(this.outStream, this.errStream);
        }
        
    	BufferedReader reader = getFileReaderForPath(sourcePath);
    	extractor.source(reader);
    	
    	return extractor;
    }
    

    private void model() throws Exception {
        
        if (modeler == null) {
            modeler = new DefaultModeler(this.outStream, this.errStream);
         }

        model = (Program) modeler.annotations(annotations)
                                 .model()
                                 .getModel();
    }

    private void graph() throws Exception {

        GraphView view = extractGraphView();

        if (grapher == null) {
            grapher = new DotGrapher(this.outStream, this.errStream);
         }
        
        String graph = grapher.workflow((Workflow)model)
                              .view(view)
                              .graph()
                              .toString();

        writeTextToOptionNamedFile("g", graph);
    }

    private void writeTextToOptionNamedFile(String option, String text) throws IOException {
        String path = (String) options.valueOf(option);
        PrintStream linesOutputStream = (path.equals("-")) ? outStream : new PrintStream(path);
        linesOutputStream.print(text);
        if (linesOutputStream != outStream) {
            linesOutputStream.close();
        }
    }
}
