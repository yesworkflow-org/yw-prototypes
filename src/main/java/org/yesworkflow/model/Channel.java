package org.yesworkflow.model;

import org.yesworkflow.annotations.Param;

public class Channel {

    public final Integer id;
    public final Data data;
    public final Program sourceProgram;
	public final Port sourcePort;
	public final Program sinkProgram;
	public final Port sinkPort;
    public final boolean isParam;
    
    private static Integer nextChannelId = 1;
	
	public Channel(Data data, Program sourceProgram, Port sourcePort, Program sinkProgram, Port sinkPort) {
	    this.id = nextChannelId++;
	    this.data = data;
	    this.sourceProgram = sourceProgram;
		this.sourcePort = sourcePort;
		this.sinkProgram = sinkProgram;
		this.sinkPort = sinkPort;		
		this.isParam =  (sinkPort.flowAnnotation instanceof Param);
	}
	
   @Override
   public String toString() {
       StringBuffer sb = new StringBuffer();
       sb.append(this.data.qualifiedName);
       sb.append("[");
       if (this.sourceProgram != null) sb.append(this.sourceProgram);
       sb.append("->");
       if (this.sinkProgram != null) sb.append(this.sinkProgram);
       sb.append("]");
       return sb.toString();
   }
}
