package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords.Tag;
import org.yesworkflow.extract.Comment;

public abstract class AliasableAnnotation extends Annotation {

	protected Qualification as;
	
    public AliasableAnnotation(Long id, Comment line, String comment, Tag tag) throws Exception {
        super(id, line, comment, tag);
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
