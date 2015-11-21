package org.yesworkflow.config;

import java.util.Map;

public interface Configurable {
    Configurable configure(Map<String,Object> config) throws Exception;
    Configurable configure(String key, Object value) throws Exception;
}
