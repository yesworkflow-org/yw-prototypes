package org.yesworkflow.config;

import java.util.Map;

/** 
 * Class representing the location of a particular setting
 * in the configuration tree.
 */
class SettingLocation {
    
    Map<String,Object> settingParent;
    String settingLeafName;
    
    public static SettingLocation find(SettingTable root, String settingName) {
        return new SettingLocation(root, settingName, false);
    }

    public static SettingLocation create(SettingTable root, String settingName) {
        return new SettingLocation(root, settingName, true);
    }

    private SettingLocation (SettingTable root, String settingName, boolean createMissingTables) {
        
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
                SettingTable tableObject =  (SettingTable) settingParent.get(partName);
                if (tableObject == null) {
                    if (!createMissingTables) {
                        settingParent = null;
                        return;
                    }
                    tableObject = new SettingTable();
                    settingParent.put(partName, tableObject);
                }
                settingParent = (Map<String, Object>)tableObject;
            }
        }
    }
}