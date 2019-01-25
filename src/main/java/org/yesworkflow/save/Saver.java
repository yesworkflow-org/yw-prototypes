package org.yesworkflow.save;

import org.yesworkflow.YWStage;
import org.yesworkflow.config.Configurable;

import java.util.Map;

public interface Saver extends YWStage, Configurable
{
    Saver configure(Map<String, Object> config) throws Exception;
    Saver configure(String key, Object value) throws Exception;
    Saver build(String model, String graph, String recon);
    Saver save();
}


