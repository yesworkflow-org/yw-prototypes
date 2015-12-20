package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.annotations.End;
import org.yesworkflow.exceptions.YWMarkupException;

public class TestEnd extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testEndComment_NameOnly() throws Exception {
        End end = new End(1L, 1L, 1L, "@end main");
        assertEquals("main", end.value);
    }

    public void testEndComment_WithDescription() throws Exception {
        End end = new End(1L, 1L, 1L, "@end main ");
        new Desc(2L, 1L, 1L, "@desc extra stuff", end);
        assertEquals("main", end.value);
        assertEquals("extra stuff", end.description());
    }
    
    public void testEndComment_NoArgument() throws Exception {
        Exception caught = null;
        try {
            new End(1L, 1L, 1L, "@end");
        } catch (YWMarkupException e) {
            caught = e;
        }
        assertNotNull(caught);
        assertEquals("No argument provided to @end keyword on line 1", caught.getMessage());
    }

}
