package org.yesworkflow.annotations;

import java.util.StringTokenizer;

public abstract class Annotation {

    public final String tag;
    public final String name;
    
    protected String description;
	protected Qualification as;
	
    public Annotation(String commentLine, String expectedTag) throws Exception {
        
        StringTokenizer commentTokens = new StringTokenizer(commentLine);
        
        this.tag = commentTokens.nextToken();
        if (!tag.equalsIgnoreCase(expectedTag)) {
            throw new Exception("Wrong tag for " + expectedTag + " comment: " + tag);
        }
       
        this.name = commentTokens.nextToken();
        
        if (commentTokens.hasMoreTokens()) {
            this.description = buildDescription(commentTokens);
        } else  {
            this.description = null;
        }
    }

	public Annotation qualifyWith(Qualification qualification) throws Exception {
		
		if (qualification instanceof As) {
			this.as = qualification;
			if (this.as.description != null) {
				if (this.description == null) {
					this.description = as.description;
				} else {
					this.description += " " + as.description;
				}
			}
			
		} else {
			throw new Exception("Annotation type does not accept qualification " + qualification);
		}
		
		return this;
	}
	
	public String binding() {
		return as == null ? name : as.name;
	}	
	
	public String description() {
		return description();
	}
	
	private String buildDescription(StringTokenizer commentTokens) {
        
        StringBuilder descriptionBuilder = new StringBuilder();
        
        while (commentTokens.hasMoreTokens()) {
            descriptionBuilder.append(' ').append(commentTokens.nextToken());
        }
        
        String description = descriptionBuilder.toString().trim();
        
        return (description.length() > 0) ? description : null;
    }

}
