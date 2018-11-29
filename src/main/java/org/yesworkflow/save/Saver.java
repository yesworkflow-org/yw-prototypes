package org.yesworkflow.save;

import java.util.Map;

public interface Saver
{
    Saver configure(Map<String, Object> config) throws Exception;
    Saver configure(String key, Object value) throws Exception;
    Saver build(String model, String graph, String recon);
    Saver save();
}
