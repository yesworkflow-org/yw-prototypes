package org.yesworkflow.model;

import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.Flow;

public class Port {

    public final Integer id;
    public final Begin beginAnnotation;
	public final Flow flowAnnotation;
	
	public Port(Integer id, Flow flowAnnotation, Begin beginAnnotation) {
	    this.id = id;
		this.flowAnnotation = flowAnnotation;
		this.beginAnnotation = beginAnnotation;
	}
	
   @Override
    public String toString() {
        return String.format("%s:%s", this.beginAnnotation.name, this.flowAnnotation.binding());
    }
}
