package org.yesworkflow.model;

import java.util.List;

public class Model {

    static final Function[] EMPTY_FUNCTION_ARRAY = new Function[]{};
    static final Data[] EMPTY_DATA_ARRAY = new Data[]{};
    
    public final Program program;
    public final Function[] functions;
    public final Data[] data;

    public Model(Program program, Function[] functions, Data[] data) {
        
        if (program == null) throw new IllegalArgumentException("Null program argument passed to Model contructor.");
        if (functions == null) throw new IllegalArgumentException("Null functions argument passed to Model contructor.");
        
        this.program = program;
        this.functions = functions;
        this.data = data;
    }
    
	public Model(Program program, List<Function> functions, List<Data> data) {
        this(program, functionListToArray(functions), dataListToArray(data));
	}
	
	private static Function[] functionListToArray(List<Function> functions) {
        if (functions == null) throw new IllegalArgumentException("Null functions argument passed to Model contructor.");	    
	    return functions.toArray(new Function[functions.size()]);
	}

   private static Data[] dataListToArray(List<Data> data) {
        if (data == null) throw new IllegalArgumentException("Null data argument passed to Model contructor.");       
        return data.toArray(new Data[data.size()]);
    }

	
	public Model(Program program) {
	    this(program, EMPTY_FUNCTION_ARRAY, EMPTY_DATA_ARRAY);
    }
	
	@Override
	public String toString() {
	    return "Model(program=" + program.beginAnnotation.value() + ")";
	}
}
