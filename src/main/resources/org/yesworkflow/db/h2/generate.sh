#!/usr/bin/env bash

mvn clean

H2_JAR='../../../../../../../target/dependency/h2-1.3.176.jar'
java -cp $H2_JAR org.h2.tools.RunScript -url jdbc:h2:target/empty -script createtables.h2

mvn generate-sources

