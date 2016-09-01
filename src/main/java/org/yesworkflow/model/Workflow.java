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
		Channel[] channels;
		
    	 if (p.programs.length > 0) {
    		 programs = p.programs;
    		 channels = p.channels;
    	 } else {
    		 
    		 Port[] inPorts = new Port[p.inPorts.length];
    		 Port[] outPorts = new Port[p.outPorts.length];
    		 for (int i = 0; i < p.outPorts.length; ++i) {
    			 Port outerPort = p.outPorts[i];
    			 outPorts[i] = new Port(100, outerPort.data, outerPort.flowAnnotation, outerPort.beginAnnotation);
    		 }
    		 for (int i = 0; i < p.inPorts.length; ++i) {
    			 Port outerPort = p.inPorts[i];
    			 inPorts[i] = new Port(100, outerPort.data, outerPort.flowAnnotation, outerPort.beginAnnotation);
    		 }
    		 
    		 Program program = new Program(80L, p.name, p.beginAnnotation, p.endAnnotation, 
    				 					   new Data[] {}, inPorts, outPorts,  
    				 					   new Program[] {}, new Channel[] {}, new Function[] {});

    		 channels = new Channel[p.inPorts.length + p.outPorts.length];
    		 int channelIndex = 0;
    		 for (int i = 0; i < p.outPorts.length; ++i) {
    			 channels[channelIndex++] = new Channel(100, p.outPorts[i].data, program, outPorts[i], null, p.outPorts[i]);
    		 }
    		 for (int i = 0; i < p.inPorts.length; ++i) {
    			 channels[channelIndex++] = new Channel(100, p.inPorts[i].data, null, p.inPorts[i], program, inPorts[i]);
    		 }

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
    	                channels,
    	                p.functions
    	                );
    }
    
    @Override
    public boolean isWorkflow() {
        return true;
    }
}