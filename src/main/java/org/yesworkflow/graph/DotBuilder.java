package org.yesworkflow.graph;

/* This file is an adaptation of GraphvizReporter.java in the org.restflow.reporter
 * package as of 28Dec2014.
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class DotBuilder {
	
    public static final String EOL = System.getProperty("line.separator");

    private boolean commentsEnabled = true;
    
    private String nodeShape = "box";
    private String nodeStyle = "filled";
    private String nodeFillcolor = "#FFFFFF";
    private int nodePeripheries = 1;
    private String nodeFont = "Courier";
    private Double nodeWidth = null;
    private String edgeFont = "Courier";
    
    private boolean horizontalLayout = true;
    private StringBuilder _buffer = new StringBuilder();
    private Set<String> uniqueNodes = new HashSet<String>();
    private Map<String,Map<String,Set<String>>> uniqueEdges = new HashMap<String,Map<String,Set<String>>>();
    
    public String toString() { return _buffer.toString(); }
    
    public DotBuilder enableComments(boolean state) { commentsEnabled = state; return this; }

    public DotBuilder nodeShape(String s)           { nodeShape = s; return this; }
    public DotBuilder nodeStyle(String s)           { nodeStyle = s; return this; }
    public DotBuilder nodeFillcolor(String fc)      { nodeFillcolor = fc; return this; }
    public DotBuilder nodePeripheries(int p)        { nodePeripheries = p;  return this; }
    public DotBuilder nodeWidth(Double w)           { nodeWidth = w; return this; }    
    public DotBuilder nodeFont(String font)         { nodeFont = font; return this; }
    
    public DotBuilder edgeFont(String font)         { edgeFont = font; return this; }
    
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
	    _buffer.append(String.format(  "label=%s", q(title))                                    );
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
    
    public DotBuilder beginSubgraph(String name, boolean visible) {

        String outer = "cluster_" + name + "_outer";
        String inner = "cluster_" + name + "_inner";
        
        if (visible) {
            _buffer.append(String.format(   "subgraph %s { label=%s; color=black; penwidth=2", q(outer), q("") ));
        } else {
            _buffer.append(String.format(   "subgraph %s { label=%s; color=white", q(outer), q("")             ));
        }
        _buffer.append(                     EOL                                                                 );
        _buffer.append(String.format(       "subgraph %s { label=%s; color=white", q(inner), q("")             ));
        _buffer.append(                     EOL                                                                 );
        
        return this;
    }
    
    public DotBuilder endSubgraph() {
        _buffer .append(    "}}" + EOL       );
        return this;
    }
    
    public DotBuilder flushNodeStyle() {
        
        _buffer.append(String.format(     "node[shape=%s style=%s fillcolor=%s peripheries=%d fontname=%s", 
                                                 nodeShape, q(nodeStyle), q(nodeFillcolor),
                                                 nodePeripheries, q(nodeFont))                              );
        if (nodeWidth != null)
            _buffer.append(String.format( " width=%s", nodeWidth)                                           );
        
        _buffer.append(                   "]"                                                               )
               .append(                   EOL                                                               );

        return this;
    }
      
    public DotBuilder node(String name, String label) {
        if (nodeIsUnique(name)) {
    		_buffer.append(String.format(      "%s", q(name)           ));
    		if (label != null && 
    		    !name.equals(label))
    		    _buffer.append(String.format(  " [label=%s]", q(label) ));
    		_buffer.append(                    EOL                      );
        }	
		return this;
	}

    public DotBuilder node(String name) {
        return node(name, name);
    }
        
    public DotBuilder recordNode(String name, String label1, String label2) {
        if (nodeIsUnique(name)) {
            _buffer.append(String.format(           "%s [shape=record rankdir=LR label=\"{", q(name) ));
            if (horizontalLayout) _buffer.append(   "{"                                               );
            _buffer.append(String.format(           "<f0> %s |<f1> %s", q(label1), esc(label2))       );
            if (horizontalLayout) _buffer.append(   "}"                                               );
            _buffer.append(                         "}\"];"                                           )
                   .append(                         EOL                                               );
            
        }            
        return this;
    }
    
   public DotBuilder flushEdgeStyle() {
        _buffer.append(String.format( "edge[fontname=%s]", edgeFont) );
        _buffer.append(               EOL                            );
        return this;
    }
    
	public DotBuilder edge(String fromNode, String toNode, String edgeLabel) {
        if (edgeIsUnique(fromNode, toNode, edgeLabel)) {
    		_buffer.append(String.format(      "%s -> %s", q(fromNode), q(toNode) ));    		
    		if (edgeLabel != null)
    		    _buffer.append(String.format(  " [label=%s]", q(edgeLabel)        ));
    		_buffer.append(                    EOL		                           );
        }
        
		return this;
	}
	
    public DotBuilder edge(String fromNode, String toNode) {
        return edge(fromNode, toNode, null);
    }
    
    private boolean nodeIsUnique(String node) {
        if (uniqueNodes.contains(node)) {
            return false;
        } else {
            uniqueNodes.add(node);
            return true;
        }
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
	
    private Pattern validDotIdPattern = Pattern.compile("[a-zA-Z_0-9]+");

    private String q(String text) {
        if (validDotIdPattern.matcher(text).matches()) {
            return text;
        } else {
            return "\"" +  text.replace("\"", "\\\"") + "\"";
        }
    }

    private String esc(String text) {
        if (validDotIdPattern.matcher(text).matches()) {
            return text;
        } else {
            text = text.replace("{", "\\{")
                       .replace("}", "\\}")
                       .replace(":", "\\:");
            return text;
        }
    }
}