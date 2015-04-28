package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.extract.SourceLine;

public class TestOutWithUri extends YesWorkflowTestCase {
    
    private static final SourceLine line = new SourceLine(1, 1, 1, "");
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testOutComment_NoUri() throws Exception {
        Out out = (Out) new Out(line, "@out x ");
        assertEquals("x", out.name);
        assertNull(out.uri());
        assertNull(out.description);
    }

    public void testOutComment_WithUri() throws Exception {
        Out out = (Out) new Out(line, "@out x ").qualifyWith(new Uri(line, "@uri longitude.txt"));
        assertEquals("x", out.name);
        assertEquals("longitude.txt", out.uri().toString());
        assertNull(out.description);
    }

    public void testOutComment_AliasThenUri() throws Exception {
        Out out = (Out) new Out(line, "@out x ").qualifyWith(new As(line, "@as longitude"))
                                          .qualifyWith(new Uri(line, "@uri longitude.txt"));
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("longitude.txt", out.uri().toString());
        assertNull(out.description);
    }

    public void testOutComment_UriThenAlias() throws Exception {
        Out out = (Out) new Out(line, "@out x ").qualifyWith(new Uri(line, "@uri longitude.txt"))
                                          .qualifyWith(new As(line, "@as longitude"));
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("longitude.txt", out.uri().toString());
        assertNull(out.description);
    }    
}
