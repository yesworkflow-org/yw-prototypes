package org.yesworkflow.model;

import java.util.List;

public class Model {

    static final Function[] EMPTY_FUNCTION_ARRAY = new Function[]{};
    
    public final Program program;
    public final Function[] functions;

    public Model(Program program, Function[] functions) {
        this.program = program;
        this.functions = functions;
    }
    
	public Model(Program program, List<Function> functions) {
	    this(program, functions.toArray(new Function[functions.size()]));
	}

	public Model(Program program) {
	    this(program, EMPTY_FUNCTION_ARRAY);
    }
	
	@Override
	public String toString() {
	    return "Model(program=" + program.beginAnnotation.name + ")";
	}
}
