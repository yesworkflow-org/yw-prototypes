# HEADER: graph-level graphviz/dot attributes, including
# - default node style (e.g. rounded boxes, ovals, etc)
# - default edge style

HEADER = """
digraph{
 rankdir=LR 
 //ranksep=0.3

 node[shape=box 
      style="rounded,filled" 
      fillcolor="#FFFFCC" 
      fontname=helvetica fontsize=12]

 edge[fontname=courier fontsize=10]
"""

# BLOCK: how to render block (=actor) nodes; e.g. 3D boxes
BLOCK = 'shape=box3d fontname=courier fillcolor="#CCFFCC"'

# Styles for in- and out-edges
INEDGE  = 'style=dashed label=in'
OUTEDGE = 'label=out'

# All good things must end sometime .. 
TRAILER = "}"

