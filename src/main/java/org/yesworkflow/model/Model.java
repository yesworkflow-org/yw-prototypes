package org.yesworkflow.model;

import java.util.List;

public class Model {

    static final Function[] EMPTY_FUNCTION_ARRAY = new Function[]{};
    static final Data[] EMPTY_DATA_ARRAY = new Data[]{};
    
    public final Workflow workflow;
    public final Function[] functions;
    public final Data[] data;

    public Model(Workflow workflow, Function[] functions, Data[] data) {
        
        if (workflow == null) throw new IllegalArgumentException("Null workflow argument passed to Model contructor.");
        if (functions == null) throw new IllegalArgumentException("Null functions argument passed to Model contructor.");
        if (data == null) throw new IllegalArgumentException("Null data argument passed to Model contructor.");
        
        this.workflow = workflow;
        this.functions = functions;
        this.data = data;
    }
    
	public Model(Workflow workflow, List<Function> functions, List<Data> data) {
        this(workflow, functionListToArray(functions), dataListToArray(data));
	}
	
	private static Function[] functionListToArray(List<Function> functions) {
        if (functions == null) throw new IllegalArgumentException("Null functions argument passed to Model contructor.");	    
	    return functions.toArray(new Function[functions.size()]);
	}

   private static Data[] dataListToArray(List<Data> data) {
        if (data == null) throw new IllegalArgumentException("Null data argument passed to Model contructor.");       
        return data.toArray(new Data[data.size()]);
    }

	
	public Model(Workflow workflow) {
	    this(workflow, EMPTY_FUNCTION_ARRAY, EMPTY_DATA_ARRAY);
    }
	
	@Override
	public String toString() {
	    return "Model(program=" + workflow.beginAnnotation.value() + ")";
	}
}
