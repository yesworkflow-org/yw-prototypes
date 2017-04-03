package org.yesworkflow.annotations;

import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.exceptions.YWMarkupException;

public class TestAssertion extends YesWorkflowTestCase {
    
    public void testAssertion_OneDependency() throws Exception {
        Assertion assertion = new Assertion(1L, 1L, 1L, "@assert y depends-on a");
        assertEquals("y", assertion.subject);
        assertEquals("depends-on", assertion.predicateText);
        assertEquals(1, assertion.objects.length); 
        assertEquals("a", assertion.objects[0]);
    }
    
    public void testAssertion_TwoDependencies() throws Exception {
        Assertion assertion = new Assertion(1L, 1L, 1L, "@assert y depends-on a b");
        assertEquals("y", assertion.subject);
        assertEquals("depends-on", assertion.predicateText);
        assertEquals(2, assertion.objects.length); 
        assertEquals("a", assertion.objects[0]);
        assertEquals("b", assertion.objects[1]);
    }
       
    public void testAssertion_ThreeDependency() throws Exception {
        Assertion assertion = new Assertion(1L, 1L, 1L, "@assert y depends-on a b c");
        assertEquals("y", assertion.subject);
        assertEquals("depends-on", assertion.predicateText);
        assertEquals(3, assertion.objects.length); 
        assertEquals("a", assertion.objects[0]);
        assertEquals("b", assertion.objects[1]);
        assertEquals("c", assertion.objects[2]);
    }
    
    public void testAssertion_NoArguments_ThrowsException() throws Exception {
        Exception caught = null;
        try {
            new Assertion(1L, 1L, 1L, "@assert");
        } catch(YWMarkupException ex) {
            caught = ex;
        }
        assertNotNull(caught);
        assertEquals("No argument provided to @assert keyword on line 1", caught.getMessage());
    }

    public void testAssertion_NoPredicate_ThrowsException() throws Exception {
        Exception caught = null;
        try {
            new Assertion(1L, 1L, 1L, "@assert y");
        } catch(YWMarkupException ex) {
            caught = ex;
        }
        assertNotNull(caught);
        assertEquals("No predicate provided to @assert keyword on line 1", caught.getMessage());
    }
    
    public void testAssertion_NoObject_ThrowsException() throws Exception {
        Exception caught = null;
        try {
            new Assertion(1L, 1L, 1L, "@assert y depends-on");
        } catch(YWMarkupException ex) {
            caught = ex;
        }
        assertNotNull(caught);
        assertEquals("No object provided to @assert keyword on line 1", caught.getMessage());
    }
    
    public void testAssertion_UnrecognizedPredicate_ThrowsException() throws Exception {
        Exception caught = null;
        try {
            new Assertion(1L, 1L, 1L, "@assert y oddly-similar-to a");
        } catch(YWMarkupException ex) {
            caught = ex;
        }
        assertNotNull(caught);
        assertEquals("Unrecognized predicate 'oddly-similar-to' given to @assert keyword on line 1", caught.getMessage());
    }
}
