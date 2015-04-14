package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;

public class TestOutWithUri extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testOutComment_NoUri() throws Exception {
        Out out = (Out) new Out("@out x ");
        assertEquals("x", out.name);
        assertNull(out.uri());
        assertNull(out.description);
    }

    public void testOutComment_WithUri() throws Exception {
        Out out = (Out) new Out("@out x ").qualifyWith(new Uri("@uri longitude.txt"));
        assertEquals("x", out.name);
        assertEquals("longitude.txt", out.uri().toString());
        assertNull(out.description);
    }

    public void testOutComment_AliasThenUri() throws Exception {
        Out out = (Out) new Out("@out x ").qualifyWith(new As("@as longitude"))
                                          .qualifyWith(new Uri("@uri longitude.txt"));
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("longitude.txt", out.uri().toString());
        assertNull(out.description);
    }

    public void testOutComment_UriThenAlias() throws Exception {
        Out out = (Out) new Out("@out x ").qualifyWith(new Uri("@uri longitude.txt"))
                                          .qualifyWith(new As("@as longitude"));
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("longitude.txt", out.uri().toString());
        assertNull(out.description);
    }    
}
