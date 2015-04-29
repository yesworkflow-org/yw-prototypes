package org.yesworkflow.annotations;

import org.yesworkflow.extract.SourceLine;

public abstract class AliasableAnnotation extends Annotation {

	protected Qualification as;
	
    public AliasableAnnotation(Integer id, SourceLine line, String comment, String expectedTag) throws Exception {
        super(id, line, comment, expectedTag);
    }

	public AliasableAnnotation qualifyWith(Qualification qualification) throws Exception {
		
		if (qualification instanceof As) {
			this.as = qualification;
			appendDescription(qualification.description);
		} else {
			super.qualifyWith(qualification);
		}
		
		return this;
	}
	
	public String alias() {
		return as == null ? null : as.name;
	}

	public String binding() {
		return as == null ? name : as.name;
	}
}
