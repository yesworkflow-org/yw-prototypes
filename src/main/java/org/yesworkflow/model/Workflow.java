package org.yesworkflow.model;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
        private List<Port> workflowInPorts = new LinkedList<Port>();
        private List<Port> workflowOutPorts = new LinkedList<Port>();

		private List<Program> nestedPrograms = new LinkedList<Program>();
        private List<Channel> nestedChannels = new LinkedList<Channel>();
		private Map<String,List<Port>> nestedProgramInPorts = new LinkedHashMap<String,List<Port>>();
		private Map<String,Port> nestedProgramOutPorts = new  LinkedHashMap<String,Port>();
        private Map<String,Program> programForName = new HashMap<String,Program>();
        
        private PrintStream stdoutStream = null;
        private PrintStream stderrStream = null;

        public Builder(PrintStream stdoutStream, PrintStream stderrStream) {
            this.stdoutStream = stdoutStream;
            this.stderrStream = stderrStream;
        }
                
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

        public Port inPort(InComment inComment) throws Exception {
            
            // model the outward facing in port
            Port inPort = new Port(inComment, beginComment);
            workflowInPorts.add(inPort);
            
            // model a corresponding, inward-facing out port
            Port outPort = new Port(inComment, beginComment);
            nestedOutPort(outPort);

            // return the outward facing port
            return inPort;
        }
        
        public Port outPort(OutComment outComment) {

            // model the outward facing out port
            Port outPort = new Port(outComment, beginComment);
            workflowOutPorts.add(outPort);
            
            // model a corresponding, inward-facing in port
            Port inPort = new Port(outComment, beginComment);
            nestedInPort(inPort);
            
            // return the outward facing port
            return outPort;
        }
        
		public Builder nestedInPort(Port inPort) {
		    String binding = inPort.portComment.binding();
			addNestedInport(binding, inPort);
			return this;
		}

		private void addNestedInport(String binding, Port inPort) {
			List<Port> ports = this.nestedProgramInPorts.get(binding);
			if (ports == null) {
				ports = new LinkedList<Port>();
				this.nestedProgramInPorts.put(binding, ports);
			}
			ports.add(inPort);
		}
		
		public Builder nestedOutPort(Port outPort) throws Exception {
			
			String binding = outPort.portComment.binding();
			
			// ensure no other writers to this @out binding
			if (nestedProgramOutPorts.containsKey(binding)) {
				throw new Exception("Multiple @out comments bound to " + binding);
			}
			
			// store the @out comment
			this.nestedProgramOutPorts.put(binding, outPort);

			return this;
		}
		
		public Program build() throws Exception {
			
			// if no subprograms then we're building a simple program
			if (nestedPrograms.size() == 0) {				
				return new Program(beginComment, endComment, workflowInPorts, workflowOutPorts);
			}
			
//			pruneUnusedWorkflowInPorts();
//			pruneUnusedWorkflowOutPorts();
			pruneUnusedNestedProgramInPorts();
			pruneUnusedNestedProgramOutPorts();
			buildNestedChannels();
			
			return new Workflow(
                beginComment,
                endComment,
                workflowInPorts,
                workflowOutPorts,
				nestedPrograms,
				nestedChannels
			);
		}
	
        private void pruneUnusedNestedProgramInPorts() {

            List<String> unmatchedInBindings = new LinkedList<String>();
            for (Map.Entry<String, List<Port>> entry : nestedProgramInPorts.entrySet()) {
                String binding = entry.getKey();
                if (!workflowInPorts.contains(binding) && !nestedProgramOutPorts.containsKey(binding)) {
                    
                    stderrStream.println(
                            "WARNING: No nested @out port and no workflow @in port for nested @in '"    +
                            binding                                                                     +
                            "' on '"                                                                    +
                            beginComment.programName                                                    +
                            "'"
                    );
                    unmatchedInBindings.add(binding);
                }
            }
            for (String binding : unmatchedInBindings) nestedProgramInPorts.remove(binding);
        }
	    
        private void pruneUnusedNestedProgramOutPorts() {

            List<String> unmatchedOutBindings = new LinkedList<String>();
            
            for (Entry<String, Port> entry : nestedProgramOutPorts.entrySet()) {
                String binding = entry.getKey();
                if (!workflowOutPorts.contains(binding) && !nestedProgramInPorts.containsKey(binding)) {
                    
                    stderrStream.println(
                            "WARNING: No nested @in port and no workflow @out port for nested @out '"   +
                            binding                                                                     +
                            "' in workflow '"                                                           +
                            beginComment.programName                                                    +
                            "'"
                    );
                    unmatchedOutBindings.add(binding);
                }
            }
            
            for (String binding : unmatchedOutBindings) nestedProgramOutPorts.remove(binding);
        }	
        
        private void buildNestedChannels() throws Exception {
            
            // build the channels between in and out ports
            for (Map.Entry<String, List<Port>> entry : nestedProgramInPorts.entrySet()) {
    
                String binding = entry.getKey();
                List<Port> boundInPorts = entry.getValue();
    
                // get information about the @out port that writes to this binding
                Port boundOutPort = nestedProgramOutPorts.get(binding);
                if (boundOutPort == null) throw new Exception("No @out corresponding to @in " + binding);
                
                String outProgramName = boundOutPort.beginComment.programName;
                Program outProgram = programForName.get(outProgramName);
                
                // iterate over @in ports that bind to the current @out port
                for (Port inPort : boundInPorts) {
                
                    // get information about this @in port
                    String inProgramName = inPort.beginComment.programName;
                    Program inProgram = programForName.get(inProgramName);
    
                    // store the new channel
                    Channel channel = new Channel(outProgram, boundOutPort, inProgram, inPort);
                    nestedChannels.add(channel);
                }   
            }
        }
	}
	
}

