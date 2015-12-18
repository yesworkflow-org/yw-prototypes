package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.annotations.Param;
import org.yesworkflow.exceptions.YWMarkupException;

public class TestParam extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testParam_VariableOnly() throws Exception {
        Param param = new Param(1L, 1L, 1L, "@param x");
        assertEquals("x", param.name);
        assertEquals("x", param.binding());
        assertNull(param.description);
    }
    
    public void testParamComment_LiteralNumberOnly() throws Exception {
        Param param = new Param(1L, 1L, 1L, "@param 30.7");
        assertEquals("30.7", param.name);
        assertEquals("30.7", param.binding());
        assertNull(param.description);
    }

    public void testParamComment_LiteralStringOnly() throws Exception {
        Param param = new Param(1L, 1L, 1L, "@param \"datafile.csv\"");
        assertEquals("\"datafile.csv\"", param.name);
        assertEquals("\"datafile.csv\"", param.binding());
        assertNull(param.description);
    }
    
    public void testParamComment_VariableAndDescription() throws Exception {
        Param param = new Param(1L, 1L, 1L, "@param x");
        new Desc(2L, 1L, 1L, "@desc The longitude", param);
        assertEquals("x", param.name);
        assertEquals("x", param.binding());
        assertEquals("The longitude", param.description());
    }
    
    public void testParamComment_VariableAndLabel() throws Exception {
        Param param = (Param) new Param(1L, 1L, 1L, "@param x ");
        new As(2L, 1L, 1L, "@as longitude", param);
        assertEquals("x", param.name);
        assertEquals("longitude", param.binding());
        assertNull(param.description);
    }

    public void testParamComment_VariableAndLabel_ExtraSpaces() throws Exception {
        Param param = (Param) new Param(1L, 1L, 1L, "@param x  ");
        new As(2L, 1L, 1L, "  @as  longitude", param);
        assertEquals("x", param.name);
        assertEquals("longitude", param.binding());
        assertNull(param.description);
    }

    public void testParamComment_VariableLabelDescription_DescriptionOnName() throws Exception {
        Param param  = (Param) new Param(1L, 1L, 1L, "@param x ");
        new As(2L, 1L, 1L, " @as  longitude", param);
        new Desc(3L, 1L, 1L, "@desc Half of the coordinates", param);
        assertEquals("x", param.name);
        assertEquals("longitude", param.binding());
        assertEquals("Half of the coordinates", param.description());
    }
    
    public void testParamComment_NoArgument() throws Exception {
        Exception caught = null;
        try {
            new Param(1L, 1L, 1L, "@param");
        } catch (YWMarkupException e) {
            caught = e;
        }
        assertNotNull(caught);
        assertEquals("No argument provided to @param keyword on line 1", caught.getMessage());
    }    
}
