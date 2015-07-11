package org.yesworkflow.recon;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import org.yesworkflow.config.YWConfiguration;
import org.yesworkflow.model.Model;
import org.yesworkflow.query.LogicLanguage;

public class DefaultReconstructor implements Reconstructor  {
    
    static private LogicLanguage DEFAULT_LOGIC_LANGUAGE = LogicLanguage.PROLOG;
    
    private Model model;    
    private PrintStream stdoutStream = null;
    @SuppressWarnings("unused")
    private PrintStream stderrStream = null;
    private Run run = null;
    private String factsFile = null;
    private String reconFacts = null;
    private LogicLanguage logicLanguage = DEFAULT_LOGIC_LANGUAGE;

    public DefaultReconstructor(PrintStream stdoutStream, PrintStream stderrStream) {
        this.stdoutStream = stdoutStream;
        this.stderrStream = stderrStream;
    }

    @Override
    public DefaultReconstructor model(Model model) {        
        if (model == null) throw new IllegalArgumentException("Null model passed to DefaultReconstructor.");
        if (model.program == null) throw new IllegalArgumentException("Model with null program passed to DefaultReconstructor.");
        this.model = model;
        return this;
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
    
    public DefaultReconstructor configure(String key, Object value) throws Exception {
        if (key.equalsIgnoreCase("factsfile")) {
            factsFile = (String)value;
         } else if (key.equalsIgnoreCase("logic")) {
             logicLanguage = LogicLanguage.toLogicLanguage((String)value);
         }
        return this;
    }

    @Override
    public DefaultReconstructor recon() throws Exception {
        reconRun();
        if (factsFile != null) {
            writeTextToFileOrStdout(factsFile, getFacts());
        }
        return this;
    }
    
    private void reconRun() {
        if (run == null) {
            run = new Run(model);
        }
    }

    @Override
    public String getFacts() {
        if (reconFacts == null) {
            reconFacts = new ReconFacts(logicLanguage, run).build().toString();
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