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
        In in = (In) new In(line, "@in x ");
        assertEquals("x", in.name);
        assertNull(in.uri());
        assertNull(in.description);
    }

    public void testInComment_WithUri() throws Exception {
        In in = (In) new In(line, "@in x ").qualifyWith(new Uri(line, "@uri longitude.txt"));
        assertEquals("x", in.name);
        assertEquals("longitude.txt", in.uri().toString());
        assertNull(in.description);
    }

    public void testInComment_AliasThenUri() throws Exception {
        In in = (In) new In(line, "@in x ").qualifyWith(new As(line, "@as longitude"))
                                     .qualifyWith(new Uri(line, "@uri longitude.txt"));
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertEquals("longitude.txt", in.uri().toString());
        assertNull(in.description);
    }

    public void testInComment_UriThenAlias() throws Exception {
        In in = (In) new In(line, "@in x ").qualifyWith(new Uri(line, "@uri longitude.txt"))
                                     .qualifyWith(new As(line, "@as longitude"));
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertEquals("longitude.txt", in.uri().toString());
        assertNull(in.description);
    }    
}
