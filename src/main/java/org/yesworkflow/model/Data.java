package org.yesworkflow.model;

public class Data {

    public final Long id;
    public String name;
    public String qualifiedName;
    
	public Data(Long dataId, String dataName) {
	    this.id = dataId;
	    this.name = dataName;
	}
	
   @Override
   public String toString() {
       return new StringBuffer()
                  .append("Data")
                  .append("[")
                  .append(name)
                  .append("]")
                  .toString();
   }
}
