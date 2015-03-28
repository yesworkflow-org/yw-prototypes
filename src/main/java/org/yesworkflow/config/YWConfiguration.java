package org.yesworkflow.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

@SuppressWarnings("serial")
public class YWConfiguration extends HashMap<String,Object> {

    public YWConfiguration() throws Exception {        
    }

    public YWConfiguration(String... paths) throws Exception {
        
        for (String path : paths) {
            File yamlFile = new File(path);
            if(yamlFile.exists()) {
                InputStream input = new FileInputStream(yamlFile);
                Yaml yaml = new Yaml();
                @SuppressWarnings("unchecked")
                Map<String,Object> yamlDefinedMap = (Map<String, Object>) yaml.load(input);
                putAll(yamlDefinedMap);
                break;
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public Map<String,Object> getSection(String key) {
        return (Map<String,Object>) get(key);
    }
}
