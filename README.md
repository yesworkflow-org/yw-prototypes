yw-prototypes
=============

This repository contains early implementations of YesWorkflow, an approach to modeling 
conventional scripts and programs as scientific workflows.  Instead of reimplementing a script
as a workflow meant to be executed by a workflow engine, a YesWorkflow user simply adds
special comments to the scripts they already have.  These comments declare how data is used 
and results produced, step by step, by the script.  The YesWorkflow tools interpret these 
comments and produce graphical output that reveals the stages of computation and the flow of data 
in the script.

The image below was produced by YesWorkflow using the comments added to a
conventional (non-dataflow oriented) python script:

![example](https://cloud.githubusercontent.com/assets/3218259/5593909/93fc7f1c-91e3-11e4-8370-aebcf1341d36.png)

The green blocks represent stages in the computation performed by the script. The labels on the arrows name
the input, intermediate, and final data products of the script.

This README briefly summarizes for developers and users how to get started with YesWorkflow.

For developers
--------------
