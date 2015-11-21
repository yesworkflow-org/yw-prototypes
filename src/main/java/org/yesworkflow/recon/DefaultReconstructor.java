package org.yesworkflow.recon;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.yesworkflow.config.YWConfiguration;
import org.yesworkflow.query.QueryEngine;

public class DefaultReconstructor implements Reconstructor  {
    
    static private QueryEngine DEFAULT_QUERY_ENGINE = QueryEngine.SWIPL;
    
    private PrintStream stdoutStream = null;
    @SuppressWarnings("unused")
    private PrintStream stderrStream = null;
    private Run run = null;
    private String factsFile = null;
    private String reconFacts = null;
    private QueryEngine queryEngine = DEFAULT_QUERY_ENGINE;
    private ResourceFinder resourceFinder = null;
    private Map<String,Object> finderConfiguration = null;

    public DefaultReconstructor(PrintStream stdoutStream, PrintStream stderrStream) {
        this.stdoutStream = stdoutStream;
        this.stderrStream = stderrStream;
    }

    @Override
    public DefaultReconstructor run(Run run) {
        if (run == null) throw new IllegalArgumentException("Null run passed to DefaultReconstructor.");
        this.run = run;
        return this;
    }
    
    @Override
    public DefaultReconstructor configure(Map<String,Object> config) throws Exception {
        if (config != null) {
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                configure(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }
    
    @SuppressWarnings("unchecked")
    public DefaultReconstructor configure(String key, Object value) throws Exception {
        if (key.equalsIgnoreCase("factsfile")) {
            factsFile = (String)value;
        } else if (key.equalsIgnoreCase("queryengine")) {
            queryEngine = QueryEngine.toQueryEngine((String)value);
        }  else if (key.equalsIgnoreCase("finderclass")) {
            resourceFinder = instantiateResourceFinder((String)value);
        } else if (key.equalsIgnoreCase("finder")) {
            finderConfiguration = (Map<String,Object>)value;
        }
        
        return this;
    }
    
    private static ResourceFinder instantiateResourceFinder(String finderClassName) throws Exception {

        Class<?> resourceFinderClass;
        try {
            resourceFinderClass = Class.forName(finderClassName);
        } catch (ClassNotFoundException cause) {
            throw new Exception("ResourceFinder class " + finderClassName +
                                                " not found.", cause);
        }
        
        Object resourceFinderInstance;
        try {
            resourceFinderInstance = resourceFinderClass.newInstance();
        } catch (Exception cause) {
            throw new Exception(
                    "Error instantiating instance of custom ResourceFinder " + 
                            resourceFinderClass + ".", cause);
        }
        
        ResourceFinder resourceFinder;
        try {
            resourceFinder = (ResourceFinder)resourceFinderInstance;
        } catch (ClassCastException cause) {
            throw new Exception(
                    "Class " + finderClassName + " does not implement the ResourceFinder interface.",
                    cause);
        }
        
        return resourceFinder;
    }
    

    @Override
    public DefaultReconstructor recon() throws Exception {
        if (factsFile != null) {
            writeTextToFileOrStdout(factsFile, getFacts());
        }
        return this;
    }
    
    @Override
    public String getFacts() throws Exception {
        
        if (resourceFinder == null) {
            resourceFinder= new FileResourceFinder();
        }
        
        if (finderConfiguration != null) {
            resourceFinder.configure(finderConfiguration);
        }
        
        if (reconFacts == null) {
            reconFacts = new ReconFacts(queryEngine, run, resourceFinder).build().toString();
        }
        return reconFacts;
    }
    
    private void writeTextToFileOrStdout(String path, String text) throws IOException {  
        PrintStream stream = (path.equals(YWConfiguration.EMPTY_VALUE) || path.equals("-")) ?
                             this.stdoutStream : new PrintStream(path);
        stream.print(text);
        if (stream != this.stdoutStream) {
            stream.close();
        }
    }
}