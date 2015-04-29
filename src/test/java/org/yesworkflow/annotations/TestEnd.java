package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.annotations.End;
import org.yesworkflow.extract.SourceLine;

public class TestEnd extends YesWorkflowTestCase {

    private static final SourceLine line = new SourceLine(1, 1, 1, "");
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testEndComment_NameOnly() throws Exception {
        End end = new End(1, line, "@end main");
        assertEquals("main", end.name);
    }

    public void testEndComment_WithDescription() throws Exception {
        End end = new End(1, line, "@end main extra stuff");
        assertEquals("main", end.name);
        assertEquals("extra stuff", end.description);
    }
}
