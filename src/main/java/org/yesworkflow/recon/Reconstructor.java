package org.yesworkflow.recon;

import java.util.Map;

import org.yesworkflow.YWStage;
import org.yesworkflow.config.Configurable;

public interface Reconstructor extends YWStage, Configurable {
    Reconstructor configure(String key, Object value) throws Exception;
    Reconstructor configure(Map<String, Object> config) throws Exception;
    Reconstructor run(Run run);
    Reconstructor recon() throws Exception;
    Map<String, String> getFacts() throws Exception;
}
