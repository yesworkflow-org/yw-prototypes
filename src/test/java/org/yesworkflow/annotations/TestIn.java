package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.annotations.In;
import org.yesworkflow.extract.CommentLine;

public class TestIn extends YesWorkflowTestCase {

    private static final CommentLine line = new CommentLine(1L, 1L, 1L, "");

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testInComment_VariableOnly() throws Exception {
        In in = new In(1L, line, "@in x");
        assertEquals("x", in.name);
        assertEquals("x", in.binding());
        assertNull(in.description);
    }
    
    public void testInComment_LiteralNumberOnly() throws Exception {
        In in = new In(1L, line, "@in 30.7");
        assertEquals("30.7", in.name);
        assertEquals("30.7", in.binding());
        assertNull(in.description);
    }

    public void testInComment_LiteralStringOnly() throws Exception {
        In in = new In(1L, line, "@in \"datafile.csv\"");
        assertEquals("\"datafile.csv\"", in.name);
        assertEquals("\"datafile.csv\"", in.binding());
        assertNull(in.description);
    }
    
    public void testInComment_VariableAndDescription() throws Exception {
        In in = new In(1L, line, "@in x The longitude");
        assertEquals("x", in.name);
        assertEquals("x", in.binding());
        assertEquals("The longitude", in.description);
    }
    
    public void testInComment_VariableAndLabel() throws Exception {
        In in = (In) new In(1L, line, "@in x ");
        new As(2L, line, "@as longitude", in);
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertNull(in.description);
    }

    public void testInComment_VariableAndLabel_ExtraSpaces() throws Exception {
        In in = (In) new In(1L, line, "@in x  ");
        new As(2L, line, "  @as  longitude", in);
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertNull(in.description);
    }

    public void testInComment_VariableLabelDescription_DescriptionOnName() throws Exception {
        In in = (In) new In(1L, line, "@in x    Half of the coordinates ");
        new As(2L, line, " @as  longitude", in);
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertEquals("Half of the coordinates", in.description);
    }

    public void testInComment_VariableLabelDescription_DescriptionOnAlias() throws Exception {
        In in = (In) new In(1L, line, "@in x   ");
        new As(2L, line, " @as  longitude  Half of the coordinates", in);
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertEquals("Half of the coordinates", in.description);
    }

    public void testInComment_VariableLabelDescription_DescriptionOnNameAndAlias() throws Exception {
        In in = (In) new In(1L, line, "@in x Half of   ");
        new As(2L, line, " @as  longitude   the coordinates", in);
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertEquals("Half of the coordinates", in.description);
    }
    
}
