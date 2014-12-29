
*** script2csv *** (Script-to-CSV file)
- Input (filename(s) or stdin) with YW comments
- Output (stdout): CSV file with the format 
  LINE, OFFSET, TAG, VALUE, DESCR

*** csv2gv *** (CSV-to-Graphviz)
- Input (filename(s) or stdin) CSV file with schema as in file2csv generated
- Output (stdout): dot (graphviz) file 
- Note: *imports* the dots.py for some simple style configuration.

*** dots.py *** 
- imported by csv2gv 

*** EXAMPLES *** 
* From script to CSV:
  $ cat ../resources/example.py | ./file2csv > example.csv

* .. alternatively: 
  $ ./filecsv ../resources/example.py > example.csv

* From CSV to .gv (graphviz / dot file):
  $ ./script2gv example.csv > example.gv

* Both tools chained together:
  $ cat ../resources/example.py | ./script2csv | ./csv2gv > example.gv

* All-in-one pipeline:
  $ cat ../resources/example.py | ./script2csv | ./csv2gv | dot -Tpdf > example.pdf; open example.pdf
