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
    private String fillcolor = "#FFFFFF";
    private String shape = "box";
    private int peripheries = 1;
    private String style = "rounded,filled";

	public DotBuilder begin() {
	    
		_buffer.append(	"digraph Workflow {" + EOL )
		       .append( "rankdir=LR"         + EOL );

		return this;
	}

   public DotBuilder shape(String shape) {
        this.shape = shape;
        return this;
    }

   public DotBuilder style(String style) {
       this.style = style;
       return this;
   }
   
	public DotBuilder fillcolor(String fillcolor) {
	    this.fillcolor = fillcolor;
        return this;
	}

	public DotBuilder node(String name) {
		
		String id = "node" + ++nodeCount;
		nodeNameToIdMap.put(name, id);
		
		_buffer	.append(	id			    )
				.append(	" [label="		)
				.append(	dq(name)	    )
				.append(	" shape="		)
				.append(	shape			)
				.append(    " style="       )
				.append(    dq(style)       )
				.append(   " fillcolor="    )
				.append(    dq(fillcolor)   )
				.append(	" peripheries="	)
				.append(	peripheries		)
				.append(	"];" + EOL		);
		
		return this;
	}
	
	public DotBuilder edge(String fromNode, String toNode, String edgeLabel) {
		
		String fromId 	= nodeNameToIdMap.get(fromNode);
		String toId 	= nodeNameToIdMap.get(toNode);
		
		_buffer .append(	fromId			)
				.append(	" -> "			)
				.append(	toId			)
				.append(	" [label="		)
				.append(	dq(edgeLabel)	)
				.append(	"];" + EOL		);
		
		return this;
	}
	
	public DotBuilder end() {
		_buffer	.append(	"}" + EOL		);
        return this;
	}

	private String dq(String text) {
		return "\"" + text + "\"";
	}
	
	public String toString() {
		return _buffer.toString();
	}
}