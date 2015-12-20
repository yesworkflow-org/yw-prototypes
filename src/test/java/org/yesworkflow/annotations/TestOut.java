package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.annotations.Out;
import org.yesworkflow.exceptions.YWMarkupException;

public class TestOut extends YesWorkflowTestCase {
        
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testOutComment_NameOnly() throws Exception {
        Out out = new Out(1L, 1L, 1L, "@out x");
        assertEquals("x", out.name);
        assertEquals("x", out.binding());
        assertNull(out.description());
    }
    
    public void testOutComment_NameAndDescription() throws Exception {
        Out out = new Out(1L, 1L, 1L, "@out x");
        new Desc(2L, 1L, 1L, "@desc The longitude", out);
        assertEquals("x", out.name);
        assertEquals("x", out.binding());
        assertEquals("The longitude", out.description());
    }
    
    public void testOutComment_NameAndAlias() throws Exception {
        Out out = (Out) new Out(1L, 1L, 1L, "@out x");
        new As(2L, 1L, 1L, "@as longitude", out);
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertNull(out.description());
    }

    public void testOutComment_NameAndAlias_ExtraSpaces() throws Exception {
        Out out = (Out) new Out(1L, 1L, 1L, "@out x  ");
        new As(2L, 1L, 1L, "  @as  longitude", out);
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertNull(out.description());
    }

    public void testOutComment_NameAndAlias_DescriptionOnName() throws Exception {
        Out out = (Out) new Out(1L, 1L, 1L, "@out x  ");
        new As(2L, 1L, 1L, "  @as  longitude ", out);
        new Desc(3L, 1L, 1L, "@desc Half of the coordinates", out);
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("Half of the coordinates", out.description());
    }

    public void testOutComment_NameAndAlias_DescriptionOnAlias() throws Exception {
        Out out = (Out) new Out(1L, 1L, 1L, "@out x  ");
        new As(2L, 1L, 1L, "  @as  longitude  ", out);
        new Desc(3L, 1L, 1L, "@desc Half of the coordinates", out);
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("Half of the coordinates", out.description());
    }

    public void testOutComment_NoArgument() throws Exception {
        Exception caught = null;
        try {
            new Out(1L, 1L, 1L, "@out");
        } catch (YWMarkupException e) {
            caught = e;
        }
        assertNotNull(caught);
        assertEquals("No argument provided to @out keyword on line 1", caught.getMessage());
    }
}
