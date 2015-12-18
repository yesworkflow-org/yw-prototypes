package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.annotations.In;

public class TestDesc extends YesWorkflowTestCase {

    public void testBeginWithouDesc() throws Exception {
        Begin begin = new Begin(1L, 1L, 1L, "@begin main");
        assertEquals("main", begin.name);
        assertNull(begin.description);
    }    

    public void testBeginComment_NameAndOneWordDescription() throws Exception {
        Begin begin = new Begin(1L, 1L, 1L, "@begin main");
        new Desc(2L, 1L, 1L, "@desc Distance calculator", begin);
        assertEquals("main", begin.name);
        assertEquals("Distance calculator", begin.description());
    }
    
    public void testInWithoutDesc() throws Exception {
        In in = new In(1L, 1L, 1L, "@in x ");
        assertEquals("x", in.name);
        assertNull(in.description());
    }

    public void testInWithDesc() throws Exception {
        In in = new In(1L, 1L, 1L, "@in x ");
        new Desc(2L, 1L, 1L, "@desc Horizontal distance", in);
        assertEquals("x", in.name);
        assertEquals("Horizontal distance", in.description().toString());
    }

    public void testOutWithoutDesc() throws Exception {
        Out out = new Out(1L, 1L, 1L, "@out y ");
        assertEquals("y", out.name);
        assertNull(out.description());
    }

    public void testOutWithDesc() throws Exception {
        Out out = new Out(1L, 1L, 1L, "@out y ");
        new Desc(2L, 1L, 1L, "@desc Vertical distance", out);
        assertEquals("y", out.name);
        assertEquals("Vertical distance", out.description().toString());
    }
}
