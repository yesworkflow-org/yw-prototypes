package org.yesworkflow.graph;

/* This file is an adaptation of GraphvizReporter.java in the org.restflow.reporter
 * package as of 28Dec2014.
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private boolean horizontalLayout = true;
    private String nodeFont = "Courier";
    private String graphFont = "Courier";
    
    Map<String,Map<String,Set<String>>> uniqueEdges = new HashMap<String,Map<String,Set<String>>>();
    
	public DotBuilder beginGraph() {
		_buffer.append(	"digraph Workflow {" + EOL );
		return this;
	}

	public DotBuilder title(String title, String location) {
	    _buffer.append(    "fontname="             )
	           .append(    graphFont               )
	           .append(    "; fontsize=18"         )
	           .append(    "; labelloc="           )
	           .append(    location + EOL          )
	           .append(    "label=\""              )
	           .append(    title                   )
	           .append(    "\"" + EOL              );
	    return this;
	}
	
	public DotBuilder rankDir(String rankdir) {
        _buffer.append( "rankdir=" + rankdir + EOL );        
        horizontalLayout = (rankdir.equalsIgnoreCase("LR") || rankdir.equalsIgnoreCase("RL")); 
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
    

    public DotBuilder beginSubgraph() {
        
        String name = "cluster" + subgraphCount++;
                
        _buffer.append(     "subgraph "         )
               .append(     name                )
               .append(     " {label="          )
               .append(     dq("")              )
               .append(     "; penwidth=2"      )
               .append(     "; fontsize=18"     );
        
        if (!showClusterBox) {
            
            _buffer.append( "; color="          )
                   .append( dq("white")         );
        }
        
        _buffer.append(     EOL                 );
            
        name = "cluster" + subgraphCount++;
        
        _buffer.append(     "subgraph "         )
               .append(     name                )
               .append(     " {label="          )
               .append(     dq("")              )
               .append(     "; color="          )
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

    public DotBuilder nodeFont(String font) {
        nodeFont = font;
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
		
		if (label != null) {
		    _buffer.append(	  " [label="	   )
				   .append(	   dq(label)	   )				
				   .append(	   "]"             );
		}
		
		_buffer.append(       EOL		       );
		
		return this;
	}

    public DotBuilder recordNode(String name, String label1, String label2) {
        
        // TODO Investigate why this is needed
        if (nodeNameToIdMap.get(name) != null) return this;
        
        if (newNodeStyle) {
            flushNodeStyle();
        }
        
        String id = "node" + ++nodeCount;
        nodeNameToIdMap.put(name, id);
        
        _buffer.append(     id                  )
               .append(     " [shape=record "   )
               .append(     " rankdir=LR "      )
               .append(     "label=\"{"         );

        if (horizontalLayout) {
            _buffer.append( "{"                 );
        }
        
        _buffer.append(     "<f0> "             )
               .append(     label1              )
               .append(     "|<f1>"             )
               .append(     label2              );
               
        if (horizontalLayout) {
            _buffer.append( "}"                 );
        }
        _buffer.append(     "}\"];"             )   
               .append(     EOL                 );
        
        
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
		
        if (edgeIsUnique(fromId, toId, edgeLabel)) {
        
    		_buffer .append(	fromId			)
    				.append(	" -> "			)
    				.append(	toId			);
    		
    		if (edgeLabel != null) {
    		 _buffer.append(	" [label="		)
    				.append(	dq(edgeLabel)	)
    				.append(	"]"             );
    		}
    		
    		_buffer.append(        EOL		);
        }
        
		return this;
	}

	boolean edgeIsUnique(String from, String to, String label) {
	    
	    Map<String,Set<String>> labelsForEdgesFromFirstNode = uniqueEdges.get(from);
	    if (labelsForEdgesFromFirstNode == null) {
	        labelsForEdgesFromFirstNode = new HashMap<String,Set<String>>();
	        uniqueEdges.put(from, labelsForEdgesFromFirstNode);
	    }
	    
	    Set<String> labels = labelsForEdgesFromFirstNode.get(to);
	    if (labels == null) {
	        labels = new HashSet<String>();
	        labelsForEdgesFromFirstNode.put(to, labels);
	    }

	    if (labels.contains(label)) {
	        return false;
	    } else {
	        labels.add(label);
	        return true;
	    }
	}
	
	public DotBuilder graphFont(String font) {
	    this.graphFont = font;
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
               .append(    " fontname="     )
               .append(    dq(nodeFont)     );
        
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