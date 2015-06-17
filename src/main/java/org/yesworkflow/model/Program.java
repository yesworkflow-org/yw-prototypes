package org.yesworkflow.model;

import java.util.LinkedList;
import java.util.List;

import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.End;
import org.yesworkflow.annotations.Param;

public class Program {

    static final Program[] EMPTY_PROGRAM_ARRAY = new Program[]{};
    static final Function[] EMPTY_FUNCTION_ARRAY = new Function[]{};
    static final Channel[] EMPTY_CHANNEL_ARRAY = new Channel[]{};

    public final Integer id;
    public final Begin beginAnnotation;
    public final End endAnnotation;
    public final Port[] inPorts;
    public final Port[] outPorts;
    public final Program[] programs;
    public final Channel[] channels;
    public final Function[] functions;
    public final String name;
    
    private Integer subworkflowCount = null;

    public Program(
            Integer id,
            String name,
            Begin beginAnnotation, 
            End endAnnotation, 
            Port[] inPorts, 
            Port[] outPorts, 
            Program[] programs,
            Channel[] channels,
            Function[] functions
    ) {
        this.id = id;
        this.beginAnnotation = beginAnnotation;
        this.endAnnotation = endAnnotation;
        this.inPorts = inPorts;
        this.outPorts = outPorts;
        this.programs = programs;
        this.channels = channels;
        this.functions = functions;
        this.name = name;
    }
    
	public Program(
	        Integer id,
	        String name,
	        Begin beginAnnotation, 
	        End endAnnotation, 
	        List<Port> inPorts, 
	        List<Port> outPorts,
	        List<Program> subprograms,
            List<Function> functions
    ) {
	    this(id,
	         name,
	         beginAnnotation,  
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
    

    public List<String> outerParamBindings() {
        List<String> bindings = new LinkedList<String>();
        for (Port p : inPorts) {
            if (p.flowAnnotation instanceof Param) {
                bindings.add(p.flowAnnotation.binding());
            }
        }
        return bindings;
    }

    public List<String> outerDataBindings() {
        List<String> bindings = new LinkedList<String>();
        for (Port p : inPorts) {
            if (! (p.flowAnnotation instanceof Param)) {
                bindings.add(p.flowAnnotation.binding());
            }
        }
        for (Port p : outPorts) {
            bindings.add(p.flowAnnotation.binding());
        }
        return bindings;
    }

    public List<String> outerBindings() {
        List<String> bindings = new LinkedList<String>();
        for (Port p : inPorts) {
            bindings.add(p.flowAnnotation.binding());
        }
        for (Port p : outPorts) {
            bindings.add(p.flowAnnotation.binding());
        }
        return bindings;
    }
        
    public List<Channel> innerParamChannels() {
        List<Channel> pc = new LinkedList<Channel>();
        for (Channel c : channels) {
            if (c.isParam) {
                pc.add(c);
            }
        }
        return pc;
    }
 
    public List<Channel> innerDataChannels() {
        List<Channel> dc = new LinkedList<Channel>();
        for (Channel c : channels) {
            if (! c.isParam) {
                dc.add(c);
            }
        }
        return dc;
    }
    
    public List<Channel> innerChannels() {
        List<Channel> ch = new LinkedList<Channel>();
        for (Channel c : channels) {
            ch.add(c);
        }
        return ch;
    }
    
    public boolean hasChannelForBinding(String binding) {
        for (Channel c : channels) {
            if (binding.equals(c.sourcePort.flowAnnotation.binding())) {
                return true;
            }
        }
        return false;
    }
    
	@Override
	public String toString() {
	    return this.name;
	}

    public Program getSubprogram(String subprogramName) {

        if (subprogramName.equals(name)) return this;
        
        for (Program nestedProgram : programs) {
            Program match = nestedProgram.getSubprogram(subprogramName);
            if (match != null) return match;
        }
        
        for (Program nestedFunction : functions) {
            Program match = nestedFunction.getSubprogram(subprogramName);
            if (match != null) return match;
        }
        
        return null;
    }

    public int subworkflowCount() {
        if (subworkflowCount == null) {
            subworkflowCount = 0;
            for (Program p : programs) {
               if (p.isWorkflow()) ++subworkflowCount;
            }
        }
        return subworkflowCount;
    }
}
