package org.yesworkflow.model;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.End;
import org.yesworkflow.annotations.In;
import org.yesworkflow.annotations.Out;
import org.yesworkflow.exceptions.YWMarkupException;

public class DefaultModeler implements Modeler {

    private List<Annotation> annotations;
    private Workflow model;
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
    public Modeler annotations(List<Annotation> annotations) {
        this.annotations = annotations;
        return this;
    }

    @Override
    public Modeler model() throws Exception {	
    	buildWorkflow();
    	return this;
    }
    
    @Override
    public Workflow getModel() {
        return getWorkflow();
    }

    @Override
    public Workflow getWorkflow() {
        return this.model;
    }
    
    private void buildWorkflow() throws Exception {

        Workflow.Builder workflowBuilder = null;
        Workflow.Builder parentBuilder = null;
        Stack<Workflow.Builder> parentWorkflowBuilders = new Stack<Workflow.Builder>();

        for (Annotation annotation : annotations) {

            if (annotation instanceof Begin) {

                if (workflowBuilder != null) {
                    parentWorkflowBuilders.push(workflowBuilder);
                    parentBuilder = workflowBuilder;
                }

                workflowBuilder = new Workflow.Builder(this.stdoutStream, this.stderrStream)
                    .begin((Begin)annotation);

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

                Program program = workflowBuilder.build();

                if (parentWorkflowBuilders.isEmpty()) {
                    this.model = new Workflow(program);
                    return;
                }

                workflowBuilder = parentWorkflowBuilders.pop();
                workflowBuilder.nestedProgram(program);

                if (!parentWorkflowBuilders.isEmpty()) {
                    parentBuilder = parentWorkflowBuilders.peek();
                }
            }
        }
        
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
}
