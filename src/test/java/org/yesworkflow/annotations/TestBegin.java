package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.extract.SourceLine;

public class TestBegin extends YesWorkflowTestCase {

    private static final SourceLine line = new SourceLine(1, 1, 1, "");
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testBeginComment_NameOnly() throws Exception {
        Begin begin = new Begin(1, line, "@begin main");
        assertEquals("main", begin.name);
        assertNull(begin.description);
    }

    public void testBeginComment_NameAndOneWordDescription() throws Exception {
        Begin begin = new Begin(1, line, "@begin main myprogram");
        assertEquals("main", begin.name);
        assertEquals("myprogram", begin.description);
    }

    public void testBeginComment_NameAndTwoWordDescription() throws Exception {
        Begin begin = new Begin(1, line, "@begin main my program");
        assertEquals("main", begin.name);
        assertEquals("my program", begin.description);
    }
}
