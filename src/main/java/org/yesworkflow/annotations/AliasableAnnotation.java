package org.yesworkflow.annotations;

public abstract class AliasableAnnotation extends Annotation {

	protected Qualification as;
	
    public AliasableAnnotation(String comment, String expectedTag) throws Exception {
        super(comment, expectedTag);
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

	public String binding() {
		return as == null ? name : as.name;
	}	
}
