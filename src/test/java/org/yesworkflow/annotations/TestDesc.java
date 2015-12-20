package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.annotations.In;

public class TestDesc extends YesWorkflowTestCase {

    public void testBeginWithouDesc() throws Exception {
        Begin begin = new Begin(1L, 1L, 1L, "@begin main");
        assertEquals("main", begin.value);
        assertNull(begin.description());
    }    

    public void testBeginComment_NameAndOneWordDescription() throws Exception {
        Begin begin = new Begin(1L, 1L, 1L, "@begin main");
        Desc desc = new Desc(2L, 1L, 1L, "@desc Distance calculator", begin);
        assertEquals("main", begin.value);
        assertEquals("Distance calculator", desc.value());
        assertEquals("Distance calculator", begin.description());
    }
    
    public void testInWithoutDesc() throws Exception {
        In in = new In(1L, 1L, 1L, "@in x ");
        assertEquals("x", in.value);
        assertNull(in.description());
    }

    public void testInWithDesc() throws Exception {
        In in = new In(1L, 1L, 1L, "@in x ");
        Desc desc = new Desc(2L, 1L, 1L, "@desc Horizontal distance", in);
        assertEquals("x", in.value);
        assertEquals("Horizontal distance", desc.value());
        assertEquals("Horizontal distance", in.description());
    }

    public void testOutWithoutDesc() throws Exception {
        Out out = new Out(1L, 1L, 1L, "@out y ");
        assertEquals("y", out.value);
        assertNull(out.description());
    }

    public void testOutWithDesc() throws Exception {
        Out out = new Out(1L, 1L, 1L, "@out y ");
        Desc desc = new Desc(2L, 1L, 1L, "@desc Vertical distance", out);
        assertEquals("y", out.value);
        assertEquals("Vertical distance", desc.value());
        assertEquals("Vertical distance", out.description());
    }
}
