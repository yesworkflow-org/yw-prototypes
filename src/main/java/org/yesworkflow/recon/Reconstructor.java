package org.yesworkflow.recon;

import java.util.Map;

import org.yesworkflow.YWStage;

public interface Reconstructor extends YWStage {
    Reconstructor run(Run run);
    Reconstructor configure(String key, Object value) throws Exception;
    Reconstructor configure(Map<String, Object> config) throws Exception;
    Reconstructor recon() throws Exception;
    String getFacts() throws Exception;
}
