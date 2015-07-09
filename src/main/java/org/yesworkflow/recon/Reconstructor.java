package org.yesworkflow.recon;

import java.util.Map;

import org.yesworkflow.YWStage;
import org.yesworkflow.model.Model;

public interface Reconstructor extends YWStage {
    Reconstructor model(Model model);
    Reconstructor configure(String key, Object value) throws Exception;
    Reconstructor configure(Map<String, Object> config) throws Exception;
    DefaultReconstructor reconstruct() throws Exception;
    String getFacts();
}
