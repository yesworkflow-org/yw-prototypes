package org.yesworkflow.comments;

import org.yesworkflow.util.YesWorkflowTestCase;

public class TestEndComment extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testEndComment_NameOnly() throws Exception {
        EndComment comment = new EndComment("@end main");
        assertEquals("main", comment.programName);
    }

    public void EndComment() throws Exception {
        EndComment comment = new EndComment("@end main extra stuff");
        assertEquals("main", comment.programName);
    }
}
