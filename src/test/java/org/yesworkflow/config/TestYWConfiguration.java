package org.yesworkflow.config;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.yaml.snakeyaml.parser.ParserException;
import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.exceptions.YWToolUsageException;

public class TestYWConfiguration extends YesWorkflowTestCase {

    private static String TEST_RESOURCE_DIR = "src/test/resources/org/yesworkflow/config/";
    
    public void testDefaultConstructor_SettingCountIsZero() {
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
    }

    public void testSetAndGet_SingleTopLevelSetting() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(1, config.tableCount());
        assertEquals(0, config.settingCount());

        config.set("settingA", "a");
        
        assertEquals(1, config.tableCount());
        assertEquals(1, config.settingCount());
        assertEquals("a", config.get("settingA"));
    }
    
    public void testSetAndGet_ThreeUniqueTopLevelSettings() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(1, config.tableCount());
        assertEquals(0, config.settingCount());
        
        config.set("settingA", "a");
        config.set("settingB", "b");
        config.set("settingC", "c");
        
        assertEquals(1, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("settingA"));
        assertEquals("b", config.get("settingB"));
        assertEquals("c", config.get("settingC"));
    }
  
    public void testSetAndGet_OneTopLevelSetting_OverwrittenTwice() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(1, config.tableCount());
        assertEquals(0, config.settingCount());
        
        config.set("settingA", "a");
        config.set("settingA", "b");
        config.set("settingA", "c");
        
        assertEquals(1, config.tableCount());
        assertEquals(1, config.settingCount());
        assertEquals("c", config.get("settingA"));
    }
    
    public void testSetAndGet_ThreeUniqueTopLevelSettings_DifferentTypes() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(1, config.tableCount());
        assertEquals(0, config.settingCount());
        
        config.set("settingA", 45);
        config.set("settingB", 'b');
        List<String> list = new LinkedList<String>();
        config.set("settingC", list);
        
        assertEquals(1, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals(45, config.get("settingA"));
        assertEquals('b', config.get("settingB"));
        assertEquals(list, config.get("settingC"));
    }

    public void testSetAndGet_SingleNestedSetting() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(1, config.tableCount());
        assertEquals(0, config.settingCount());
        
        config.set("section1.settingA", "a");
        
        assertEquals(2, config.tableCount());
        assertEquals(1, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
    }
    
    public void testSetAndGet_ThreeUniqueNestedSettings() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(1, config.tableCount());
        assertEquals(0, config.settingCount());
        
        config.set("section1.settingA", "a");
        config.set("section2.settingB", "b");
        config.set("section3.settingC", "c");
        
        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section2.settingB"));
        assertEquals("c", config.get("section3.settingC"));
    }
    
    public void testSetAndGet_ThreeIncreasinglyNestedSettings() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(1, config.tableCount());
        assertEquals(0, config.settingCount());
        
        config.set("section1.settingA", "a");
        config.set("section1.section2.settingB", "b");
        config.set("section1.section2.section3.settingC", "c");
        
        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section1.section2.settingB"));
        assertEquals("c", config.get("section1.section2.section3.settingC"));
    }
  
    public void testSetAndGet_OneNestedSetting_OverwrittenTwice() throws Exception{
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(1, config.tableCount());
        assertEquals(0, config.settingCount());
        
        config.set("section1.settingA", "a");
        config.set("section1.settingA", "b");
        config.set("section1.settingA", "c");
        
        assertEquals(2, config.tableCount());
        assertEquals(1, config.settingCount());
        assertEquals("c", config.get("section1.settingA"));
    }

    public void testSetAndGet_ExceptionOverwritingSettingWithSettingTable() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
        
        config.set("section1", "a");
 
        Exception caught = null;
        try {
            config.set("section1.settingB", "b");
        } catch(YWToolUsageException e) {
            caught = e;
        }
        
        assertNotNull(caught);
        assertEquals("Attempt to create a setting table that overwrites an existing setting value: section1.settingB", 
                     caught.getMessage());
    }

    public void testSetAndGet_ExceptionAccessingSettingAsASettingTable() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
        
        config.set("section1", "a");
 
        Exception caught = null;
        try {
            config.get("section1.settingB");
        } catch(YWToolUsageException e) {
            caught = e;
        }
        
        assertNotNull(caught);
        assertEquals("Attempt to access a setting value as a setting table: section1.settingB", 
                     caught.getMessage());
    }

    public void testFromYamlStream_ThreeUniqueTopLevelSettings_DifferentTypes() throws Exception {
        
        String yaml = 
              "settingA: 45"    + EOL +
              "settingB: 'b'"   + EOL +
              "settingC: "      + EOL +
              "  - 1"           + EOL;
          
        InputStream yamlStream = new ByteArrayInputStream(yaml.getBytes("UTF8"));
        YWConfiguration config = YWConfiguration.fromYamlStream(yamlStream);
            
        assertEquals(1, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals(45, config.get("settingA"));
        assertEquals("b", config.get("settingB"));
        assertTrue(config.get("settingC") instanceof List);
    }
    
    public void testFromYamlStream_ThreeUniqueNestedSettings() throws Exception {
        
        String yaml = 
            "section1:"         + EOL +
            "  settingA: a"     + EOL +
            "section2:"         + EOL +
            "  settingB: b"     + EOL +
            "section3:"         + EOL +
            "  settingC: c"     + EOL;
        
        InputStream yamlStream = new ByteArrayInputStream(yaml.getBytes("UTF8"));
        YWConfiguration config = YWConfiguration.fromYamlStream(yamlStream);

        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section2.settingB"));
        assertEquals("c", config.get("section3.settingC"));
    }

    public void testFromYamlStream_ThreeIncreasinglyNestedSettings() throws Exception {

        String yaml = 
                "section1:"             + EOL +
                "  settingA: a"         + EOL +
                ""                      + EOL +
                "  section2:"           + EOL +
                "    settingB: b"       + EOL +
                ""                      + EOL +
                "    section3:"         + EOL +
                "      settingC: c"     + EOL;
            
        InputStream yamlStream = new ByteArrayInputStream(yaml.getBytes("UTF8"));
        YWConfiguration config = YWConfiguration.fromYamlStream(yamlStream);

        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section1.section2.settingB"));
        assertEquals("c", config.get("section1.section2.section3.settingC"));
    }

    public void testFromYamlStream_MalformedYaml() throws Exception {

        String yaml = 
                "section1:"             + EOL +
                "  settingA: a"         + EOL +
                " bad"                  + EOL;
        InputStream yamlStream = new ByteArrayInputStream(yaml.getBytes("UTF8"));
        
        Exception caught = null;
        try {
            YWConfiguration.fromYamlStream(yamlStream);
        } catch(ParserException e) {
            caught = e;
        }
        
        assertNotNull(caught);
    }
    
    public void testFromYamlFile_ThreeUniqueTopLevelSettings_DifferentTypes() throws Exception {
        
        YWConfiguration config = YWConfiguration.fromYamlFile(
                TEST_RESOURCE_DIR + "config_three_types.yaml");
            
        assertEquals(1, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals(45, config.get("settingA"));
        assertEquals("b", config.get("settingB"));
        assertTrue(config.get("settingC") instanceof List);
    }
    
    public void testFromYamlFile_ThreeUniqueNestedSettings() throws Exception {
        
        YWConfiguration config = YWConfiguration.fromYamlFile(
                TEST_RESOURCE_DIR + "config_nested.yaml");

        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section2.settingB"));
        assertEquals("c", config.get("section3.settingC"));
    }
        
    public void testFromYamlFile_ThreeIncreasinglyNestedSettings() throws Exception {

        YWConfiguration config = YWConfiguration.fromYamlFile(
                TEST_RESOURCE_DIR + "config_increasing_nesting.yaml");

        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section1.section2.settingB"));
        assertEquals("c", config.get("section1.section2.section3.settingC"));
    }
    
    public void testFromYamlFile_FileNotFound() throws Exception {

        Exception caught = null;
        try {
            YWConfiguration.fromYamlFile("not_a_file.yaml");
        } catch(FileNotFoundException e) {
            caught = e;
        }
        
        assertNotNull(caught);
        assertEquals("YAML configuration file not found: not_a_file.yaml", caught.getMessage());
    }

    public void testFromYamlFile_MalformedYaml() throws Exception {

        Exception caught = null;
        try {
            YWConfiguration.fromYamlFile(TEST_RESOURCE_DIR + "config_bad.yaml");
        } catch(ParserException e) {
            caught = e;
        }
        
        assertNotNull(caught);
    }
    
    public void testApplyPropertiesFromReader_ThreeUniqueTopLevelSettings() throws Exception {
        
        String properties = 
                "settingA= a" + EOL +
                "settingB= b" + EOL +
                "settingC= c" + EOL;
 
        Reader reader = new StringReader(properties);
        
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(reader);
        
        config.set("settingA", "a");
        config.set("settingB", "b");
        config.set("settingC", "c");
        
        assertEquals(1, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("settingA"));
        assertEquals("b", config.get("settingB"));
        assertEquals("c", config.get("settingC"));
    }
    
    public void testApplyPropertiesFromReader_ThreeUniqueNestedSettings() throws Exception {
        
        String properties = 
            "section1.settingA = a"     + EOL +
            "section2.settingB = b"     + EOL +
            "section3.settingC = c"     + EOL;
        
        Reader reader = new StringReader(properties);
        
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(reader);

        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section2.settingB"));
        assertEquals("c", config.get("section3.settingC"));
    }
    
    public void testApplyPropertiesFromReader_ThreeIncreasinglyNestedSettings() throws Exception {

        String properties = 
                "section1.settingA                   = a"   + EOL +
                "section1.section2.settingB          = b"   + EOL +
                "section1.section2.section3.settingC = c"   + EOL;
            
        Reader reader = new StringReader(properties);
        
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(reader);

      assertEquals(4, config.tableCount());
      assertEquals(3, config.settingCount());
      assertEquals("a", config.get("section1.settingA"));
      assertEquals("b", config.get("section1.section2.settingB"));
      assertEquals("c", config.get("section1.section2.section3.settingC"));
    }
    
    public void testFromPropertyFile_ThreeUniqueNestedSettings() throws Exception {

        YWConfiguration config = new YWConfiguration();
        config.applyPropertyFile(
                TEST_RESOURCE_DIR + "config_nested.properties");

        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section2.settingB"));
        assertEquals("c", config.get("section3.settingC"));
    }
    
    public void testFromPropertyFile_ThreeIncreasinglyNestedSettings() throws Exception {

        YWConfiguration config = new YWConfiguration();
        config.applyPropertyFile(
                TEST_RESOURCE_DIR + "config_increasing_nesting.properties");

        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section1.section2.settingB"));
        assertEquals("c", config.get("section1.section2.section3.settingC"));
    }
    
    public void testFromPropertiesFile_FileNotFound() throws Exception {

        YWConfiguration config = new YWConfiguration();

        Exception caught = null;
        try {
            config.applyPropertyFile("not_a_file.properties");
        } catch(Exception e) {
            caught = e;
        }
        
        assertNotNull(caught);
        assertEquals("Property file not found: not_a_file.properties", caught.getMessage());
    }
    
    public void testApplyProperties_ThreeUniqueTopLevelSettings() throws Exception {
        
        Properties properties = new Properties();
        properties.setProperty("settingA", "a");
        properties.setProperty("settingB", "b");
        properties.setProperty("settingC", "c");
 
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(properties);
        
        assertEquals(1, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("settingA"));
        assertEquals("b", config.get("settingB"));
        assertEquals("c", config.get("settingC"));
    }
    
    public void testApplyProperties_ThreeUniqueNestedSettings() throws Exception {
        
        Properties properties = new Properties(); 
        properties.setProperty("section1.settingA", "a");
        properties.setProperty("section2.settingB", "b");
        properties.setProperty("section3.settingC", "c");
        
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(properties);

        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section2.settingB"));
        assertEquals("c", config.get("section3.settingC"));
    }
    
    public void testApplyProperties_ThreeIncreasinglyNestedSettings() throws Exception {

        Properties properties = new Properties(); 
        properties.setProperty("section1.settingA", "a");
        properties.setProperty("section1.section2.settingB", "b");
        properties.setProperty("section1.section2.section3.settingC", "c");
            
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(properties);

        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section1.section2.settingB"));
        assertEquals("c", config.get("section1.section2.section3.settingC"));
    }

    public void testApplyProperties_Exception_OverwritesSettingTable() throws Exception {
        
        String properties = 
                "extract.sources            = ../simulate_data_collection.py"  + EOL +
                "extract.listfile           = listing.txt"                     + EOL +
                "extract.language.factsfile = xsb/extractfacts.P"              + EOL;
      
        Reader reader = new StringReader(properties);
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(reader);

        String conflictingProperty =
                "extract.language = python"              + EOL;
        reader = new StringReader(conflictingProperty);
        
        Exception caught = null;
        try {
            config.applyProperties(reader);
        } catch(Exception e) {
            caught = e;
        }
        
        assertNotNull(caught);
        assertEquals("Attempt to assign a setting value that overwrites an existing setting table: extract.language", 
                     caught.getMessage());
    }

    public void testApplyProperties_Exception_OverwritesSettingWithTable() throws Exception {
        
        String properties = 
                "extract.sources            = ../simulate_data_collection.py"   + EOL +
                "extract.listfile           = listing.txt"                      + EOL +
                "extract.language = python"                                     + EOL;
      
        Reader reader = new StringReader(properties);
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(reader);

        String conflictingProperty =
                "extract.language.factsfile = xsb/extractfacts.P"              + EOL;
        
        reader = new StringReader(conflictingProperty);
        
        Exception caught = null;
        try {
            config.applyProperties(reader);
        } catch(Exception e) {
            caught = e;
        }
        
        assertNotNull(caught);
        assertEquals("Attempt to create a setting table that overwrites an existing setting value: extract.language.factsfile", 
                     caught.getMessage());
    }
    
    public void testApplyOption_ThreeUniqueTopLevelSettings() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        config.applyOption("settingA=a");
        config.applyOption("settingB=b");
        config.applyOption("settingC=c");
 
        assertEquals(1, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("settingA"));
        assertEquals("b", config.get("settingB"));
        assertEquals("c", config.get("settingC"));
    }
    
    public void testApplyOption_ThreeUniqueNestedSettings() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        config.applyOption("section1.settingA=a");
        config.applyOption("section2.settingB=b");
        config.applyOption("section3.settingC=c");
        
        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section2.settingB"));
        assertEquals("c", config.get("section3.settingC"));
    }
    
    public void testApplyOption_ThreeIncreasinglyNestedSettings() throws Exception {

        YWConfiguration config = new YWConfiguration();
        config.applyOption("section1.settingA=a");
        config.applyOption("section1.section2.settingB=b");
        config.applyOption("section1.section2.section3.settingC=c");
            
        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section1.section2.settingB"));
        assertEquals("c", config.get("section1.section2.section3.settingC"));
    }

    public void testApplyOptions_ThreeUniqueTopLevelSettings() throws Exception {

        List<String> options = new LinkedList<String>();
        options.add("settingA=a");
        options.add("settingB=b");
        options.add("settingC=c");
 
        YWConfiguration config = new YWConfiguration();
        config.applyOptions(options);
        
        assertEquals(1, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("settingA"));
        assertEquals("b", config.get("settingB"));
        assertEquals("c", config.get("settingC"));
    }
    
    public void testApplyOptions_ThreeUniqueNestedSettings() throws Exception {
        
        List<String> options = new LinkedList<String>();
        options.add("section1.settingA=a");
        options.add("section2.settingB=b");
        options.add("section3.settingC=c");
        
        YWConfiguration config = new YWConfiguration();
        config.applyOptions(options);

        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section2.settingB"));
        assertEquals("c", config.get("section3.settingC"));
    }
    
    public void testApplyOptions_ThreeIncreasinglyNestedSettings() throws Exception {

        List<String> options = new LinkedList<String>();
        options.add("section1.settingA=a");
        options.add("section1.section2.settingB=b");
        options.add("section1.section2.section3.settingC=c");
            
        YWConfiguration config = new YWConfiguration();
        config.applyOptions(options);

        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section1.section2.settingB"));
        assertEquals("c", config.get("section1.section2.section3.settingC"));
    }    

    public void testGetSection_ThreeIncreasinglyNestedSettings() throws Exception {

        List<String> options = new LinkedList<String>();
        options.add("setting0=0");
        options.add("section1.settingA=a");
        options.add("section1.section2.settingB=b");
        options.add("section1.section2.section3.settingC=c");
            
        YWConfiguration config = new YWConfiguration();
        config.applyOptions(options);
        
        assertEquals(4, config.tableCount());
        assertEquals(4, config.settingCount());
        assertEquals("0", config.get("setting0"));
        
        Map<String,Object> section1 = config.getSection("section1");
        assertEquals(3, YWConfiguration.tableCount(section1));
        assertEquals(3, YWConfiguration.settingCount(section1));
        assertEquals("a", section1.get("settingA"));

        Map<String,Object> section2 = config.getSection("section1.section2");
        assertEquals(2, YWConfiguration.tableCount(section2));
        assertEquals(2, YWConfiguration.settingCount(section2));
        assertEquals("b", section2.get("settingB"));

        Map<String,Object> section3 = config.getSection("section1.section2.section3");
        assertEquals(1, YWConfiguration.tableCount(section3));
        assertEquals(1, YWConfiguration.settingCount(section3));
        assertEquals("c", section3.get("settingC"));
    }

    private static String ywproperties = 
            "extract.sources     = ../simulate_data_collection.py"  + EOL +
            "extract.listfile    = listing.txt"                     + EOL +
            "extract.language    = python"                          + EOL +
            "extract.factsfile   = xsb/extractfacts.P"              + EOL +
            ""                                                      + EOL +
            "model.workflow      = simulate_data_collection"        + EOL +
            "model.factsfile     = xsb/modelfacts.P"                + EOL +
            ""                                                      + EOL +
            "recon.rundir        = ../"                             + EOL +
            "recon.factsfile     = xsb/reconfacts.P"                + EOL +
             ""                                                     + EOL +
            "graph.view          = combined"                        + EOL +
            "graph.layout        = tb"                              + EOL +
            "graph.dotcomments   = on"                              + EOL +
            "graph.dotfile       = combined.gv"                     + EOL +
            "graph.workflowbox   = show"                            + EOL +
            "graph.edgelabels    = show"                            + EOL +
            "graph.portlayout    = group"                           + EOL +
            "graph.datalabel     = both"                            + EOL +
            "graph.params        = reduce"                          + EOL +
            "graph.titleposition = top"                             + EOL +
            ""                                                      + EOL +
            "query.engine        = xsb"                             + EOL;

    
    public void testGetSection_YesWorkflowProperties() throws Exception {
        
        Reader reader = new StringReader(ywproperties);
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(reader);
                
        assertEquals(6, config.tableCount());
        assertEquals(19, config.settingCount());
        
        Map<String,Object> extract = config.getSection("extract");
        assertEquals(4, YWConfiguration.settingCount(extract));
        assertEquals("../simulate_data_collection.py", config.get("extract.sources"));
        assertEquals("../simulate_data_collection.py", extract.get("sources"));

        Map<String,Object> model = config.getSection("model");
        assertEquals(2, YWConfiguration.settingCount(model));
        assertEquals("simulate_data_collection", config.get("model.workflow"));
        assertEquals("simulate_data_collection", model.get("workflow"));

        Map<String,Object> recon = config.getSection("recon");
        assertEquals(2, YWConfiguration.settingCount(recon));
        assertEquals("../", config.get("recon.rundir"));
        assertEquals("../", recon.get("rundir"));

        Map<String,Object> graph = config.getSection("graph");
        assertEquals(10, YWConfiguration.settingCount(graph));
        assertEquals("combined", config.get("graph.view"));
        assertEquals("combined", graph.get("view"));

        Map<String,Object> query = config.getSection("query");
        assertEquals(1, YWConfiguration.settingCount(query));
        assertEquals("xsb", config.get("query.engine"));
        assertEquals("xsb", query.get("engine"));
    }
    
    public void testGet_YesWorkflowProperties_MissingTopLevelProperty() throws Exception {
        
        Reader reader = new StringReader(ywproperties);
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(reader);
        
        assertNull(config.get("foo"));
    }

    public void testGet_YesWorkflowProperties_MissingNestedProperty() throws Exception {
        
        Reader reader = new StringReader(ywproperties);
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(reader);
        
        assertNull(config.get("graph.foo"));
    }

    public void testGet_YesWorkflowProperties_MissingSection() throws Exception {
        
        Reader reader = new StringReader(ywproperties);
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(reader);
        
        assertNull(config.get("foo.bar"));
    }

    public void testGetSection_YesWorkflowProperties_MissingSection() throws Exception {
        
        Reader reader = new StringReader(ywproperties);
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(reader);
        
        assertNull(config.getSection("foo"));
    }

    public void testGetSection_YesWorkflowProperties_MissingNestingSection() throws Exception {
        
        Reader reader = new StringReader(ywproperties);
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(reader);
        
        assertNull(config.getSection("graph.foo"));
    }
    
    public void testGetSection_YesWorkflowProperties_SettingNotASection() throws Exception {
        
        Reader reader = new StringReader(ywproperties);
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(reader);
        
        assertEquals("combined", config.get("graph.view"));
        assertNull(config.getSection("graph.view"));
    }

    public void testSet_YesWorkflowProperties_SettingNotASection() throws Exception {
        
        Reader reader = new StringReader(ywproperties);
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(reader);

        Exception caught = null;
        try {
            config.set("graph.view.size", "large");
        } catch(Exception e) {
            caught = e;
        }
        
        assertNotNull(caught);
        assertEquals("Attempt to create a setting table that overwrites an existing setting value: graph.view.size", 
                     caught.getMessage());
    }

    public void testSet_YesWorkflowProperties_SetUnsupportedOnSection() throws Exception {
        
        Reader reader = new StringReader(ywproperties);
        YWConfiguration config = new YWConfiguration();
        config.applyProperties(reader);

        Map<String,Object> graph = config.getSection("graph");
        
        Exception caught = null;
        try {
            graph.put("size", "large");
        } catch(UnsupportedOperationException e) {
            caught = e;
        }
        
        assertNotNull(caught);
        assertEquals("Configuration section may not be modified.", 
                     caught.getMessage());
    }    
}
