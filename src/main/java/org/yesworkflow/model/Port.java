package org.yesworkflow.model;

import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.Flow;

public class Port {
	
    private static Integer nextId = 1;
    
    public final Begin beginAnnotation;
	public final Flow flowAnnotation;
	private final Integer id;
	
	public Port(Flow flowAnnotation, Begin beginAnnotation) {
		this.flowAnnotation = flowAnnotation;
		this.beginAnnotation = beginAnnotation;
		this.id = nextId++;
	}
	
   @Override
    public String toString() {
        return String.format("%s:%s", this.beginAnnotation.name, this.flowAnnotation.binding());
    }
}
