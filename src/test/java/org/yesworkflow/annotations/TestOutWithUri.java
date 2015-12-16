package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.exceptions.YWMarkupException;

public class TestOutWithUri extends YesWorkflowTestCase {
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testOutComment_NoUri() throws Exception {
        Out out = (Out) new Out(1L, 1L, 1L, "@out x ");
        assertEquals("x", out.name);
        assertNull(out.uriAnnotation());
        assertNull(out.description);
    }

    public void testOutComment_WithUri() throws Exception {
        Out out = (Out) new Out(1L, 1L, 1L, "@out x ");
        new UriAnnotation(2L, 1L, 1L, "@uri longitude.txt", out);
        assertEquals("x", out.name);
        assertEquals("longitude.txt", out.uriAnnotation().toString());
        assertNull(out.description);
    }

    public void testOutComment_AliasThenUri() throws Exception {
        Out out = (Out) new Out(1L, 1L, 1L, "@out x ");
        new As(2L, 1L, 1L, "@as longitude", out);
        new UriAnnotation(3L, 1L, 1L, "@uri longitude.txt", out);
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("longitude.txt", out.uriAnnotation().toString());
        assertNull(out.description);
    }

    public void testOutComment_UriThenAlias() throws Exception {
        Out out = (Out) new Out(1L, 1L, 1L, "@out x ");
        new UriAnnotation(2L, 1L, 1L, "@uri longitude.txt", out);
        new As(3L, 1L, 1L, "@as longitude", out);
        assertEquals("x", out.name);
        assertEquals("longitude", out.binding());
        assertEquals("longitude.txt", out.uriAnnotation().toString());
        assertNull(out.description);
    }
    
    public void testOutCommentWithUri_NoArgumentToUri() throws Exception {
        Out out = (Out) new Out(1L, 1L, 1L, "@out x ");
        Exception caught = null;
        try {
            new UriAnnotation(2L, 1L, 1L, "@uri", out);
        } catch (YWMarkupException e) {
            caught = e;
        }
        assertNotNull(caught);
        assertEquals("No argument provided to @uri keyword on line 1", caught.getMessage());
    }

}
