__author__ = 'tianhong'

if len(sys.argv) < 2:
    print "One argument expected: Input_file_name"
    print "Usage: python comment_to_graph.py [Input_file_name]"
    sys.exit()
else:
    fileName = sys.argv[1]

vocab = ["name", "param", "return"]

f = open(fileName, "r")
g = open(fileName.replace(".py", ".gv"), "w")

blockStart = False
node = {}
result = []

for line in f:
    #print line
    if "##/" in line:
        blockStart = True
        node = {}
        continue
    if blockStart:
        for item in vocab:
            if item in line:
                node[item] = line.split(item)[1].strip()
        if "#/" in line:
            blockStart = False
            result.append(node)

g.write("digraph {\n")
g.write("rankdir=LR\n")
for each in result:
    g.write(each["name"] + "[shape=box];\n")

    if len(each["param"]) > 0:
        param = each["param"].split(" ")
        for var in param:
            g.write(var + " -> " + each["name"] + ";\n")

    if len(each["return"]) > 0:
        re = each["return"].split(" ")
        for var in re:
            g.write( each["name"] + " -> " + var + ";\n")

g.write("}")
f.close()
g.close()
