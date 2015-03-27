package org.yesworkflow.model;

import java.util.List;

import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.End;

public class Program {

    public final Begin beginAnnotation;
    public final End endAnnotation;
    public final Port[] inPorts;
    public final Port[] outPorts;
    public final Program[] programs;
    public final Channel[] channels;
    
    static final Program[] EMPTY_PROGRAM_ARRAY = new Program[]{};
    static final Channel[] EMPTY_CHANNEL_ARRAY = new Channel[]{};
    
	public Program(Begin beginAnnotation, End endAnnotation, List<Port> inPorts, List<Port> outPorts) {
	    this.beginAnnotation = beginAnnotation;
        this.endAnnotation = endAnnotation;
	    this.inPorts = inPorts.toArray(new Port[inPorts.size()]);
	    this.outPorts = outPorts.toArray(new Port[outPorts.size()]);
	    this.programs = EMPTY_PROGRAM_ARRAY;
	    this.channels = EMPTY_CHANNEL_ARRAY;
	}
	
	protected Program(Begin beginAnnotation, End endAnnotation, List<Port> inPorts, List<Port> outPorts,
	        Program[] programs, Channel[] channels) {
        this.beginAnnotation = beginAnnotation;
        this.endAnnotation = endAnnotation;
        this.inPorts = inPorts.toArray(new Port[inPorts.size()]);
        this.outPorts = outPorts.toArray(new Port[outPorts.size()]);
        this.programs = programs;
        this.channels = channels;
    }

    public boolean isWorkflow() {
	    return false;
	}
	
	@Override
	public String toString() {
	    return this.beginAnnotation.name;
	}
}
