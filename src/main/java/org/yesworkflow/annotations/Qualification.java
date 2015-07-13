package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords.Tag;
import org.yesworkflow.extract.SourceLine;

public class Qualification extends Annotation {

    public final Annotation primaryAnnotation;
    
	public Qualification(Integer id, SourceLine line, String comment, Tag tag, Annotation primaryAnnotation) throws Exception {
		
	    super(id, line, comment, tag);

	    if (primaryAnnotation == null) {
		    throw new Exception("Qualification annotation found before primary annotation.");
		}
		
        this.primaryAnnotation = primaryAnnotation;
        primaryAnnotation.qualifyWith(this);
	}
}
