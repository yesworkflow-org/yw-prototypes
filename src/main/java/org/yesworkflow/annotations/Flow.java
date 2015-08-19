package org.yesworkflow.annotations;

import org.yesworkflow.YWKeywords.Tag;

public abstract class Flow extends AliasableAnnotation {
    	
    protected UriAnnotation uriAnnotation;
    
    public Flow(Long id, Long sourceId, Long lineNumber, String comment, Tag tag) throws Exception {
    	super(id, sourceId, lineNumber, comment, tag);    	
    }
	
    @Override
    public Flow qualifyWith(Qualification qualification) throws Exception {
        
        if (qualification instanceof UriAnnotation) {
            this.uriAnnotation = (UriAnnotation)qualification;
            appendDescription(qualification.description);
        } else {
            super.qualifyWith(qualification);
        }
        
        return this;
    }

    public UriAnnotation uriAnnotation() {
        return uriAnnotation;
    }

    
    @Override
    public String toString() {
        
        StringBuffer sb = new StringBuffer();
        
        sb.append(keyword)
          .append("{name=")
          .append(name);

        if (as != null) {
            sb.append(",alias=")
              .append(as.name);
        }
        
        if (this.description != null) {
          sb.append(",description=")
            .append(description);
        }
        
        sb.append("}");
        
        return sb.toString();
    }
}
