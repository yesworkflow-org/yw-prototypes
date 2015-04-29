package org.yesworkflow.annotations;

import org.yesworkflow.extract.SourceLine;

public abstract class Flow extends AliasableAnnotation {
    	
    protected UriAnnotation uriAnnotation;
    
    public Flow(Integer id, SourceLine line, String comment, String expectedTag) throws Exception {
    	super(id, line, comment, expectedTag);    	
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
        
        sb.append(tag)
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
