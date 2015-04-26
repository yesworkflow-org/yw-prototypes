package org.yesworkflow.model;

import java.util.List;

import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.End;

public class Program {

    static final Program[] EMPTY_PROGRAM_ARRAY = new Program[]{};
    static final Function[] EMPTY_FUNCTION_ARRAY = new Function[]{};
    static final Channel[] EMPTY_CHANNEL_ARRAY = new Channel[]{};

    public final Begin beginAnnotation;
    public final End endAnnotation;
    public final Port[] inPorts;
    public final Port[] outPorts;
    public final Program[] programs;
    public final Channel[] channels;
    public final Function[] functions;

    public Program(
            Begin beginAnnotation, 
            End endAnnotation, 
            Port[] inPorts, 
            Port[] outPorts, 
            Program[] programs,
            Channel[] channels,
            Function[] functions
    ) {
        this.beginAnnotation = beginAnnotation;
        this.endAnnotation = endAnnotation;
        this.inPorts = inPorts;
        this.outPorts = outPorts;
        this.programs = programs;
        this.channels = channels;
        this.functions = functions;
    }
    
	public Program(
	        Begin beginAnnotation, 
	        End endAnnotation, 
	        List<Port> inPorts, 
	        List<Port> outPorts,
	        List<Program> subprograms,
            List<Function> functions
	        
    ) {
	    this(beginAnnotation,  
	         endAnnotation, 
	         inPorts.toArray(new Port[inPorts.size()]),
	         outPorts.toArray(new Port[outPorts.size()]),
	         subprograms.toArray(new Program[subprograms.size()]),
	         EMPTY_CHANNEL_ARRAY,
	         functions.toArray(new Function[functions.size()]));
	}

    public boolean isWorkflow() {
	    return false;
	}
	
	@Override
	public String toString() {
	    return this.beginAnnotation.name;
	}
}
