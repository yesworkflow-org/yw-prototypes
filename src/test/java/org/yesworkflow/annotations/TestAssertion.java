package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;

public class TestAssertion extends YesWorkflowTestCase {
    
    public void testAssertion_SingleDependency() throws Exception {
        Assertion assertion = new Assertion(1L, 1L, 1L, "@assert y depends-on x");
        assertEquals("y", assertion.subject);
        assertEquals("depends-on", assertion.predicate);
        assertEquals("x", assertion.object);
    }
}
