package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.annotations.In;
import org.yesworkflow.extract.SourceLine;

public class TestInWithUri extends YesWorkflowTestCase {
    
    private static final SourceLine line = new SourceLine(1, 1, 1, "");
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testInComment_NoUri() throws Exception {
        In in = (In) new In(1, line, "@in x ");
        assertEquals("x", in.name);
        assertNull(in.uriAnnotation());
        assertNull(in.description);
    }

    public void testInComment_WithUri() throws Exception {
        In in = (In) new In(1, line, "@in x ");
        new UriAnnotation(2, line, "@uri longitude.txt", in);
        assertEquals("x", in.name);
        assertEquals("longitude.txt", in.uriAnnotation().toString());
        assertNull(in.description);
    }

    public void testInComment_AliasThenUri() throws Exception {
        In in = (In) new In(1, line, "@in x ");
        new As(2, line, "@as longitude", in);
        new UriAnnotation(3, line, "@uri longitude.txt", in);
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertEquals("longitude.txt", in.uriAnnotation().toString());
        assertNull(in.description);
    }

    public void testInComment_UriThenAlias() throws Exception {
        In in = (In) new In(1, line, "@in x ");
        new UriAnnotation(2, line, "@uri longitude.txt", in);
        new As(3, line, "@as longitude", in);
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertEquals("longitude.txt", in.uriAnnotation().toString());
        assertNull(in.description);
    }    
}
