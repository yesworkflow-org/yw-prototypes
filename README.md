YesWorkflow Prototypes
======================

Th yw-prototypes repository contains early implementations of YesWorkflow, an approach to modeling conventional scripts and programs as scientific workflows. 

Overview
--------

YesWorkflow aims to provide a number of the benefits of using a scientific workflow management system without having to rewrite scripts and other scientific sofware.  Rather than reimplementing code so that it can be executed and managed by a workflow engine, a YesWorkflow user simply adds special YesWorkflow (YW) comments to existing scripts.  These comments declare how data is used and results produced, step by step, by the script.  The YesWorkflow tools interpret the YW comments and produce graphical output that reveals the stages of computation and the flow of data in the script.

##### Example YesWorkflow output

The image below was produced by YesWorkflow using the YW comments added to a conventional (non-dataflow oriented) python script ([example.py](https://github.com/yesworkflow-org/yw-prototypes/blob/master/src/main/resources/example.py "example.py")):

![example](https://cloud.githubusercontent.com/assets/3218259/5593909/93fc7f1c-91e3-11e4-8370-aebcf1341d36.png)

The green blocks represent stages in the computation performed by the script. The labels on arrows name the input, intermediate, and final data products of the script.

##### Introduction to YesWorkflow comments

The [example.py](https://github.com/yesworkflow-org/yw-prototypes/blob/master/src/main/resources/example.py "example.py") script includes YesWorkflow comments that precede the `main` function and declare the inputs and outputs of the script as a whole:

    ## @begin main
    #  @in LandWaterMask_Global_CRUNCEP.nc @as input_mask_file
    #  @in NEE_first_year.nc @as input_data_file
    #  @out result_simple.pdf @as result_NEE_pdf

Each YesWorkflow (YW) comment is identified by a keyword that begins with the '`@`' symbol.  A `@begin` comment declares the beginning of the script or of a block of computation within the script.  Each `@begin` tag is paired with a `@end` later in the script, and together these tags delimit the code annotated by other YW comments found in that block.  The script `example.py` ends with this YW comment:

    ## @end main

The script inputs (`input_data_file` and `input_mask_file`) and outputs (`result_NEE_pdf`) appear in the diagram produced by YesWorkflow because they are declared using the `@in` and `@out` comments shown above.  The text following the `@as` keyword in each of these comments provides an *alias* for the actual value or variable (the term immediately following the `@in` or `@out` keyword) that represents that input or output in the script.  It is the alias that is displayed in YesWorkflow results and that is used to infer how data flows through the script.

For example, `example.py` includes YW comments annotating a block of code performing the `fetch_mask` operation (represented as a green box in the diagram above):

    ## @begin fetch_mask
    #  @in "LandWaterMask_Global_CRUNCEP.nc" @as input_mask_file
    #  @out mask @as land_water_mask

    g = netCDF4.Dataset(db_pth+'land_water_mask/LandWaterMask_Global_CRUNCEP.nc', 'r')
    mask=g.variables['land_water_mask']
    mask = mask[:].swapaxes(0,1)

    ## @end fetch_mask

Note that in the diagram the arrow labeled `input_mask_file` is connected to the `fetch_mask` block because the alias for the `@in` comment for `fetch_mask` matches the alias for an `@in` comment on the encompassing `main` block.  The alias in both cases is `input_mask_file`.  Note also that the `@out` comment for `fetch_mask` declares the  name of the variable (`mask`) used to store the mask in the code, but also provides an alias ('`land_water_mask`') that is displayed in the graphical output of YesWorkflow. This alias matches the alias on an `@in` comment on the downstream `standardize_with_mask` block, and YesWorkflow draws an arrow in the diagram accordingly.

YesWorkflow comments of the kind discussed here can be added to any script to highlight how data is processed by that script.  YesWorkflow tools discover these comments in the script and produce graphical representations of the script that highlight its workflow-like structure.

##### Getting started with YesWorkflow

The remainder of this README provides instructions for getting started with the YesWorkflow tools, either as a user or as a developer.  There currently are two YesWorkflow prototypes in this repository.  A python implementation can be found in `src/resources/main/python`.  A  README.txt in that directory provides further information and instructions.  The remainder of this file pertains to the Java implementation.

Instructions for users
----------------------

These instruction explain how to set up an environment for running the YesWorkflow prototype on a script that has been marked up with YW comments.


##### 1. Check installed version of Java

YesWorkflow requires Java (JRE) version 1.7 or higher. To determine the version of java installed on your computer use the -version option to the java command. For example,


    $ java -version
    java version "1.7.0_67"
    Java(TM) SE Runtime Environment (build 1.7.0_67-b01)
    Java HotSpot(TM) 64-Bit Server VM (build 24.65-b04, mixed mode)
    $

 Instructions for installing Java may be found at [http://docs.oracle.com/javase/7/docs/webnotes/install/](http://docs.oracle.com/javase/7/docs/webnotes/install/).  If you plan to develop with YesWorkflow be sure that you install the JDK.

##### 2. Download the YesWorkflow jar file

The YesWorkflow prototype is distributed as a jar (Java archive) file that can be executed using the `java -jar` command.  

If you will be building YesWorkflow yourself using Maven (see *Instructions for Developers* below) then you may simply use the file `target/yesworkflow-0.1-executable.jar` produced by the `mvn package` command.

Otherwise download the latest automatically built jar from the build server.  Navigate to the results for the [last successful build](https://opensource.ncsa.illinois.edu/bamboo/browse/KURATOR-YW/latestSuccessful "last successful build") of YesWorkflow, click the *Artifacts* tab, then download the `executable jar`.  The file will be named `yesworkflow-0.1-executable.jar`.

Once you have obtained the YesWorkflow jar, save the file in a convenient location.   YesWorkflow can now be run using the `java -jar` command.  Test that the jar works correctly using the `--help` option to display the command line options for YesWorkflow:

    $ java -jar yesworkflow-0.1-executable.jar --help

    Option                     Description
    ------                     -----------
    -c, --command <command>    command to YesWorkflow
    -d, --database <database>  path to database file for storing
                                 extracted workflow graph
    -g, --graph [dot file]     path to graphviz dot file for storing
                                 rendered workflow graph (default: -)
    -h, --help                 display help
    -l, --lines [lines file]   path to file for saving extracted
                                 comment lines (default: -)
    -s, --source [script]      path to source file to analyze
                                 (default: -)
    $

##### 3.  Create an alias for YesWorkflow

If you are running YesWorkflow on an Apple OSX or Linux system (or have installed Git Bash or Cygwin on Windows), you may define a bash alias to simplify running YesWorkflow at the command line.  For example, if you have saved  `yesworkflow-0.1-executable.jar` to the bin subdirectory of your home directory, the following bash command will create an alias for running YesWorkflow simply by typing `yw` at the command prompt.

    alias yw='java -jar ~/bin/yesworkflow-0.1-executable.jar' 

The command to display YesWorkflow command line options is now simply:

    $ yw --help

If you do not define an alias you will need to type `java -jar yesworkflow-0.1-executable.jar` instead of `yw` in the examples below (and prepend yesworkflow-0.1-executable.jar with the path to the jar file).  You may of course change the name of the jar file to `yw.jar` if you like.

Instructions for developers
---------------------------

##### JDK and Maven configuration

The Java prototype is built using Maven 3. Before building YesWorkflow confirm that the `mvn` command is in your path and that a JDK version 1.7 or higher is found by maven:
    
    $ mvn --version
    Apache Maven 3.2.3 (33f8c3e1027c3ddde99d3cdebad2656a31e8fdf4; 2014-08-11T13:58:10-07:00)
    Maven home: c:\Program Files\apache-maven-3.2.3
    Java version: 1.7.0_67, vendor: Oracle Corporation
    Java home: c:\Program Files\Java\jdk1.7.0_67\jre
    Default locale: en_US, platform encoding: Cp1252
    OS name: "windows 7", version: "6.1", arch: "amd64", family: "windows"
    $

JDK 7 and Maven 3 downloads and installation instructions can be found at the following links.

- [http://docs.oracle.com/javase/7/docs/webnotes/install/](http://docs.oracle.com/javase/7/docs/webnotes/install/) 
- [http://maven.apache.org/download.cgi](http://maven.apache.org/download.cgi)


##### Project directory layout  

YesWorkflow adopts the default organization of source code, resources, and tests as defined by Maven.  See [maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html](http://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html) for more information.  The most important directories are listed below:

Directory | Description
----------| -----------
src/main/java | Source code to be built and packaged for distribution.
src/main/resources | Resource files to be packaged with production code.
src/test/java | Source code for unit and functional tests. Not included in packaged distributions.
src/test/resources | Resource files available to tests. Not included in packaged distributions.
target | Destination directory for packaged distributions (jar files) built by maven.
target/classes | Compiled java classes for source code found under src/main/java.
target/test-classes | Compiled java classes for test code found under src/test/java.
target/dependency | Automatically resolved and downloaded dependencies (jars) that will be included in the standalone distribution.


##### Building and testing with maven

YesWorkflow can be built and tested from the command line using the following commands:

Maven command            | Description
-------------------|------------
mvn clean        | Delete the target directory including all compiled code.
mvn compile      | Download required dependencies and compile source code in src/main/java.  Only those source files changes since the last compilation are built.
mvn test         | Compile the classes in src/test/java and run all tests found therein. Peforms *mvn compile* first.
mvn package      | Packages the compiled classes in target/classes and files found in src/main/resources in two jar files, **yesworkflow-0.1.jar** and **yesworkflow-0.1-executable.jar**.  The latter also contains all jar dependencies. Peforms *mvn compile* and *mvn test* first, and will not perform packaging step if any tests fail. Use the *-DskipTests* option to bypass tests. 

##### Continuous build and integration with Bamboo

All code is built and tests run automatically on a build server at NCSA whenever changes are committed to directories used by maven.  Please confirm that the automated build and tests succeed after committing changes to code or resource files (it may take up to two minutes for a commit-triggered build to start).  Functional tests depend on the scripts in src/main/resources and are likely to fail if not updated following changes to these scripts.

Site | Url
-----| ---
Build history | https://opensource.ncsa.illinois.edu/bamboo/browse/KURATOR-YW
Last build | https://opensource.ncsa.illinois.edu/bamboo/browse/KURATOR-YW/latest
Last successful build | https://opensource.ncsa.illinois.edu/bamboo/browse/KURATOR-YW/latestSuccessful

The link to the latest successful build is useful for obtaining the most recently built jar file without building it yourself.  Follow the link to the [last successful build](https://opensource.ncsa.illinois.edu/bamboo/browse/KURATOR-YW/latestSuccessful "last successful build"), click the Artifacts tab, then download the executable jar.