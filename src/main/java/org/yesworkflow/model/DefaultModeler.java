package org.yesworkflow.model;

import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import org.yesworkflow.comments.BeginComment;
import org.yesworkflow.comments.Comment;
import org.yesworkflow.comments.EndComment;
import org.yesworkflow.comments.InComment;
import org.yesworkflow.comments.OutComment;
import org.yesworkflow.exceptions.YWMarkupException;

public class DefaultModeler implements Modeler {

    private List<Comment> comments;
    private Program model;
    private PrintStream stdoutStream = null;
    private PrintStream stderrStream = null;
    
    public DefaultModeler(PrintStream stdoutStream, PrintStream stderrStream) {
        this.stdoutStream = stdoutStream;
        this.stderrStream = stderrStream;
    }
    
    @Override
    public Modeler comments(List<Comment> comments) {
        this.comments = comments;
        return this;
    }

    @Override
    public Modeler model() throws Exception {

        Workflow.Builder workflowBuilder = null;
        Workflow.Builder parentBuilder = null;
        Stack<Workflow.Builder> parentWorkflowBuilders = new Stack<Workflow.Builder>();

        for (Comment comment: comments) {

            if (comment instanceof BeginComment) {

                if (workflowBuilder != null) {
                    parentWorkflowBuilders.push(workflowBuilder);
                    parentBuilder = workflowBuilder;
                }

                workflowBuilder = new Workflow.Builder(this.stdoutStream, this.stderrStream)
                    .begin((BeginComment)comment);

            } else if (comment instanceof OutComment) {
                Port outPort = workflowBuilder.outPort((OutComment)comment);
                if (parentBuilder != null) {
                    parentBuilder.nestedOutPort(outPort);
                }

            } else if (comment instanceof InComment) {
                Port inPort = workflowBuilder.inPort((InComment)comment);
                if (parentBuilder != null) {
                    parentBuilder.nestedInPort(inPort);
                }

            } else if (comment instanceof EndComment) {

                workflowBuilder.end((EndComment)comment);

                Program program = workflowBuilder.build();

                if (parentWorkflowBuilders.isEmpty()) {
                    this.model = program;
                    return this;
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
    
    public Program getModel() {
        return this.model;
    }

}
