package org.yesworkflow.comments;

import org.yesworkflow.util.YesWorkflowTestCase;

public class TestBeginComment extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testBeginComment_NameOnly() throws Exception {
        BeginComment comment = new BeginComment("@begin main");
        assertEquals("main", comment.programName);
        assertNull(comment.description);
    }

    public void testBeginComment_NameAndOneWordDescription() throws Exception {
        BeginComment comment = new BeginComment("@begin main myprogram");
        assertEquals("main", comment.programName);
        assertEquals("myprogram", comment.description);
    }

    public void testBeginComment_NameAndTwoWordDescription() throws Exception {
        BeginComment comment = new BeginComment("@begin main my program");
        assertEquals("main", comment.programName);
        assertEquals("my program", comment.description);
    }
}
