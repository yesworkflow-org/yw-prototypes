package org.yesworkflow.annotations;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.yesworkflow.YWKeywords.Tag;
import org.yesworkflow.exceptions.YWMarkupException;

public abstract class Annotation {

    public final Long id;
    public final Long sourceId;
    public final Long lineNumber;
    public final String keyword;
    public final String name;
    public final String comment;
    public final Tag tag;
    
    protected String description = null;
    
    public Annotation(Long id, Long sourceId, Long lineNumber, String comment, Tag tag) throws YWMarkupException {

        this.id = id;
        this.sourceId = sourceId;
        this.lineNumber = lineNumber;
        this.comment = comment;
        this.tag = tag;
        
        StringTokenizer commentTokens = new StringTokenizer(comment);
        
        keyword = commentTokens.nextToken();
        String expectedKeyword = "@" + tag.toString();
        if (!keyword.equalsIgnoreCase(expectedKeyword)) {
            throw new YWMarkupException("Wrong keyword for " + expectedKeyword.toLowerCase() + " annotation: " + keyword);
        }
       
        try {
            name = commentTokens.nextToken();
        } catch (NoSuchElementException e) {
            throw new YWMarkupException("No argument provided to " + keyword + " keyword on line " + lineNumber);
        }
        
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
