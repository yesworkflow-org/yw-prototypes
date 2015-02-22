package org.yesworkflow.annotations;

public abstract class Flow extends Annotation {
    	
    public Flow(String comment, String expectedTag) throws Exception {
    	super(comment, expectedTag);    	
    }
	
    @Override
    public String toString() {
        
        StringBuffer sb = new StringBuffer();
        
        sb.append(this.tag)
          .append("{name=")
          .append(this.name);

        if (this.as != null) {
            sb.append(",alias=")
              .append(this.as.name);
        }
        
        if (this.description != null) {
          sb.append(",description=")
            .append(this.description);
        }
        
        sb.append("}");
        
        return sb.toString();
    }
}
