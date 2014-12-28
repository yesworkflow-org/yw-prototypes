package org.yesworkflow.model;

public class Channel {

	public final Program sourceProgram;
	public final Port sourcePort;
	
	public final Program sinkProgram;
	public final Port sinkPort;
	
	public Channel(Program sourceProgram, Port sourcePort, Program sinkProgram, Port sinkPort) {
		this.sourceProgram = sourceProgram;
		this.sourcePort = sourcePort;
		this.sinkProgram = sinkProgram;
		this.sinkPort = sinkPort;
	}
	
	public class Builder {
		
		public Program sourceProgram;
		public Port sourcePort;
		public Program sinkProgram;
		public Port sinkPort;
		
		Channel build() {
			return new Channel(sourceProgram, sourcePort, sinkProgram, sinkPort);
		}		
	}
}
