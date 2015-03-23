package org.yesworkflow.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

@SuppressWarnings("serial")
public class YWConfiguration extends HashMap<String,Object> {

    public YWConfiguration(String path) throws Exception {
        
        File yamlFile = new File(path);
        if(yamlFile.exists()) {
            InputStream input = new FileInputStream(yamlFile);
            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String,Object> config = (Map<String, Object>) yaml.load(input);
            put("graph",config.get("graph"));
        }
    }
    
    @SuppressWarnings("unchecked")
    public Map<String,Object> getMap(String key) {
        return (Map<String,Object>) get(key);
    }
}
