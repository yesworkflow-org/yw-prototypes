package org.yesworkflow.model;

import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.Flow;
import org.yesworkflow.annotations.UriAnnotation;
import org.yesworkflow.data.UriTemplate;

public class Port {

    public final Integer id;
    public final Begin beginAnnotation;
	public final Flow flowAnnotation;
	public final UriTemplate uriTemplate;
	public final Data data;
	
	public Port(Integer id, Data data, Flow flowAnnotation, Begin beginAnnotation) {
	    
	    this.id = id;
	    this.data = data;
		this.flowAnnotation = flowAnnotation;
		this.beginAnnotation = beginAnnotation;
		
		UriAnnotation uriAnnotation = this.flowAnnotation.uriAnnotation();
		this.uriTemplate = (uriAnnotation != null) ? new UriTemplate(uriAnnotation.name) : null;
	}
	
	@Override
    public String toString() {
        return String.format("%s:%s", this.beginAnnotation.name, this.flowAnnotation.binding());
    }
}
