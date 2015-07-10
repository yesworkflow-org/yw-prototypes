package org.yesworkflow.recon;

import java.util.Map;

import org.yesworkflow.YWStage;
import org.yesworkflow.model.Model;

public interface Reconstructor extends YWStage {
    Reconstructor model(Model model);
    Reconstructor run(Run run);
    Reconstructor configure(String key, Object value) throws Exception;
    Reconstructor configure(Map<String, Object> config) throws Exception;
    Reconstructor recon() throws Exception;
    String getFacts();
}
