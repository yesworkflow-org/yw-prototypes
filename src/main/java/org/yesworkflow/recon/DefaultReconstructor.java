package org.yesworkflow.recon;

import java.io.PrintStream;
import java.util.Map;

import org.yesworkflow.model.Model;

public class DefaultReconstructor implements Reconstructor  {
    
    @SuppressWarnings("unused")
    private Model model;    
    @SuppressWarnings("unused")
    private PrintStream stdoutStream = null;
    @SuppressWarnings("unused")
    private PrintStream stderrStream = null;
    
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
    public DefaultReconstructor configure(Map<String,Object> config) throws Exception {
        if (config != null) {
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                configure(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }
    
    public DefaultReconstructor configure(String key, Object value) throws Exception {
        return this;
    }

    @Override
    public DefaultReconstructor reconstruct() throws Exception {
        return null;
    }

    @Override
    public String getFacts() {
        return null;
    }
}