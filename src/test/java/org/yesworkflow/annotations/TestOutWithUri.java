package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.extract.Comment;

public class TestOutWithUri extends YesWorkflowTestCase {
    
    private static final Comment line = new Comment(1L, 1L, 1L, "");
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testOutComment_NoUri() throws Exception {
        Out out = (Out) new Out(1L, line, "@out x ");
        assertEquals("x", out.name);
        assertNull(out.uriAnnotation());
        assertNull(out.description);
    }

    public void testOutComment_WithUri() throws Exception {
        Out out = (Out) new Out(1L, line, "@out x ");
        new UriAnnotation(2L, line, "@uri longitude.txt", out);
        assertEquals("x", out.name);
        assertEquals("longitude.txt", out.uriAnnotation().toString());
        assertNull(out.description);
    }

    public void testOutComment_AliasThenUri() throws Exception {
        Out out = (Out) new Out(1L, line, "@out x ");
        new As(2L, line, "@as longitude", out);
        new UriAnnotation(3L, line, "@uri longitude.txt", out);
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("longitude.txt", out.uriAnnotation().toString());
        assertNull(out.description);
    }

    public void testOutComment_UriThenAlias() throws Exception {
        Out out = (Out) new Out(1L, line, "@out x ");
        new UriAnnotation(2L, line, "@uri longitude.txt", out);
        new As(3L, line, "@as longitude", out);
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("longitude.txt", out.uriAnnotation().toString());
        assertNull(out.description);
    }    
}
