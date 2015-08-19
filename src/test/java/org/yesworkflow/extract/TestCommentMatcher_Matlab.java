package org.yesworkflow.extract;

import java.io.IOException;

import org.yesworkflow.Language;
import org.yesworkflow.LanguageModel;
import org.yesworkflow.YWKeywords;
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
        Long sourceId = ywdb.insertSource("__reader__");
        matcher = new CommentMatcher(this.ywdb, 
                new KeywordMatcher(new YWKeywords().getKeywords()), sourceId, lm);
    }

    public void test_Matlab_EmptySource()  throws IOException {
        String source = "";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("", comments);
    }

    public void test_Matlab_BlankSource_OneLine()  throws IOException {
        String source = "           ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("", comments);
    }

    public void test_Matlab_BlankSource_MultiLine()  throws IOException {
        String source = "           "  + EOL +
                        "           "  + EOL +
                        "           "  + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("", comments);
    }
    
    public void test_Matlab_OneLineComment_Paired()  throws IOException {
        String source = "  %{ a comment %} ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"    + EOL, comments);
    }

    public void test_Matlab_OneFullLineComment_Single()  throws IOException {
        String source = "  % a comment ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"    + EOL, comments);
    }
    
    public void test_Matlab_TwoFullLineComment_Paired()  throws IOException {
        String source = "  %{ a comment %} " + EOL +
                        "  %{ another comment %} ";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_Matlab_TwoFullLineComment_Single()  throws IOException {
        String source = "  % a comment " + EOL +
                        "  % another comment ";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_Matlab_TwoLineComment_Paired()  throws IOException {
        String source = "  %{ a comment "           + EOL +
                        "     another comment %} "  + EOL;        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_Matlab_TwoSeparatedComments_Paired()  throws IOException {
        String source = "  %{ a comment    %}  "    + EOL +
                        "  some code"               + EOL +
                        "  %{ another comment%}";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_Matlab_TwoMultilineComments_Paired()  throws IOException {
        String source = "  %{ a comment    "        + EOL +
                        "      on two lines %}"     + EOL +
                        "  some code"               + EOL +
                        "  %{ another comment     " + EOL +
                        "     on two lines  %} " + EOL;        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "on two lines"     + EOL +
                     "another comment"  + EOL +
                     "on two lines"     + EOL,
                     comments);
    }

    public void test_Matlab_TwoMultilineComments_WithBlankCommentLines_Paired()  throws IOException {
        String source = "  %{ a comment    "        + EOL +
                        "                %}"        + EOL +
                        "  some code"               + EOL +
                        "  %{ another comment     " + EOL +
                        "                      %} " + EOL;        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }
    
    public void test_Matlab_MixedCodeAndOneLineComments_Paired() throws IOException {
        String source = "  some initial code"           + EOL +
                        "  a second line of code "      + EOL +
                        "  %{ a comment  %} "           + EOL +
                        "  some more code"              + EOL +
                        "  %{ another comment   %}"     + EOL +
                        "  a final bit of code";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }
    
    public void test_Matlab_MixedCodeAndOneLineComments_MultilineComments_Paired() throws IOException {
        String source = "  some initial code"           + EOL +
                        "  a second line of code "      + EOL +
                        "  %{ a comment   "             + EOL +
                        "     on two lines %} "         + EOL +
                        "  some more code"              + EOL +
                        "  %{ another comment   "       + EOL +
                        "     this one is on "          + EOL +
                        "  three lines%}"               + EOL +
                        "  a final bit of code";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "on two lines"     + EOL +
                     "another comment"  + EOL +
                     "this one is on"   + EOL +
                     "three lines"      + EOL,
                     comments);
    }

   
    public void test_Matlab_OnePartialLineComment_Paired()  throws IOException {
        String source = "  some code %{ a comment%} ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL, comments);
    }

    public void test_Matlab_OnePartialLineComment_SpansTwoLines_Paired()  throws IOException {
        String source = "  some code %{ a comment "                 + EOL +
                        "  the rest of the comment %} more code"    + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL +
                     "the rest of the comment" + EOL, 
                     comments);
    }

    public void test_Matlab_TwoPartialLineComment_Paired()  throws IOException {
        String source = "  some code %{ a comment %}" + EOL +
                        "  some more code  %{ another comment %}";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     comments);
    }

    public void test_Matlab_TwoPartialLineComments_Paired()  throws IOException {
        String source = "  code %{ a comment %} more code %{ another comment %}" + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL +
                     "another comment" + EOL,
                     comments);
    }
    
    public void test_Matlab_ThreeCommentsOnOneLine_Paired()  throws IOException {
        String source = " %{ one %} %{ two %} %{ three %}" + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("one" + EOL +
                     "two" + EOL +
                     "three" + EOL,
                     comments);
    }
    
    public void test_Matlab_VeryMixedStyles()  throws IOException {
        String source = "  %{ a comment %} code % second comment  "         + EOL +
                        "  %{ another comment %}%{ fourth comment %} code"  + EOL +
                        "  % fifth comment %{ not a separate comment %}"    + EOL +      
                        "  code %{ sixth comment %} code"                   + EOL +  
                        "  yet more code %{ a multiline comment   "         + EOL +
                        "  % not a separate comment      "                  + EOL +
                        "  third line of % the comment %} code % last one"  + EOL;
        String comments = matcher.getCommentsAsString(source);
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
                comments);
    }

    
    

}
