package org.yesworkflow.model;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
		private Map<String,List<Port>> nestedInPorts = new LinkedHashMap<String,List<Port>>();
		private Map<String,Port> nestedOutPorts = new  LinkedHashMap<String,Port>();

        private Map<Port,String> programNameForPort = new HashMap<Port,String>();
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
            Port inPort = new Port(inComment);
            inPorts.add(inPort);
            
            // model a corresponding, inward-facing out port
            Port outPort = new Port(inComment);
            nestedOutPort(outPort, this.beginComment.programName);

            // return the outward facing port
            return inPort;
        }
        
        public Port outPort(OutComment outComment) {

            // model the outward facing out port
            Port outPort = new Port(outComment);
            outPorts.add(outPort);
            
            // model a corresponding, inward-facing in port
            Port inPort = new Port(outComment);
            nestedInPort(inPort, this.beginComment.programName);
            
            // return the outward facing port
            return outPort;
        }
        
		public Builder nestedInPort(Port inPort, String programName) {
		    String binding = inPort.comment.binding();
			addNestedInport(binding, inPort);
			this.programNameForPort.put(inPort, programName);
			return this;
		}

		private void addNestedInport(String binding, Port inPort) {
			List<Port> ports = this.nestedInPorts.get(binding);
			if (ports == null) {
				ports = new LinkedList<Port>();
				this.nestedInPorts.put(binding, ports);
			}
			ports.add(inPort);
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
			
			// remove from model any workflow in ports with no corresponding nested in ports
			for (Port port : inPorts) {
			    if (nestedInPorts.get(port.comment.binding()) == null) {
                    
			        stderrStream.println(
                            "WARNING: No nested @in corresponding to workflow @in '" +
                            port.comment.binding()                                   +
                            "' on '"                                                 +
                            beginComment.programName                                 +
                            "'"
                    );

                    inPorts.remove(port);
			    }
			}

            // remove from model any workflow in ports with no corresponding nested in ports
            for (Port port : outPorts) {
                if (nestedOutPorts.get(port.comment.binding()) == null) {
                    
                    stderrStream.println(
                            "WARNING: No nested @out corresponding to workflow @out '" +
                            port.comment.binding()                                   +
                            "' on '"                                                 +
                            beginComment.programName                                 +
                            "'"
                    );

                    outPorts.remove(port);
                }
            }			
			
			// build the channels between in and out ports
			for (Map.Entry<String, List<Port>> entry : nestedInPorts.entrySet()) {

				String binding = entry.getKey();
				List<Port> boundInPorts = entry.getValue();

				// get information about the @out port that writes to this binding
				Port boundOutPort = nestedOutPorts.get(binding);

				if (boundOutPort == null) {
				    //throw new Exception("No @out corresponding to @in " + binding);
					continue;
				}
				
			    String outProgramName = programNameForPort.get(boundOutPort);
				Program outProgram = programForName.get(outProgramName);
				
				// iterate over @in ports that bind to the current @out port
				for (Port inPort : boundInPorts) {
				
					// get information about this @in port
					String inProgramName = programNameForPort.get(inPort);
					Program inProgram = programForName.get(inProgramName);
	
					// store the new channel
					Channel channel = new Channel(outProgram, boundOutPort, inProgram, inPort);
					nestedChannels.add(channel);
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

