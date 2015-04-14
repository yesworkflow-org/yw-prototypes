package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.annotations.In;

public class TestInWithUri extends YesWorkflowTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testInComment_NoUri() throws Exception {
        In in = (In) new In("@in x ");
        assertEquals("x", in.name);
        assertNull(in.uri());
        assertNull(in.description);
    }

    public void testInComment_WithUri() throws Exception {
        In in = (In) new In("@in x ").qualifyWith(new Uri("@uri longitude.txt"));
        assertEquals("x", in.name);
        assertEquals("longitude.txt", in.uri().toString());
        assertNull(in.description);
    }

    public void testInComment_AliasThenUri() throws Exception {
        In in = (In) new In("@in x ").qualifyWith(new As("@as longitude"))
                                     .qualifyWith(new Uri("@uri longitude.txt"));
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertEquals("longitude.txt", in.uri().toString());
        assertNull(in.description);
    }

    public void testInComment_UriThenAlias() throws Exception {
        In in = (In) new In("@in x ").qualifyWith(new Uri("@uri longitude.txt"))
                                     .qualifyWith(new As("@as longitude"));
        assertEquals("x", in.name);
        assertEquals("longitude", in.binding());
        assertEquals("longitude.txt", in.uri().toString());
        assertNull(in.description);
    }    
}
