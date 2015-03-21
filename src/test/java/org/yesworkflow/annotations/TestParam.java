package org.yesworkflow.annotations;

import org.yesworkflow.annotations.Param;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestParam extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testParam_VariableOnly() throws Exception {
        Param param = new Param("@param x");
        assertEquals("x", param.name);
        assertEquals("x", param.binding());
        assertNull(param.description);
    }
    
    public void testParamComment_LiteralNumberOnly() throws Exception {
        Param param = new Param("@param 30.7");
        assertEquals("30.7", param.name);
        assertEquals("30.7", param.binding());
        assertNull(param.description);
    }

    public void testParamComment_LiteralStringOnly() throws Exception {
        Param param = new Param("@param \"datafile.csv\"");
        assertEquals("\"datafile.csv\"", param.name);
        assertEquals("\"datafile.csv\"", param.binding());
        assertNull(param.description);
    }
    
    public void testParamComment_VariableAndDescription() throws Exception {
        Param param = new Param("@param x The longitude");
        assertEquals("x", param.name);
        assertEquals("x", param.binding());
        assertEquals("The longitude", param.description);
    }
    
    public void testParamComment_VariableAndLabel() throws Exception {
        Param param = (Param) new Param("@param x ").qualifyWith(new As("@as longitude"));
        assertEquals("x", param.name);
        assertEquals("longitude", param.binding());
        assertNull(param.description);
    }

    public void testParamComment_VariableAndLabel_ExtraSpaces() throws Exception {
        Param param = (Param) new Param("@param x  ").qualifyWith(new As("  @as  longitude"));
        assertEquals("x", param.name);
        assertEquals("longitude", param.binding());
        assertNull(param.description);
    }

    public void testParamComment_VariableLabelDescription_DescriptionOnName() throws Exception {
        Param param = (Param) new Param("@param x    Half of the coordinates ")
        	.qualifyWith(new As(" @as  longitude"));
        assertEquals("x", param.name);
        assertEquals("longitude", param.binding());
        assertEquals("Half of the coordinates", param.description);
    }

    public void testParamComment_VariableLabelDescription_DescriptionOnAlias() throws Exception {
        Param param = (Param) new Param("@param x   ")
        	.qualifyWith(new As(" @as  longitude  Half of the coordinates"));
        assertEquals("x", param.name);
        assertEquals("longitude", param.binding());
        assertEquals("Half of the coordinates", param.description);
    }

    public void testParamComment_VariableLabelDescription_DescriptionOnNameAndAlias() throws Exception {
        Param param = (Param) new Param("@param x Half of   ")
        	.qualifyWith(new As(" @as  longitude   the coordinates"));
        assertEquals("x", param.name);
        assertEquals("longitude", param.binding());
        assertEquals("Half of the coordinates", param.description);
    }
    
}
