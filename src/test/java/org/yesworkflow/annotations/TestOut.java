package org.yesworkflow.annotations;

import org.yesworkflow.annotations.Out;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestOut extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testOutComment_NameOnly() throws Exception {
        Out out = new Out("@out x");
        assertEquals("x", out.name);
        assertEquals("x", out.binding());
        assertNull(out.description);
    }
    
    public void testOutComment_NameAndDescription() throws Exception {
        Out out = new Out("@out x The longitude");
        assertEquals("x", out.name);
        assertEquals("x", out.binding());
        assertEquals("The longitude", out.description);
    }
    
    public void testOutComment_NameAndAlias() throws Exception {
        Out out = (Out) new Out("@out x").qualifyWith(new As("@as longitude"));
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertNull(out.description);
    }

    public void testOutComment_NameAndAlias_ExtraSpaces() throws Exception {
        Out out = (Out) new Out("@out x  ").qualifyWith(new As("  @as  longitude"));
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertNull(out.description);
    }

    public void testOutComment_NameAndAlias_DescriptionOnName() throws Exception {
        Out out = (Out) new Out("@out x  Half of the coordinates ").qualifyWith(new As("  @as  longitude "));
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("Half of the coordinates", out.description);
    }

    public void testOutComment_NameAndAlias_DescriptionOnAlias() throws Exception {
        Out out = (Out) new Out("@out x  ").qualifyWith(new As("  @as  longitude  Half of the coordinates"));
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("Half of the coordinates", out.description);
    }
    
    public void testOutComment_NameAndAlias_DescriptionOnNameAndAlias() throws Exception {
        Out out = (Out) new Out("@out x Half of  ").qualifyWith(new As("  @as  longitude  the coordinates"));
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("Half of the coordinates", out.description);
    }    
}
