package org.yesworkflow.model;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.End;
import org.yesworkflow.annotations.In;
import org.yesworkflow.annotations.Out;
import org.yesworkflow.annotations.Return;
import org.yesworkflow.config.YWConfiguration;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.exceptions.YWMarkupException;
import org.yesworkflow.query.QueryEngine;

public class DefaultModeler implements Modeler {

    static private QueryEngine DEFAULT_QUERY_ENGINE = QueryEngine.SWIPL;

    private YesWorkflowDB ywdb;
    private List<Annotation> annotations;
    private Model model;
    private String topWorkflowName = null;
    private PrintStream stdoutStream = null;
    private PrintStream stderrStream = null;
    private String factsFile = null;
    private String modelFacts = null;
    private QueryEngine queryEngine = DEFAULT_QUERY_ENGINE;
    
    
    public DefaultModeler() throws Exception {
        this(YesWorkflowDB.getGlobalInstance(), System.out, System.err);
    }

    
    public DefaultModeler(YesWorkflowDB ywdb) throws Exception {
        this(ywdb, System.out, System.err);
    }

    public DefaultModeler(YesWorkflowDB ywdb, PrintStream stdoutStream, PrintStream stderrStream) {
        this.ywdb = ywdb;
        this.stdoutStream = stdoutStream;
        this.stderrStream = stderrStream;
    }
    
    @Override
    public DefaultModeler configure(Map<String,Object> config) throws Exception {
        if (config != null) {
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                configure(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    @Override
    public DefaultModeler configure(String key, Object value) throws Exception {
        if (key.equalsIgnoreCase("workflow")) {
            topWorkflowName = (String)value;
        } else if (key.equalsIgnoreCase("factsfile")) {
           factsFile = (String)value;
        } else if (key.equalsIgnoreCase("queryengine")) {
            queryEngine = QueryEngine.toQueryEngine((String)value);
        }
        return this;
    }  
    
    @Override
    public Modeler annotations(List<Annotation> annotations) {
        this.annotations = annotations;
        return this;
    }

    @Override
    public Modeler model() throws Exception {	
    	buildModel();
    	if (factsFile != null) {
    	    writeTextToFileOrStdout(factsFile, getFacts());
    	}
    	return this;
    }
    
    @Override
    public Model getModel() {
        return this.model;
    }

    @Override
    public String getFacts() {
        if (modelFacts == null) {
            modelFacts = new ModelFacts(queryEngine, model).build().toString();
        }
        return modelFacts;
    }

    private void writeTextToFileOrStdout(String path, String text) throws IOException {  
        PrintStream stream = (path.equals(YWConfiguration.EMPTY_VALUE) || path.equals("-")) ?
                             this.stdoutStream : new PrintStream(path);
        stream.print(text);
        if (stream != this.stdoutStream) {
            stream.close();
        }
    }

    private void buildModel() throws Exception {

        WorkflowBuilder superBuilder = new WorkflowBuilder(ywdb, this.stdoutStream, this.stderrStream);
        
        WorkflowBuilder workflowBuilder = null;
        WorkflowBuilder topWorkflowBuilder = null;
        Program topProgram = null;
        WorkflowBuilder parentBuilder = null;
        Stack<WorkflowBuilder> parentWorkflowBuilders = new Stack<WorkflowBuilder>();
        List<Function> functions = new LinkedList<Function>();
        
        for (Annotation annotation : annotations) {

            if (annotation instanceof Begin) {

                String parentName = null;
                if (workflowBuilder != null) {
                    parentWorkflowBuilders.push(workflowBuilder);
                    parentBuilder = workflowBuilder;
                    parentName = parentBuilder.getName();
                }

                workflowBuilder = new WorkflowBuilder(ywdb, parentName, 
                                      (parentBuilder == null) ? superBuilder : parentBuilder, 
                                      this.stdoutStream, this.stderrStream);
                
                workflowBuilder.begin((Begin)annotation);
                
                if (topWorkflowBuilder == null) { 
                    String blockName = ((Begin)annotation).value();
                    if (topWorkflowName == null || topWorkflowName.equals(blockName)) {
                        topWorkflowBuilder = workflowBuilder;
                    }
                }

            } else if (annotation instanceof Return) {
                workflowBuilder.returnPort((Return)annotation);
                
            } else if (annotation instanceof Out) {
                workflowBuilder.outPort((Out)annotation);
                
            } else if (annotation instanceof In) {
                workflowBuilder.inPort((In)annotation);
                
            } else if (annotation instanceof End) {

                workflowBuilder.end((End)annotation);                
                
                if (parentWorkflowBuilders.isEmpty()) {
                    
                    if (workflowBuilder == topWorkflowBuilder) {
                        topProgram = workflowBuilder.build();
                    } else {
                        functions.add(workflowBuilder.buildFunction());
                    }

                    workflowBuilder = null;
                    
                } else {

                    Program program = workflowBuilder.build();

                    if (program instanceof Function) {
                        parentBuilder.nestedFunction((Function)program);
                    } else {
                        parentBuilder.nestedProgram(program);
                    }
                    
                    workflowBuilder = parentWorkflowBuilders.pop();
                }
                
                if (!parentWorkflowBuilders.isEmpty()) {
                    parentBuilder = parentWorkflowBuilders.peek();
                }
            }
        }
        
        if (workflowBuilder != null) {
            // throw exception if missing any paired end comments
            StringBuilder messageBuilder = new StringBuilder();
            do {
                messageBuilder.append("ERROR: No @end comment paired with '@begin ");
                messageBuilder.append(workflowBuilder.getProgramName());
                messageBuilder.append("'");
                messageBuilder.append(EOL);
                workflowBuilder = parentWorkflowBuilders.isEmpty() ? null : parentWorkflowBuilders.pop();
            } while (workflowBuilder != null);        
            throw new YWMarkupException(messageBuilder.toString());
        }
        
        if (topProgram == null) {
            if (topWorkflowName != null) throw new YWMarkupException("No workflow named '" + topWorkflowName + "' found in source.");
            if (functions.size() == 0) throw new Exception("No program or functions found in script.");
        }
        
        model = new Model(topProgram, functions, superBuilder.getData());
    }
}
