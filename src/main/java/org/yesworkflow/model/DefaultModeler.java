package org.yesworkflow.model;

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
import org.yesworkflow.exceptions.YWMarkupException;

public class DefaultModeler implements Modeler {

    private List<Annotation> annotations;
    private Model model;
    private String topWorkflowName;
    private PrintStream stdoutStream = null;
    private PrintStream stderrStream = null;
    
    public DefaultModeler() {
        this(System.out, System.err);
    }

    public DefaultModeler(PrintStream stdoutStream, PrintStream stderrStream) {
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
    	return this;
    }
    
    @Override
    public Model getModel() {
        return this.model;
    }

    @Override
    public Workflow getWorkflow() {
        return this.model.workflow;
    }
    
    private void buildModel() throws Exception {

        WorkflowBuilder workflowBuilder = null;
        WorkflowBuilder topWorkflowBuilder = null;
        Workflow topWorkflow = null;
        WorkflowBuilder parentBuilder = null;
        Stack<WorkflowBuilder> parentWorkflowBuilders = new Stack<WorkflowBuilder>();
        List<Function> functions = new LinkedList<Function>();

        for (Annotation annotation : annotations) {

            if (annotation instanceof Begin) {

                if (workflowBuilder != null) {
                    parentWorkflowBuilders.push(workflowBuilder);
                    parentBuilder = workflowBuilder;
                }

                workflowBuilder = new WorkflowBuilder(this.stdoutStream, this.stderrStream)
                    .begin((Begin)annotation);
                
                if (topWorkflowBuilder == null) { 
                    String blockName = ((Begin)annotation).name;
                    if (topWorkflowName == null || topWorkflowName.equals(blockName)) {
                        topWorkflowBuilder = workflowBuilder;
                    }
                }

            } else if (annotation instanceof Out) {
                Port outPort = workflowBuilder.outPort((Out)annotation);
                if (parentBuilder != null) {
                    parentBuilder.nestedOutPort(outPort);
                }

            } else if (annotation instanceof In) {
                Port inPort = workflowBuilder.inPort((In)annotation);
                if (parentBuilder != null) {
                    parentBuilder.nestedInPort(inPort);
                }

            } else if (annotation instanceof End) {

                workflowBuilder.end((End)annotation);
                    
                if (parentWorkflowBuilders.isEmpty()) {
                    
                    if (workflowBuilder == topWorkflowBuilder) {
                        topWorkflow = workflowBuilder.buildWorkflow();
                    } else {
                        functions.add(workflowBuilder.buildFunction());
                    }

                    workflowBuilder = null;
                    
                } else {

                    Program program = (workflowBuilder.hasNestedPrograms()) ?
                        workflowBuilder.buildWorkflow() : workflowBuilder.buildProgram();
                    workflowBuilder = parentWorkflowBuilders.pop();
                    workflowBuilder.nestedProgram(program);
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
        
        model = new Model(topWorkflow, functions);
    }
}
