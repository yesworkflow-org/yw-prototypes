package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords.Tag;

public class Qualification extends Annotation {

    public final Annotation primaryAnnotation;
    
	public Qualification(Long id, Long sourceId, Long lineNumber, String comment, Tag tag, Annotation primaryAnnotation) throws Exception {
		
	    super(id, sourceId, lineNumber,comment, tag);

	    if (primaryAnnotation == null) {
		    throw new Exception("Qualification annotation found before primary annotation.");
		}
		
        this.primaryAnnotation = primaryAnnotation;
        primaryAnnotation.qualifyWith(this);
	}
}
