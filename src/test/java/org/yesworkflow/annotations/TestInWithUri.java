package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.annotations.In;

public class TestInWithUri extends YesWorkflowTestCase {
        
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testInComment_NoUri() throws Exception {
        In in = (In) new In(1L, 1L, 1L, "@in x ");
        assertEquals("x", in.name);
        assertNull(in.uriAnnotation());
        assertNull(in.description);
    }

    public void testInComment_WithUri() throws Exception {
        In in = (In) new In(1L, 1L, 1L, "@in x ");
        new UriAnnotation(2L, 1L, 1L, "@uri longitude.txt", in);
        assertEquals("x", in.name);
        assertEquals("longitude.txt", in.uriAnnotation().toString());
        assertNull(in.description);
    }

    public void testInComment_AliasThenUri() throws Exception {
        In in = (In) new In(1L, 1L, 1L, "@in x ");
        new As(2L, 1L, 1L, "@as longitude", in);
        new UriAnnotation(3L, 1L, 1L, "@uri longitude.txt", in);
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertEquals("longitude.txt", in.uriAnnotation().toString());
        assertNull(in.description);
    }

    public void testInComment_UriThenAlias() throws Exception {
        In in = (In) new In(1L, 1L, 1L, "@in x ");
        new UriAnnotation(2L, 1L, 1L, "@uri longitude.txt", in);
        new As(3L, 1L, 1L, "@as longitude", in);
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertEquals("longitude.txt", in.uriAnnotation().toString());
        assertNull(in.description);
    }    
}
