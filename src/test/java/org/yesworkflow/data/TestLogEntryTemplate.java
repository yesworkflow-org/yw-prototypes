package org.yesworkflow.data;

import java.util.Map;

import org.yesworkflow.YesWorkflowTestCase;

public class TestLogEntryTemplate extends YesWorkflowTestCase {

    public void testTemplate_RunLog_ProcessingSamplesInCassette() throws Exception {

        LogEntryTemplate t = new LogEntryTemplate("{timestamp} Processing samples in cassette {cassette_id}");
        assertEquals("{} Processing samples in cassette {}", t.reducedTemplate);
        
        assertEquals(2, t.fragments.length);
        assertEquals("", t.fragments[0]);
        assertEquals(" Processing samples in cassette ", t.fragments[1]);
 
        assertEquals(2, t.variables.length);
        assertEquals("timestamp", t.variables[0].name);
        assertEquals("cassette_id", t.variables[1].name);
        
        assertEquals(2, t.instances.length);
        assertEquals("timestamp", t.instances[0].name);
        assertEquals("cassette_id", t.instances[1].name);

        Map<String,String> variables = t.extractValuesFromLogEntry(
                "2016-03-29 19:15:45 Processing samples in cassette q55");
        assertEquals(2, variables.size());
        assertEquals("2016-03-29 19:15:45", variables.get("timestamp"));
        assertEquals("q55", variables.get("cassette_id"));
        
        assertNull(t.extractValuesFromLogEntry("Processing samples in cassette q55"));
        assertNull(t.extractValuesFromLogEntry("2016-03-29 19:15:45 Processing samples in cassette"));
    }
    
	public void testTemplate_RunLog_SampleQualityCutoff() throws Exception {

	    LogEntryTemplate t = new LogEntryTemplate("{timestamp} Sample quality cutoff: {sample_score_cutoff}");
	    assertEquals("{} Sample quality cutoff: {}", t.reducedTemplate);
	    
	    assertEquals(2, t.fragments.length);
        assertEquals("", t.fragments[0]);
	    assertEquals(" Sample quality cutoff: ", t.fragments[1]);

	    assertEquals(2, t.variables.length);
        assertEquals("timestamp", t.variables[0].name);
	    assertEquals("sample_score_cutoff", t.variables[1].name);
	    
	    assertEquals(2, t.instances.length);
        assertEquals("timestamp", t.instances[0].name);
        assertEquals("sample_score_cutoff", t.instances[1].name);
        
        Map<String,String> variables = t.extractValuesFromLogEntry(
                "2016-03-29 19:15:45 Sample quality cutoff: 12.0");
        assertEquals(2, variables.size());
        assertEquals("2016-03-29 19:15:45", variables.get("timestamp"));
        assertEquals("12.0", variables.get("sample_score_cutoff"));

        assertNull(t.extractValuesFromLogEntry(
                "2016-03-29 19:15:45 Processing samples in cassette q55"));
	}

   public void testTemplate_RunLog_SampleQualityCutoff_WithPeriod() {
        LogEntryTemplate t = new LogEntryTemplate("Sample quality cutoff: {sample_score_cutoff}.");
        assertEquals("Sample quality cutoff: {}.", t.reducedTemplate);
        assertEquals(2, t.fragments.length);
        assertEquals("Sample quality cutoff: ", t.fragments[0]);
        assertEquals(".", t.fragments[1]);
        assertEquals(1, t.variables.length);
        assertEquals("sample_score_cutoff", t.variables[0].name);
        assertEquals(1, t.instances.length);
        assertEquals("sample_score_cutoff", t.instances[0].name);
    }

}
	
