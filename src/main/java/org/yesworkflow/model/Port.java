package org.yesworkflow.model;

import org.yesworkflow.comments.BeginComment;
import org.yesworkflow.comments.PortComment;

public class Port {
	
    public final BeginComment beginComment;
	public final PortComment portComment;
	
	public Port(PortComment portComment, BeginComment beginComment) {
		this.portComment = portComment;
		this.beginComment = beginComment;
	}
	
   @Override
    public String toString() {
        return String.format("%s:%s", this.beginComment.programName, this.portComment.binding());
    }
}
