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
        assertEquals("main", end.name);
    }

    public void testEndComment_WithDescription() throws Exception {
        End end = new End(1L, 1L, 1L, "@end main extra stuff");
        assertEquals("main", end.name);
        assertEquals("extra stuff", end.description);
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
