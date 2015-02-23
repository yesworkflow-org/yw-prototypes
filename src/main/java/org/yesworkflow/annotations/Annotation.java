package org.yesworkflow.annotations;

import java.util.StringTokenizer;

public abstract class Annotation {

    public final String tag;
    public final String name;
    
    protected String description = null;
    
    public Annotation(String comment, String expectedTag) throws Exception {
        
        StringTokenizer commentTokens = new StringTokenizer(comment);
        
        tag = commentTokens.nextToken();
        if (!tag.equalsIgnoreCase(expectedTag)) {
            throw new Exception("Wrong tag for " + expectedTag + " comment: " + tag);
        }
       
        name = commentTokens.nextToken();
        
        description = buildDescription(commentTokens);
    }

	public Annotation qualifyWith(Qualification qualification) throws Exception {		
		return this;
	}

	public void appendDescription(String extraDescription) {
		if (extraDescription != null) {
			if (this.description == null) {
				this.description = extraDescription;
			} else {
				this.description += " " + extraDescription;
			}
		}
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
