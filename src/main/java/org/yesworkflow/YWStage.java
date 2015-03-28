package org.yesworkflow;

import java.util.Map;

public interface YWStage {
    
    public static final String EOL = System.getProperty("line.separator");

    YWStage configure(String key, Object value) throws Exception;
    YWStage configure(Map<String, Object> config) throws Exception;
}
