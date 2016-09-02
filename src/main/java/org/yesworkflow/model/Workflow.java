package org.yesworkflow.model;

import java.sql.SQLException;
import java.util.List;

import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.End;
import org.yesworkflow.db.YesWorkflowDB;

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
	   	 super(id, name, beginAnnotation, endAnnotation, data,
	         	inPorts, outPorts, programs, channels, functions);	
	}
    
    @Override
    public boolean isWorkflow() {
        return true;
    }

	public static Workflow createFromProgram(YesWorkflowDB ywdb, Program program) throws SQLException {
		if (program.programs.length > 0) {
			return workflowFromProgramWithChildren(program);
		} else {
			return workflowFromProgramWithNoChildren(ywdb, program);
		}
	}
	
    private static Workflow workflowFromProgramWithChildren(Program program) {
    	return new Workflow( 
    			program.id, 
    			program.name, 
    			program.beginAnnotation, 
    			program.endAnnotation, 
    			program.data,
    			program.inPorts,
    			program.outPorts,
    			program.programs,
    			program.channels,
    			program.functions
    			);
    }
		
	private static Workflow workflowFromProgramWithNoChildren(YesWorkflowDB ywdb, Program parent) throws SQLException {
		
		 Port[] childInPorts = new Port[parent.inPorts.length];
		 for (int i = 0; i < parent.inPorts.length; ++i) {
			 Port parentInPort = parent.inPorts[i];
			 childInPorts[i] = new Port(parentInPort.data, parentInPort.flowAnnotation, parentInPort.beginAnnotation);
		 }
		 
		 Port[] childOutPorts = new Port[parent.outPorts.length];
		 for (int i = 0; i < parent.outPorts.length; ++i) {
			 Port parentOutPort = parent.outPorts[i];
			 childOutPorts[i] = new Port(parentOutPort.data, parentOutPort.flowAnnotation, parentOutPort.beginAnnotation);
		 }
		 		 
		 Long childProgramId = ywdb.insertDefaultProgramBlock(parent.id);
		 String qualifiedChilidName = parent.name + "." + parent.name;
		 ywdb.updateProgramBlock(childProgramId, parent.beginAnnotation.id, parent.endAnnotation.id, parent.name, qualifiedChilidName, false, false);
		 ywdb.updateProgramBlock(parent.id, parent.beginAnnotation.id, parent.endAnnotation.id, parent.name, parent.name, true, false);
		 
		 Program child = new Program(childProgramId, parent.name, parent.beginAnnotation, parent.endAnnotation, 
				 					   new Data[] {}, childInPorts, childOutPorts,  
				 					   new Program[] {}, new Channel[] {}, new Function[] {});

		 Channel[] childChannels = new Channel[parent.inPorts.length + parent.outPorts.length];
		 int childChannelIndex = 0;
		 for (int i = 0; i < parent.outPorts.length; ++i) {
			 childChannels[childChannelIndex++] = new Channel(parent.outPorts[i].data, child, childOutPorts[i], null, parent.outPorts[i]);
		 }
		 for (int i = 0; i < parent.inPorts.length; ++i) {
			 childChannels[childChannelIndex++] = new Channel(parent.inPorts[i].data, null, parent.inPorts[i], child, childInPorts[i]);
		 }

		 return new Workflow(
				 	parent.id, 
	    			parent.name, 
	    			parent.beginAnnotation, 
	    			parent.endAnnotation, 
	                parent.data,
	                parent.inPorts,
	                parent.outPorts,
	                new Program[] { child },
	                childChannels,
	                parent.functions
	                );
	}
}