package org.yesworkflow.model;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.End;
import org.yesworkflow.annotations.Flow;
import org.yesworkflow.annotations.In;
import org.yesworkflow.annotations.Out;
import org.yesworkflow.annotations.Return;
import org.yesworkflow.db.YesWorkflowDB;

public class WorkflowBuilder {
		
        public final Long programId;
        public final String parentName;
        private final WorkflowBuilder parentBuilder;
        private String name;
        private Begin beginAnnotation;
        private End endAnnotation;
        private List<Port> workflowInPorts = new LinkedList<Port>();
        private List<Port> workflowOutPorts = new LinkedList<Port>();
        private List<Port> workflowReturnPorts = new LinkedList<Port>();

        private YesWorkflowDB ywdb;
		private List<Program> nestedPrograms = new LinkedList<Program>();
        private List<Channel> nestedChannels = new LinkedList<Channel>();
        private List<Function> nestedFunctions = new LinkedList<Function>();
        private List<Data> nestedData = new LinkedList<Data>();
		private Map<String,List<Port>> nestedProgramInPorts = new LinkedHashMap<String,List<Port>>();
		private Map<String,Port> nestedProgramOutPorts = new  LinkedHashMap<String,Port>();
        private Map<String,Port> nestedProgramReturnPorts = new  LinkedHashMap<String,Port>();
        private Map<String,Program> programForName = new HashMap<String,Program>();
        private Map<String,Function> functionForName = new HashMap<String,Function>();        
        private Map<String,Data> dataForBinding = new HashMap<String,Data>();
        
        private static Integer nextChannelId = 1;
        private static Integer nextPortId = 1;
        private static Integer nextDataId = 1;
        
        @SuppressWarnings("unused")
        private PrintStream stdoutStream = null;
        
        @SuppressWarnings("unused")
        private PrintStream stderrStream = null;

        public WorkflowBuilder(YesWorkflowDB ywdb, PrintStream stdoutStream, PrintStream stderrStream) throws SQLException {
            this(ywdb, null, null, stdoutStream, stderrStream);
        }

        public WorkflowBuilder(YesWorkflowDB ywdb, String parentName, WorkflowBuilder parentBuilder, PrintStream stdoutStream, PrintStream stderrStream) throws SQLException {
            this.ywdb = ywdb;
            this.parentName = parentName;
            this.parentBuilder = parentBuilder;
            this.stdoutStream = stdoutStream;
            this.stderrStream = stderrStream;
            this.programId = (parentBuilder == null) ? null :
                          ywdb.insertDefaultProgram(parentBuilder.programId);
        }
        
		public WorkflowBuilder begin(Begin annotation) {
			this.beginAnnotation = annotation;
			this.name = (parentName == null) ? annotation.name : parentName + "." + annotation.name; 
			return this;
		}

		public String getName() {
		    return this.name;
		}
		
        public void end(End annotation) {
            this.endAnnotation = annotation;
        }

		public String getProgramName() {
			return beginAnnotation.name;
		}
		
		public Begin getBeginAnnotation() {
		    return beginAnnotation;
		}
		
		public List<Data> getData() {
		    return nestedData;
		}
		
		public WorkflowBuilder nestedProgram(Program program) {
			this.nestedPrograms.add(program);
			this.programForName.put(program.beginAnnotation.name, program);
			return this;
		}

        public WorkflowBuilder nestedFunction(Function function) {
            this.nestedFunctions.add(function);
            this.functionForName.put(function.beginAnnotation.name, function);
            return this;
        }
		
        public void inPort(In inPortAnnotation) throws Exception {
            Port inPort = addPort(inPortAnnotation);
            workflowInPorts.add(inPort);
            nestedOutPort(inPort);
            parentBuilder.nestedInPort(inPort);
        }
        
        public void outPort(Out outPortAnnotation) throws Exception {
            Port outPort = addPort(outPortAnnotation);
            workflowOutPorts.add(outPort);
            nestedInPort(outPort);
            parentBuilder.nestedOutPort(outPort);
        }

        public void returnPort(Return returnAnnotation) {
            Port returnPort = addPort(returnAnnotation);
            workflowReturnPorts.add(returnPort);
            nestedInPort(returnPort);
        }
        
        private Port addPort(Flow portAnnotation) {
            Data data = parentBuilder.addNestedData(portAnnotation.binding());
            Port port = new Port(nextPortId++, data, portAnnotation, beginAnnotation);
            return port;
        }

        private Data addNestedData(String binding) {
            Data data = dataForBinding.get(binding);
            if (data == null) {
                data = new Data(nextDataId++, binding);
                nestedData.add(data);
                dataForBinding.put(binding, data);
            }
            return data;
        }
        
        private WorkflowBuilder nestedInPort(Port inPort) {
		    String binding = inPort.flowAnnotation.binding();
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
		
		private WorkflowBuilder nestedOutPort(Port outPort) throws Exception {
			
			String binding = outPort.flowAnnotation.binding();
			
			// ensure no other writers to this @out binding
			if (nestedProgramOutPorts.containsKey(binding)) {
				throw new Exception("Multiple @out comments bound to " + binding);
			}
			
			// store the @out comment
			this.nestedProgramOutPorts.put(binding, outPort);

			return this;
		}

        @SuppressWarnings("unused")
        private WorkflowBuilder nestedReturnPort(Port returnPort) throws Exception {
            String binding = returnPort.flowAnnotation.binding();
            
            // ensure no other writers to this @out binding
            if (nestedProgramOutPorts.containsKey(binding)) {
                throw new Exception("Multiple outputs bound to " + binding);
            }
            
            // store the @out comment
            this.nestedProgramReturnPorts.put(binding, returnPort);

            return this;
        }

		private WorkflowBuilder buildChannels() throws Exception {
            pruneUnusedNestedProgramInPorts();
            pruneUnusedNestedProgramOutPorts();
            buildInternalChannels();
            buildInflowChannels();
            buildOutflowChannels();
            return this;
		}
		
		public Program build() throws Exception {	
		    if (workflowReturnPorts.size() > 0) return buildFunction();
            buildChannels();
            if (nestedChannels.size() > 0) return buildWorkflow();
            return buildProgram();
		}
		
		public Function buildFunction() throws Exception {
            buildChannels();
            ywdb.updateProgram(programId, beginAnnotation.id, 
                    endAnnotation.id, beginAnnotation.name, name, false, true);
		    return new Function(
		            programId,
		            name,
		            beginAnnotation,
                    endAnnotation,
                    nestedData,
                    workflowInPorts,
                    workflowOutPorts,
                    workflowReturnPorts,
                    nestedPrograms,
                    nestedChannels,
                    nestedFunctions
		            );
        }
		
        private Program buildProgram() throws Exception {
            ywdb.updateProgram(programId, beginAnnotation.id, 
                    endAnnotation.id, beginAnnotation.name, name, false, false);
            return new Program(
                    programId,
                    name,
                    beginAnnotation, 
                    endAnnotation, 
                    nestedData,
                    workflowInPorts, 
                    workflowOutPorts,
                    nestedPrograms,
                    nestedFunctions
                    );
        }
	            
        private Workflow buildWorkflow() throws Exception {
            ywdb.updateProgram(programId, beginAnnotation.id, 
                    endAnnotation.id, beginAnnotation.name, name, true, false);
			return new Workflow(
                    programId,
			        name,
                    beginAnnotation,
                    endAnnotation,
                    nestedData,
                    workflowInPorts,
                    workflowOutPorts,
                    nestedPrograms,
                    nestedChannels,
                    nestedFunctions
		            );
		}
	
        private void pruneUnusedNestedProgramInPorts() {

            List<String> unmatchedInBindings = new LinkedList<String>();
            for (Map.Entry<String, List<Port>> entry : nestedProgramInPorts.entrySet()) {
                String binding = entry.getKey();
                if (!workflowInPorts.contains(binding) && !nestedProgramOutPorts.containsKey(binding)) {
                    unmatchedInBindings.add(binding);
                }
            }
            for (String binding : unmatchedInBindings) {
                nestedProgramInPorts.remove(binding);
            }
        }
	    
        private void pruneUnusedNestedProgramOutPorts() {

            List<String> unmatchedOutBindings = new LinkedList<String>();
            
            for (Entry<String, Port> entry : nestedProgramOutPorts.entrySet()) {
                String binding = entry.getKey();
                if (!workflowOutPorts.contains(binding) && !nestedProgramInPorts.containsKey(binding)) {
                    unmatchedOutBindings.add(binding);
                }
            }
            
            for (String binding : unmatchedOutBindings) {
                nestedProgramOutPorts.remove(binding);
            }
        }
        
        private void buildInternalChannels() throws Exception {
            for (Map.Entry<String, List<Port>> entry : nestedProgramInPorts.entrySet()) {
                String binding = entry.getKey();
                List<Port> boundInPorts = entry.getValue();
                Port boundOutPort = nestedProgramOutPorts.get(binding);
                if (boundOutPort == null) throw new Exception("No @out corresponding to @in " + binding);
                String outProgramName = boundOutPort.beginAnnotation.name;
                Program outProgram = programForName.get(outProgramName);
                for (Port inPort : boundInPorts) {
                    String inProgramName = inPort.beginAnnotation.name;
                    Program inProgram = programForName.get(inProgramName);
                    Channel channel = new Channel(nextChannelId++, inPort.data, outProgram, boundOutPort, inProgram, inPort);
                    nestedChannels.add(channel);
                }   
            }
        }        
        
        private void buildInflowChannels() throws Exception {
            for (Port workflowPort : workflowInPorts) {
                String binding = workflowPort.flowAnnotation.binding();
                Collection<Port> matchingInPorts = nestedProgramInPorts.get(binding);
                if (matchingInPorts != null) {
                    for (Port inPort : matchingInPorts) {
                        String inProgramName = inPort.beginAnnotation.name;
                        Program inProgram = programForName.get(inProgramName);
                        Channel channel = new Channel(nextChannelId++, workflowPort.data, null, workflowPort, inProgram, inPort);
                        nestedChannels.add(channel);
                    }
                }
            }
        }

        private void buildOutflowChannels() throws Exception {
            for (Port workflowPort : workflowOutPorts) {
                String binding = workflowPort.flowAnnotation.binding();
                Port matchingOutPort = nestedProgramOutPorts.get(binding);
                if (matchingOutPort != null) {
                    String outProgramName = matchingOutPort.beginAnnotation.name;
                    Program outProgram = programForName.get(outProgramName);
                    Channel channel = new Channel(nextChannelId++, matchingOutPort.data, outProgram, matchingOutPort, null, workflowPort);
                    nestedChannels.add(channel);
                }
            }
        }
}