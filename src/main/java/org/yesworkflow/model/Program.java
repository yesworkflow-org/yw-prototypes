package org.yesworkflow.model;

import java.util.List;

import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.End;

public class Program {

    public final Begin beginAnnotation;
    public final End endAnnotation;
    public final Port[] inPorts;
    public final Port[] outPorts;
    	
	public Program (Begin beginAnnotation, End endAnnotation, List<Port> inPorts, List<Port> outPorts) {
		this.beginAnnotation = beginAnnotation;
        this.endAnnotation = endAnnotation;
	    this.inPorts = inPorts.toArray(new Port[inPorts.size()]);
	    this.outPorts = outPorts.toArray(new Port[outPorts.size()]);
	}
	
	@Override
	public String toString() {
	    return this.beginAnnotation.name;
	}
}
