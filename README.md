YesWorkflow Prototypes
======================

Th yw-prototypes repository contains early implementations of YesWorkflow, an approach to modeling conventional scripts and programs as scientific workflows. 

Overview
--------

 YesWorkflow aims to provide a number of the benefits of using a scientific workflow management system without having to rewrite scripts and other scientific sofware.  Rather than reimplementing code so that it can be executed and managed by a workflow engine, a YesWorkflow user simply adds special comments to existing scripts.  These comments declare how data is used and results produced, step by step, by the script.  The YesWorkflow tools interpret these comments and produce graphical output that reveals the stages of computation and the flow of data in the script.

The image below was produced by YesWorkflow using the comments added to a conventional (non-dataflow oriented) python script:

![example](https://cloud.githubusercontent.com/assets/3218259/5593909/93fc7f1c-91e3-11e4-8370-aebcf1341d36.png)

The green blocks represent stages in the computation performed by the script. The labels on arrows name the input, intermediate, and final data products of the script.

This README briefly summarizes for developers and users how to get started with YesWorkflow.


Instructions for developers
---------------------------

There currently are two YesWorkflow prototypes in this repository.  A python implementation can be found in `src/resources/main/python`.  A  README.txt in that directory provides further information and instructions.  The remainder of this file pertains to the Java implementation.

### JDK and Maven configuration

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

- [docs.oracle.com/javase/7/docs/webnotes/install/](http://docs.oracle.com/javase/7/docs/webnotes/install/) 
- [maven.apache.org/download.cgi](http://maven.apache.org/download.cgi)


### Repository directory layout  

YesWorkflow adopts the default organization of source code, resources, and tests as defined by Maven.  See [maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html](http://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html) for more information.  The most important directories are listed below:

Directory | Description
----------| -----------
src/main/java | Source code built and packaged for distribution.
src/main/resources | Resource files available to production code.
src/test/java | Source code for unit and functional tests. Not included in packaged distributions.
src/test/resources | Resource files available to tests. Not included in packaged distributions.
target | Destination directory for packaged distributions (jar files) built by maven.
target/classes | Compiled java classes for source code under src/main/java.
target/test-classes | Compiled java classes for test code under src/test/java.
target/dependency | Resolved dependencies (automatically downloaded jars).


### Building and testing with maven

YesWorkflow can be built and tested from the command line using the following commands:

Maven command            | Description
-------------------|------------
mvn clean        | Delete the target directory including all compiled code and downloaded dependencies.
mvn compile      | Download required dependencies and compile source code in src/main/java.  Only builds source files changes since the last compilation.
mvn test         | Compile the classes in src/test/java and run all tests found therein. Peforms *mvn compile* first.
mvn package      | Packages the compiled classes in target/classes and files found in src/main/resources in two jar files, **yesworkflow-0.1.jar** and **yesworkflow-0.1-executable.jar**.  The latter also contains all jar dependencies. Peforms *mvn compile* and *mvn test* first and will not perform packaging step if any tests fail. Use the *-DskipTests* option to bypass tests. 

### Continuous build and integration with Bamboo

All code is built and tests run automatically and a build server at NCSA whenever changes are committed to directories used by maven.  

Site | Url
-----| ---
Build history | https://opensource.ncsa.illinois.edu/bamboo/browse/KURATOR-YW
Last build | https://opensource.ncsa.illinois.edu/bamboo/browse/KURATOR-YW/latest
Last successful build | https://opensource.ncsa.illinois.edu/bamboo/browse/KURATOR-YW/latestSuccessful

The link to the latest successful build is useful for obtaining the most recently built jar file without building it yourself.  Follow the link to the [last successful build](https://opensource.ncsa.illinois.edu/bamboo/browse/KURATOR-YW/latestSuccessful "last successful build"), click the Artifacts tab, then download the executable jar.

Instructions for users
----------------------
