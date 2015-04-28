package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.annotations.Out;
import org.yesworkflow.extract.SourceLine;

public class TestOut extends YesWorkflowTestCase {
    
    private static final SourceLine line = new SourceLine(1, 1, 1, "");
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testOutComment_NameOnly() throws Exception {
        Out out = new Out(line, "@out x");
        assertEquals("x", out.name);
        assertEquals("x", out.binding());
        assertNull(out.description);
    }
    
    public void testOutComment_NameAndDescription() throws Exception {
        Out out = new Out(line, "@out x The longitude");
        assertEquals("x", out.name);
        assertEquals("x", out.binding());
        assertEquals("The longitude", out.description);
    }
    
    public void testOutComment_NameAndAlias() throws Exception {
        Out out = (Out) new Out(line, "@out x").qualifyWith(new As(line, "@as longitude"));
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertNull(out.description);
    }

    public void testOutComment_NameAndAlias_ExtraSpaces() throws Exception {
        Out out = (Out) new Out(line, "@out x  ").qualifyWith(new As(line, "  @as  longitude"));
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertNull(out.description);
    }

    public void testOutComment_NameAndAlias_DescriptionOnName() throws Exception {
        Out out = (Out) new Out(line, "@out x  Half of the coordinates ").qualifyWith(new As(line, "  @as  longitude "));
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("Half of the coordinates", out.description);
    }

    public void testOutComment_NameAndAlias_DescriptionOnAlias() throws Exception {
        Out out = (Out) new Out(line, "@out x  ").qualifyWith(new As(line, "  @as  longitude  Half of the coordinates"));
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("Half of the coordinates", out.description);
    }
    
    public void testOutComment_NameAndAlias_DescriptionOnNameAndAlias() throws Exception {
        Out out = (Out) new Out(line, "@out x Half of  ").qualifyWith(new As(line, "  @as  longitude  the coordinates"));
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("Half of the coordinates", out.description);
    }    
}
