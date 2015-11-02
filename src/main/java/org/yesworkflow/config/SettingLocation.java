package org.yesworkflow.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.yesworkflow.exceptions.YWToolUsageException;

/** 
 * Class representing the location of a particular setting
 * in the configuration tree.
 */
class SettingLocation {
    
    Map<String,Object> settingParent;
    String settingLeafName;
    
    public static SettingLocation find(Map<String,Object> root, String settingName) throws YWToolUsageException {
        return new SettingLocation(root, settingName, false);
    }

    public static SettingLocation create(Map<String,Object> root, String settingName) throws YWToolUsageException {
        return new SettingLocation(root, settingName, true);
    }

    @SuppressWarnings("unchecked")
    private SettingLocation (Map<String,Object> root, String settingName, 
            boolean createMissingTables) throws YWToolUsageException {
        
        if (settingName == null) {
            throw new IllegalArgumentException("Null configName argument.");
        }
        
        // parse full setting name
        String[] settingNameParts = settingName.split("\\.");
        settingLeafName = settingNameParts[settingNameParts.length - 1];
        
        // locate the parent node for the setting
        settingParent = root;
        if (settingNameParts.length > 0) {
            for (int i = 0; i < settingNameParts.length - 1; ++i) {
                String partName = settingNameParts[i];
                Object value =  settingParent.get(partName);
                Map<String, Object> table;
                if (value == null) {
                    if (!createMissingTables) {
                        settingParent = null;
                        return;
                    }
                    table = new LinkedHashMap<String,Object>();
                    settingParent.put(partName, table);
                } else {
                    if (!(value instanceof LinkedHashMap)) {
                        if (createMissingTables) {
                            throw new YWToolUsageException(
                                "Attempt to create a setting table that overwrites an existing setting value: " + settingName);
                        } else {
                            throw new YWToolUsageException(
                                "Attempt to access a setting value as a setting table: " + settingName);
                        }
                    }
                    table = (Map<String, Object>) value;
                }
                settingParent = table;
            }
        }
        
        if (createMissingTables && settingParent.get(settingLeafName) instanceof LinkedHashMap) {
            throw new YWToolUsageException(
                    "Attempt to assign a setting value that overwrites an existing setting table: " + settingName);
        }
    }
}