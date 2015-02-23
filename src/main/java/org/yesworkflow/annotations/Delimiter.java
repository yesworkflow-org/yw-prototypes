package org.yesworkflow.annotations;

public abstract class Delimiter extends Annotation {

    public Delimiter(String comment, String expectedTag) throws Exception {
    	super(comment, expectedTag);    	
    }
    
    @Override
    public String toString() {
        
        StringBuffer sb = new StringBuffer();
        
        sb.append(tag)
          .append("{name=")
          .append(name);

        if (description != null) {
          sb.append(",description=")
            .append(description);
        }
        
        sb.append("}");
        
        return sb.toString();
    }
}

