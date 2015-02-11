package org.yesworkflow.extract;

import java.io.IOException;

import org.yesworkflow.LanguageModel;
import org.yesworkflow.LanguageModel.Language;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestCommentMatcher_Bash extends YesWorkflowTestCase {

    private CommentMatcher matcher;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        LanguageModel lm = new LanguageModel(Language.BASH);
        matcher = new CommentMatcher(lm);
    }
    
    public void test_Bash_EmptySource()  throws IOException {
        String source = "";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("", comments);
    }

    public void test_Bash_BlankSource_OneLine()  throws IOException {
        String source = "           ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("", comments);
    }

    public void test_Bash_BlankSource_MultiLine()  throws IOException {
        String source = "           "  + EOL +
                        "           "  + EOL +
                        "           "  + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("", comments);
    }

    public void test_Bash_OneFullLineComment_NoSpaceOnEnds()  throws IOException {
        String source = "# a comment";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL, comments);
    }

    public void test_Bash_OneFullLineComment_SpaceOnEnds()  throws IOException {
        String source = "  # a comment ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL, comments);
    }
    
    public void test_Bash_TwoFullLineComment()  throws IOException {
        String source = "  # a comment " + EOL +
                        "  # another comment ";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     comments);
    }

    public void test_Bash_TwoSeparatedComments()  throws IOException {
        String source = "  # a comment "        + EOL +
                        "  some code"           + EOL +
                        "  # another comment ";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     comments);
    }

    public void test_Bash_MixedCodeAndOneLineComments() throws IOException {
        String source = "  some initial code"           + EOL +
                        "    a second line of code "    + EOL +
                        "  # a comment "                + EOL +
                        "  some more code"              + EOL +
                        "  # another comment "          + EOL +
                        " a final bit of code";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     comments);
    }

    public void test_Comment_MixedCodeAndOneLineComments() throws IOException {
        String source = "  some initial code"           + EOL +
                        "    a second line of code "    + EOL +
                        "  # a comment "                + EOL +
                        "  some more code"              + EOL +
                        "  # another comment "          + EOL +
                        " a final bit of code";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     comments);
    }
    
    
    public void test_Bash_OnePartialLineComment()  throws IOException {
        String source = "  some code # a comment ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment" + EOL, comments);
    }

    public void test_Bash_TwoPartialLineComment()  throws IOException {
        String source = "  some code # a comment " + EOL +
                        "some more cod  # another comment ";        
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     comments);
    }
    
}
