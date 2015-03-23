package org.yesworkflow.extract;

import java.io.IOException;

import org.yesworkflow.LanguageModel;
import org.yesworkflow.LanguageModel.Language;
import org.yesworkflow.YesWorkflowTestCase;

public class TestCommentMatcher_Java extends YesWorkflowTestCase {

    private CommentMatcher matcher;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        LanguageModel lm = new LanguageModel(Language.JAVA);
        matcher = new CommentMatcher(lm);
    }

    public void test_Java_EmptySource()  throws IOException {
        String source = "";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("", comments);
    }

    public void test_Java_BlankSource_OneLine()  throws IOException {
        String source = "           ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("", comments);
    }

    public void test_Java_BlankSource_MultiLine()  throws IOException {
        String source = "           "  + EOL +
                        "           "  + EOL +
                        "           "  + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("", comments);
    }

    public void test_Java_OneFullLineComment()  throws IOException {
        String source = "  // a comment ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"    + EOL, comments);
    }

    public void test_Java_TwoFullLineComment()  throws IOException {
        String source = "  // a comment " + EOL +
                        "  // another comment ";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_Java_TwoSeparatedComments()  throws IOException {
        String source = "  // a comment "           + EOL +
                        "  some code"               + EOL +
                        "  // another comment ";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_Java_MixedCodeAndOneLineComments() throws IOException {
        String source = "  some initial code"           + EOL +
                        "  a second line of code "      + EOL +
                        "  // a comment "               + EOL +
                        "  some more code"              + EOL +
                        "  // another comment "         + EOL +
                        "  a final bit of code";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }
    
    public void test_Java_OnePartialLineComment()  throws IOException {
        String source = "  some code // a comment ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL, comments);
    }

    public void test_Java_TwoPartialLineComment()  throws IOException {
        String source = "  some code // a comment " + EOL +
                        "  some more code  // another comment ";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }
    
    public void test_Java_OneFullLineComment_Delimited()  throws IOException {
        String source = "  /* a comment */ ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"    + EOL, comments);
    }
    
    public void test_Java_TwoFullLineComment_Delimited()  throws IOException {
        String source = "  /* a comment */ " + EOL +
                        "  /* another comment */ ";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_Java_TwoLineComment_Delimited()  throws IOException {
        String source = "  /* a comment "           + EOL +
                        "     another comment */ "  + EOL;        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }
    
    public void test_Java_TwoSeparatedComments_Delimited()  throws IOException {
        String source = "  /* a comment    */  "    + EOL +
                        "  some code"               + EOL +
                        "  /* another comment*/";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_Java_TwoMultilineComments()  throws IOException {
        String source = "  /* a comment    "        + EOL +
                        "      on two lines */"     + EOL +
                        "  some code"               + EOL +
                        "  /* another comment     " + EOL +
                        "     on two lines  */ " + EOL;        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "on two lines"     + EOL +
                     "another comment"  + EOL +
                     "on two lines"     + EOL,
                     comments);
    }

    public void test_Java_TwoMultilineComments_WithBlankCommentLines()  throws IOException {
        String source = "  /* a comment    "        + EOL +
                        "                */"        + EOL +
                        "  some code"               + EOL +
                        "  /* another comment     " + EOL +
                        "                      */ " + EOL;        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_Java_MixedCodeAndOneLineComments_Delimited() throws IOException {
        String source = "  some initial code"           + EOL +
                        "  a second line of code "      + EOL +
                        "  /* a comment  */ "           + EOL +
                        "  some more code"              + EOL +
                        "  /* another comment   */"     + EOL +
                        "  a final bit of code";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }
    
    public void test_Java_MixedCodeAndOneLineComments_MultilineComments() throws IOException {
        String source = "  some initial code"           + EOL +
                        "  a second line of code "      + EOL +
                        "  /* a comment   "             + EOL +
                        "     on two lines */ "         + EOL +
                        "  some more code"              + EOL +
                        "  /* another comment   "       + EOL +
                        "     this one is on "          + EOL +
                        "  three lines*/"               + EOL +
                        "  a final bit of code";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "on two lines"     + EOL +
                     "another comment"  + EOL +
                     "this one is on"   + EOL +
                     "three lines"      + EOL,
                     comments);
    }
   
    public void test_Java_OnePartialLineComment_Delimited()  throws IOException {
        String source = "  some code /* a comment*/ ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL, comments);
    }
   
    public void test_Java_OnePartialLineComment_SpansTwoLines()  throws IOException {
        String source = "  some code /* a comment "                 + EOL +
                        "  the rest of the comment */ more code"    + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL +
                     "the rest of the comment" + EOL, 
                     comments);
    }
    
    public void test_Java_TwoPartialLineComment_Delimited()  throws IOException {
        String source = "  some code /* a comment */" + EOL +
                        "  some more code  /* another comment */";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_Java_TwoPartialLineComments_OneOneLine()  throws IOException {
        String source = "  code /* a comment */ more code // another comment" + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL +
                     "another comment" + EOL,
                     comments);
    }
    
    public void test_Java_ThreeCommentsOnOneLine()  throws IOException {
        String source = " /* one */ /* two */ /* three */" + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("one" + EOL +
                     "two" + EOL +
                     "three" + EOL,
                     comments);
    }    
}
