package org.yesworkflow.model;

public class Data {

    public final Integer id;
    public String name;
    public String qualifiedName;
    
	public Data(Integer id, String dataName) {
	    this.id = id;
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
