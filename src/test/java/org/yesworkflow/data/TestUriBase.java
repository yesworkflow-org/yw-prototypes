package org.yesworkflow.data;

import org.yesworkflow.YesWorkflowTestCase;


public class TestUriBase extends YesWorkflowTestCase {

	private UriBase u;
	
	private static class UriBaseImp extends UriBase {

		public UriBaseImp(String expression, boolean trimTerminalSlash) {
			super(expression, trimTerminalSlash);
		}

		public UriBaseImp(String expression) {
			super(expression);
		}
	}

	public void testTrimTerminalSlash_HasNoSchemeNoSlash() {

		assertEquals("/", UriBaseImp.trimTerminalSlash("/"));
		assertEquals("/a", UriBaseImp.trimTerminalSlash("/a"));
		assertEquals("/ab", UriBaseImp.trimTerminalSlash("/ab"));
		assertEquals("/foo", UriBaseImp.trimTerminalSlash("/foo"));
		assertEquals("/foo/bar", UriBaseImp.trimTerminalSlash("/foo/bar"));
	}
	
	public void testTrimTerminalSlash_HasSlashNoScheme() {

		assertEquals("/a", UriBaseImp.trimTerminalSlash("/a/"));
		assertEquals("/ab", UriBaseImp.trimTerminalSlash("/ab/"));
		assertEquals("/foo", UriBaseImp.trimTerminalSlash("/foo/"));
		assertEquals("/foo/bar", UriBaseImp.trimTerminalSlash("/foo/bar/"));
		
	}

	public void testTrimTerminalSlash_HasSchemeNoSlash() {

		assertEquals("foo:/", UriBaseImp.trimTerminalSlash("foo:/"));
		assertEquals("foo:/a", UriBaseImp.trimTerminalSlash("foo:/a"));
		assertEquals("foo:/ab", UriBaseImp.trimTerminalSlash("foo:/ab"));
		assertEquals("file:/foo", UriBaseImp.trimTerminalSlash("file:/foo"));
		assertEquals("data:/foo/bar", UriBaseImp.trimTerminalSlash("data:/foo/bar"));
	}

	public void testTrimTerminalSlash_HasSlashAndScheme() {

		assertEquals("foo:/a", UriBaseImp.trimTerminalSlash("foo:/a/"));
		assertEquals("foo:/ab", UriBaseImp.trimTerminalSlash("foo:/ab/"));
		assertEquals("file:/foo", UriBaseImp.trimTerminalSlash("file:/foo/"));
		assertEquals("data:/foo/bar", UriBaseImp.trimTerminalSlash("data:/foo/bar/"));
	}

	public void testExtractSchemeAndPath_HasScheme() {
		
		UriBaseImp.SchemePathPair spp;
		
		spp = UriBaseImp.extractSchemeAndPath("file:/");
		assertEquals("file", spp.scheme);
		assertEquals("/", spp.path);
		
		spp = UriBaseImp.extractSchemeAndPath("file:/foo");
		assertEquals("file", spp.scheme);
		assertEquals("/foo", spp.path);

		spp = UriBaseImp.extractSchemeAndPath("data:/foo/bar");
		assertEquals("data", spp.scheme);
		assertEquals("/foo/bar", spp.path);
		
		spp = UriBaseImp.extractSchemeAndPath("foo:/1/2/bar");
		assertEquals("foo", spp.scheme);
		assertEquals("/1/2/bar", spp.path);
	}

	public void testExtractSchemeAndPath_HasSchemeAndDoubleLeadingSlashes() {
		
		UriBaseImp.SchemePathPair spp;
		
		spp = UriBaseImp.extractSchemeAndPath("file://");
		assertEquals("file", spp.scheme);
		assertEquals("/", spp.path);
		
		spp = UriBaseImp.extractSchemeAndPath("file://foo");
		assertEquals("file", spp.scheme);
		assertEquals("/foo", spp.path);

		spp = UriBaseImp.extractSchemeAndPath("data://foo/bar");
		assertEquals("data", spp.scheme);
		assertEquals("/foo/bar", spp.path);
		
		spp = UriBaseImp.extractSchemeAndPath("foo://1/2/bar");
		assertEquals("foo", spp.scheme);
		assertEquals("/1/2/bar", spp.path);
	}

	public void testExtractSchemeAndPath_HasSchemeAndDoubleAndIntermediateLeadingSlashes() {
		
		UriBaseImp.SchemePathPair spp;
		
		spp = UriBaseImp.extractSchemeAndPath("file://");
		assertEquals("file", spp.scheme);
		assertEquals("/", spp.path);
		
		spp = UriBaseImp.extractSchemeAndPath("file://foo");
		assertEquals("file", spp.scheme);
		assertEquals("/foo", spp.path);

		spp = UriBaseImp.extractSchemeAndPath("data://foo//bar");
		assertEquals("data", spp.scheme);
		assertEquals("/foo/bar", spp.path);
		
		spp = UriBaseImp.extractSchemeAndPath("foo://1//2//bar");
		assertEquals("foo", spp.scheme);
		assertEquals("/1/2/bar", spp.path);
	}

	
	public void testExtractSchemeAndPath_HasNoScheme() {
		
		UriBaseImp.SchemePathPair spp;
		
		spp = UriBaseImp.extractSchemeAndPath("/");
		assertSame(UriBaseImp.NO_SCHEME, spp.scheme);
		assertEquals("/", spp.path);
		
		spp = UriBaseImp.extractSchemeAndPath("/foo");
		assertSame(UriBaseImp.NO_SCHEME, spp.scheme);
		assertEquals("/foo", spp.path);

		spp = UriBaseImp.extractSchemeAndPath("/foo/bar");
		assertSame(UriBaseImp.NO_SCHEME, spp.scheme);
		assertEquals("/foo/bar", spp.path);
		
		spp = UriBaseImp.extractSchemeAndPath("/1/2/bar");
		assertSame(UriBaseImp.NO_SCHEME,spp.scheme);
		assertEquals("/1/2/bar", spp.path);
	}

	public void testExtractSchemeAndPath_HasNoSchemeButDoubleLeadingSlashes() {
		
		UriBaseImp.SchemePathPair spp;
		
		spp = UriBaseImp.extractSchemeAndPath("//");
		assertSame(UriBaseImp.NO_SCHEME, spp.scheme);
		assertEquals("/", spp.path);
		
		spp = UriBaseImp.extractSchemeAndPath("//foo");
		assertSame(UriBaseImp.NO_SCHEME, spp.scheme);
		assertEquals("/foo", spp.path);

		spp = UriBaseImp.extractSchemeAndPath("//foo/bar");
		assertSame(UriBaseImp.NO_SCHEME, spp.scheme);
		assertEquals("/foo/bar", spp.path);
		
		spp = UriBaseImp.extractSchemeAndPath("//1/2/bar");
		assertSame(UriBaseImp.NO_SCHEME,spp.scheme);
		assertEquals("/1/2/bar", spp.path);
	}

	public void testExtractSchemeAndPath_HasNoSchemeButLeadingAndIntermediateDoubleSlashes() {
		
		UriBaseImp.SchemePathPair spp;
		
		spp = UriBaseImp.extractSchemeAndPath("//foo//bar");
		assertSame(UriBaseImp.NO_SCHEME, spp.scheme);
		assertEquals("/foo/bar", spp.path);
		
		spp = UriBaseImp.extractSchemeAndPath("//1//2//bar");
		assertSame(UriBaseImp.NO_SCHEME,spp.scheme);
		assertEquals("/1/2/bar", spp.path);
	}

	
	public void testGetScheme_HasScheme() {
		
		u = new UriBaseImp("file:/foo", false);
		assertEquals("file", u.getScheme());

		u = new UriBaseImp("data:/foo/bar", false);
		assertEquals("data", u.getScheme());

		u = new UriBaseImp("foo:/1/2/bar", false);
		assertEquals("foo", u.getScheme());

		u = new UriBaseImp("file:/foo", true);
		assertEquals("file", u.getScheme());

		u = new UriBaseImp("data:/foo/bar", true);
		assertEquals("data", u.getScheme());

		u = new UriBaseImp("foo:/1/2/bar", true);
		assertEquals("foo", u.getScheme());

		u = new UriBaseImp("file:/foo");
		assertEquals("file", u.getScheme());

		u = new UriBaseImp("data:/foo/bar");
		assertEquals("data", u.getScheme());

		u = new UriBaseImp("foo:/1/2/bar");
		assertEquals("foo", u.getScheme());
	}

	public void testGetScheme_HasNoScheme() {
		
		u = new UriBaseImp("/bar", false);
		assertSame(UriBaseImp.NO_SCHEME, u.getScheme());
		assertEquals("", u.getScheme());

		u = new UriBaseImp("/", false);
		assertSame(UriBaseImp.NO_SCHEME, u.getScheme());
		assertEquals("", u.getScheme());
		
		u = new UriBaseImp("/bar", true);
		assertSame(UriBaseImp.NO_SCHEME, u.getScheme());
		assertEquals("", u.getScheme());

		u = new UriBaseImp("/", true);
		assertSame(UriBaseImp.NO_SCHEME, u.getScheme());
		assertEquals("", u.getScheme());

		u = new UriBaseImp("/bar");
		assertSame(UriBaseImp.NO_SCHEME, u.getScheme());
		assertEquals("", u.getScheme());

		u = new UriBaseImp("/");
		assertSame(UriBaseImp.NO_SCHEME, u.getScheme());
		assertEquals("", u.getScheme());
	}

	public void testGetPath_HasScheme() {
		
		u = new UriBaseImp("file:/foo", false);
		assertEquals("/foo", u.getPath());

		u = new UriBaseImp("data:/foo/bar", false);
		assertEquals("/foo/bar", u.getPath());

		u = new UriBaseImp("foo:/1/2/bar", false);
		assertEquals("/1/2/bar", u.getPath());

		u = new UriBaseImp("file:/foo", true);
		assertEquals("/foo", u.getPath());

		u = new UriBaseImp("data:/foo/bar", true);
		assertEquals("/foo/bar", u.getPath());

		u = new UriBaseImp("foo:/1/2/bar", true);
		assertEquals("/1/2/bar", u.getPath());

		u = new UriBaseImp("file:/foo");
		assertEquals("/foo", u.getPath());

		u = new UriBaseImp("data:/foo/bar");
		assertEquals("/foo/bar", u.getPath());

		u = new UriBaseImp("foo:/1/2/bar");
		assertEquals("/1/2/bar", u.getPath());
	}

	public void testGetPath_HasSchemeAndDoubleLeadingSlashes() {
		
		u = new UriBaseImp("file://foo", false);
		assertEquals("/foo", u.getPath());

		u = new UriBaseImp("data://foo/bar", false);
		assertEquals("/foo/bar", u.getPath());

		u = new UriBaseImp("foo://1/2/bar", false);
		assertEquals("/1/2/bar", u.getPath());

		u = new UriBaseImp("file://foo", true);
		assertEquals("/foo", u.getPath());

		u = new UriBaseImp("data://foo/bar", true);
		assertEquals("/foo/bar", u.getPath());

		u = new UriBaseImp("foo://1/2/bar", true);
		assertEquals("/1/2/bar", u.getPath());

		u = new UriBaseImp("file://foo");
		assertEquals("/foo", u.getPath());

		u = new UriBaseImp("data://foo/bar");
		assertEquals("/foo/bar", u.getPath());

		u = new UriBaseImp("foo://1/2/bar");
		assertEquals("/1/2/bar", u.getPath());
	}
	
	public void testGetPath_HasNoScheme() {
		
		u = new UriBaseImp("/foo", true);
		assertEquals("/foo", u.getPath());

		u = new UriBaseImp("/foo/bar", true);
		assertEquals("/foo/bar", u.getPath());

		u = new UriBaseImp("/1/2/bar", true);
		assertEquals("/1/2/bar", u.getPath());

		u = new UriBaseImp("/foo", false);
		assertEquals("/foo", u.getPath());

		u = new UriBaseImp("/foo/bar", false);
		assertEquals("/foo/bar", u.getPath());

		u = new UriBaseImp("/1/2/bar", false);
		assertEquals("/1/2/bar", u.getPath());

		u = new UriBaseImp("/foo");
		assertEquals("/foo", u.getPath());

		u = new UriBaseImp("/foo/bar");
		assertEquals("/foo/bar", u.getPath());

		u = new UriBaseImp("/1/2/bar");
		assertEquals("/1/2/bar", u.getPath());
	}

	public void testGetPath_HasNoSchemeButDoubleLeadingSlashes() {
		
		u = new UriBaseImp("//foo", true);
		assertEquals("/foo", u.getPath());

		u = new UriBaseImp("//foo/bar", true);
		assertEquals("/foo/bar", u.getPath());

		u = new UriBaseImp("//1/2/bar", true);
		assertEquals("/1/2/bar", u.getPath());

		u = new UriBaseImp("//foo", false);
		assertEquals("/foo", u.getPath());

		u = new UriBaseImp("//foo/bar", false);
		assertEquals("/foo/bar", u.getPath());

		u = new UriBaseImp("//1/2/bar", false);
		assertEquals("/1/2/bar", u.getPath());

		u = new UriBaseImp("//foo");
		assertEquals("/foo", u.getPath());

		u = new UriBaseImp("//foo/bar");
		assertEquals("/foo/bar", u.getPath());

		u = new UriBaseImp("//1/2/bar");
		assertEquals("/1/2/bar", u.getPath());
}
	
	public void testGetPath_HasTerminalSlash() {
		
		u = new UriBaseImp("file:/foo/", false);
		assertEquals("/foo/", u.getPath());

		u = new UriBaseImp("data:/foo/bar/", false);
		assertEquals("/foo/bar/", u.getPath());

		u = new UriBaseImp("foo:/1/2/bar/", false);
		assertEquals("/1/2/bar/", u.getPath());
		
		u = new UriBaseImp("/foo/", false);
		assertEquals("/foo/", u.getPath());

		u = new UriBaseImp("/foo/bar/", false);
		assertEquals("/foo/bar/", u.getPath());

		u = new UriBaseImp("/1/2/bar/", false);
		assertEquals("/1/2/bar/", u.getPath());

		u = new UriBaseImp("file:/foo/", true);
		assertEquals("/foo", u.getPath());

		u = new UriBaseImp("data:/foo/bar/", true);
		assertEquals("/foo/bar", u.getPath());

		u = new UriBaseImp("foo:/1/2/bar/", true);
		assertEquals("/1/2/bar", u.getPath());
		
		u = new UriBaseImp("/foo", true);
		assertEquals("/foo", u.getPath());

		u = new UriBaseImp("/foo/bar", true);
		assertEquals("/foo/bar", u.getPath());

		u = new UriBaseImp("/1/2/bar", true);
		assertEquals("/1/2/bar", u.getPath());

		u = new UriBaseImp("file:/foo/");
		assertEquals("/foo/", u.getPath());

		u = new UriBaseImp("data:/foo/bar/");
		assertEquals("/foo/bar/", u.getPath());

		u = new UriBaseImp("foo:/1/2/bar/");
		assertEquals("/1/2/bar/", u.getPath());
		
		u = new UriBaseImp("/foo/");
		assertEquals("/foo/", u.getPath());

		u = new UriBaseImp("/foo/bar/");
		assertEquals("/foo/bar/", u.getPath());

		u = new UriBaseImp("/1/2/bar/");
		assertEquals("/1/2/bar/", u.getPath());
		
	}	
	
	public void testGetExpression_HasNoEndSlash() {
		
		u = new UriBaseImp("foo:/bar", false);
		assertEquals("foo:/bar", u.getExpression());		

		u = new UriBaseImp("foo:/1/bar", false);
		assertEquals("foo:/1/bar", u.getExpression());		
		
		u = new UriBaseImp("foo:/1/2/bar", false);
		assertEquals("foo:/1/2/bar", u.getExpression());		
		
		u = new UriBaseImp("foo:/bar", true);
		assertEquals("foo:/bar", u.getExpression());		

		u = new UriBaseImp("foo:/1/bar", true);
		assertEquals("foo:/1/bar", u.getExpression());		
		
		u = new UriBaseImp("foo:/1/2/bar", true);
		assertEquals("foo:/1/2/bar", u.getExpression());		

		u = new UriBaseImp("foo:/bar");
		assertEquals("foo:/bar", u.getExpression());		

		u = new UriBaseImp("foo:/1/bar");
		assertEquals("foo:/1/bar", u.getExpression());		
		
		u = new UriBaseImp("foo:/1/2/bar");
		assertEquals("foo:/1/2/bar", u.getExpression());		
	
	}

	
	public void testGetExpression_HasEndSlash() {
		
		u = new UriBaseImp("/", false);
		assertEquals("/", u.getExpression());		

		u = new UriBaseImp("foo:/", false);
		assertEquals("foo:/", u.getExpression());		

		u = new UriBaseImp("foo:/bar/", false);
		assertEquals("foo:/bar/", u.getExpression());		

		u = new UriBaseImp("foo:/1/bar/", false);
		assertEquals("foo:/1/bar/", u.getExpression());		
		
		u = new UriBaseImp("foo:/1/2/bar/", false);
		assertEquals("foo:/1/2/bar/", u.getExpression());
		
		u = new UriBaseImp("foo:/1/2/v1/bar/", false);
		assertEquals("foo:/1/2/v1/bar/", u.getExpression());

		u = new UriBaseImp("/", true);
		assertEquals("/", u.getExpression());		

		u = new UriBaseImp("foo:/", true);
		assertEquals("foo:/", u.getExpression());		

		u = new UriBaseImp("foo:/bar/", true);
		assertEquals("foo:/bar", u.getExpression());		

		u = new UriBaseImp("foo:/1/bar/", true);
		assertEquals("foo:/1/bar", u.getExpression());		
		
		u = new UriBaseImp("foo:/1/2/bar/", true);
		assertEquals("foo:/1/2/bar", u.getExpression());
		
		u = new UriBaseImp("foo:/1/2/v1/bar/", true);
		assertEquals("foo:/1/2/v1/bar", u.getExpression());
		
		u = new UriBaseImp("/");
		assertEquals("/", u.getExpression());		

		u = new UriBaseImp("foo:/");
		assertEquals("foo:/", u.getExpression());		

		u = new UriBaseImp("foo:/bar/");
		assertEquals("foo:/bar/", u.getExpression());		

		u = new UriBaseImp("foo:/1/bar/");
		assertEquals("foo:/1/bar/", u.getExpression());		
		
		u = new UriBaseImp("foo:/1/2/bar/");
		assertEquals("foo:/1/2/bar/", u.getExpression());
		
		u = new UriBaseImp("foo:/1/2/v1/bar/");
		assertEquals("foo:/1/2/v1/bar/", u.getExpression());
	}

	
	public void testExtractPathName() {
		
		assertEquals("", UriBaseImp.extractPathName(""));
		assertEquals("", UriBaseImp.extractPathName("/"));

		assertEquals("", UriBaseImp.extractPathName("/bar/"));
		assertEquals("bar", UriBaseImp.extractPathName("/bar"));
		assertEquals("bar", UriBaseImp.extractPathName("bar"));
		assertEquals("", UriBaseImp.extractPathName("bar/"));

		assertEquals("bar", UriBaseImp.extractPathName("/1/2/v1/bar"));
		assertEquals("", UriBaseImp.extractPathName("/1/2/v1/bar/"));

		assertEquals("bar", UriBaseImp.extractPathName("bar"));
		assertEquals("v1bar", UriBaseImp.extractPathName("/1/2/v1bar"));
		assertEquals("", UriBaseImp.extractPathName("/1/2/v1bar/"));
		assertEquals("v1bar", UriBaseImp.extractPathName("/1/2/v1bar"));
		assertEquals("", UriBaseImp.extractPathName("/1/2/v1bar/"));
	}
	
	public void testGetName() {
		
		u = new UriBaseImp("", false);
		assertEquals("", u.getName());		

		u = new UriBaseImp("/", false);
		assertEquals("", u.getName());

		u = new UriBaseImp("/bar/", false);
		assertEquals("", u.getName());

		u = new UriBaseImp("/bar", false);
		assertEquals("bar", u.getName());

		u = new UriBaseImp("bar", false);
		assertEquals("bar", u.getName());

		u = new UriBaseImp("bar/", false);
		assertEquals("", u.getName());

		u = new UriBaseImp("foo:/bar", false);
		assertEquals("bar", u.getName());		

		u = new UriBaseImp("foo:/1/bar", false);
		assertEquals("bar", u.getName());		
		
		u = new UriBaseImp("foo:/1/2/bar", false);
		assertEquals("bar", u.getName());		
				
		u = new UriBaseImp("/1/bar", false);
		assertEquals("bar", u.getName());		
		
		u = new UriBaseImp("/1/2/bar", false);
		assertEquals("bar", u.getName());		
				
		u = new UriBaseImp("foo:/", false);
		assertEquals("", u.getName());		

		u = new UriBaseImp("foo:/1/", false);
		assertEquals("", u.getName());
		
		u = new UriBaseImp("foo:/1/2/", false);
		assertEquals("", u.getName());		

		u = new UriBaseImp("foo:/1/2/", false);
		assertEquals("", u.getName());

		u = new UriBaseImp("/1/", false);
		assertEquals("", u.getName());		
		
		u = new UriBaseImp("/1/2/", false);
		assertEquals("", u.getName());		
		
		u = new UriBaseImp("/1/2/", false);
		assertEquals("", u.getName());
		
		
		u = new UriBaseImp("", true);
		assertEquals("", u.getName());		

		u = new UriBaseImp("/", true);
		assertEquals("", u.getName());

		u = new UriBaseImp("/bar/", true);
		assertEquals("bar", u.getName());

		u = new UriBaseImp("/bar", true);
		assertEquals("bar", u.getName());

		u = new UriBaseImp("bar", true);
		assertEquals("bar", u.getName());

		u = new UriBaseImp("bar/", true);
		assertEquals("bar", u.getName());

		u = new UriBaseImp("foo:/bar", true);
		assertEquals("bar", u.getName());		

		u = new UriBaseImp("foo:/1/bar", true);
		assertEquals("bar", u.getName());		
		
		u = new UriBaseImp("foo:/1/2/bar", true);
		assertEquals("bar", u.getName());		
				
		u = new UriBaseImp("/1/bar", true);
		assertEquals("bar", u.getName());		
		
		u = new UriBaseImp("/1/2/bar", true);
		assertEquals("bar", u.getName());		
				
		u = new UriBaseImp("foo:/", true);
		assertEquals("", u.getName());		

		u = new UriBaseImp("foo:/1/", true);
		assertEquals("1", u.getName());
		
		u = new UriBaseImp("foo:/1/2/", true);
		assertEquals("2", u.getName());		

		u = new UriBaseImp("foo:/1/2/", true);
		assertEquals("2", u.getName());

		u = new UriBaseImp("/1/", true);
		assertEquals("1", u.getName());		
		
		u = new UriBaseImp("/1/2/", true);
		assertEquals("2", u.getName());		
		
		u = new UriBaseImp("/1/2/", true);
		assertEquals("2", u.getName());
		
		
		u = new UriBaseImp("");
		assertEquals("", u.getName());		

		u = new UriBaseImp("/");
		assertEquals("", u.getName());

		u = new UriBaseImp("/bar/");
		assertEquals("", u.getName());

		u = new UriBaseImp("/bar");
		assertEquals("bar", u.getName());

		u = new UriBaseImp("bar");
		assertEquals("bar", u.getName());

		u = new UriBaseImp("bar/");
		assertEquals("", u.getName());

		u = new UriBaseImp("foo:/bar");
		assertEquals("bar", u.getName());		

		u = new UriBaseImp("foo:/1/bar");
		assertEquals("bar", u.getName());		
		
		u = new UriBaseImp("foo:/1/2/bar");
		assertEquals("bar", u.getName());		
				
		u = new UriBaseImp("/1/bar");
		assertEquals("bar", u.getName());		
		
		u = new UriBaseImp("/1/2/bar");
		assertEquals("bar", u.getName());		
				
		u = new UriBaseImp("foo:/");
		assertEquals("", u.getName());		

		u = new UriBaseImp("foo:/1/");
		assertEquals("", u.getName());
		
		u = new UriBaseImp("foo:/1/2/");
		assertEquals("", u.getName());		

		u = new UriBaseImp("foo:/1/2/");
		assertEquals("", u.getName());

		u = new UriBaseImp("/1/");
		assertEquals("", u.getName());		
		
		u = new UriBaseImp("/1/2/");
		assertEquals("", u.getName());		
		
		u = new UriBaseImp("/1/2/");
		assertEquals("", u.getName());
	}
	
	public void testExtractParent() {
		
		assertEquals("", UriBaseImp.extractParent(""));
		assertEquals("", UriBaseImp.extractParent("/"));

		assertEquals("", UriBaseImp.extractParent("/bar/"));
		assertEquals("/", UriBaseImp.extractParent("/bar"));
		assertEquals("", UriBaseImp.extractParent("bar"));
		assertEquals("", UriBaseImp.extractParent("bar/"));

		assertEquals("/1/2/v1/", UriBaseImp.extractParent("/1/2/v1/bar"));
		assertEquals("", UriBaseImp.extractParent("/1/2/v1/bar/"));

		assertEquals("", UriBaseImp.extractParent("bar"));
		assertEquals("/1/2/", UriBaseImp.extractParent("/1/2/v1bar"));
		assertEquals("", UriBaseImp.extractParent("/1/2/v1bar/"));
		assertEquals("/1/2/", UriBaseImp.extractParent("/1/2/v1bar"));
		assertEquals("", UriBaseImp.extractParent("/1/2/v1bar/"));
		
		assertEquals("foo:", UriBaseImp.extractParent("foo:bar"));
		assertEquals("foo:/1/2/", UriBaseImp.extractParent("foo:/1/2/v1bar"));
		assertEquals("", UriBaseImp.extractParent("foo:/1/2/v1bar/"));
		assertEquals("foo:/1/2/", UriBaseImp.extractParent("foo:/1/2/v1bar"));
		assertEquals("", UriBaseImp.extractParent("foo:/1/2/v1bar/"));
	}
	
	public void testEncodeString() {
		assertEquals("abcd_1234_ABC-123~aBcD.5zZ", UriBaseImp.encodeString("abcd_1234_ABC-123~aBcD.5zZ"));
		assertEquals("abc%20def%20%20123%20%20%20456%20%20%20%20_~.-", UriBaseImp.encodeString("abc def  123   456    _~.-"));
		assertEquals("a%24a", UriBaseImp.encodeString("a$a"));
		assertEquals("a%09a", UriBaseImp.encodeString("a\ta"));
	}
}
	

