package org.yesworkflow.extract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.yesworkflow.comments.BeginComment;
import org.yesworkflow.comments.Comment;
import org.yesworkflow.comments.EndComment;
import org.yesworkflow.comments.InComment;
import org.yesworkflow.comments.OutComment;
import org.yesworkflow.exceptions.UsageException;
import org.yesworkflow.model.Port;
import org.yesworkflow.model.Program;
import org.yesworkflow.model.Workflow;

public class DefaultExtractor implements Extractor {

    private char commentCharacter;
    private BufferedReader sourceReader = null;
    private String sourcePath = null;
    private List<String> commentLines;
    private List<Comment> comments;
    private Program program;

    @SuppressWarnings("unused")
    private String databasePath = null;

    @Override
    public DefaultExtractor commentCharacter(char c) {
        this.commentCharacter = c;
        return this;
    }

    @Override
    public DefaultExtractor sourceReader(Reader reader) {
        this.sourceReader = new BufferedReader(reader);
        return this;
    }

    @Override
    public DefaultExtractor sourcePath(String path) {
        this.sourcePath = path;
        return this;
    }

    @Override
    public DefaultExtractor databasePath(String path) {
        this.databasePath = path;
        return this;
    }

    @Override
    public void extract() throws Exception {

        if (sourceReader == null) {
            sourceReader = getFileReaderForPath(sourcePath);
        }

        extractLines();
        extractComments();
        extractWorkflow();
    }

    @Override
    public List<String> getLines() {
        return commentLines;
    }

    @Override
    public List<Comment> getComments() {
        return comments;
    }

    @Override
	public Program getProgram() {
    	return program;
    }

    private void extractLines() throws IOException {

        commentLines = new LinkedList<String>();

        String line = null;
        while ((line = sourceReader.readLine()) != null) {
            String ywCommentLine = extractCommentLine(line);
            if (ywCommentLine != null) {
                commentLines.add(ywCommentLine);
            }
        }
    }

    private void extractComments() throws Exception {

        comments = new LinkedList<Comment>();

        for (String commentLine : commentLines) {

            String tag = extractTag(commentLine);

            Comment comment;
            if (tag.equalsIgnoreCase("@begin")) {
                comment = new BeginComment(commentLine);
            } else if (tag.equalsIgnoreCase("@end")) {
                comment = new EndComment(commentLine);
            } else if (tag.equalsIgnoreCase("@in")) {
                comment = new InComment(commentLine);
            } else if (tag.equalsIgnoreCase("@out")) {
                comment = new OutComment(commentLine);
            } else {
                throw new Exception("Comment tag " + tag + " is not supported");
            }

            comments.add(comment);
        }
    }

    public void extractWorkflow() throws Exception {

    	Workflow.Builder workflowBuilder = null;
    	Workflow.Builder parentBuilder = null;
    	Stack<Workflow.Builder> parentWorkflowBuilders = new Stack<Workflow.Builder>();

    	for (Comment comment: comments) {

    		if (comment instanceof BeginComment) {

    			if (workflowBuilder != null) {
    				parentWorkflowBuilders.push(workflowBuilder);
    				parentBuilder = workflowBuilder;
    			}

    			workflowBuilder = new Workflow.Builder()
    				.begin((BeginComment)comment);

    		} else if (comment instanceof OutComment) {
    		    Port outPort = workflowBuilder.outPort((OutComment)comment);
    			if (parentBuilder != null) {
    				parentBuilder.nestedOutPort(outPort, workflowBuilder.getProgramName());
    			}

    		} else if (comment instanceof InComment) {
                Port inPort = workflowBuilder.inPort((InComment)comment);
                if (parentBuilder != null) {
    				parentBuilder.nestedInPort(inPort, workflowBuilder.getProgramName());
    			}

    		} else if (comment instanceof EndComment) {

    		    workflowBuilder.end((EndComment)comment);

    			Program program = workflowBuilder.build();

    			if (parentWorkflowBuilders.isEmpty()) {
    				this.program = program;
    				return;
    			}

    			workflowBuilder = parentWorkflowBuilders.pop();
    			workflowBuilder.nestedProgram(program);

    			if (!parentWorkflowBuilders.isEmpty()) {
    				parentBuilder = parentWorkflowBuilders.peek();
    			}
    		}
    	}
    }

    private String extractTag(String commentLine) {

        int tagEndIndex = commentLine.indexOf(' ');
        if (tagEndIndex == -1) tagEndIndex = commentLine.indexOf('\t');

        if (tagEndIndex == -1) {
            return commentLine;
        } else {
            return commentLine.substring(0, tagEndIndex);
        }
    }

    private String extractCommentLine(String line) {

        String trimmedLine = line.trim();

        if (trimmedLine.length() == 0 || trimmedLine.charAt(0) != commentCharacter) return null;

        int ywCommentTagBegin = trimmedLine.indexOf('@');
        if (ywCommentTagBegin == -1) return null;

        return trimmedLine.substring(ywCommentTagBegin);
    }

    private BufferedReader getFileReaderForPath(String path) throws UsageException {

        if (sourcePath == null) throw new UsageException("No source path provided to extractor");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {
            throw new UsageException("Input source file not found: " + path);
        }

        return reader;
    }

	@Override
	public char getCommentCharacter() {
		return commentCharacter;
	}
}
