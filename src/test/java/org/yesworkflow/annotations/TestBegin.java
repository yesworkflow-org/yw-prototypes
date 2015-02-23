package org.yesworkflow.annotations;

import org.yesworkflow.annotations.Begin;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestBegin extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testBeginComment_NameOnly() throws Exception {
        Begin begin = new Begin("@begin main");
        assertEquals("main", begin.name);
        assertNull(begin.description);
    }

    public void testBeginComment_NameAndOneWordDescription() throws Exception {
        Begin begin = new Begin("@begin main myprogram");
        assertEquals("main", begin.name);
        assertEquals("myprogram", begin.description);
    }

    public void testBeginComment_NameAndTwoWordDescription() throws Exception {
        Begin begin = new Begin("@begin main my program");
        assertEquals("main", begin.name);
        assertEquals("my program", begin.description);
    }
}
