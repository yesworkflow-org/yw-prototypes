package org.yesworkflow.model;

import java.util.List;

public class Model {

    static final Function[] EMPTY_FUNCTION_ARRAY = new Function[]{};
    
    public final Program program;
    public final Function[] functions;

    public Model(Program program, Function[] functions) {
        
        if (program == null) throw new IllegalArgumentException("Null program argument passed to Model contructor.");
        if (functions == null) throw new IllegalArgumentException("Null functions argument passed to Model contructor.");
        
        this.program = program;
        this.functions = functions;
    }
    
	public Model(Program program, List<Function> functions) {
        this(program, functionListToArray(functions));
	}
	
	private static Function[] functionListToArray(List<Function> functions) {
        if (functions == null) throw new IllegalArgumentException("Null functions argument passed to Model contructor.");	    
	    return functions.toArray(new Function[functions.size()]);
	}

	public Model(Program program) {
	    this(program, EMPTY_FUNCTION_ARRAY);
    }
	
	@Override
	public String toString() {
	    return "Model(program=" + program.beginAnnotation.name + ")";
	}
}
