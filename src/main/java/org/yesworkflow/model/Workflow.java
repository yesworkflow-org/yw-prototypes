package org.yesworkflow.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.MultiValueMap;

import org.yesworkflow.comments.BeginComment;
import org.yesworkflow.comments.InComment;
import org.yesworkflow.comments.OutComment;

public class Workflow extends Program {
	
	public final Program[] programs;
	public final Channel[] channels;
    
	public Workflow(List<Program> programs, List<Channel> channels, BeginComment comment) {
		super(comment);
		
    	this.programs = programs.toArray(new Program[programs.size()]);
    	this.channels = channels.toArray(new Channel[channels.size()]);
	}
	
	public static class Builder {
		
		private List<Program> programs = new LinkedList<Program>();
		private List<Channel> channels = new LinkedList<Channel>();
		private Map<Port,String> programNameForPort = new HashMap<Port,String>();
		private Map<String,Program> programForName = new HashMap<String,Program>();
		private MultiValueMap<String,Port> inPorts = new MultiValueMap<String,Port>();
		private Map<String,Port> outPorts = new HashMap<String,Port>();
		private BeginComment beginComment;
		
		public Builder begin(BeginComment comment) {
			this.beginComment = comment;
			return this;
		}
		
		public String getProgramName() {
			return beginComment.programName;
		}
		
		public Builder program(Program program) {
			this.programs.add(program);
			this.programForName.put(program.beginComment.programName, program);
			return this;
		}

		public Builder in(InComment inComment, String programName) {
			
		    String binding = inComment.binding();
			
			Port port = new Port(inComment);
			this.inPorts.put(binding, port);
			this.programNameForPort.put(port, programName);
			return this;
		}

		public Builder out(OutComment outComment, String programName) throws Exception {
			
			String binding = outComment.binding();
			
			// ensure no other writers to this @out binding
			if (outPorts.containsKey(binding)) {
				throw new Exception("Multiple @out comments bound to " + binding);
			}
			
			// store the @out comment
			Port port = new Port(outComment);
			this.outPorts.put(binding, port);
			this.programNameForPort.put(port, programName);

			return this;
		}
		
		public Program build() throws Exception {
			
			// if no subprograms then we're building a simple program
			if (programs.size() == 0) {				
				return new Program(beginComment);
			}
			
			// otherwise we're building a workflow and must build its channels
			for (Iterator<Entry<String, Port>> inPortIterator = inPorts.iterator(); inPortIterator.hasNext(); ) {
				
				// get information about this @in port
				Map.Entry<String,Port> entry = inPortIterator.next();
				String binding = entry.getKey();
				Port inPort = entry.getValue();
				String inProgramName = programNameForPort.get(inPort);
				Program inProgram = programForName.get(inProgramName);

				// get information about corresponding @out port
				Port outPort = outPorts.get(binding);
				if (outPort != null) {
    			
				    String outProgramName = programNameForPort.get(outPort);
    				Program outProgram = programForName.get(outProgramName);
    
    				// store the new channel
    				Channel channel = new Channel(outProgram, outPort, inProgram, inPort);
    				channels.add(channel);
	            
				} else {
	                
				    //throw new Exception("No @out corresponding to @in " + binding);
	            }
			}
			
			return new Workflow(
					programs,
					channels,
					beginComment
			);
		}
	}
}

