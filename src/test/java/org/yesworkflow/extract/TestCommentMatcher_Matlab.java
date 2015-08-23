package org.yesworkflow.extract;

import org.yesworkflow.Language;
import org.yesworkflow.LanguageModel;
import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.db.YesWorkflowDB;

public class TestCommentMatcher_Matlab extends YesWorkflowTestCase {

    private CommentMatcher matcher;
    private YesWorkflowDB ywdb = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.ywdb = YesWorkflowDB.createInMemoryDB();
        LanguageModel lm = new LanguageModel(Language.MATLAB);
        matcher = new CommentMatcher(this.ywdb, lm);
    }

    public void test_Matlab_EmptySource()  throws Exception {
        String source = "";
        matcher.extractComments(source);
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Matlab_BlankSource_OneLine()  throws Exception {
        String source = "           ";
        matcher.extractComments(source);
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Matlab_BlankSource_MultiLine()  throws Exception {
        String source = "           "  + EOL +
                        "           "  + EOL +
                        "           "  + EOL;
        matcher.extractComments(source);
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Matlab_OneLineComment_Paired()  throws Exception {
        String source = "  %{ a comment %} ";
        matcher.extractComments(source);
        assertEquals("a comment"    + EOL, DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Matlab_OneFullLineComment_Single()  throws Exception {
        String source = "  % a comment ";
        matcher.extractComments(source);
        assertEquals("a comment"    + EOL, DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Matlab_TwoFullLineComment_Paired()  throws Exception {
        String source = "  %{ a comment %} " + EOL +
                        "  %{ another comment %} ";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Matlab_TwoFullLineComment_Single()  throws Exception {
        String source = "  % a comment " + EOL +
                        "  % another comment ";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Matlab_TwoLineComment_Paired()  throws Exception {
        String source = "  %{ a comment "           + EOL +
                        "     another comment %} "  + EOL;        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Matlab_TwoSeparatedComments_Paired()  throws Exception {
        String source = "  %{ a comment    %}  "    + EOL +
                        "  some code"               + EOL +
                        "  %{ another comment%}";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Matlab_TwoMultilineComments_Paired()  throws Exception {
        String source = "  %{ a comment    "        + EOL +
                        "      on two lines %}"     + EOL +
                        "  some code"               + EOL +
                        "  %{ another comment     " + EOL +
                        "     on two lines  %} " + EOL;        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "on two lines"     + EOL +
                     "another comment"  + EOL +
                     "on two lines"     + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Matlab_TwoMultilineComments_WithBlankCommentLines_Paired()  throws Exception {
        String source = "  %{ a comment    "        + EOL +
                        "                %}"        + EOL +
                        "  some code"               + EOL +
                        "  %{ another comment     " + EOL +
                        "                      %} " + EOL;        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Matlab_MixedCodeAndOneLineComments_Paired() throws Exception {
        String source = "  some initial code"           + EOL +
                        "  a second line of code "      + EOL +
                        "  %{ a comment  %} "           + EOL +
                        "  some more code"              + EOL +
                        "  %{ another comment   %}"     + EOL +
                        "  a final bit of code";
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Matlab_MixedCodeAndOneLineComments_MultilineComments_Paired() throws Exception {
        String source = "  some initial code"           + EOL +
                        "  a second line of code "      + EOL +
                        "  %{ a comment   "             + EOL +
                        "     on two lines %} "         + EOL +
                        "  some more code"              + EOL +
                        "  %{ another comment   "       + EOL +
                        "     this one is on "          + EOL +
                        "  three lines%}"               + EOL +
                        "  a final bit of code";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "on two lines"     + EOL +
                     "another comment"  + EOL +
                     "this one is on"   + EOL +
                     "three lines"      + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }

   
    public void test_Matlab_OnePartialLineComment_Paired()  throws Exception {
        String source = "  some code %{ a comment%} ";
        matcher.extractComments(source);
        assertEquals("a comment" + EOL, DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Matlab_OnePartialLineComment_SpansTwoLines_Paired()  throws Exception {
        String source = "  some code %{ a comment "                 + EOL +
                        "  the rest of the comment %} more code"    + EOL;
        matcher.extractComments(source);
        assertEquals("a comment" + EOL +
                     "the rest of the comment" + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Matlab_TwoPartialLineComment_Paired()  throws Exception {
        String source = "  some code %{ a comment %}" + EOL +
                        "  some more code  %{ another comment %}";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Matlab_TwoPartialLineComments_Paired()  throws Exception {
        String source = "  code %{ a comment %} more code %{ another comment %}" + EOL;
        matcher.extractComments(source);
        assertEquals("a comment" + EOL +
                     "another comment" + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Matlab_ThreeCommentsOnOneLine_Paired()  throws Exception {
        String source = " %{ one %} %{ two %} %{ three %}" + EOL;
        matcher.extractComments(source);
        assertEquals("one" + EOL +
                     "two" + EOL +
                     "three" + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Matlab_VeryMixedStyles()  throws Exception {
        String source = "  %{ a comment %} code % second comment  "         + EOL +
                        "  %{ another comment %}%{ fourth comment %} code"  + EOL +
                        "  % fifth comment %{ not a separate comment %}"    + EOL +      
                        "  code %{ sixth comment %} code"                   + EOL +  
                        "  yet more code %{ a multiline comment   "         + EOL +
                        "  % not a separate comment      "                  + EOL +
                        "  third line of % the comment %} code % last one"  + EOL;
        matcher.extractComments(source);
        assertEquals(
                "a comment"                                     + EOL +
                "second comment"                                + EOL +
                "another comment"                               + EOL +
                "fourth comment"                                + EOL +
                "fifth comment %{ not a separate comment %}"    + EOL +
                "sixth comment"                                 + EOL +
                "a multiline comment"                           + EOL +
                "% not a separate comment"                      + EOL +
                "third line of % the comment"                   + EOL +
                "last one"                                      + EOL, 
                DefaultExtractor.commentsAsString(ywdb));
    }
}
