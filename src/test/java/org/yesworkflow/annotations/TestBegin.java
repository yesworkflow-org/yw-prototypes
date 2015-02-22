package org.yesworkflow.annotations;

import org.yesworkflow.annotations.Begin;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestBegin extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testBeginComment_NameOnly() throws Exception {
        Begin annotation = new Begin("@begin main");
        assertEquals("main", annotation.name);
        assertNull(annotation.description);
    }

    public void testBeginComment_NameAndOneWordDescription() throws Exception {
        Begin annotation = new Begin("@begin main myprogram");
        assertEquals("main", annotation.name);
        assertEquals("myprogram", annotation.description);
    }

    public void testBeginComment_NameAndTwoWordDescription() throws Exception {
        Begin annotation = new Begin("@begin main my program");
        assertEquals("main", annotation.name);
        assertEquals("my program", annotation.description);
    }
}
