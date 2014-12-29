package org.yesworkflow.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.MultiValueMap;
import org.yesworkflow.comments.BeginComment;
import org.yesworkflow.comments.EndComment;
import org.yesworkflow.comments.InComment;
import org.yesworkflow.comments.OutComment;

public class Workflow extends Program {
	
	public final Program[] programs;
	public final Channel[] channels;
    
	public Workflow(
        BeginComment beginComment,
        EndComment endComment,
        List<Port> inPorts,
        List<Port> outPorts,
        List<Program> programs,
        List<Channel> channels
    ) {

	    super(beginComment, endComment, inPorts, outPorts);
		
    	this.programs = programs.toArray(new Program[programs.size()]);
    	this.channels = channels.toArray(new Channel[channels.size()]);
	}
	
	public static class Builder {
		
        private BeginComment beginComment;
        private EndComment endComment;
        private List<Port> inPorts = new LinkedList<Port>();
        private List<Port> outPorts = new LinkedList<Port>();

		private List<Program> nestedPrograms = new LinkedList<Program>();
        private List<Channel> nestedChannels = new LinkedList<Channel>();
		private MultiValueMap<String,Port> nestedInPorts = new MultiValueMap<String,Port>();
		private Map<String,Port> nestedOutPorts = new HashMap<String,Port>();

        private Map<Port,String> programNameForPort = new HashMap<Port,String>();
        private Map<String,Program> programForName = new HashMap<String,Program>();

		public Builder begin(BeginComment comment) {
			this.beginComment = comment;
			return this;
		}

        public void end(EndComment comment) {
            this.endComment = comment;
        }

		public String getProgramName() {
			return beginComment.programName;
		}
		
		public Builder nestedProgram(Program program) {
			this.nestedPrograms.add(program);
			this.programForName.put(program.beginComment.programName, program);
			return this;
		}

        public Port inPort(InComment inComment) {
            Port port = new Port(inComment);
            inPorts.add(port);
            return port;
        }
        
        public Port outPort(OutComment outComment) {
            Port port = new Port(outComment);
            outPorts.add(port);
            return port;
        }
        
		public Builder nestedInPort(Port inPort, String programName) {
		    String binding = inPort.comment.binding();
			this.nestedInPorts.put(binding, inPort);
			this.programNameForPort.put(inPort, programName);
			return this;
		}
		
		public Builder nestedOutPort(Port outPort, String programName) throws Exception {
			
			String binding = outPort.comment.binding();
			
			// ensure no other writers to this @out binding
			if (nestedOutPorts.containsKey(binding)) {
				throw new Exception("Multiple @out comments bound to " + binding);
			}
			
			// store the @out comment
			this.nestedOutPorts.put(binding, outPort);
			this.programNameForPort.put(outPort, programName);

			return this;
		}
		
		public Program build() throws Exception {
			
			// if no subprograms then we're building a simple program
			if (nestedPrograms.size() == 0) {				
				return new Program(beginComment, endComment, inPorts, outPorts);
			}
			
			// otherwise we're building a workflow and must build its channels
			for (Iterator<Entry<String, Port>> inPortIterator = nestedInPorts.iterator(); inPortIterator.hasNext(); ) {
				
				// get information about this @in port
				Map.Entry<String,Port> entry = inPortIterator.next();
				String binding = entry.getKey();
				Port inPort = entry.getValue();
				String inProgramName = programNameForPort.get(inPort);
				Program inProgram = programForName.get(inProgramName);

				// get information about corresponding @out port
				Port outPort = nestedOutPorts.get(binding);
				if (outPort != null) {
    			
				    String outProgramName = programNameForPort.get(outPort);
    				Program outProgram = programForName.get(outProgramName);
    
    				// store the new channel
    				Channel channel = new Channel(outProgram, outPort, inProgram, inPort);
    				nestedChannels.add(channel);
	            
				} else {
	                
				    //throw new Exception("No @out corresponding to @in " + binding);
	            }
			}
			
			return new Workflow(
                beginComment,
                endComment,
                inPorts,
                outPorts,
				nestedPrograms,
				nestedChannels
			);
		}
	}
}

