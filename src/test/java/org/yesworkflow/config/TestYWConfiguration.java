package org.yesworkflow.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.exceptions.YWToolUsageException;

public class TestYWConfiguration extends YesWorkflowTestCase {

    public void testDefaultConstructor_SettingCountIsZero() {
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
    }

    public void testSetAndGet_SingleTopLevelSetting() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
        
        config.set("settingA", "a");
        
        assertEquals(1, config.settingCount());
        assertEquals("a", config.get("settingA"));
    }
    
    public void testSetAndGet_ThreeUniqueTopLevelSettings() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
        
        config.set("settingA", "a");
        config.set("settingB", "b");
        config.set("settingC", "c");
        
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("settingA"));
        assertEquals("b", config.get("settingB"));
        assertEquals("c", config.get("settingC"));
    }
  
    public void testSetAndGet_OneTopLevelSetting_OverwrittenTwice() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
        
        config.set("settingA", "a");
        config.set("settingA", "b");
        config.set("settingA", "c");
        
        assertEquals(1, config.settingCount());
        assertEquals("c", config.get("settingA"));
    }
    
    public void testSetAndGet_ThreeUniqueTopLevelSettings_DifferentTypes() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
        
        config.set("settingA", 45);
        config.set("settingB", 'b');
        List<String> list = new LinkedList<String>();
        config.set("settingC", list);
        
        assertEquals(3, config.settingCount());
        assertEquals(45, config.get("settingA"));
        assertEquals('b', config.get("settingB"));
        assertEquals(list, config.get("settingC"));
    }

    public void testSetAndGet_SingleNestedSetting() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
        
        config.set("section1.settingA", "a");
        
        assertEquals(1, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
    }
    
    public void testSetAndGet_ThreeUniqueNestedSettings() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
        
        config.set("section1.settingA", "a");
        config.set("section2.settingB", "b");
        config.set("section3.settingC", "c");
        
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section2.settingB"));
        assertEquals("c", config.get("section3.settingC"));
    }
    
    public void testSetAndGet_ThreeIncreasinglyNestedSettings() throws Exception {
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
        
        config.set("section1.settingA", "a");
        config.set("section1.section2.settingB", "b");
        config.set("section1.section2.section3.settingC", "c");
        
        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section1.section2.settingB"));
        assertEquals("c", config.get("section1.section2.section3.settingC"));
    }
  
    public void testSetAndGet_OneNestedSetting_OverwrittenTwice() throws Exception{
        
        YWConfiguration config = new YWConfiguration();
        assertEquals(0, config.settingCount());
        
        config.set("section1.settingA", "a");
        config.set("section1.settingA", "b");
        config.set("section1.settingA", "c");
        
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

        assertEquals(3, config.settingCount());
        assertEquals("a", config.get("section1.settingA"));
        assertEquals("b", config.get("section1.section2.settingB"));
        assertEquals("c", config.get("section1.section2.section3.settingC"));
    }
}
