package org.yesworkflow.model;

import java.util.List;

public class Model {

    static final Function[] EMPTY_FUNCTION_ARRAY = new Function[]{};
    
    public final Workflow workflow;
    public final Function[] functions;

    public Model(Workflow workflow, Function[] functions) {
        this.workflow = workflow;
        this.functions = functions;
    }
    
	public Model(Workflow workflow, List<Function> functions) {
	    this(workflow, functions.toArray(new Function[functions.size()]));
	}

	public Model(Workflow workflow) {
	    this(workflow, EMPTY_FUNCTION_ARRAY);
    }
	
	@Override
	public String toString() {
	    return "Model(workflow=" + workflow.beginAnnotation.name + ")";
	}
}
