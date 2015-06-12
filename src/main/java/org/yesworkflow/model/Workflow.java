package org.yesworkflow.model;

import java.util.List;

import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.End;

public class Workflow extends Program {

    public Workflow(
           Integer id,
           String name,
           Begin beginAnnotation,
           End endAnnotation,
           List<Port> inPorts,
           List<Port> outPorts,
           List<Program> programs,
           List<Channel> channels,
           List<Function> functions
    ) {
        super(id, name, beginAnnotation, endAnnotation, 
             inPorts.toArray(new Port[inPorts.size()]),
             outPorts.toArray(new Port[outPorts.size()]),
             programs.toArray(new Program[programs.size()]),
             channels.toArray(new Channel[channels.size()]),
             functions.toArray(new Function[functions.size()]));
    }

    @Override
    public boolean isWorkflow() {
        return true;
    }
}

