package org.yesworkflow.annotations;

import org.yesworkflow.annotations.End;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestEnd extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testEndComment_NameOnly() throws Exception {
        End annotation = new End("@end main");
        assertEquals("main", annotation.name);
    }

    public void EndComment() throws Exception {
        End annotation = new End("@end main extra stuff");
        assertEquals("main", annotation.name);
    }
}
