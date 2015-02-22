package org.yesworkflow.annotations;

import org.yesworkflow.annotations.Out;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestOut extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testOutComment_VariableOnly() throws Exception {
        Out annotation = new Out("@out x");
        assertEquals("x", annotation.name);
        assertEquals("x", annotation.binding());
        assertNull(annotation.description);
    }
    
    public void testOutComment_VariableAndDescription() throws Exception {
        Out annotation = new Out("@out x The longitude");
        assertEquals("x", annotation.name);
        assertEquals("x", annotation.binding());
        assertEquals("The longitude", annotation.description);
    }
    
    public void testOutComment_VariableAndLabel() throws Exception {
        Out annotation = (Out) new Out("@out x").qualifyWith(new As(" @as longitude"));
        assertEquals("x", annotation.name);
        assertEquals("longitude", annotation.binding());
        assertNull(annotation.description);
    }

    public void testOutComment_VariableAndLabel_ExtraSpaces() throws Exception {
        Out comment = (Out) new Out("@out x  ").qualifyWith(new As("  @as  longitude"));
        assertEquals("x", comment.name);
        assertEquals("longitude", comment.binding());
        assertNull(comment.description);
    }

    public void testOutComment_VariableLabelDescription_ExtraSpaces() throws Exception {
        Out comment = (Out) new Out("@out x  ").qualifyWith(new As("  @as  longitude  Half of the coordinates"));
        assertEquals("x", comment.name);
        assertEquals("longitude", comment.binding());
        assertEquals("Half of the coordinates", comment.description);
    }
}
