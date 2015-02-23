package org.yesworkflow.annotations;

public abstract class Flow extends AliasableAnnotation {
    	
    public Flow(String comment, String expectedTag) throws Exception {
    	super(comment, expectedTag);    	
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
