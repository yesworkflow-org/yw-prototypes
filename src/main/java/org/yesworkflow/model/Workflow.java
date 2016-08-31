package org.yesworkflow.model;

import java.util.List;

import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.End;

public class Workflow extends Program {

    public Workflow(
           Long id,
           String name,
           Begin beginAnnotation,
           End endAnnotation,
           List<Data> data,
           List<Port> inPorts,
           List<Port> outPorts,
           List<Program> programs,
           List<Channel> channels,
           List<Function> functions
    ) {
        super(id, name, beginAnnotation, endAnnotation, 
             data.toArray(new Data[data.size()]),
             inPorts.toArray(new Port[inPorts.size()]),
             outPorts.toArray(new Port[outPorts.size()]),
             programs.toArray(new Program[programs.size()]),
             channels.toArray(new Channel[channels.size()]),
             functions.toArray(new Function[functions.size()]));
    }

    public Workflow(Program p) {
    	 super(	p.id, 
    			p.name, 
    			p.beginAnnotation, 
    			p.endAnnotation, 
                p.data,
                p.inPorts,
                p.outPorts,
                p.programs,
                p.channels,
                p.functions
                );
    }

    public Workflow(Long id, String name, Begin beginAnnotation, End endAnnotation, Data[] data, Port[] inPorts,
			Port[] outPorts, Program[] programs, Channel[] channels, Function[] functions) {
	   	 super(	id, name, beginAnnotation, endAnnotation, data,
	         	inPorts, outPorts, programs, channels, functions);	
	}

	public static Workflow createFromProgram(Program p) {
		Program[] programs;
    	 if (p.programs.length > 0) {
    		 programs = p.programs;
    	 } else {
    		 Program program = new Program(80L, p.name, p.beginAnnotation, p.endAnnotation, 
    				 					   new Data[] {}, new Port[] {}, new Port[] {}, 
    				 					   new Program[] {}, new Channel[] {}, new Function[] {});
    		 programs = new Program[] { program };
    	 }
    	 
    	 return new Workflow(
    			 		p.id, 
    	    			p.name, 
    	    			p.beginAnnotation, 
    	    			p.endAnnotation, 
    	                p.data,
    	                p.inPorts,
    	                p.outPorts,
    	                programs,
    	                p.channels,
    	                p.functions
    	                );
    	 
    }
    
    @Override
    public boolean isWorkflow() {
        return true;
    }
}