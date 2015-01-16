package org.yesworkflow.comments;

import org.yesworkflow.util.YesWorkflowTestCase;

public class TestOutComment extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testOutComment_VariableOnly() throws Exception {
        OutComment comment = new OutComment("@out x");
        assertEquals("x", comment.name);
        assertNull(comment.alias);
        assertNull(comment.description);
    }
    
    public void testOutComment_VariableAndDescription() throws Exception {
        OutComment comment = new OutComment("@out x The longitude");
        assertEquals("x", comment.name);
        assertEquals("The longitude", comment.description);
        assertNull(comment.alias);
    }
    
    public void testOutComment_VariableAndLabel() throws Exception {
        OutComment comment = new OutComment("@out x @as longitude");
        assertEquals("x", comment.name);
        assertEquals("longitude", comment.alias);
        assertNull(comment.description);
    }

    public void testOutComment_VariableAndLabel_ExtraSpaces() throws Exception {
        OutComment comment = new OutComment("@out x    @as  longitude");
        assertEquals("x", comment.name);
        assertEquals("longitude", comment.alias);
        assertNull(comment.description);
    }

    public void testOutComment_VariableLabelDescription_ExtraSpaces() throws Exception {
        OutComment comment = new OutComment("@out x    @as  longitude  Half of the coordinates");
        assertEquals("x", comment.name);
        assertEquals("longitude", comment.alias);
        assertEquals("Half of the coordinates", comment.description);
    }
}
