package org.yesworkflow.annotations;

import org.yesworkflow.annotations.In;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestIn extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testInComment_VariableOnly() throws Exception {
        In annotation = new In("@in x");
        assertEquals("x", annotation.name);
        assertEquals("x", annotation.binding());
        assertNull(annotation.description);
    }
    
    public void testInComment_LiteralNumberOnly() throws Exception {
        In annotation = new In("@in 30.7");
        assertEquals("30.7", annotation.name);
        assertEquals("30.7", annotation.binding());
        assertNull(annotation.description);
    }

    public void testInComment_LiteralStringOnly() throws Exception {
        In annotation = new In("@in \"datafile.csv\"");
        assertEquals("\"datafile.csv\"", annotation.name);
        assertEquals("\"datafile.csv\"", annotation.binding());
        assertNull(annotation.description);
    }
    
    public void testInComment_VariableAndDescription() throws Exception {
        In annotation = new In("@in x The longitude");
        assertEquals("x", annotation.name);
        assertEquals("x", annotation.binding());
        assertEquals("The longitude", annotation.description);
    }
    
    public void testInComment_VariableAndLabel() throws Exception {
        In comment = (In) new In("@in x ").qualifyWith(new As("@as longitude"));
        assertEquals("x", comment.name);
        assertEquals("longitude", comment.binding());
        assertNull(comment.description);
    }

    public void testInComment_VariableAndLabel_ExtraSpaces() throws Exception {
        In annotation = (In) new In("@in x  ").qualifyWith(new As("  @as  longitude"));
        assertEquals("x", annotation.name);
        assertEquals("longitude", annotation.binding());
        assertNull(annotation.description);
    }

    public void testInComment_VariableLabelDescription_ExtraSpaces() throws Exception {
        In annotation = (In) new In("@in x   ")
        	.qualifyWith(new As(" @as  longitude  Half of the coordinates"));
        assertEquals("x", annotation.name);
        assertEquals("longitude", annotation.binding());
        assertEquals("Half of the coordinates", annotation.description);
    }
}
