package org.yesworkflow.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;
import org.yesworkflow.exceptions.YWToolUsageException;

/** 
 * Class responsible for storing and retrieving configuration settings used for customizing
 * runs of YesWorkflow.
 *  
 * <p>
 * An instance of YWConfiguration can be thought of as a tree of configuration setting names 
 * and values. It is represented as an arbitrarily nested table of setting lookup tables.
 * Each node in the tree has a name and a value; a leaf node stores a single configuration 
 * setting, and each internal node stores a set of configuration settings in a (more deeply) 
 * nested setting table.
 * </p>
 * 
 * <p>
 * The full name of any configuration setting stored at a leaf node is the dot-delimited
 * concatenation of individual node names leading from the root to the leaf.
 * The root node has no name.
 * </p>
 * 
 * <p>
 * For example,  a value 'TB' assigned to the property 'graph.layout' is stored in 
 * a setting table entry with key 'layout' and value 'TB'.  The setting table containing this entry
 * is stored as the value of an entry in the root setting table and is associated with the key 'graph'.
 * </p>
 * 
 * <p>
 * Configuration settings can be assigned by providing the full name of the setting
 * along with the value to assign to the setting. This class will create intermediate setting tables
 * as needed to store the value at the correct position in the tree. Setting values can
 * be retrieved in the same way, by providing the full name of the setting to be retrieved.
 * The class will navigate the tree of setting tables to retrieve the setting value.
 * </p>
 * 
 * <p>
 * Configuration settings can be loaded in bulk either from a YAML file, via the 
 * {@link #fromYamlFile(String) fromYamlFile()} static factory method, or from 
 * Java-style properties files, via the {@link #applyPropertyFile(String) applyPropertyFile()} 
 * instance method. The nested structure of an input YAML file is preserved and becomes the structure 
 * of the YWConfiguration tree. Entries loaded from a Java property files are assigned individually to 
 * the configuration when loaded. Because loading a YAML file will overwrite existing settings, 
 * only one such file can be loaded, and only before configuration settings from other
 * sources are applied.
 * 
 * <p><b>Limitations: </b> YWConfiguration uses instances of {@link java.util.LinkedHashMap LinkedHashMap} 
 * to represent setting tables. Consequently, an instance of LinkedHashMap may not used as the value 
 * for a setting. Further, because the {@link org.yaml.snakeyaml.Yaml} class used to read YAML files 
 * also represents maps using LinkedHashMap, any maps defined in an input YAML file  are considered 
 * to be setting tables rather than setting values.
 * </p>
 * </p>
 */
public class YWConfiguration {
    
    /**
     * Configuration setting value representing the presence of a setting with no value asssigned.
     * This is used to represent settings values derived from command-line options that take
     * no value argument.
     */
    public static String EMPTY_VALUE = "";
    
    /** The root setting table */
    private Map<String,Object> root = new LinkedHashMap<String,Object>();

    /**
     * Factory for creating a YWConfiguration instance initialized with the contents 
     * of the given YAML file.
     * 
     * @param yamlFile The name of the YAML file to load.
     * @return The new YWConfiguration instance.
     * @throws IllegalArgumentException If the yamlFile argument is null.
     * @throws FileNotFoundException If the specified YAML file does not exist.
     */
    public static YWConfiguration fromYamlFile(String yamlFile) throws FileNotFoundException {
        
        // validate the input argument
        if (yamlFile == null) {
            throw new IllegalArgumentException("Null yamlFile argument.");
        }
        if (! new File(yamlFile).exists()) {
            throw new FileNotFoundException("YAML configuration file not found: " + yamlFile);
        }

        // read the specified yaml file into memory
        InputStream input = new FileInputStream(yamlFile);
        
        return fromYamlStream(input);
    }

    /**
     * Factory for creating a YWConfiguration instance initialized with YAML-formatted 
     * text read from a provided input stream.
     * 
     * @param yamlStream The input stream from which to read the YAML.
     * @return The new YWConfiguration instance.
     * @throws IllegalArgumentException If the yamlStream argument is null.
     */
    @SuppressWarnings("unchecked")
    public static YWConfiguration fromYamlStream(InputStream yamlStream) {

        // validate the input argument
        if (yamlStream == null) {
            throw new IllegalArgumentException("Null yamlStream argument.");
        }

        //  parse the yaml stream
        Yaml yaml = new Yaml();
        Map<String,Object> yamlDefinedMap = (Map<String, Object>) yaml.load(yamlStream);
    
        // load the yaml-defined data structure into a new YWConfiguration instance
        YWConfiguration config = new YWConfiguration();
        config.root.putAll(yamlDefinedMap);
    
        // return the new instance
        return config;
    }
    
    /**
     * Loads configuration properties from a Java property file.
     * 
     * @param propertyFile The name of the property file to load.
     * @throws IllegalArgumentException If the propertyFile argument is null.
     * @throws FileNotFoundException If the specified property file does not exist.
     * @throws IOException If an error occurs reading the property file.
     * @throws YWToolUsageException If the setting name indicates a leaf node is internal.
     */
    public void applyPropertyFile(String propertyFile) throws IOException, YWToolUsageException {
        
        // validate the input argument
        if (propertyFile == null) {
            throw new IllegalArgumentException("Null propertyFile argument.");
        }
        if (! new File(propertyFile).exists()) {
            throw new FileNotFoundException("Property file not found: " + propertyFile);
        }

        // load the properties from the file
        applyProperties(new FileReader(propertyFile));
    }
    
    /**
     * Loads Java properties from a character stream {@link java.io.Reader Reader}.
     * 
     * @param reader The {@link java.io.Reader Reader} to read Java properties from.
     * @throws IllegalArgumentException If the reader argument is null.
     * @throws IOException If an error occurs reading from the {@link java.io.Reader Reader}.
     * @throws YWToolUsageException If the setting name indicates a leaf node is internal.
     */
    public void applyProperties(Reader reader) throws IOException, YWToolUsageException {

        // validate the input argument
        if (reader == null) {
            throw new IllegalArgumentException("Null reader argument.");
        }

        // load the properties from the reader
        Properties properties = new Properties();
        properties.load(reader);
        
        // apply each property
        for (Map.Entry<Object, Object> entry: properties.entrySet()) {
            String settingName = (String) entry.getKey();
            Object settingValue = entry.getValue();
            set(settingName, settingValue);
        }
    }
    
    /**
     * Applies a list of configuration options. Each element of the list must be
     * a String representing a YW command-line configuration option and have the form
     * <i>settingName</i> or <i>settingName=settingValue</i>.
     * 
     * @param options The list of configuration options to apply.
     * @throws IllegalArgumentException If the options argument is null or not 
     *                                  all elements of options are of type String.
     * @throws YWToolUsageException If one of the option strings is not of the correct form.
     */
    public void applyOptions(List<?> options) throws YWToolUsageException {
        
        // validate the input argument
        if (options == null) {
            throw new IllegalArgumentException("Null options argument.");
        }

        // apply each option in the list
        for (Object option : options) {
            if (!(option instanceof String)) {
                throw new IllegalArgumentException("Element of options argument not of type String: " + option);
            }
            applyOption((String) option);
        }
    }
    
    /**
     * Applies a single YW command-line configuration option.  Argument must be of 
     * the form <i>settingName</i> or <i>settingName=settingValue</i>.
     * 
     * @param option The configuration option to apply.
     * @throws IllegalArgumentException If the option argument is null.
     * @throws YWToolUsageException If the option string is not of the correct form.
     */
    public void applyOption(String option) throws YWToolUsageException {
        
        // validate the input argument
        if (option == null) {
            throw new IllegalArgumentException("Null option argument.");
        }

        // split option string at the equal sign if present
        String[] optionParts = option.split("=");
        
        // detect disallowed option forms
        if (optionParts.length > 2) {
            throw new YWToolUsageException(
                "Configuration option should be a name-value pair separated by an equal sign.");
        }
        
        // parse the configuration name and value from the option string
        String settingName = optionParts[0];
        String settingValue = (optionParts.length == 2) ? optionParts[1] : EMPTY_VALUE;
        
        // assign the setting value
        set(settingName, settingValue);
    }

    /**
     * Assign a single setting value.
     * 
     * @param settingName The full name of the configuration setting.
     * @param settingValue The value to assign.
     * @throws YWToolUsageException If the setting name indicates a leaf node is internal.
     * @throws IllegalArgumentException If the settingName or settingValue argument is null.
     */
    public void set(String settingName, Object settingValue) throws YWToolUsageException {

        // validate the input arguments
        if (settingName == null) {
            throw new IllegalArgumentException("Null settingName argument.");
        }
        if (settingValue == null) {
            throw new IllegalArgumentException("Null settingValue argument.");
        } 
        if (settingValue instanceof LinkedHashMap) {
            throw new IllegalArgumentException("A LinkedHashMap may not be used as a setting value.");
        } 
        
        // create the nested setting tables needed to store the configuration
        SettingLocation location = SettingLocation.create(root, settingName);
        
        // store the configuration value in the setting table at the parent node 
        location.settingParent.put(location.settingLeafName, settingValue);
    }
    
    /**
     * Gets the value for a configuration setting.
     * 
     * @param settingName The full name of the configuration setting.
     * @return The value of the configuration setting or null if not found.
     * @throws YWToolUsageException If the setting name indicates a leaf node is internal.
     * @throws IllegalArgumentException If the settingName or settingValue argument is null.
     */
    public Object get(String settingName) throws YWToolUsageException {
        
        // validate the input arguments
        if (settingName == null) {
            throw new IllegalArgumentException("Null settingName argument.");
        }
        
        // locate the configuration setting
        SettingLocation location = SettingLocation.find(root, settingName);
        
        // return null if there is not setting table corresponding to the expected parent node
        if (location.settingParent == null) {
            return null;
        }
        
        // return the setting value or null if not found in the setting table at the parent node
        return location.settingParent.get(location.settingLeafName);
    }
    
    /**
     * Gets the value for a configuration setting as a String.
     * 
     * @param settingName The full name of the configuration setting.
     * @return The value of the configuration setting or null if not found.
     * @throws YWToolUsageException If the setting name indicates a leaf node is internal.
     * @throws IllegalArgumentException If the settingName or settingValue argument is null.
     */
    public String getStringValue(String settingName) throws YWToolUsageException {        
        Object value = this.get(settingName);
        return value == null ? null : value.toString();
    }

    /**
     * Gets a subtree of the full configuration.
     * 
     * @param sectionName The name of configuration subtree.
     * @throws YWToolUsageException If the setting name indicates a leaf node is internal.
     * @throws IllegalArgumentException If the settingName or settingValue argument is null.
     */
    @SuppressWarnings("unchecked")
    public Map<String,Object> getSection(String sectionName) throws YWToolUsageException {
        Object section = this.get(sectionName);
        if (section == null || (!(section instanceof LinkedHashMap<?,?>))) {
            return null;
        }
        return (Map<String,Object>) section;
    }
    
    /**
     * Returns the total number of settings that have been assigned.
     * @return The number of settings.
     */
    public int settingCount() {
        return settingCount(root);
    }
    
    @SuppressWarnings("unchecked")
    private int settingCount(Map<String,Object> table) {
        int count = 0;
        for (Object value : table.values()) {
            if (value instanceof LinkedHashMap<?,?>) {
                count += settingCount((Map<String,Object>)value);
            } else {
                count += 1;
            }
        }
        return count;
    }
}
