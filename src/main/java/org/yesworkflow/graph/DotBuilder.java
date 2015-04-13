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
    private int subgraphCount = 0;
	private Map<String,String> nodeNameToIdMap = new HashMap<String,String>();
    private String fillcolor = "#FFFFFF";
    private String shape = "box";
    private int peripheries = 1;
    private String style = "filled";
    private boolean newNodeStyle = true;
    private Double width = null;
    private boolean commentsEnabled = true;
    private boolean showClusterBox = true;
    
	public DotBuilder beginGraph() {
		_buffer.append(	"digraph Workflow {" + EOL );
		return this;
	}

	public DotBuilder rankDir(String rankdir) {
        _buffer.append( "rankdir=" + rankdir + EOL );
	    return this;
	}
	
    public DotBuilder enableComments(boolean state) {
        commentsEnabled = state;
        return this;
    }

    public DotBuilder showClusterBox(boolean show) {
        showClusterBox = show;
        return this;
    }
    
    public DotBuilder comment(String c) {
        
        if (commentsEnabled) {
            _buffer.append(     EOL     )
                   .append(     "/* "   )
                   .append(     c       )
                   .append(     " */"   )
                   .append(     EOL     );
        }
        
        return this;
    }

    public DotBuilder beginSubgraph() {
        return beginSubgraph("");
    }
    
    public DotBuilder beginHiddenSubgraph() {
        
        String name = "cluster" + subgraphCount++;
                
        _buffer.append(     "subgraph "         )
               .append(     name                )
               .append(     " {"                )
               .append(     " label="           )
               .append(     dq("")              )
               .append(     " color="           )
               .append(     dq("white")         )
               .append(     EOL                 );
            
        name = "cluster" + subgraphCount++;
        
        _buffer.append(     "subgraph "         )
               .append(     name                )
               .append(     " {"                )
               .append(     " label="           )
               .append(     dq("")              )
               .append(     " color="           )
               .append(     dq("white")         )
               .append(     EOL                 );

        return this;
    }
    

    public DotBuilder beginSubgraph(String label) {
        
        String name = "cluster" + subgraphCount++;
                
        _buffer.append(     "subgraph "         )
               .append(     name                )
               .append(     " {"                )
               .append(     EOL                 )
               .append(     "label="            )
               .append(     dq(label)           )
               .append(     EOL                 )
               .append(     "penwidth=2"        )
               .append(     EOL                 )
               .append(     "fontsize=18"       )
               .append(     EOL                 );
        
        if (!showClusterBox) {
            
            _buffer.append(     "color="            )
                   .append(     dq("white")         )
                   .append(     EOL                 );
        }
            
        name = "cluster" + subgraphCount++;
        
        _buffer.append(     "subgraph "         )
               .append(     name                )
               .append(     " {"                )
               .append(     EOL                 )
               .append(     "label="            )
               .append(     dq("")              )
               .append(     EOL                 )
               .append(     "color="            )
               .append(     dq("white")         )
               .append(     EOL                 );

        return this;
    }
    
    
   public DotBuilder endSubgraph() {
        _buffer .append(    "}}" + EOL       );
        return this;
    }
		
   public DotBuilder shape(String s) {
        this.shape = s;
        newNodeStyle = true;
        return this;
    }

   public DotBuilder style(String s) {
       this.style = s;
       newNodeStyle = true;
       return this;
   }
   
	public DotBuilder fillcolor(String fc) {
	    this.fillcolor = fc;
        newNodeStyle = true;
        return this;
	}

    public DotBuilder peripheries(int p) {
        this.peripheries = p;
        newNodeStyle = true;
        return this;
    }
    
    public DotBuilder width(double w) {
        return width(new Double(w));
    }
    
    public DotBuilder width(Double w) {
        this.width = w;
        newNodeStyle = true;
        return this;
    }

    public DotBuilder node(String name) {
        return node(name, name);
    }
        
    public DotBuilder node(String name, String label) {

        // TODO Investigate why this is needed
        if (nodeNameToIdMap.get(name) != null) return this;
        
	    if (newNodeStyle) {
	        flushNodeStyle();
	    }
	    
		String id = "node" + ++nodeCount;
		nodeNameToIdMap.put(name, id);
		
		_buffer	.append(      id			   );
		
		if (label != null && !label.isEmpty()) {
		    _buffer.append(	  " [label="	   )
				   .append(	   dq(label)	   )				
				   .append(	   "]"             );
		}
		
		_buffer.append(       EOL		       );
		
		return this;
	}

    
    public DotBuilder edge(String fromNode, String toNode) {
        return edge(fromNode, toNode, null);
    }

    
	public DotBuilder edge(String fromNode, String toNode, String edgeLabel) {
	    
		String fromId = nodeNameToIdMap.get(fromNode);
		if (fromId == null) System.err.println("WARNING: No graph edge from-node with name '" + fromNode + "'");
		
		String toId = nodeNameToIdMap.get(toNode);
        if (toId == null) System.err.println("WARNING: No graph edge to-node with name '" + toNode + "'");
		
		_buffer .append(	fromId			)
				.append(	" -> "			)
				.append(	toId			);
		
		if (edgeLabel != null) {
		 _buffer.append(	" [label="		)
				.append(	dq(edgeLabel)	)
				.append(	"]"             );
		}
		
		_buffer.append(        EOL		);
		
		return this;
	}
	
	public DotBuilder graphFont(String font) {
	    
	    _buffer.append(    "graph[fontname="   )
	           .append(    font                )
	           .append(    "]"                 )
	           .append(    EOL                 );

	    return this;
	}

    public DotBuilder nodeFont(String font) {
        
        _buffer.append(    "node[fontname="    )
               .append(    font                )
               .append(    "]"                 )
               .append(    EOL                 );
        
        return this;
    }

    public DotBuilder edgeFont(String font) {
       
        _buffer.append(    "edge[fontname="    )
               .append(    font                )
               .append(    "]"                 )
               .append(    EOL                 );
       
        return this;
    }
   
	public void flushNodeStyle() {
	    
        _buffer.append(    "node["          )
               .append(    "shape="         )
               .append(    shape            )
               .append(    " style="        )
               .append(    dq(style)        )
               .append(   " fillcolor="     )
               .append(    dq(fillcolor)    )
               .append(    " peripheries="  )
               .append(    peripheries      )
               .append(    " label="        )
               .append(    dq("")           );
        
        if (width != null) {
            _buffer.append(   " width="     )
                   .append(   width         );
        }
        
        _buffer.append(    "]" + EOL        );
        
        newNodeStyle = false;
	}

	public DotBuilder endGraph() {
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