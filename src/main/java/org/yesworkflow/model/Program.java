package org.yesworkflow.model;

import java.util.List;

import org.yesworkflow.comments.BeginComment;
import org.yesworkflow.comments.EndComment;

public class Program {

    public final BeginComment beginComment;
    public final EndComment endComment;
    public final Port[] inPorts;
    public final Port[] outPorts;
    	
	public Program (BeginComment beginComment, EndComment endComment, List<Port> inPorts, List<Port> outPorts) {
		this.beginComment = beginComment;
        this.endComment = endComment;
	    this.inPorts = inPorts.toArray(new Port[inPorts.size()]);
	    this.outPorts = outPorts.toArray(new Port[outPorts.size()]);
	}
	
	@Override
	public String toString() {
	    return this.beginComment.programName;
	}
}
