package org.yesworkflow.annotations;

public abstract class Delimiter extends Annotation {

    public Delimiter(String commentLine, String expectedTag) throws Exception {
    	super(commentLine, expectedTag);    	
    }
    
    @Override
    public String toString() {
        
        StringBuffer sb = new StringBuffer();
        
        sb.append(this.tag)
          .append("{name=")
          .append(this.name);

        if (this.description != null) {
          sb.append(",description=")
            .append(this.description);
        }
        
        sb.append("}");
        
        return sb.toString();
    }
}

