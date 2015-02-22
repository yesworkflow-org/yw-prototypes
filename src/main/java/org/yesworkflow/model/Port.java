package org.yesworkflow.model;

import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.Flow;

public class Port {
	
    public final Begin beginAnnotation;
	public final Flow flowAnnotation;
	
	public Port(Flow flowAnnotation, Begin beginAnnotation) {
		this.flowAnnotation = flowAnnotation;
		this.beginAnnotation = beginAnnotation;
	}
	
   @Override
    public String toString() {
        return String.format("%s:%s", this.beginAnnotation.name, this.flowAnnotation.binding());
    }
}
