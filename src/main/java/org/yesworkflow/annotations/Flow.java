package org.yesworkflow.annotations;

public abstract class Flow extends AliasableAnnotation {
    	
    protected Uri uri;
    
    public Flow(String comment, String expectedTag) throws Exception {
    	super(comment, expectedTag);    	
    }
	
    @Override
    public Flow qualifyWith(Qualification qualification) throws Exception {
        
        if (qualification instanceof Uri) {
            this.uri = (Uri)qualification;
            appendDescription(qualification.description);
        } else {
            super.qualifyWith(qualification);
        }
        
        return this;
    }

    public Uri uri() {
        return uri;
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
