package org.yesworkflow.extract;

import java.io.IOException;

import org.yesworkflow.Language;
import org.yesworkflow.LanguageModel;
import org.yesworkflow.YWKeywords;
import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.db.YesWorkflowDB;

public class TestCommentMatcher_Bash extends YesWorkflowTestCase {

    private CommentMatcher matcher;
    private YesWorkflowDB ywdb = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.ywdb = YesWorkflowDB.createInMemoryDB();
        LanguageModel lm = new LanguageModel(Language.BASH);
        Long sourceId = ywdb.insertSource("__reader__");
        matcher = new CommentMatcher(this.ywdb, 
                new KeywordMatcher(new YWKeywords().getKeywords()), sourceId, lm);
    }
    
    public void test_Bash_EmptySource()  throws IOException {
        String source = "";
        matcher.extractComments(source);
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Bash_BlankSource_OneLine()  throws IOException {
        String source = "           ";
        matcher.extractComments(source);
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Bash_BlankSource_MultiLine()  throws IOException {
        String source = "           "  + EOL +
                        "           "  + EOL +
                        "           "  + EOL;
        matcher.extractComments(source);
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Bash_OneFullLineComment_NoSpaceOnEnds()  throws IOException {
        String source = "# a comment";
        matcher.extractComments(source);
        assertEquals("a comment" + EOL, DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Bash_OneFullLineComment_SpaceOnEnds()  throws IOException {
        String source = "  # a comment ";
        matcher.extractComments(source);
        assertEquals("a comment" + EOL,  DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Bash_TwoFullLineComment()  throws IOException {
        String source = "  # a comment " + EOL +
                        "  # another comment ";        
        matcher.extractComments(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Bash_TwoSeparatedComments()  throws IOException {
        String source = "  # a comment "        + EOL +
                        "  some code"           + EOL +
                        "  # another comment ";
        matcher.extractComments(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Bash_MixedCodeAndOneLineComments() throws IOException {
        String source = "  some initial code"           + EOL +
                        "    a second line of code "    + EOL +
                        "  # a comment "                + EOL +
                        "  some more code"              + EOL +
                        "  # another comment "          + EOL +
                        " a final bit of code";        
        matcher.extractComments(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Comment_MixedCodeAndOneLineComments() throws IOException {
        String source = "  some initial code"           + EOL +
                        "    a second line of code "    + EOL +
                        "  # a comment "                + EOL +
                        "  some more code"              + EOL +
                        "  # another comment "          + EOL +
                        " a final bit of code";
        matcher.extractComments(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }
    
    
    public void test_Bash_OnePartialLineComment()  throws IOException {
        String source = "  some code # a comment ";
        matcher.extractComments(source);
        assertEquals("a comment" + EOL, DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Bash_TwoPartialLineComment()  throws IOException {
        String source = "  some code # a comment " + EOL +
                        "some more cod  # another comment ";        
        matcher.extractComments(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));    
    }
    
}
