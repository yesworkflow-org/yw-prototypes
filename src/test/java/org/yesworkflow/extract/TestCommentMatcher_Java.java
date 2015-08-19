package org.yesworkflow.extract;

import java.io.IOException;

import org.yesworkflow.Language;
import org.yesworkflow.LanguageModel;
import org.yesworkflow.YWKeywords;
import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.db.YesWorkflowDB;

public class TestCommentMatcher_Java extends YesWorkflowTestCase {

    private CommentMatcher matcher;
    private YesWorkflowDB ywdb = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.ywdb = YesWorkflowDB.createInMemoryDB();
        LanguageModel lm = new LanguageModel(Language.JAVA);
        Long sourceId = ywdb.insertSource("__reader__");
        matcher = new CommentMatcher(this.ywdb, 
                new KeywordMatcher(new YWKeywords().getKeywords()), sourceId, lm);
    }

    public void test_Java_EmptySource()  throws IOException {
        String source = "";
        matcher.extractComments(source);
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_BlankSource_OneLine()  throws IOException {
        String source = "           ";
        matcher.extractComments(source);
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_BlankSource_MultiLine()  throws IOException {
        String source = "           "  + EOL +
                        "           "  + EOL +
                        "           "  + EOL;
        matcher.extractComments(source);
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_OneFullLineComment()  throws IOException {
        String source = "  // a comment ";
        matcher.extractComments(source);
        assertEquals("a comment"    + EOL, DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_TwoFullLineComment()  throws IOException {
        String source = "  // a comment " + EOL +
                        "  // another comment ";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_TwoSeparatedComments()  throws IOException {
        String source = "  // a comment "           + EOL +
                        "  some code"               + EOL +
                        "  // another comment ";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_MixedCodeAndOneLineComments() throws IOException {
        String source = "  some initial code"           + EOL +
                        "  a second line of code "      + EOL +
                        "  // a comment "               + EOL +
                        "  some more code"              + EOL +
                        "  // another comment "         + EOL +
                        "  a final bit of code";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Java_OnePartialLineComment()  throws IOException {
        String source = "  some code // a comment ";
        matcher.extractComments(source);
        assertEquals("a comment" + EOL, DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_TwoPartialLineComment()  throws IOException {
        String source = "  some code // a comment " + EOL +
                        "  some more code  // another comment ";
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Java_OneFullLineComment_Delimited()  throws IOException {
        String source = "  /* a comment */ ";
        matcher.extractComments(source);
        assertEquals("a comment"    + EOL, DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Java_TwoFullLineComment_Delimited()  throws IOException {
        String source = "  /* a comment */ " + EOL +
                        "  /* another comment */ ";
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_TwoLineComment_Delimited()  throws IOException {
        String source = "  /* a comment "           + EOL +
                        "     another comment */ "  + EOL;        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Java_TwoSeparatedComments_Delimited()  throws IOException {
        String source = "  /* a comment    */  "    + EOL +
                        "  some code"               + EOL +
                        "  /* another comment*/";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_TwoMultilineComments()  throws IOException {
        String source = "  /* a comment    "        + EOL +
                        "      on two lines */"     + EOL +
                        "  some code"               + EOL +
                        "  /* another comment     " + EOL +
                        "     on two lines  */ " + EOL;        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "on two lines"     + EOL +
                     "another comment"  + EOL +
                     "on two lines"     + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_TwoMultilineComments_WithBlankCommentLines()  throws IOException {
        String source = "  /* a comment    "        + EOL +
                        "                */"        + EOL +
                        "  some code"               + EOL +
                        "  /* another comment     " + EOL +
                        "                      */ " + EOL;
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_MixedCodeAndOneLineComments_Delimited() throws IOException {
        String source = "  some initial code"           + EOL +
                        "  a second line of code "      + EOL +
                        "  /* a comment  */ "           + EOL +
                        "  some more code"              + EOL +
                        "  /* another comment   */"     + EOL +
                        "  a final bit of code";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
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
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "on two lines"     + EOL +
                     "another comment"  + EOL +
                     "this one is on"   + EOL +
                     "three lines"      + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }
   
    public void test_Java_OnePartialLineComment_Delimited()  throws IOException {
        String source = "  some code /* a comment*/ ";
        matcher.extractComments(source);
        assertEquals("a comment" + EOL,  DefaultExtractor.commentsAsString(ywdb));
    }
   
    public void test_Java_OnePartialLineComment_SpansTwoLines()  throws IOException {
        String source = "  some code /* a comment "                 + EOL +
                        "  the rest of the comment */ more code"    + EOL;
        matcher.extractComments(source);
        assertEquals("a comment" + EOL +
                     "the rest of the comment" + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Java_TwoPartialLineComment_Delimited()  throws IOException {
        String source = "  some code /* a comment */" + EOL +
                        "  some more code  /* another comment */";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_TwoPartialLineComments_OneOneLine()  throws IOException {
        String source = "  code /* a comment */ more code // another comment" + EOL;
        matcher.extractComments(source);
        assertEquals("a comment" + EOL +
                     "another comment" + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Java_ThreeCommentsOnOneLine()  throws IOException {
        String source = " /* one */ /* two */ /* three */" + EOL;
        matcher.extractComments(source);
        assertEquals("one" + EOL +
                     "two" + EOL +
                     "three" + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }    
}
