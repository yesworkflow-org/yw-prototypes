package org.yesworkflow.extract;

import java.io.IOException;

import org.yesworkflow.LanguageModel;
import org.yesworkflow.LanguageModel.Language;
import org.yesworkflow.YesWorkflowTestCase;

public class TestCommentMatcher_SAS extends YesWorkflowTestCase {

    private CommentMatcher matcher;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        LanguageModel lm = new LanguageModel(Language.SAS);
        matcher = new CommentMatcher(lm);
    }

    public void test_SAS_EmptySource()  throws IOException {
        String source = "";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("", comments);
    }

    public void test_SAS_BlankSource_OneLine()  throws IOException {
        String source = "           ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("", comments);
    }

    public void test_SAS_BlankSource_MultiLine()  throws IOException {
        String source = "           "  + EOL +
                        "           "  + EOL +
                        "           "  + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("", comments);
    }
    
    public void test_SAS_OneFullLineComment_JavaStyle()  throws IOException {
        String source = "  /* a comment */ ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"    + EOL, comments);
    }

    public void test_SAS_OneFullLineComment_SASStyle()  throws IOException {
        String source = "  * a comment ; ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"    + EOL, comments);
    }

    
    public void test_SAS_TwoFullLineComment_JavaStyle()  throws IOException {
        String source = "  /* a comment */ " + EOL +
                        "  /* another comment */ ";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_SAS_TwoFullLineComment_SASStyle()  throws IOException {
        String source = "  * a comment ; " + EOL +
                        "  * another comment ; ";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_SAS_TwoLineComment_JavaStyle()  throws IOException {
        String source = "  /* a comment "           + EOL +
                        "     another comment */ "  + EOL;        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_SAS_TwoLineComment_SASStyle()  throws IOException {
        String source = "  * a comment "           + EOL +
                        "     another comment ; "  + EOL;        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }
    
    public void test_SAS_TwoSeparatedComments_JavaStyle()  throws IOException {
        String source = "  /* a comment    */  "    + EOL +
                        "  some code"               + EOL +
                        "  /* another comment*/";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_SAS_TwoSeparatedComments_SASStyle()  throws IOException {
        String source = "  * a comment    ;  "    + EOL +
                        "  some code"               + EOL +
                        "  * another comment;";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_SAS_TwoMultilineComments_JavaStyle()  throws IOException {
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
    
    public void test_SAS_TwoMultilineComments_SASStyle()  throws IOException {
        String source = "  * a comment    "        + EOL +
                        "      on two lines ;"     + EOL +
                        "  some code"              + EOL +
                        "  * another comment     " + EOL +
                        "     on two lines  ; "    + EOL;        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "on two lines"     + EOL +
                     "another comment"  + EOL +
                     "on two lines"     + EOL,
                     comments);
    }

    public void test_SAS_TwoMultilineComments_WithBlankCommentLines_JavaStyle()  throws IOException {
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

    public void test_SAS_TwoMultilineComments_WithBlankCommentLines_SASStyle()  throws IOException {
        String source = "  * a comment    "        + EOL +
                        "                ;"        + EOL +
                        "  some code"              + EOL +
                        "  * another comment     " + EOL +
                        "                      ; " + EOL;        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }   
    
    public void test_SAS_MixedCodeAndOneLineComments_Delimited_JavaStyle() throws IOException {
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
    
    public void test_SAS_MixedCodeAndOneLineComments_Delimited_SASStyle() throws IOException {
        String source = "  some initial code"           + EOL +
                        "  a second line of code "      + EOL +
                        "  * a comment  ;   "           + EOL +
                        "  some more code"              + EOL +
                        "  * another comment   ;"       + EOL +
                        "  a final bit of code";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }
    
    public void test_SAS_MixedCodeAndOneLineComments_MultilineComments_JavaStyle() throws IOException {
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
    
    public void test_SAS_MixedCodeAndOneLineComments_MultilineComments_SASStyle() throws IOException {
        String source = "  some initial code"           + EOL +
                        "  a second line of code "      + EOL +
                        "  * a comment   "              + EOL +
                        "     on two lines ; "          + EOL +
                        "  some more code"              + EOL +
                        "  * another comment   "        + EOL +
                        "     this one is on "          + EOL +
                        "  three lines;"                + EOL +
                        "  a final bit of code";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "on two lines"     + EOL +
                     "another comment"  + EOL +
                     "this one is on"   + EOL +
                     "three lines"      + EOL,
                     comments);
    }
   
    public void test_SAS_OnePartialLineComment_JavaStyle()  throws IOException {
        String source = "  some code /* a comment*/ ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL, comments);
    }

    public void test_SAS_OnePartialLineComment_SASStyle()  throws IOException {
        String source = "  some code * a comment; ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL, comments);
    }

    public void test_SAS_OnePartialLineComment_SpansTwoLines_JavaStyle()  throws IOException {
        String source = "  some code /* a comment "                 + EOL +
                        "  the rest of the comment */ more code"    + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL +
                     "the rest of the comment" + EOL, 
                     comments);
    }

    public void test_SAS_OnePartialLineComment_SpansTwoLines_SASStyle()  throws IOException {
        String source = "  some code * a comment "                 + EOL +
                        "  the rest of the comment ; more code"    + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL +
                     "the rest of the comment" + EOL, 
                     comments);
    }

    public void test_SAS_TwoPartialLineComment_JavaStyle()  throws IOException {
        String source = "  some code /* a comment */" + EOL +
                        "  some more code  /* another comment */";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_SAS_TwoPartialLineComment_SASStyle()  throws IOException {
        String source = "  some code * a comment ;" + EOL +
                        "  some more code  * another comment ;";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_SAS_TwoPartialLineComments_JavaStyle()  throws IOException {
        String source = "  code /* a comment */ more code /* another comment */" + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL +
                     "another comment" + EOL,
                     comments);
    }

    public void test_SAS_TwoPartialLineComments_SASStyle()  throws IOException {
        String source = "  code * a comment ; more code * another comment ;" + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL +
                     "another comment" + EOL,
                     comments);
    }

    public void test_SAS_TwoPartialLineComments_MixedStyle()  throws IOException {
        String source = "  code * a comment */ more code ; /* another comment ; */" + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment */ more code" + EOL +
                     "another comment ;" + EOL,
                     comments);
    }
    
    public void test_SAS_ThreeCommentsOnOneLine_JavaStyle()  throws IOException {
        String source = " /* one */ /* two */ /* three */" + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("one" + EOL +
                     "two" + EOL +
                     "three" + EOL,
                     comments);
    }
    
    public void test_SAS_ThreeCommentsOnOneLine_MixedStyles()  throws IOException {
        String source = " /* one */ /* two ; /* three */" + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("one" + EOL +
                     "two ; /* three" + EOL,
                     comments);
    }

    public void test_SAS_ThreeCommentsOnOneLine_SASStyle()  throws IOException {
        String source = " * one ; * two ; * three ;" + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("one" + EOL +
                     "two" + EOL +
                     "three" + EOL,
                     comments);
    }    
}
