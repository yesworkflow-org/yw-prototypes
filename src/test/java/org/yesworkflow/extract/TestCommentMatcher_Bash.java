package org.yesworkflow.extract;

import static org.yesworkflow.db.Column.*;

import java.io.IOException;

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
        Long sourceId = ywdb.insertSource("__reader__");
        matcher = new CommentMatcher(this.ywdb, sourceId, lm);
    }
    
    @SuppressWarnings({ "unchecked" })
    private Result<Record> selectComments() {
        return ywdb.jooq().select(ID, SOURCE_ID, LINE_NUMBER, RANK, TEXT)
                .from(Table.COMMENT)
                .orderBy(ID, LINE_NUMBER, RANK)
                .fetch();
    }
    
    public void test_Bash_EmptySource()  throws IOException {
        String source = "";
        matcher.extractComments(source);
        assertEquals(0, selectComments().size());
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Bash_BlankSource_OneLine()  throws IOException {
        String source = "           ";
        matcher.extractComments(source);
        assertEquals(0, selectComments().size());
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Bash_BlankSource_MultiLine()  throws IOException {
        String source = "           "  + EOL +
                        "           "  + EOL +
                        "           "  + EOL;
        matcher.extractComments(source);
        assertEquals(0, selectComments().size());
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Bash_OneFullLineComment_NoSpaceOnEnds()  throws IOException {
        String source = "# a comment";
        matcher.extractComments(source);
        assertEquals("a comment" + EOL, DefaultExtractor.commentsAsString(ywdb));
        assertEquals(
                "+----+---------+-----------+----+---------+"   + EOL +
                "|id  |source_id|line_number|rank|text     |"   + EOL +
                "+----+---------+-----------+----+---------+"   + EOL +
                "|1   |1        |1          |1   |a comment|"   + EOL +
                "+----+---------+-----------+----+---------+", 
                selectComments().toString());
    }

    public void test_Bash_OneFullLineComment_SpaceOnEnds()  throws IOException {
        String source = "  # a comment ";
        matcher.extractComments(source);
        assertEquals("a comment" + EOL,  DefaultExtractor.commentsAsString(ywdb));
        assertEquals(
                "+----+---------+-----------+----+---------+"   + EOL +
                "|id  |source_id|line_number|rank|text     |"   + EOL +
                "+----+---------+-----------+----+---------+"   + EOL +
                "|1   |1        |1          |1   |a comment|"   + EOL +
                "+----+---------+-----------+----+---------+", 
                selectComments().toString());
    }
    
    public void test_Bash_TwoFullLineComment()  throws IOException {
        String source = "  # a comment " + EOL +
                        "  # another comment ";        
        matcher.extractComments(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
        assertEquals(
                "+----+---------+-----------+----+---------------+"   + EOL +
                "|id  |source_id|line_number|rank|text           |"   + EOL +
                "+----+---------+-----------+----+---------------+"   + EOL +
                "|1   |1        |1          |1   |a comment      |"   + EOL +
                "|2   |1        |2          |1   |another comment|"   + EOL +
                "+----+---------+-----------+----+---------------+", 
                selectComments().toString());
    }

    public void test_Bash_TwoSeparatedComments()  throws IOException {
        String source = "  # a comment "        + EOL +
                        "  some code"           + EOL +
                        "  # another comment ";
        matcher.extractComments(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
        assertEquals(
                "+----+---------+-----------+----+---------------+"   + EOL +
                "|id  |source_id|line_number|rank|text           |"   + EOL +
                "+----+---------+-----------+----+---------------+"   + EOL +
                "|1   |1        |1          |1   |a comment      |"   + EOL +
                "|2   |1        |3          |1   |another comment|"   + EOL +
                "+----+---------+-----------+----+---------------+", 
                selectComments().toString());
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

        assertEquals(
                "+----+---------+-----------+----+---------------+"   + EOL +
                "|id  |source_id|line_number|rank|text           |"   + EOL +
                "+----+---------+-----------+----+---------------+"   + EOL +
                "|1   |1        |3          |1   |a comment      |"   + EOL +
                "|2   |1        |5          |1   |another comment|"   + EOL +
                "+----+---------+-----------+----+---------------+", 
                selectComments().toString());
    }

    public void test_Comment_MixedCodeAndOneLineComments() throws IOException {
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
                "+----+---------+-----------+----+---------------+"   + EOL +
                "|id  |source_id|line_number|rank|text           |"   + EOL +
                "+----+---------+-----------+----+---------------+"   + EOL +
                "|1   |1        |3          |1   |a comment      |"   + EOL +
                "|2   |1        |5          |1   |another comment|"   + EOL +
                "+----+---------+-----------+----+---------------+", 
                selectComments().toString());
    }
    
    
    public void test_Bash_OnePartialLineComment()  throws IOException {
        String source = "  some code # a comment ";
        matcher.extractComments(source);
        assertEquals("a comment" + EOL, DefaultExtractor.commentsAsString(ywdb));
        assertEquals(
                "+----+---------+-----------+----+---------+"   + EOL +
                "|id  |source_id|line_number|rank|text     |"   + EOL +
                "+----+---------+-----------+----+---------+"   + EOL +
                "|1   |1        |1          |1   |a comment|"   + EOL +
                "+----+---------+-----------+----+---------+", 
                selectComments().toString());
    }

    public void test_Bash_TwoPartialLineComment()  throws IOException {
        String source = "  some code # a comment " + EOL +
                        "some more cod  # another comment ";        
        matcher.extractComments(source);
        assertEquals("a comment"          + EOL +
                     "another comment"    + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));    
        assertEquals(
                "+----+---------+-----------+----+---------------+"     + EOL +
                "|id  |source_id|line_number|rank|text           |"     + EOL +
                "+----+---------+-----------+----+---------------+"     + EOL +
                "|1   |1        |1          |1   |a comment      |"     + EOL +
                "|2   |1        |2          |1   |another comment|"     + EOL +
                "+----+---------+-----------+----+---------------+", 
                selectComments().toString());
    }
    
}
