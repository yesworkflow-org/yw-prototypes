package org.yesworkflow.model;

public class Channel {

    public final Integer id;

    public final Program sourceProgram;
	public final Port sourcePort;
	
	public final Program sinkProgram;
	public final Port sinkPort;
	
	public Channel(Integer id, Program sourceProgram, Port sourcePort, Program sinkProgram, Port sinkPort) {
	    this.id = id;
	    this.sourceProgram = sourceProgram;
		this.sourcePort = sourcePort;
		this.sinkProgram = sinkProgram;
		this.sinkPort = sinkPort;
	}
	
   @Override
   public String toString() {
       StringBuffer sb = new StringBuffer();
       sb.append(this.sourcePort.flowAnnotation.binding());
       sb.append("[");
       if (this.sourceProgram != null) sb.append(this.sourceProgram);
       sb.append("->");
       if (this.sinkProgram != null) sb.append(this.sinkProgram);
       sb.append("]");
       return sb.toString();
   }
}
