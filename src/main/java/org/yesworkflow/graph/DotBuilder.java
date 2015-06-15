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
    private boolean horizontalLayout = true;
    private String nodeFont = "Courier";
    
    Map<String,Map<String,Set<String>>> uniqueEdges = new HashMap<String,Map<String,Set<String>>>();
    
    public String toString() {
        return _buffer.toString();
    }

    private String dq(String text) {
        return "\"" + text + "\"";
    }
    
    public DotBuilder enableComments(boolean state) {
        commentsEnabled = state;
        return this;
    }
        
    public DotBuilder nodeShape(String s) {
        this.shape = s;
        newNodeStyle = true;
        return this;
    }
    
    public DotBuilder nodeStyle(String s) {
       this.style = s;
       newNodeStyle = true;
       return this;
    }
    
    public DotBuilder nodeFillcolor(String fc) {
        this.fillcolor = fc;
        newNodeStyle = true;
        return this;
    }
    
    public DotBuilder nodePeripheries(int p) {
        this.peripheries = p;
        newNodeStyle = true;
        return this;
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
    
	public DotBuilder beginGraph() {
		_buffer.append(   "digraph Workflow {" );
        _buffer.append(   EOL                  );
		return this;
	}
	
	public DotBuilder endGraph() {
        _buffer.append(   "}"   );
        _buffer.append(   EOL   );
        return this;
    }
	
	public DotBuilder title(String title, String font, String location) {

	    _buffer.append(String.format(  "fontname=%s; fontsize=18; labelloc=%s", font, location ));
        _buffer.append(                EOL                                                      );	    
	    _buffer.append(String.format(  "label=%s", dq(title))                                   );
        _buffer.append(                EOL                                                      );

        return this;
	}
	
	public DotBuilder rankDir(String rankdir) {
        horizontalLayout = (rankdir.equalsIgnoreCase("LR") || rankdir.equalsIgnoreCase("RL"));
        
        _buffer.append(String.format(   "rankdir=%s", rankdir ));
        _buffer.append(                 EOL                    );
        
	    return this;
	}
	
    public DotBuilder comment(String c) {
        if (commentsEnabled) {
            _buffer.append(                 EOL            );
            _buffer.append(String.format(   "/* %s */", c ));
            _buffer.append(                 EOL            );
        }
        return this;
    }
    
    public DotBuilder beginSubgraph(boolean visible) {
        
        String c1 = "cluster" + subgraphCount++;
        String c2 = "cluster" + subgraphCount++;
        
        if (visible) {
            _buffer.append(String.format(   "subgraph %s {label=%s; penwidth=2; fontsize=18", c1, dq("") ));
            _buffer.append(                 EOL                                                           );        
            _buffer.append(String.format(   "subgraph %s {label=%s; color=%s", c2, dq(""), dq("white")   ));
            _buffer.append(EOL);
        } else {
            _buffer.append(String.format(   "subgraph %s { label=%s color=%s", c1, dq(""), dq("white") ));
            _buffer.append(                 EOL                                                         );
            _buffer.append(String.format(   "subgraph %s { label=%s color=%s", c2, dq(""), dq("white") ));
            _buffer.append(                 EOL                                                         );
        }
        
        return this;
    }
    
    public DotBuilder endSubgraph() {
        _buffer .append(    "}}" + EOL       );
        return this;
    }
    
    public DotBuilder flushNodeStyle() {
        _buffer.append(String.format(       "node[shape=%s style=%s fillcolor=%s peripheries=%d fontname=%s", 
                                                 shape, dq(style), dq(fillcolor), peripheries,  dq(nodeFont))  );
        if (width != null) 
            _buffer.append(String.format(   " width=%s", width)                                                );
        _buffer.append(                     "]"                                                                );            
        _buffer.append(                     EOL                                                                );
        
        newNodeStyle = false;
        return this;
    }
    
    public DotBuilder edgeFont(String font) {
        
        _buffer.append(String.format(   "edge[fontname=%s]", font) );
        _buffer.append(                 EOL                        );
        
        return this;
    }
   
    public DotBuilder node(String name, String label) {        
        if (nodeNameToIdMap.get(name) == null) {            
            if (newNodeStyle) flushNodeStyle();
            String id = "node" + ++nodeCount;
    		nodeNameToIdMap.put(name, id);
    		
    		_buffer.append(String.format(  "%s [label=%s]", id, dq(label) ));
    		_buffer.append(                EOL                             );
        }	
		return this;
	}

    public DotBuilder node(String name) {
        return node(name, name);
    }
        
    public DotBuilder recordNode(String name, String label1, String label2) {
        if (nodeNameToIdMap.get(name) == null) {
            if (newNodeStyle) flushNodeStyle();     
            String id = "node" + ++nodeCount;
            nodeNameToIdMap.put(name, id);
            
            _buffer.append(String.format(           "%s [shape=record rankdir=LR label=\"{", id) );
            if (horizontalLayout) _buffer.append(   "{"                                          );
            _buffer.append(String.format(           "<f0> %s |<f1> %s", label1, label2)          );
            if (horizontalLayout) _buffer.append(   "}"                                          );
            _buffer.append(                         "}\"];"                                      )   
                   .append(                         EOL                                          );
            
        }            
        return this;
    }
    
	public DotBuilder edge(String fromNode, String toNode, String edgeLabel) {
	    
		String fromId = nodeNameToIdMap.get(fromNode);
		if (fromId == null) System.err.println("WARNING: No graph edge from-node with name '" + fromNode + "'");
		
		String toId = nodeNameToIdMap.get(toNode);
        if (toId == null) System.err.println("WARNING: No graph edge to-node with name '" + toNode + "'");
		
        if (edgeIsUnique(fromId, toId, edgeLabel)) {
        
    		_buffer.append(String.format(      "%s -> %s", fromId, toId     ));    		
    		if (edgeLabel != null)
    		    _buffer.append(String.format(  " [label=%s]", dq(edgeLabel) ));
    		_buffer.append(                    EOL		                    );
        }
        
		return this;
	}
	
    public DotBuilder edge(String fromNode, String toNode) {
        return edge(fromNode, toNode, null);
    }
    
	private boolean edgeIsUnique(String from, String to, String label) {
	    
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
}