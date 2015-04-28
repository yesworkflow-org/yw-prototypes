package org.yesworkflow.annotations;

import java.util.StringTokenizer;

import org.yesworkflow.extract.SourceLine;

public abstract class Annotation {

    public final SourceLine line;
    public final String tag;
    public final String name;
    
    protected String description = null;
    
    public Annotation(SourceLine line, String comment, String expectedTag) throws Exception {

        this.line = line;
        
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
			if (description == null) {
				description = extraDescription;
			} else {
				description += " " + extraDescription;
			}
		}
	}
	
	public String description() {
		return description;
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
