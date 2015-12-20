package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.annotations.In;
import org.yesworkflow.exceptions.YWMarkupException;

public class TestIn extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testInComment_VariableOnly() throws Exception {
        In in = new In(1L, 1L, 1L, "@in x");
        assertEquals("x", in.value);
        assertEquals("x", in.binding());
        assertNull(in.description());
    }
    
    public void testInComment_LiteralNumberOnly() throws Exception {
        In in = new In(1L, 1L, 1L, "@in 30.7");
        assertEquals("30.7", in.value);
        assertEquals("30.7", in.binding());
        assertNull(in.description());
    }

    public void testInComment_LiteralStringOnly() throws Exception {
        In in = new In(1L, 1L, 1L, "@in \"datafile.csv\"");
        assertEquals("\"datafile.csv\"", in.value);
        assertEquals("\"datafile.csv\"", in.binding());
        assertNull(in.description());
    }
    
    public void testInComment_VariableAndDescription() throws Exception {
        In in = new In(1L, 1L, 1L, "@in x");
        new Desc(2L, 1L, 1L, "@desc The longitude", in);
        assertEquals("x", in.value);
        assertEquals("x", in.binding());
        assertEquals("The longitude", in.description());
    }
    
    public void testInComment_VariableAndLabel() throws Exception {
        In in = (In) new In(1L, 1L, 1L, "@in x ");
        new As(2L, 1L, 1L, "@as longitude", in);
        assertEquals("x", in.value);
        assertEquals("longitude", in.binding());
        assertNull(in.description());
    }

    public void testInComment_VariableAndLabel_ExtraSpaces() throws Exception {
        In in = (In) new In(1L, 1L, 1L, "@in x  ");
        new As(2L, 1L, 1L, "  @as  longitude", in);
        assertEquals("x", in.value);
        assertEquals("longitude", in.binding());
        assertNull(in.description());
    }

    public void testInComment_VariableLabelDescription_DescriptionOnNameAndAlias() throws Exception {
        In in = (In) new In(1L, 1L, 1L, "@in x ");
        new As(2L, 1L, 1L, " @as  longitude  ", in);
        new Desc(3L, 1L, 1L, "@desc Half of  the coordinates", in);
        assertEquals("x", in.value);
        assertEquals("longitude", in.binding());
        assertEquals("Half of the coordinates", in.description());
    }

    public void testInComment_NoArgument() throws Exception {
        Exception caught = null;
        try {
            new In(1L, 1L, 1L, "@in");
        } catch (YWMarkupException e) {
            caught = e;
        }
        assertNotNull(caught);
        assertEquals("No argument provided to @in keyword on line 1", caught.getMessage());
    }
}
