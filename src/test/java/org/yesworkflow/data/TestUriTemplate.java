package org.yesworkflow.data;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.yesworkflow.YesWorkflowTestCase;

public class TestUriTemplate extends YesWorkflowTestCase {

	private UriTemplate t1;
	private UriTemplate t2;
	
	public void testRemoveTerminalSlash_HasNoSchemeNoSlash() {

		assertEquals("/", UriTemplate.trimTerminalSlash("/"));
		assertEquals("/a", UriTemplate.trimTerminalSlash("/a"));
		assertEquals("/ab", UriTemplate.trimTerminalSlash("/ab"));
		assertEquals("/foo", UriTemplate.trimTerminalSlash("/foo"));
		assertEquals("/foo/bar", UriTemplate.trimTerminalSlash("/foo/bar"));
	}

	
	public void testRemoveTerminalSlash_HasSlashNoScheme() {

		assertEquals("/a", UriTemplate.trimTerminalSlash("/a/"));
		assertEquals("/ab", UriTemplate.trimTerminalSlash("/ab/"));
		assertEquals("/foo", UriTemplate.trimTerminalSlash("/foo/"));
		assertEquals("/foo/bar", UriTemplate.trimTerminalSlash("/foo/bar/"));
		
	}

	public void testRemoveTerminalSlash_HasSchemeNoSlash() {

		assertEquals("foo:/", UriTemplate.trimTerminalSlash("foo:/"));
		assertEquals("foo:/a", UriTemplate.trimTerminalSlash("foo:/a"));
		assertEquals("foo:/ab", UriTemplate.trimTerminalSlash("foo:/ab"));
		assertEquals("file:/foo", UriTemplate.trimTerminalSlash("file:/foo"));
		assertEquals("data:/foo/bar", UriTemplate.trimTerminalSlash("data:/foo/bar"));
	}

	public void testRemoveTerminalSlash_HasSlashAndScheme() {

		assertEquals("foo:/a", UriTemplate.trimTerminalSlash("foo:/a/"));
		assertEquals("foo:/ab", UriTemplate.trimTerminalSlash("foo:/ab/"));
		assertEquals("file:/foo", UriTemplate.trimTerminalSlash("file:/foo/"));
		assertEquals("data:/foo/bar", UriTemplate.trimTerminalSlash("data:/foo/bar/"));
	}

	public void testExtractSchemeAndPath_TemplateHasScheme() {
		
		UriTemplate.SchemePathPair spp;
		
		spp = UriTemplate.extractSchemeAndPath("file:/");
		assertEquals("file", spp.scheme);
		assertEquals("/", spp.path);
		
		spp = UriTemplate.extractSchemeAndPath("file:/foo");
		assertEquals("file", spp.scheme);
		assertEquals("/foo", spp.path);

		spp = UriTemplate.extractSchemeAndPath("data:/foo/bar");
		assertEquals("data", spp.scheme);
		assertEquals("/foo/bar", spp.path);
		
		spp = UriTemplate.extractSchemeAndPath("foo:/1/{2}/bar");
		assertEquals("foo", spp.scheme);
		assertEquals("/1/{2}/bar", spp.path);
	}
	
	public void testExtractSchemeAndPath_TemplateHasSchemeAndDoubleLeadingSlashes() {
		
		UriTemplate.SchemePathPair spp;
		
		spp = UriTemplate.extractSchemeAndPath("file://");
		assertEquals("file", spp.scheme);
		assertEquals("/", spp.path);
		
		spp = UriTemplate.extractSchemeAndPath("file://foo");
		assertEquals("file", spp.scheme);
		assertEquals("/foo", spp.path);

		spp = UriTemplate.extractSchemeAndPath("data://foo/bar");
		assertEquals("data", spp.scheme);
		assertEquals("/foo/bar", spp.path);
		
		spp = UriTemplate.extractSchemeAndPath("foo://1/{2}/bar");
		assertEquals("foo", spp.scheme);
		assertEquals("/1/{2}/bar", spp.path);
	}
	
	public void testExtractSchemeAndPath_TemplateHasSchemeAndDoubleLeadingAndIntermediateSlashes() {
	
	UriTemplate.SchemePathPair spp;
	
	spp = UriTemplate.extractSchemeAndPath("data://foo//bar");
	assertEquals("data", spp.scheme);
	assertEquals("/foo/bar", spp.path);
	
	spp = UriTemplate.extractSchemeAndPath("foo://1//{2}//bar");
	assertEquals("foo", spp.scheme);
	assertEquals("/1/{2}/bar", spp.path);
}

	public void testExtractSchemeAndPath_TemplateHasNoScheme() {
		
		UriTemplate.SchemePathPair spp;
		
		spp = UriTemplate.extractSchemeAndPath("/");
		assertSame(UriTemplate.NO_SCHEME, spp.scheme);
		assertEquals("/", spp.path);
		
		spp = UriTemplate.extractSchemeAndPath("/foo");
		assertSame(UriTemplate.NO_SCHEME, spp.scheme);
		assertEquals("/foo", spp.path);

		spp = UriTemplate.extractSchemeAndPath("/foo/bar");
		assertSame(UriTemplate.NO_SCHEME, spp.scheme);
		assertEquals("/foo/bar", spp.path);
		
		spp = UriTemplate.extractSchemeAndPath("/1/{2}/bar");
		assertSame(UriTemplate.NO_SCHEME,spp.scheme);
		assertEquals("/1/{2}/bar", spp.path);
	}
	
	public void testExtractSchemeAndPath_TemplateHasNoSchemeButLeadingAndIntermediateDoubleSlashes() {
		
		UriTemplate.SchemePathPair spp;

		spp = UriTemplate.extractSchemeAndPath("//foo//bar");
		assertSame(UriTemplate.NO_SCHEME, spp.scheme);
		assertEquals("/foo/bar", spp.path);
		
		spp = UriTemplate.extractSchemeAndPath("//1//{2}//bar");
		assertSame(UriTemplate.NO_SCHEME,spp.scheme);
		assertEquals("/1/{2}/bar", spp.path);
	}
	
	public void testExtractSchemeAndPath_TemplateHasNoSchemeButDoubleLeadingSlashes() {
		
		UriTemplate.SchemePathPair spp;
		
		spp = UriTemplate.extractSchemeAndPath("//");
		assertSame(UriTemplate.NO_SCHEME, spp.scheme);
		assertEquals("/", spp.path);
		
		spp = UriTemplate.extractSchemeAndPath("//foo");
		assertSame(UriTemplate.NO_SCHEME, spp.scheme);
		assertEquals("/foo", spp.path);

		spp = UriTemplate.extractSchemeAndPath("//foo/bar");
		assertSame(UriTemplate.NO_SCHEME, spp.scheme);
		assertEquals("/foo/bar", spp.path);
		
		spp = UriTemplate.extractSchemeAndPath("//1/{2}/bar");
		assertSame(UriTemplate.NO_SCHEME,spp.scheme);
		assertEquals("/1/{2}/bar", spp.path);
	}


	public void testTemplatePathAndExtractVariables_PathHasNoVariables() {
		
		List<String> variables = new LinkedList<String>();
		List<String> fragments;

		fragments = new LinkedList<String>();
		assertEquals("/", UriTemplate.reduceTemplateAndExtractVariables("/", variables, fragments));
		assertEquals(0, variables.size());
		assertEquals(1, fragments.size());
		assertEquals("/", fragments.get(0));
		
		fragments = new LinkedList<String>();
		assertEquals("/foo", UriTemplate.reduceTemplateAndExtractVariables("/foo", variables, fragments));
		assertEquals(0, variables.size());
		assertEquals(1, fragments.size());
		assertEquals("/foo", fragments.get(0));

		fragments = new LinkedList<String>();
		assertEquals("/foo/bar", UriTemplate.reduceTemplateAndExtractVariables("/foo/bar", variables, fragments));
		assertEquals(0, variables.size());
		assertEquals(1, fragments.size());
		assertEquals("/foo/bar", fragments.get(0));
	}
	
	public void testReduceTemplateAndExtractVariables_PathHasUniqueVariables() {
		
		List<String> variables;
		List<String> fragments;

		variables = new LinkedList<String>();
		fragments = new LinkedList<String>();
		assertEquals("/1/2/{}/bar", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{v1}/bar", variables, fragments));
		assertEquals(1, variables.size());
		assertEquals("v1", variables.get(0));
		assertEquals(2, fragments.size());
		assertEquals("/1/2/", fragments.get(0));
		assertEquals("/bar", fragments.get(1));

		variables = new LinkedList<String>();
		fragments = new LinkedList<String>();
		assertEquals("/1/2/{}/bar", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{v1}/bar", variables, fragments));
		assertEquals(1, variables.size());
		assertEquals("v1", variables.get(0));
		assertEquals(2, fragments.size());
		assertEquals("/1/2/", fragments.get(0));
		assertEquals("/bar", fragments.get(1));

		variables = new LinkedList<String>();
		fragments = new LinkedList<String>();
		assertEquals("/1/2/{}/bar/{}", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{v1}/bar/{v2}", variables, fragments));
		assertEquals(2, variables.size());
		assertEquals("v1", variables.get(0));
		assertEquals("v2", variables.get(1));
		assertEquals(3, fragments.size());
		assertEquals("/1/2/", fragments.get(0));
		assertEquals("/bar/", fragments.get(1));
		assertEquals("", fragments.get(2));
		
		variables = new LinkedList<String>();
		fragments = new LinkedList<String>();
		assertEquals("/1/2/{}bar{}", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{v1}bar{v2}", variables, fragments));
		assertEquals(2, variables.size());
		assertEquals("v1", variables.get(0));
		assertEquals("v2", variables.get(1));
		assertEquals(3, fragments.size());
		assertEquals("/1/2/", fragments.get(0));
		assertEquals("bar", fragments.get(1));
		assertEquals("", fragments.get(2));

		variables = new LinkedList<String>();
		fragments = new LinkedList<String>();
		assertEquals("/1/2/{}{}", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{v1}{v2}", variables, fragments));
		assertEquals(2, variables.size());
		assertEquals("v1", variables.get(0));
		assertEquals("v2", variables.get(1));
		assertEquals(3, fragments.size());
		assertEquals("/1/2/", fragments.get(0));
		assertEquals("", fragments.get(1));
		assertEquals("", fragments.get(2));

		variables = new LinkedList<String>();
		fragments = new LinkedList<String>();
		assertEquals("/1/2/{}{}/3/{}", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{v1}{v2}/3/{v3}", variables, fragments));
		assertEquals(3, variables.size());
		assertEquals("v1", variables.get(0));
		assertEquals("v2", variables.get(1));
		assertEquals("v3", variables.get(2));
		assertEquals(4, fragments.size());
		assertEquals("/1/2/", fragments.get(0));
		assertEquals("", fragments.get(1));
		assertEquals("/3/", fragments.get(2));
		assertEquals("", fragments.get(3));
	}
	
public void testReduceTemplateAndExtractVariables_PathHasRepeatVariables() {
		
		List<String> variables;
		List<String> fragments;

		variables = new LinkedList<String>();
		fragments = new LinkedList<String>();
		assertEquals("/1/2/{}/bar/{}", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{v1}/bar/{v1}", variables, fragments));
		assertEquals(2, variables.size());
		assertEquals("v1", variables.get(0));
		assertEquals("v1", variables.get(1));
		assertEquals(3, fragments.size());
		assertEquals("/1/2/", fragments.get(0));
		assertEquals("/bar/", fragments.get(1));
		assertEquals("", fragments.get(2));
		
		variables = new LinkedList<String>();
		fragments = new LinkedList<String>();
		assertEquals("/1/2/{}bar{}", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{v2}bar{v2}", variables, fragments));
		assertEquals(2, variables.size());
		assertEquals("v2", variables.get(0));
		assertEquals("v2", variables.get(1));
		assertEquals(3, fragments.size());
		assertEquals("/1/2/", fragments.get(0));
		assertEquals("bar", fragments.get(1));
		assertEquals("", fragments.get(2));

		variables = new LinkedList<String>();
		fragments = new LinkedList<String>();
		assertEquals("/1/2/{}{}", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{v3}{v3}", variables, fragments));
		assertEquals(2, variables.size());
		assertEquals("v3", variables.get(0));
		assertEquals("v3", variables.get(1));
		assertEquals("/1/2/", fragments.get(0));
		assertEquals("", fragments.get(1));
		assertEquals("", fragments.get(2));

		variables = new LinkedList<String>();
		fragments = new LinkedList<String>();
		assertEquals("/1/2/{}{}/3/{}", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{v1}{v2}/3/{v1}", variables, fragments));
		assertEquals(3, variables.size());
		assertEquals("v1", variables.get(0));
		assertEquals("v2", variables.get(1));
		assertEquals("v1", variables.get(2));
		assertEquals(4, fragments.size());
		assertEquals("/1/2/", fragments.get(0));
		assertEquals("", fragments.get(1));
		assertEquals("/3/", fragments.get(2));
		assertEquals("", fragments.get(3));
	}

public void testReduceTemplateAndExtractVariables_PathHasEmptyVariableBraces() {
	
	List<String> variables;
	List<String> fragments;

	variables = new LinkedList<String>();
	fragments = new LinkedList<String>();
	assertEquals("/1/2/{}/bar", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{}/bar", variables, fragments));
	assertEquals(1, variables.size());
	assertEquals("", variables.get(0));
	assertEquals(2, fragments.size());
	assertEquals("/1/2/", fragments.get(0));
	assertEquals("/bar", fragments.get(1));

	variables = new LinkedList<String>();
	fragments = new LinkedList<String>();
	assertEquals("/1/2/{}/bar/{}", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{}/bar/{v2}", variables, fragments));
	assertEquals(2, variables.size());
	assertEquals("", variables.get(0));
	assertEquals("v2", variables.get(1));
	assertEquals(3, fragments.size());
	assertEquals("/1/2/", fragments.get(0));
	assertEquals("/bar/", fragments.get(1));
	assertEquals("", fragments.get(2));
	
	variables = new LinkedList<String>();
	fragments = new LinkedList<String>();
	assertEquals("/1/2/{}bar{}", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{v1}bar{}", variables, fragments));
	assertEquals(2, variables.size());
	assertEquals("v1", variables.get(0));
	assertEquals("", variables.get(1));
	assertEquals(3, fragments.size());
	assertEquals("/1/2/", fragments.get(0));
	assertEquals("bar", fragments.get(1));
	assertEquals("", fragments.get(2));

	variables = new LinkedList<String>();
	fragments = new LinkedList<String>();
	assertEquals("/1/2/{}{}", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{v1}{}", variables, fragments));
	assertEquals(2, variables.size());
	assertEquals("v1", variables.get(0));
	assertEquals("", variables.get(1));
	assertEquals(3, fragments.size());
	assertEquals("/1/2/", fragments.get(0));
	assertEquals("", fragments.get(1));
	assertEquals("", fragments.get(2));

	variables = new LinkedList<String>();
	fragments = new LinkedList<String>();
	assertEquals("/1/2/{}{}/3/{}", UriTemplate.reduceTemplateAndExtractVariables("/1/2/{v1}{}/3/{}", variables, fragments));
	assertEquals(3, variables.size());
	assertEquals("v1", variables.get(0));
	assertEquals("", variables.get(1));
	assertEquals("", variables.get(2));
	assertEquals(4, fragments.size());
	assertEquals("/1/2/", fragments.get(0));
	assertEquals("", fragments.get(1));
	assertEquals("/3/", fragments.get(2));
	assertEquals("", fragments.get(3));
}

	public void testGetScheme_TemplateHasScheme() {
		
		t1 = new UriTemplate("file:/foo");
		assertEquals("file", t1.getScheme());

		t1 = new UriTemplate("data:/foo/bar");
		assertEquals("data", t1.getScheme());

		t1 = new UriTemplate("foo:/1/{2}/bar");
		assertEquals("foo", t1.getScheme());
	}

	public void testGetScheme_TemplateHasNoScheme() {
		
		t1 = new UriTemplate("/bar");
		assertSame(UriTemplate.NO_SCHEME, t1.getScheme());
		assertEquals("", t1.getScheme());

		t1 = new UriTemplate("/");
		assertSame(UriTemplate.NO_SCHEME, t1.getScheme());
		assertEquals("", t1.getScheme());
	}

	public void testGetPath_TemplateHasScheme() {
		
		t1 = new UriTemplate("file:/foo");
		assertEquals("/foo", t1.getPath());

		t1 = new UriTemplate("data:/foo/bar");
		assertEquals("/foo/bar", t1.getPath());

		t1 = new UriTemplate("foo:/1/{2}/bar");
		assertEquals("/1/{2}/bar", t1.getPath());
	}

	public void testGetPath_TemplateHasNoScheme() {
		
		t1 = new UriTemplate("/foo");
		assertEquals("/foo", t1.getPath());

		t1 = new UriTemplate("/foo/bar");
		assertEquals("/foo/bar", t1.getPath());

		t1 = new UriTemplate("/1/{2}/bar");
		assertEquals("/1/{2}/bar", t1.getPath());
	}
	
	public void testGetPath_TemplateHasTerminalSlash() {
		
		t1 = new UriTemplate("file:/foo/");
		assertEquals("/foo", t1.getPath());

		t1 = new UriTemplate("data:/foo/bar/");
		assertEquals("/foo/bar", t1.getPath());

		t1 = new UriTemplate("foo:/1/{2}/bar/");
		assertEquals("/1/{2}/bar", t1.getPath());
		
		t1 = new UriTemplate("/foo/");
		assertEquals("/foo", t1.getPath());

		t1 = new UriTemplate("/foo/bar/");
		assertEquals("/foo/bar", t1.getPath());

		t1 = new UriTemplate("/1/{2}/bar/");
		assertEquals("/1/{2}/bar", t1.getPath());
	}	
	
	public void testGetFullTemplate_TemplateHasNoEndSlash() {
		
		t1 = new UriTemplate("foo:/bar");
		assertEquals("foo:/bar", t1.getExpression());		

		t1 = new UriTemplate("foo:/1/bar");
		assertEquals("foo:/1/bar", t1.getExpression());		
		
		t1 = new UriTemplate("foo:/1/2/bar");
		assertEquals("foo:/1/2/bar", t1.getExpression());
		
		t1 = new UriTemplate("foo:/1/2/{v1}/bar");
		assertEquals("foo:/1/2/{v1}/bar", t1.getExpression());

		t1 = new UriTemplate("foo:/1/2/{v1}bar");
		assertEquals("foo:/1/2/{v1}bar", t1.getExpression());
	}

	public void testGetFullTemplate_TemplateHasEndSlash() {
		
		t1 = new UriTemplate("/");
		assertEquals("/", t1.getExpression());		

		t1 = new UriTemplate("foo:/");
		assertEquals("foo:/", t1.getExpression());		

		t1 = new UriTemplate("foo:/bar/");
		assertEquals("foo:/bar", t1.getExpression());		

		t1 = new UriTemplate("foo:/1/bar/");
		assertEquals("foo:/1/bar", t1.getExpression());		
		
		t1 = new UriTemplate("foo:/1/2/bar/");
		assertEquals("foo:/1/2/bar", t1.getExpression());
		
		t1 = new UriTemplate("foo:/1/2/{v1}/bar/");
		assertEquals("foo:/1/2/{v1}/bar", t1.getExpression());

		t1 = new UriTemplate("foo:/1/2/{v1}bar/");
		assertEquals("foo:/1/2/{v1}bar", t1.getExpression());
	}

	public void testGetReducedTemplate_TemplateHasNoEndSlash() {
		
		t1 = new UriTemplate("foo:/bar");
		assertEquals("/bar", t1.getReducedPath());		

		t1 = new UriTemplate("foo:/1/bar");
		assertEquals("/1/bar", t1.getReducedPath());		
		
		t1 = new UriTemplate("foo:/1/2/bar");
		assertEquals("/1/2/bar", t1.getReducedPath());
		
		t1 = new UriTemplate("foo:/1/2/{v1}/bar");
		assertEquals("/1/2/{}/bar", t1.getReducedPath());

		t1 = new UriTemplate("foo:/1/2/{v1}bar");
		assertEquals("/1/2/{}bar", t1.getReducedPath());
	}	

	public void testGetReducedTemplate_TemplateHasEndSlash() {
		
		t1 = new UriTemplate("/");
		assertEquals("/", t1.getReducedPath());		

		t1 = new UriTemplate("foo:/");
		assertEquals("/", t1.getReducedPath());		

		t1 = new UriTemplate("foo:/bar/");
		assertEquals("/bar", t1.getReducedPath());		

		t1 = new UriTemplate("foo:/1/bar/");
		assertEquals("/1/bar", t1.getReducedPath());		
		
		t1 = new UriTemplate("foo:/1/2/bar/");
		assertEquals("/1/2/bar", t1.getReducedPath());
		
		t1 = new UriTemplate("foo:/1/2/{v1}/bar/");
		assertEquals("/1/2/{}/bar", t1.getReducedPath());

		t1 = new UriTemplate("foo:/1/2/{v1}bar/");
		assertEquals("/1/2/{}bar", t1.getReducedPath());
	}

	public void testGetVariableCount_PathIsJustSlash() {
		
		t1 = new UriTemplate("/");
		assertEquals(0, t1.variables.length);		

		t1 = new UriTemplate("foo:/");
		assertEquals(0, t1.variables.length);		
	}
	
	
	public void testGetVariableCount_TemplateHasNoVariables() {
		
		t1 = new UriTemplate("foo:/bar/");
		assertEquals(0, t1.variables.length);
	
		t1 = new UriTemplate("/foo/bar/");
		assertEquals(0, t1.variables.length);

		t1 = new UriTemplate("data:/foo/1/bar/");
		assertEquals(0, t1.variables.length);

		t1 = new UriTemplate("/foo/1/bar/");
		assertEquals(0, t1.variables.length);
	}
	
	public void testGetVariableCount_TemplateHasUniqueVariables() {

		t1 = new UriTemplate("foo:/1/2/{v1}/bar");
		assertEquals(1, t1.variables.length);

		t1 = new UriTemplate("foo:/1/2/{v1}bar");
		assertEquals(1, t1.variables.length);

		t1 = new UriTemplate("foo:/1/2/{v1}/bar/{v2}");
		assertEquals(2, t1.variables.length);

		t1 = new UriTemplate("foo:/1/2/{v1}bar{v2}");
		assertEquals(2, t1.variables.length);

		t1 = new UriTemplate("foo:/1/2/{v1}{v2}");
		assertEquals(2, t1.variables.length);

		t1 = new UriTemplate("foo:/1/2/{v1}{v2}/3");
		assertEquals(2, t1.variables.length);
	}
	
	public void testGetVariableCount_TemplateHasRepeatVariables() {

		t1 = new UriTemplate("foo:/1/2/{v1}/bar/{v1}");
		assertEquals(1, t1.variables.length);

		t1 = new UriTemplate("foo:/1/2/{v1}bar{v1}");
		assertEquals(1, t1.variables.length);

		t1 = new UriTemplate("foo:/1/2/{v1}{v1}");
		assertEquals(1, t1.variables.length);

		t1 = new UriTemplate("foo:/1/2/{v1}{v1}/3");
		assertEquals(1, t1.variables.length);
	}

	public void testGetVariableNames_TemplateHasNoVariables() {

		t1 = new UriTemplate("/");
		assertEquals(0, t1.variables.length);		

		t1 = new UriTemplate("foo:/");
		assertEquals(0, t1.variables.length);
	
		t1 = new UriTemplate("foo:/bar/");
		assertEquals(0, t1.variables.length);
	
		t1 = new UriTemplate("/foo/bar/");
		assertEquals(0, t1.variables.length);

		t1 = new UriTemplate("data:/foo/1/bar/");
		assertEquals(0, t1.variables.length);

		t1 = new UriTemplate("/foo/1/bar/");
		assertEquals(0, t1.variables.length);
	}
	
	public void testGetVariables_TemplateHasUniqueVariables() {

		t1 = new UriTemplate("foo:/1/2/{v1}/bar");
		assertEquals(1, t1.variables.length);
		assertEquals("v1", t1.variables[0].name);

		t1 = new UriTemplate("foo:/1/2/{v1}bar");
		assertEquals(1, t1.variables.length);
		assertEquals("v1", t1.variables[0].name);

		t1 = new UriTemplate("foo:/1/2/{v1}/bar/{v2}");
		assertEquals(2, t1.variables.length);
		assertEquals("v1", t1.variables[0].name);
		assertEquals("v2", t1.variables[1].name);

		t1 = new UriTemplate("foo:/1/2/{v1}bar{v2}");
		assertEquals(2, t1.variables.length);
		assertEquals("v1", t1.variables[0].name);
		assertEquals("v2", t1.variables[1].name);

		t1 = new UriTemplate("foo:/1/2/{v1}{v2}");
		assertEquals(2, t1.variables.length);
		assertEquals("v1", t1.variables[0].name);
		assertEquals("v2", t1.variables[1].name);

		t1 = new UriTemplate("foo:/1/2/{v1}{v2}/3");
		assertEquals(2, t1.variables.length);
		assertEquals("v1", t1.variables[0].name);
		assertEquals("v2", t1.variables[1].name);
	}
	
	
	public void testGetVariableNames_TemplateHasRepeatVariables() {
		
		t1 = new UriTemplate("foo:/1/2/{v1}/bar/{v1}");
		assertEquals(1, t1.variables.length);
		assertEquals("v1", t1.variables[0].name);

		t1 = new UriTemplate("foo:/1/2/{v1}bar{v1}");
		assertEquals(1, t1.variables.length);
		assertEquals("v1", t1.variables[0].name);

		t1 = new UriTemplate("foo:/1/2/{v1}{v1}");
		assertEquals(1, t1.variables.length);
		assertEquals("v1", t1.variables[0].name);
		
		t1 = new UriTemplate("foo:/1/2/{v1}{v1}/3");
		assertEquals(1, t1.variables.length);
		assertEquals("v1", t1.variables[0].name);
	}	

	public void testGetVariableNames_TemplateHasEmptyVariableBraces() {
		
		t1 = new UriTemplate("foo:/1/2/{}/bar/");
		assertEquals(0, t1.variables.length);
		
		t1 = new UriTemplate("foo:/1/2/{v1}/bar/{}");
		assertEquals(1, t1.variables.length);
		assertEquals("v1", t1.variables[0].name);

		t1 = new UriTemplate("foo:/1/2/{}/bar/{}");
		assertEquals(0, t1.variables.length);

		t1 = new UriTemplate("foo:/1/2/{}/bar{v1}/{}");
		assertEquals(1, t1.variables.length);
		assertEquals("v1", t1.variables[0].name);
	}

	public void testExtractPathName() {
		
		assertEquals("", UriTemplate.extractPathName(""));
		assertEquals("", UriTemplate.extractPathName("/"));

		assertEquals("", UriTemplate.extractPathName("/bar/"));
		assertEquals("bar", UriTemplate.extractPathName("/bar"));
		assertEquals("bar", UriTemplate.extractPathName("bar"));
		assertEquals("", UriTemplate.extractPathName("bar/"));

		assertEquals("bar", UriTemplate.extractPathName("/1/2/{v1}/bar"));
		assertEquals("", UriTemplate.extractPathName("/1/2/{v1}/bar/"));

		assertEquals("bar{1}", UriTemplate.extractPathName("bar{1}"));
		assertEquals("{v1}bar", UriTemplate.extractPathName("/1/2/{v1}bar"));
		assertEquals("", UriTemplate.extractPathName("/1/2/{v1}bar/"));
		assertEquals("{v1}bar", UriTemplate.extractPathName("/1/2/{v1}bar"));
		assertEquals("", UriTemplate.extractPathName("/1/2/{v1}bar/"));
	}
	
	public void testLeadingPath() {

	    t1 = new UriTemplate("");
	    assertEquals("", t1.leadingPath.toString());

	    t1 = new UriTemplate(".");
	    assertEquals(".", t1.leadingPath.toString());

        t1 = new UriTemplate("..");
        assertEquals("..", t1.leadingPath.toString());

        t1 = new UriTemplate("/");
        assertEquals("/", t1.leadingPath.toString());

        t1 = new UriTemplate("./");
        assertEquals(".", t1.leadingPath.toString());

        t1 = new UriTemplate("../");
        assertEquals("..", t1.leadingPath.toString());

        t1 = new UriTemplate("/bar/");
        assertEquals("/bar", t1.leadingPath.toString());

        t1 = new UriTemplate("./bar/");
        assertEquals("./bar", t1.leadingPath.toString());

        t1 = new UriTemplate("../bar/");
        assertEquals("../bar", t1.leadingPath.toString());

        t1 = new UriTemplate("bar");
        assertEquals("bar", t1.leadingPath.toString());

        t1 = new UriTemplate("bar/");
        assertEquals("bar", t1.leadingPath.toString());

        t1 = new UriTemplate("/1/bar/");
        assertEquals("/1/bar", t1.leadingPath.toString());

        t1 = new UriTemplate("/1/2/bar/");
        assertEquals("/1/2/bar", t1.leadingPath.toString());

        t1 = new UriTemplate("/1/2/{v1}/bar");
        assertEquals("/1/2", t1.leadingPath.toString());

        t1 = new UriTemplate("/1/2/f{v1}/bar");
        assertEquals("/1/2", t1.leadingPath.toString());

        t1 = new UriTemplate("/1/2/foo{v1}/bar");
        assertEquals("/1/2", t1.leadingPath.toString());

        t1 = new UriTemplate("1/2/{v1}/bar");
        assertEquals("1/2", t1.leadingPath.toString());

        t1 = new UriTemplate("1/2/f{v1}/bar");
        assertEquals("1/2", t1.leadingPath.toString());

        t1 = new UriTemplate("1/2/foo{v1}/bar");
        assertEquals("1/2", t1.leadingPath.toString());

        t1 = new UriTemplate("./2/foo{v1}/bar");
        assertEquals("./2", t1.leadingPath.toString());

	}
	
	public void testGetName() {
		
		t1 = new UriTemplate("");
		assertEquals("", t1.getName());

		t1 = new UriTemplate("/");
		assertEquals("", t1.getName());

		t1 = new UriTemplate("/bar/");
		assertEquals("bar", t1.getName());

		t1 = new UriTemplate("/bar");
		assertEquals("bar", t1.getName());

		t1 = new UriTemplate("bar");
		assertEquals("bar", t1.getName());

		t1 = new UriTemplate("bar/");
		assertEquals("bar", t1.getName());

		t1 = new UriTemplate("foo:/bar");
		assertEquals("bar", t1.getName());		

		t1 = new UriTemplate("foo:/1/bar");
		assertEquals("bar", t1.getName());		
		
		t1 = new UriTemplate("foo:/1/2/bar");
		assertEquals("bar", t1.getName());		
		
		t1 = new UriTemplate("foo:/1/2/{v1}/bar");
		assertEquals("bar", t1.getName());		

		t1 = new UriTemplate("foo:/1/2/{v1}bar");
		assertEquals("{v1}bar", t1.getName());
		
		t1 = new UriTemplate("/1/bar");
		assertEquals("bar", t1.getName());		
		
		t1 = new UriTemplate("/1/2/bar");
		assertEquals("bar", t1.getName());		
		
		t1 = new UriTemplate("/1/2/{v1}/bar");
		assertEquals("bar", t1.getName());		

		t1 = new UriTemplate("/1/2/{v1}bar");
		assertEquals("{v1}bar", t1.getName());
		
		t1 = new UriTemplate("foo:/");
		assertEquals("", t1.getName());		

		t1 = new UriTemplate("foo:/1/");
		assertEquals("1", t1.getName());		
		
		t1 = new UriTemplate("foo:/1/2/");
		assertEquals("2", t1.getName());		

		t1 = new UriTemplate("foo:/1/2/{v1}/");
		assertEquals("{v1}", t1.getName());		

		t1 = new UriTemplate("foo:/1/2/");
		assertEquals("2", t1.getName());

		t1 = new UriTemplate("/1/");
		assertEquals("1", t1.getName());		
		
		t1 = new UriTemplate("/1/2/");
		assertEquals("2", t1.getName());		
		
		t1 = new UriTemplate("/1/2/{v1}/");
		assertEquals("{v1}", t1.getName());		

		t1 = new UriTemplate("/1/2/");
		assertEquals("2", t1.getName());
	}
	
	public void testMatch_TemplatesHaveNoVariables() {
		
		t1 = new UriTemplate("/");
		t2 = new UriTemplate("/");
		assertTrue(t1.matches(t2));
		
		t1 = new UriTemplate("foo:/");
		t2 = new UriTemplate("foo:/");
		assertTrue(t1.matches(t2));

		t1 = new UriTemplate("foo:/");
		t2 = new UriTemplate("/");
		assertTrue(t1.matches(t2));
		
		t1 = new UriTemplate("/");
		t2 = new UriTemplate("foo:/");
		assertTrue(t1.matches(t2));

		t1 = new UriTemplate("foo:/bar/");
		t2 = new UriTemplate("foo:/bar/");
		assertTrue(t1.matches(t2));

		t1 = new UriTemplate("foo:/bar/");
		t2 = new UriTemplate("foo:/bar");
		assertTrue(t1.matches(t2));

		t1 = new UriTemplate("foo:/bar/");
		t2 = new UriTemplate("/bar/");
		assertTrue(t1.matches(t2));
		
		t1 = new UriTemplate("foo:/bar/");
		t2 = new UriTemplate("/bar");
		assertTrue(t1.matches(t2));
	}

	public void testMatch_TemplatesHaveSameVariablesNames() {
		
		t1 = new UriTemplate("foo:/1/2/{v1}/bar/");
		t2 = new UriTemplate("foo:/1/2/{v1}/bar/");
		assertTrue(t1.matches(t2));

		t1 = new UriTemplate("/1/2/{v1}/bar/");
		t2 = new UriTemplate("foo:/1/2/{v1}/bar/");
		assertTrue(t1.matches(t2));

		t1 = new UriTemplate("foo:/1/2/{v1}/bar{v2}/");
		t2 = new UriTemplate("/1/2/{v1}/bar{v2}/");
		assertTrue(t1.matches(t2));		
	}
	
	public void testMatch_TemplatesHaveDifferentVariableNames() {
		
		t1 = new UriTemplate("foo:/1/2/{v1}/bar/");
		t2 = new UriTemplate("foo:/1/2/{v2}/bar/");
		assertTrue(t1.matches(t2));

		t1 = new UriTemplate("foo:/1/2/{v1}/bar/");
		t2 = new UriTemplate("foo:/1/2/{}/bar/");
		assertTrue(t1.matches(t2));
		
		t1 = new UriTemplate("/1/2/{v1}/bar/");
		t2 = new UriTemplate("foo:/1/2/{v2}/bar/");
		assertTrue(t1.matches(t2));

		t1 = new UriTemplate("/1/2/{}/bar/");
		t2 = new UriTemplate("foo:/1/2/{v2}/bar/");
		assertTrue(t1.matches(t2));

		t1 = new UriTemplate("foo:/1/2/{v1}/bar{v2}/");
		t2 = new UriTemplate("/1/2/{v2}/bar{v1}/");
		assertTrue(t1.matches(t2));		

		t1 = new UriTemplate("foo:/1/2/{}/bar{}/");
		t2 = new UriTemplate("/1/2/{}/bar{}/");
		assertTrue(t1.matches(t2));

		t1 = new UriTemplate("foo:/1/2/{}/bar{v2}/");
		t2 = new UriTemplate("/1/2/{v2}/bar{}/");
		assertTrue(t1.matches(t2));	
	}

	public void testGetExpandedPath_PathHasNoVariables() throws Exception {

		Map<String,Object> nameValueMap;
		Object[] values = new Object[0];
		
		t1 = new UriTemplate("/");
		nameValueMap = new HashMap<String,Object>();
		assertEquals("/", t1.getExpandedPath(nameValueMap, values));
		
		t1 = new UriTemplate("/foo");
		nameValueMap = new HashMap<String,Object>();
		assertEquals("/foo", t1.getExpandedPath(nameValueMap, values));

		t1 = new UriTemplate("/foo/bar");
		nameValueMap = new HashMap<String,Object>();
		assertEquals("/foo/bar", t1.getExpandedPath(nameValueMap, values));
	}	

	public void testGetExpandedPath_PathHasUniqueVariables() throws Exception {

		Map<String,Object> nameValueMap;
		Object[] values;
		
		t1 = new UriTemplate("/1/2/{v1}/bar");
		values = new Object[1];
		nameValueMap = new HashMap<String,Object>();
		nameValueMap.put("v1","valueOne");
		assertEquals("/1/2/valueOne/bar", t1.getExpandedPath(nameValueMap, values));
		assertEquals("valueOne", values[0]);
		
		t1 = new UriTemplate("/1/2/{v1}/bar/{v2}");
		values = new Object[2];
		nameValueMap = new HashMap<String,Object>();
		nameValueMap.put("v1","valueOne");
		nameValueMap.put("v2","valueTwo");
		assertEquals("/1/2/valueOne/bar/valueTwo", t1.getExpandedPath(nameValueMap, values));
		assertEquals("valueOne", values[0]);
		assertEquals("valueTwo", values[1]);

		t1 = new UriTemplate("/1/2/{v2}bar{v1}");
		values = new Object[2];
		nameValueMap = new HashMap<String,Object>();
		nameValueMap.put("v1","valueOne");
		nameValueMap.put("v2","valueTwo");
		assertEquals("/1/2/valueTwobarvalueOne", t1.getExpandedPath(nameValueMap, values));
		assertEquals("valueTwo", values[0]);
		assertEquals("valueOne", values[1]);

		t1 = new UriTemplate("/1/2/{v1}{v2}");
		values = new Object[2];
		nameValueMap = new HashMap<String,Object>();
		nameValueMap.put("v1","valueOne");
		nameValueMap.put("v2","valueTwo");
		assertEquals("/1/2/valueOnevalueTwo", t1.getExpandedPath(nameValueMap, values));
		assertEquals("valueOne", values[0]);
		assertEquals("valueTwo", values[1]);

		t1 = new UriTemplate("/1/2/{v1}{v3}/3/{v2}");
		values = new Object[3];
		nameValueMap = new HashMap<String,Object>();
		nameValueMap.put("v1","valueOne");
		nameValueMap.put("v2","valueTwo");
		nameValueMap.put("v3","valueThree");
		assertEquals("/1/2/valueOnevalueThree/3/valueTwo", t1.getExpandedPath(nameValueMap, values));
		assertEquals("valueOne", values[0]);
		assertEquals("valueThree", values[1]);
		assertEquals("valueTwo", values[2]);
	}
}
	
