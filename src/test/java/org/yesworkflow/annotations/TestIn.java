package org.yesworkflow.annotations;

import org.yesworkflow.annotations.In;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestIn extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testInComment_VariableOnly() throws Exception {
        In in = new In("@in x");
        assertEquals("x", in.name);
        assertEquals("x", in.binding());
        assertNull(in.description);
    }
    
    public void testInComment_LiteralNumberOnly() throws Exception {
        In in = new In("@in 30.7");
        assertEquals("30.7", in.name);
        assertEquals("30.7", in.binding());
        assertNull(in.description);
    }

    public void testInComment_LiteralStringOnly() throws Exception {
        In in = new In("@in \"datafile.csv\"");
        assertEquals("\"datafile.csv\"", in.name);
        assertEquals("\"datafile.csv\"", in.binding());
        assertNull(in.description);
    }
    
    public void testInComment_VariableAndDescription() throws Exception {
        In in = new In("@in x The longitude");
        assertEquals("x", in.name);
        assertEquals("x", in.binding());
        assertEquals("The longitude", in.description);
    }
    
    public void testInComment_VariableAndLabel() throws Exception {
        In in = (In) new In("@in x ").qualifyWith(new As("@as longitude"));
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertNull(in.description);
    }

    public void testInComment_VariableAndLabel_ExtraSpaces() throws Exception {
        In in = (In) new In("@in x  ").qualifyWith(new As("  @as  longitude"));
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertNull(in.description);
    }

    public void testInComment_VariableLabelDescription_DescriptionOnName() throws Exception {
        In in = (In) new In("@in x    Half of the coordinates ")
        	.qualifyWith(new As(" @as  longitude"));
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertEquals("Half of the coordinates", in.description);
    }

    public void testInComment_VariableLabelDescription_DescriptionOnAlias() throws Exception {
        In in = (In) new In("@in x   ")
        	.qualifyWith(new As(" @as  longitude  Half of the coordinates"));
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertEquals("Half of the coordinates", in.description);
    }

    public void testInComment_VariableLabelDescription_DescriptionOnNameAndAlias() throws Exception {
        In in = (In) new In("@in x Half of   ")
        	.qualifyWith(new As(" @as  longitude   the coordinates"));
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertEquals("Half of the coordinates", in.description);
    }
    
}
