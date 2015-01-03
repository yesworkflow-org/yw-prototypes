

for filename in folder myfolder...
  f = open(filename,'r');
  g = open(filename+'bar.csv','w');

  y = random...

  # [[ calculateBMI
  # the following is for tracing every read and write:
  # @trace in:f(r) out:g(r,w)
  # alternatively, you could do coarse:
  # @trace in:f() out:g()
  # these variables are to be included in the 'lift' but not traced
  # @notrace in:y
  for x in f.read():
      l = ...x...y ...
      write(g,l);
  # calculateBMI ]

  close(g);
  close(f);

print 'done';
