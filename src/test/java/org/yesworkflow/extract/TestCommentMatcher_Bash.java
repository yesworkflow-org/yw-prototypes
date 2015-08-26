package org.yesworkflow.extract;

import static org.yesworkflow.db.Column.*;

import org.jooq.Record;
import org.jooq.Result;
import org.yesworkflow.Language;
import org.yesworkflow.LanguageModel;
import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.db.Table;
import org.yesworkflow.db.YesWorkflowDB;

public class TestCommentMatcher_Bash extends YesWorkflowTestCase {

    private CommentMatcher matcher;
    private YesWorkflowDB ywdb = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.ywdb = YesWorkflowDB.createInMemoryDB();
        LanguageModel lm = new LanguageModel(Language.BASH);
        matcher = new CommentMatcher(this.ywdb, lm);
    }
    
    @SuppressWarnings({ "unchecked" })
    private Result<Record> selectComments() {
        return ywdb.jooq().select(ID, SOURCE_ID, LINE_NUMBER, RANK_IN_LINE, COMMENT_TEXT)
                          .from(Table.COMMENT)
                          .orderBy(SOURCE_ID, LINE_NUMBER, RANK_IN_LINE)
                          .fetch();
    }
    
    public void test_Bash_EmptySource()  throws Exception {
        String source = "";
        matcher.extractComments(source);
        assertEquals(0, selectComments().size());
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Bash_BlankSource_OneLine()  throws Exception {
        String source = "           ";
        matcher.extractComments(source);
        assertEquals(0, selectComments().size());
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Bash_BlankSource_MultiLine()  throws Exception {
        String source = "           "  + EOL +
                        "           "  + EOL +
                        "           "  + EOL;
        matcher.extractComments(source);
        assertEquals(0, selectComments().size());
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Bash_OneFullLineComment_NoSpaceOnEnds()  throws Exception {
        String source = "# a comment";
        matcher.extractComments(source);
        assertEquals("a comment" + EOL, DefaultExtractor.commentsAsString(ywdb));
        assertEquals(
                "+----+------+-----------+------------+------------+"   + EOL +
                "|id  |source|line_number|rank_in_line|comment_text|"   + EOL +
                "+----+------+-----------+------------+------------+"   + EOL +
                "|1   |1     |1          |1           |a comment   |"   + EOL +
                "+----+------+-----------+------------+------------+", 
                selectComments().toString());
    }

    public void test_Bash_OneFullLineComment_SpaceOnEnds()  throws Exception {
        String source = "  # a comment ";
        matcher.extractComments(source);
        assertEquals("a comment" + EOL,  DefaultExtractor.commentsAsString(ywdb));
        assertEquals(
                "+----+------+-----------+------------+------------+"   + EOL +
                "|id  |source|line_number|rank_in_line|comment_text|"   + EOL +
                "+----+------+-----------+------------+------------+"   + EOL +
                "|1   |1     |1          |1           |a comment   |"   + EOL +
                "+----+------+-----------+------------+------------+", 
                selectComments().toString());
    }
    
    public void test_Bash_TwoFullLineComment()  throws Exception {
        String source = "  # a comment " + EOL +
                        "  # another comment ";        
        matcher.extractComments(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
        assertEquals(
                "+----+------+-----------+------------+---------------+"    + EOL +
                "|id  |source|line_number|rank_in_line|comment_text   |"    + EOL +
                "+----+------+-----------+------------+---------------+"    + EOL +
                "|1   |1     |1          |1           |a comment      |"    + EOL +
                "|2   |1     |2          |1           |another comment|"    + EOL +
                "+----+------+-----------+------------+---------------+", 
                selectComments().toString());
    }

    public void test_Bash_TwoSeparatedComments()  throws Exception {
        String source = "  # a comment "        + EOL +
                        "  some code"           + EOL +
                        "  # another comment ";
        matcher.extractComments(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
        assertEquals(
                "+----+------+-----------+------------+---------------+"    + EOL +
                "|id  |source|line_number|rank_in_line|comment_text   |"    + EOL +
                "+----+------+-----------+------------+---------------+"    + EOL +
                "|1   |1     |1          |1           |a comment      |"    + EOL +
                "|2   |1     |3          |1           |another comment|"    + EOL +
                "+----+------+-----------+------------+---------------+", 
                selectComments().toString());
    }

    public void test_Bash_MixedCodeAndOneLineComments() throws Exception {
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

        assertEquals(
                "+----+------+-----------+------------+---------------+"    + EOL +
                "|id  |source|line_number|rank_in_line|comment_text   |"    + EOL +
                "+----+------+-----------+------------+---------------+"    + EOL +
                "|1   |1     |3          |1           |a comment      |"    + EOL +
                "|2   |1     |5          |1           |another comment|"    + EOL +
                "+----+------+-----------+------------+---------------+", 
                selectComments().toString());
    }

    public void test_Comment_MixedCodeAndOneLineComments() throws Exception {
        String source = "  some initial code"                   + EOL +
                        "    a second line of code "            + EOL +
                        "  a third line of code # a comment "   + EOL +
                        "  some more code"                      + EOL +
                        "  # another comment "                  + EOL +
                        " a final bit of code";
        matcher.extractComments(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
        assertEquals(
                "+----+------+-----------+------------+---------------+"   + EOL +
                "|id  |source|line_number|rank_in_line|comment_text   |"   + EOL +
                "+----+------+-----------+------------+---------------+"   + EOL +
                "|1   |1     |3          |1           |a comment      |"   + EOL +
                "|2   |1     |5          |1           |another comment|"   + EOL +
                "+----+------+-----------+------------+---------------+", 
                selectComments().toString());
    }
    
    
    public void test_Bash_OnePartialLineComment()  throws Exception {
        String source = "  some code # a comment ";
        matcher.extractComments(source);
        assertEquals("a comment" + EOL, DefaultExtractor.commentsAsString(ywdb));
        assertEquals(
                "+----+------+-----------+------------+------------+"   + EOL +
                "|id  |source|line_number|rank_in_line|comment_text|"   + EOL +
                "+----+------+-----------+------------+------------+"   + EOL +
                "|1   |1     |1          |1           |a comment   |"   + EOL +
                "+----+------+-----------+------------+------------+", 
                selectComments().toString());
    }

    public void test_Bash_TwoPartialLineComment()  throws Exception {
        String source = "  some code # a comment " + EOL +
                        "some more cod  # another comment ";        
        matcher.extractComments(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));    
        assertEquals(
                "+----+------+-----------+------------+---------------+"    + EOL + 
                "|id  |source|line_number|rank_in_line|comment_text   |"    + EOL + 
                "+----+------+-----------+------------+---------------+"    + EOL + 
                "|1   |1     |1          |1           |a comment      |"    + EOL + 
                "|2   |1     |2          |1           |another comment|"    + EOL + 
                "+----+------+-----------+------------+---------------+", 
                selectComments().toString());
    }
    
}
