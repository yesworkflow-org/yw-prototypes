package org.yesworkflow.graph;

/* This file is an adaptation of GraphvizReporter.java in the org.restflow.reporter
 * package as of 28Dec2014.
 */

import java.util.HashMap;
import java.util.Map;

public class DotBuilder {
	
    public static final String EOL = System.getProperty("line.separator");

    private StringBuilder _buffer = new StringBuilder();
	private int nodeCount = 0;
	private Map<String,String> nodeNameToIdMap = new HashMap<String,String>();

	public  void begin() {
		_buffer.append(	"digraph Workflow {" + EOL )
		       .append( "rankdir=LR"         + EOL );
	}
	
	public void node(String name, String shape) {
		node(name, shape, 1);
	}

	public void node(String name, String shape, int peripheries) {
		
		String id = "node" + ++nodeCount;
		nodeNameToIdMap.put(name, id);
		
		_buffer	.append(	id			)
				.append(	" [label="		)
				.append(	dq(name)			)
				.append(	",shape="		)
				.append(	shape			)
				.append(	",peripheries="	)
				.append(	peripheries		)
				.append(	"];" + EOL		);
	}

	
	public void edge(String fromNode, String toNode, String edgeLabel) {
		
		String fromId 	= nodeNameToIdMap.get(fromNode);
		String toId 	= nodeNameToIdMap.get(toNode);
		
		_buffer .append(	fromId			)
				.append(	" -> "			)
				.append(	toId			)
				.append(	" [label="		)
				.append(	dq(edgeLabel)	)
				.append(	"];" + EOL		);
	}
	
	public void end() {
		_buffer	.append(	"}" + EOL		);
	}

	private String dq(String text) {
		return "\"" + text + "\"";
	}
	
	public String toString() {
		return _buffer.toString();
	}
}