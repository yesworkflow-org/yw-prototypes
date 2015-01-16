package org.yesworkflow.comments;

import org.yesworkflow.util.YesWorkflowTestCase;

public class TestInComment extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testInComment_VariableOnly() throws Exception {
        InComment comment = new InComment("@in x");
        assertEquals("x", comment.name);
        assertNull(comment.alias);
        assertNull(comment.description);
    }
    
    public void testInComment_LiteralNumberOnly() throws Exception {
        InComment comment = new InComment("@in 30.7");
        assertEquals("30.7", comment.name);
        assertNull(comment.alias);
        assertNull(comment.description);
    }

    public void testInComment_LiteralStringOnly() throws Exception {
        InComment comment = new InComment("@in \"datafile.csv\"");
        assertEquals("\"datafile.csv\"", comment.name);
        assertNull(comment.alias);
        assertNull(comment.description);
    }
    
    public void testInComment_VariableAndDescription() throws Exception {
        InComment comment = new InComment("@in x The longitude");
        assertEquals("x", comment.name);
        assertEquals("The longitude", comment.description);
        assertNull(comment.alias);
    }
    
    public void testInComment_VariableAndLabel() throws Exception {
        InComment comment = new InComment("@in x @as longitude");
        assertEquals("x", comment.name);
        assertEquals("longitude", comment.alias);
        assertNull(comment.description);
    }

    public void testInComment_VariableAndLabel_ExtraSpaces() throws Exception {
        InComment comment = new InComment("@in x    @as  longitude");
        assertEquals("x", comment.name);
        assertEquals("longitude", comment.alias);
        assertNull(comment.description);
    }

    public void testInComment_VariableLabelDescription_ExtraSpaces() throws Exception {
        InComment comment = new InComment("@in x    @as  longitude  Half of the coordinates");
        assertEquals("x", comment.name);
        assertEquals("longitude", comment.alias);
        assertEquals("Half of the coordinates", comment.description);
    }
}
