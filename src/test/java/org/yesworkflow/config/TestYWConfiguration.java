package org.yesworkflow.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

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
    
    public void testFromYamlFile_ThreeUniqueTopLevelSettings_DifferentTypes() throws Exception {
        
        YWConfiguration config = YWConfiguration.fromYamlFile(
                TEST_RESOURCE_DIR + "config_three_types.yaml");
            
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
    
    public void testFromYamlFile_ThreeUniqueNestedSettings() throws Exception {
        
        YWConfiguration config = YWConfiguration.fromYamlFile(
                TEST_RESOURCE_DIR + "config_nested.yaml");

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
    
    public void testFromYamlFile_ThreeIncreasinglyNestedSettings() throws Exception {

        YWConfiguration config = YWConfiguration.fromYamlFile(
                TEST_RESOURCE_DIR + "config_increasing_nesting.yaml");

        assertEquals(4, config.tableCount());
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section1.section2.settingB"));
        assertEquals("c", config.get("section1.section2.section3.settingC"));
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
}
