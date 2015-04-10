package org.yesworkflow.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yesworkflow.exceptions.YWToolUsageException;

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
    
    public void applyConfigOptions(List<?> options) throws YWToolUsageException {        
        for (Object option : options) {
            applyConfigOption((String) option);
        }
    }
    
    public void applyConfigOption(String option) throws YWToolUsageException {
        String[] optionParts = option.split("=");
        if (optionParts.length != 2) {
            throw new YWToolUsageException(
                "Configuration options should be key-value pairs separated by equal signs.");
        }
        String name = optionParts[0];
        String value = optionParts[1];
        ConfigAddress address = configurationAddress(name, true);
        address.table.put(address.key, value);
    }

    public String getConfigOptionValue(String optionName) {
        ConfigAddress address = configurationAddress(optionName, false);
        return address == null ? null : (String)address.table.get(address.key);
    }
    
    public class ConfigAddress {
        String key;
        Map<String,Object> table;
    }
    
    @SuppressWarnings("unchecked")
    private ConfigAddress configurationAddress(String configName, boolean createMissingTables) {
        String[] configNameParts = configName.split("\\.");
        ConfigAddress address = new ConfigAddress();
        address.key = configNameParts[configNameParts.length - 1];
        address.table = this;
        if (configNameParts.length > 0) {
            for (int i = 0; i < configNameParts.length - 1; ++i) {
                String partName = configNameParts[i];
                Object tableObject =  address.table.get(partName);
                if (tableObject == null) {
                    if (!createMissingTables) return null;
                    tableObject = new HashMap<String,Object>();
                    address.table.put(partName, tableObject);
                }
                address.table = (Map<String, Object>)tableObject;
            }
        }
        return address;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String,Object> getSection(String key) {
        return (Map<String,Object>) get(key);
    }
}
