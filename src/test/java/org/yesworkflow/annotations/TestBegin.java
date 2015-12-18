package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.exceptions.YWMarkupException;

public class TestBegin extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testBeginComment_NameOnly() throws Exception {
        Begin begin = new Begin(1L, 1L, 1L, "@begin main");
        assertEquals("main", begin.name);
        assertNull(begin.description);
    }

    public void testBeginComment_NameAndTwoWordDescription() throws Exception {
        Begin begin = new Begin(1L, 1L, 1L, "@begin main ");
        new Desc(2L, 1L, 1L, "@desc my program", begin);
        assertEquals("main", begin.name);
        assertEquals("my program", begin.description());
    }
    
    public void testBeginComment_NoArgument() throws Exception {
        Exception caught = null;
        try {
            new Begin(1L, 1L, 1L, "@begin");
        } catch (YWMarkupException e) {
            caught = e;
        }
        assertNotNull(caught);
        assertEquals("No argument provided to @begin keyword on line 1", caught.getMessage());
    }
}
