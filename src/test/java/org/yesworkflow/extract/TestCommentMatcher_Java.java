package org.yesworkflow.extract;

import static org.yesworkflow.db.Column.*;

import org.jooq.Record;
import org.jooq.Result;
import org.yesworkflow.Language;
import org.yesworkflow.LanguageModel;
import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.db.Table;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.util.FileIO;
import static org.jooq.impl.DSL.field;

public class TestCommentMatcher_Java extends YesWorkflowTestCase {

    private CommentMatcher matcher;
    private YesWorkflowDB ywdb = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.ywdb = YesWorkflowDB.createInMemoryDB();
        this.matcher = new CommentMatcher(this.ywdb, new LanguageModel(Language.JAVA));
    }

    @SuppressWarnings("unchecked")
    private Result<Record> selectCode() {
        return ywdb.jooq().select(ID, SOURCE_ID, LINE_NUMBER, LINE_TEXT)
                          .from(Table.SOURCE_LINE)
                          .orderBy(SOURCE_ID, LINE_NUMBER)
                          .fetch();
    }
    
    @SuppressWarnings({ "unchecked" })
    private Result<Record> selectComments() {
        return ywdb.jooq().select(ID, SOURCE_ID, LINE_NUMBER, RANK_IN_LINE.as(field("rank")), COMMENT_TEXT)
                          .from(Table.COMMENT)
                          .orderBy(SOURCE_ID, LINE_NUMBER, RANK_IN_LINE)
                          .fetch();
    }
    
    public void test_Java_EmptySource()  throws Exception {
        matcher.extractComments("");
        assertEquals(0, selectComments().size());
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_BlankSource_OneLine()  throws Exception {
        matcher.extractComments("           ");
        assertEquals(0, selectComments().size());
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_BlankSource_MultiLine()  throws Exception {
        matcher.extractComments(
                "           "  + EOL +
                "           "  + EOL +
                "           "  + EOL);
        assertEquals(0, selectComments().size());
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_OneFullLineComment()  throws Exception {
        matcher.extractComments("  // a comment ");
        assertEquals(
                "+----+------+-----------+---------------+"   + EOL +
                "|id  |source|line_number|line_text      |"   + EOL +
                "+----+------+-----------+---------------+"   + EOL +
                "|1   |1     |1          |  // a comment |"   + EOL +
                "+----+------+-----------+---------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text|"   + EOL +
                "+----+------+-----------+----+------------+"   + EOL +
                "|1   |1     |1          |1   |a comment   |"   + EOL +
                "+----+------+-----------+----+------------+", 
                selectComments().toString());
        assertEquals("a comment"    + EOL, DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_TwoFullLineComment()  throws Exception {
        matcher.extractComments(
                "  // a comment " + EOL +
                "  // another comment ");
        assertEquals(
                "+----+------+-----------+---------------------+"   + EOL +
                "|id  |source|line_number|line_text            |"   + EOL +
                "+----+------+-----------+---------------------+"   + EOL +
                "|1   |1     |1          |  // a comment       |"   + EOL +
                "|2   |1     |2          |  // another comment |"   + EOL +
                "+----+------+-----------+---------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+---------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text   |"   + EOL +
                "+----+------+-----------+----+---------------+"   + EOL +
                "|1   |1     |1          |1   |a comment      |"   + EOL +
                "|2   |1     |2          |1   |another comment|"   + EOL +
                "+----+------+-----------+----+---------------+", 
                selectComments().toString());    
        assertEquals("a comment"        + EOL +
                "another comment"  + EOL, 
                DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_TwoSeparatedComments()  throws Exception {
        matcher.extractComments(
                "  // a comment "           + EOL +
                "  some code"               + EOL +
                "  // another comment ");
        assertEquals(
                "+----+------+-----------+---------------------+"   + EOL +
                "|id  |source|line_number|line_text            |"   + EOL +
                "+----+------+-----------+---------------------+"   + EOL +
                "|1   |1     |1          |  // a comment       |"   + EOL +
                "|2   |1     |2          |  some code          |"   + EOL +
                "|3   |1     |3          |  // another comment |"   + EOL +
                "+----+------+-----------+---------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+---------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text   |"   + EOL +
                "+----+------+-----------+----+---------------+"   + EOL +
                "|1   |1     |1          |1   |a comment      |"   + EOL +
                "|2   |1     |3          |1   |another comment|"   + EOL +
                "+----+------+-----------+----+---------------+", 
                selectComments().toString());
        assertEquals("a comment"        + EOL +
                "another comment"  + EOL, 
                DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_MixedCodeAndOneLineComments() throws Exception {
        matcher.extractComments(
                "  some initial code"           + EOL +
                "  a second line of code "      + EOL +
                "  // a comment "               + EOL +
                "  some more code"              + EOL +
                "  // another comment "         + EOL +
                "  a final bit of code");
        assertEquals(
                "+----+------+-----------+------------------------+"   + EOL +
                "|id  |source|line_number|line_text               |"   + EOL +
                "+----+------+-----------+------------------------+"   + EOL +
                "|1   |1     |1          |  some initial code     |"   + EOL +
                "|2   |1     |2          |  a second line of code |"   + EOL +
                "|3   |1     |3          |  // a comment          |"   + EOL +
                "|4   |1     |4          |  some more code        |"   + EOL +
                "|5   |1     |5          |  // another comment    |"   + EOL +
                "|6   |1     |6          |  a final bit of code   |"   + EOL +
                "+----+------+-----------+------------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+---------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text   |"   + EOL +
                "+----+------+-----------+----+---------------+"   + EOL +
                "|1   |1     |3          |1   |a comment      |"   + EOL +
                "|2   |1     |5          |1   |another comment|"   + EOL +
                "+----+------+-----------+----+---------------+", 
                selectComments().toString());
        assertEquals("a comment"        + EOL +
                "another comment"  + EOL, 
                DefaultExtractor.commentsAsString(ywdb));
    }
    
    
    public void test_Java_MixedCodeAndOneLineComments_TwoSources() throws Exception {
        matcher.extractComments(
                "  some initial code"           + EOL +
                "  a second line of code "      + EOL +
                "  // a comment "               + EOL);
        matcher.extractComments(
                "  some more code"              + EOL +
                "  // another comment "         + EOL +
                "  a final bit of code");
        assertEquals(
                "+----+------+-----------+------------------------+"   + EOL +
                "|id  |source|line_number|line_text               |"   + EOL +
                "+----+------+-----------+------------------------+"   + EOL +
                "|1   |1     |1          |  some initial code     |"   + EOL +
                "|2   |1     |2          |  a second line of code |"   + EOL +
                "|3   |1     |3          |  // a comment          |"   + EOL +
                "|4   |2     |1          |  some more code        |"   + EOL +
                "|5   |2     |2          |  // another comment    |"   + EOL +
                "|6   |2     |3          |  a final bit of code   |"   + EOL +
                "+----+------+-----------+------------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+---------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text   |"   + EOL +
                "+----+------+-----------+----+---------------+"   + EOL +
                "|1   |1     |3          |1   |a comment      |"   + EOL +
                "|2   |2     |2          |1   |another comment|"   + EOL +
                "+----+------+-----------+----+---------------+", 
                selectComments().toString());
        assertEquals("a comment"        + EOL +
                "another comment"  + EOL, 
                DefaultExtractor.commentsAsString(ywdb));
    }
        
    public void test_Java_OnePartialLineComment()  throws Exception {
        matcher.extractComments("  some code // a comment ");
        assertEquals(
                "+----+------+-----------+-------------------------+"   + EOL +
                "|id  |source|line_number|line_text                |"   + EOL +
                "+----+------+-----------+-------------------------+"   + EOL +
                "|1   |1     |1          |  some code // a comment |"   + EOL +
                "+----+------+-----------+-------------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text|"   + EOL +
                "+----+------+-----------+----+------------+"   + EOL +
                "|1   |1     |1          |1   |a comment   |"   + EOL +
                "+----+------+-----------+----+------------+", 
                selectComments().toString());
        assertEquals("a comment" + EOL, DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_TwoPartialLineComment()  throws Exception {
        matcher.extractComments(
                "  some code // a comment " + EOL +
                "  some more code  // another comment ");
        assertEquals(
                "+----+------+-----------+-------------------------------------+"     + EOL +
                "|id  |source|line_number|line_text                            |"     + EOL +
                "+----+------+-----------+-------------------------------------+"     + EOL +
                "|1   |1     |1          |  some code // a comment             |"     + EOL +
                "|2   |1     |2          |  some more code  // another comment |"     + EOL +
                "+----+------+-----------+-------------------------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+---------------+"     + EOL +
                "|id  |source|line_number|rank|comment_text   |"     + EOL +
                "+----+------+-----------+----+---------------+"     + EOL +
                "|1   |1     |1          |1   |a comment      |"     + EOL +
                "|2   |1     |2          |1   |another comment|"     + EOL +
                "+----+------+-----------+----+---------------+", 
                selectComments().toString());
        assertEquals(
                "a comment"        + EOL +
                "another comment"  + EOL, 
                DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Java_OneFullLineComment_Delimited()  throws Exception {
        matcher.extractComments("  /* a comment */ ");
        assertEquals(
                "+----+------+-----------+------------------+"   + EOL +
                "|id  |source|line_number|line_text         |"   + EOL +
                "+----+------+-----------+------------------+"   + EOL +
                "|1   |1     |1          |  /* a comment */ |"   + EOL +
                "+----+------+-----------+------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text|"   + EOL +
                "+----+------+-----------+----+------------+"   + EOL +
                "|1   |1     |1          |1   |a comment   |"   + EOL +
                "+----+------+-----------+----+------------+", 
                selectComments().toString());
        assertEquals("a comment"    + EOL, DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Java_TwoFullLineComment_Delimited()  throws Exception {
        matcher.extractComments(
                "  /* a comment */ " + EOL +
                "  /* another comment */ ");
        assertEquals(
                "+----+------+-----------+------------------------+"   + EOL +
                "|id  |source|line_number|line_text               |"   + EOL +
                "+----+------+-----------+------------------------+"   + EOL +
                "|1   |1     |1          |  /* a comment */       |"   + EOL +
                "|2   |1     |2          |  /* another comment */ |"   + EOL +
                "+----+------+-----------+------------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+---------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text   |"   + EOL +
                "+----+------+-----------+----+---------------+"   + EOL +
                "|1   |1     |1          |1   |a comment      |"   + EOL +
                "|2   |1     |2          |1   |another comment|"   + EOL +
                "+----+------+-----------+----+---------------+", 
                selectComments().toString());
        assertEquals("a comment"        + EOL +
                "another comment"  + EOL, 
                DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_TwoLineComment_Delimited()  throws Exception {
        matcher.extractComments(
                "  /* a comment "           + EOL +
                "     another comment */ "  + EOL);
        assertEquals(
                "+----+------+-----------+------------------------+"   + EOL +
                "|id  |source|line_number|line_text               |"   + EOL +
                "+----+------+-----------+------------------------+"   + EOL +
                "|1   |1     |1          |  /* a comment          |"   + EOL +
                "|2   |1     |2          |     another comment */ |"   + EOL +
                "+----+------+-----------+------------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+---------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text   |"   + EOL +
                "+----+------+-----------+----+---------------+"   + EOL +
                "|1   |1     |1          |1   |a comment      |"   + EOL +
                "|2   |1     |2          |1   |another comment|"   + EOL +
                "+----+------+-----------+----+---------------+", 
                selectComments().toString());
        assertEquals("a comment"        + EOL +
                "another comment"  + EOL, 
                DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Java_TwoSeparatedComments_Delimited()  throws Exception {
        matcher.extractComments(
                "  /* a comment    */  "    + EOL +
                "  some code"               + EOL +
                "  /* another comment*/");
        assertEquals(
                "+----+------+-----------+----------------------+"   + EOL +
                "|id  |source|line_number|line_text             |"   + EOL +
                "+----+------+-----------+----------------------+"   + EOL +
                "|1   |1     |1          |  /* a comment    */  |"   + EOL +
                "|2   |1     |2          |  some code           |"   + EOL +
                "|3   |1     |3          |  /* another comment*/|"   + EOL +
                "+----+------+-----------+----------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+---------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text   |"   + EOL +
                "+----+------+-----------+----+---------------+"   + EOL +
                "|1   |1     |1          |1   |a comment      |"   + EOL +
                "|2   |1     |3          |1   |another comment|"   + EOL +
                "+----+------+-----------+----+---------------+", 
                selectComments().toString());
        assertEquals("a comment"        + EOL +
                "another comment"  + EOL,
                DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_TwoMultilineComments()  throws Exception {
        matcher.extractComments(
                "  /* a comment    "        + EOL +
                "      on two lines */"     + EOL +
                "  some code"               + EOL +
                "  /* another comment     " + EOL +
                "     on two lines  */ "    + EOL);
        assertEquals(
                "+----+------+-----------+-------------------------+"   + EOL +
                "|id  |source|line_number|line_text                |"   + EOL +
                "+----+------+-----------+-------------------------+"   + EOL +
                "|1   |1     |1          |  /* a comment           |"   + EOL +
                "|2   |1     |2          |      on two lines */    |"   + EOL +
                "|3   |1     |3          |  some code              |"   + EOL +
                "|4   |1     |4          |  /* another comment     |"   + EOL +
                "|5   |1     |5          |     on two lines  */    |"   + EOL +
                "+----+------+-----------+-------------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+---------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text   |"   + EOL +
                "+----+------+-----------+----+---------------+"   + EOL +
                "|1   |1     |1          |1   |a comment      |"   + EOL +
                "|2   |1     |2          |1   |on two lines   |"   + EOL +
                "|3   |1     |4          |1   |another comment|"   + EOL +
                "|4   |1     |5          |1   |on two lines   |"   + EOL +
                "+----+------+-----------+----+---------------+", 
                selectComments().toString());
        assertEquals("a comment"        + EOL +
                "on two lines"     + EOL +
                "another comment"  + EOL +
                "on two lines"     + EOL,
                DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_TwoMultilineComments_WithBlankCommentLines()  throws Exception {
        matcher.extractComments(
                "  /* a comment    "        + EOL +
                "                */"        + EOL +
                "  some code"               + EOL +
                "  /* another comment     " + EOL +
                "                      */ " + EOL);
        assertEquals(
                "+----+------+-----------+-------------------------+"   + EOL +
                "|id  |source|line_number|line_text                |"   + EOL +
                "+----+------+-----------+-------------------------+"   + EOL +
                "|1   |1     |1          |  /* a comment           |"   + EOL +
                "|2   |1     |2          |                */       |"   + EOL +
                "|3   |1     |3          |  some code              |"   + EOL +
                "|4   |1     |4          |  /* another comment     |"   + EOL +
                "|5   |1     |5          |                      */ |"   + EOL +
                "+----+------+-----------+-------------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+---------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text   |"   + EOL +
                "+----+------+-----------+----+---------------+"   + EOL +
                "|1   |1     |1          |1   |a comment      |"   + EOL +
                "|2   |1     |4          |1   |another comment|"   + EOL +
                "+----+------+-----------+----+---------------+", 
                selectComments().toString());
        assertEquals("a comment"        + EOL +
                "another comment"  + EOL, 
                DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_MixedCodeAndOneLineComments_Delimited() throws Exception {
        matcher.extractComments(
                "  some initial code"           + EOL +
                "  a second line of code "      + EOL +
                "  /* a comment  */ "           + EOL +
                "  some more code"              + EOL +
                "  /* another comment   */"     + EOL +
                "  a final bit of code");
        assertEquals(
                "+----+------+-----------+-------------------------+"   + EOL +
                "|id  |source|line_number|line_text                |"   + EOL +
                "+----+------+-----------+-------------------------+"   + EOL +
                "|1   |1     |1          |  some initial code      |"   + EOL +
                "|2   |1     |2          |  a second line of code  |"   + EOL +
                "|3   |1     |3          |  /* a comment  */       |"   + EOL +
                "|4   |1     |4          |  some more code         |"   + EOL +
                "|5   |1     |5          |  /* another comment   */|"   + EOL +
                "|6   |1     |6          |  a final bit of code    |"   + EOL +
                "+----+------+-----------+-------------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+---------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text   |"   + EOL +
                "+----+------+-----------+----+---------------+"   + EOL +
                "|1   |1     |3          |1   |a comment      |"   + EOL +
                "|2   |1     |5          |1   |another comment|"   + EOL +
                "+----+------+-----------+----+---------------+", 
                selectComments().toString());
        assertEquals("a comment"        + EOL +
                "another comment"  + EOL, 
                DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Java_MixedCodeAndOneLineComments_MultilineComments() throws Exception {
        matcher.extractComments(
                "  some initial code"           + EOL +
                "  a second line of code "      + EOL +
                "  /* a comment   "             + EOL +
                "     on two lines */ "         + EOL +
                "  some more code"              + EOL +
                "  /* another comment   "       + EOL +
                "     this one is on "          + EOL +
                "  three lines*/"               + EOL +
                "  a final bit of code");
        assertEquals(
                "+----+------+-----------+------------------------+"   + EOL +
                "|id  |source|line_number|line_text               |"   + EOL +
                "+----+------+-----------+------------------------+"   + EOL +
                "|1   |1     |1          |  some initial code     |"   + EOL +
                "|2   |1     |2          |  a second line of code |"   + EOL +
                "|3   |1     |3          |  /* a comment          |"   + EOL +
                "|4   |1     |4          |     on two lines */    |"   + EOL +
                "|5   |1     |5          |  some more code        |"   + EOL +
                "|6   |1     |6          |  /* another comment    |"   + EOL +
                "|7   |1     |7          |     this one is on     |"   + EOL +
                "|8   |1     |8          |  three lines*/         |"   + EOL +
                "|9   |1     |9          |  a final bit of code   |"   + EOL +
                "+----+------+-----------+------------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+---------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text   |"   + EOL +
                "+----+------+-----------+----+---------------+"   + EOL +
                "|1   |1     |3          |1   |a comment      |"   + EOL +
                "|2   |1     |4          |1   |on two lines   |"   + EOL +
                "|3   |1     |6          |1   |another comment|"   + EOL +
                "|4   |1     |7          |1   |this one is on |"   + EOL +
                "|5   |1     |8          |1   |three lines    |"   + EOL +
                "+----+------+-----------+----+---------------+", 
                selectComments().toString());
        assertEquals("a comment"        + EOL +
                "on two lines"     + EOL +
                "another comment"  + EOL +
                "this one is on"   + EOL +
                "three lines"      + EOL,
                DefaultExtractor.commentsAsString(ywdb));
    }
   
    public void test_Java_OnePartialLineComment_Delimited()  throws Exception {
        matcher.extractComments("  some code /* a comment*/ ");
        assertEquals(
                "+----+------+-----------+---------------------------+"   + EOL +
                "|id  |source|line_number|line_text                  |"   + EOL +
                "+----+------+-----------+---------------------------+"   + EOL +
                "|1   |1     |1          |  some code /* a comment*/ |"   + EOL +
                "+----+------+-----------+---------------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        assertEquals(
                "+----+------+-----------+----+------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text|"   + EOL +
                "+----+------+-----------+----+------------+"   + EOL +
                "|1   |1     |1          |1   |a comment   |"   + EOL +
                "+----+------+-----------+----+------------+", 
                selectComments().toString());
        assertEquals("a comment" + EOL,  DefaultExtractor.commentsAsString(ywdb));
    }
   
    public void test_Java_OnePartialLineComment_SpansTwoLines()  throws Exception {
        matcher.extractComments(
                "  some code /* a comment "                 + EOL +
                "  the rest of the comment */ more code"    + EOL);
        assertEquals(
                "+----+------+-----------+----+-----------------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text           |"   + EOL +
                "+----+------+-----------+----+-----------------------+"   + EOL +
                "|1   |1     |1          |1   |a comment              |"   + EOL +
                "|2   |1     |2          |1   |the rest of the comment|"   + EOL +
                "+----+------+-----------+----+-----------------------+", 
                selectComments().toString());
        assertEquals("a comment" + EOL +
                "the rest of the comment" + EOL, 
                DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Java_TwoPartialLineComment_Delimited()  throws Exception {
         matcher.extractComments(
                 "  some code /* a comment */" + EOL +
                 "  some more code  /* another comment */");
        assertEquals(
                "+----+------+-----------+----+---------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text   |"   + EOL +
                "+----+------+-----------+----+---------------+"   + EOL +
                "|1   |1     |1          |1   |a comment      |"   + EOL +
                "|2   |1     |2          |1   |another comment|"   + EOL +
                "+----+------+-----------+----+---------------+", 
                selectComments().toString());
        assertEquals("a comment"        + EOL +
                "another comment"  + EOL, 
                DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_Java_TwoPartialLineComments_OneOneLine()  throws Exception {
        matcher.extractComments("  code /* a comment */ more code // another comment" + EOL);
        assertEquals(
                "+----+------+-----------+----+---------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text   |"   + EOL +
                "+----+------+-----------+----+---------------+"   + EOL +
                "|1   |1     |1          |1   |a comment      |"   + EOL +
                "|2   |1     |1          |2   |another comment|"   + EOL +
                "+----+------+-----------+----+---------------+",
                selectComments().toString());
        assertEquals("a comment" + EOL +
                "another comment" + EOL,
                DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_Java_ThreeCommentsOnOneLine()  throws Exception {
        matcher.extractComments(" /* one */ /* two */ /* three */" + EOL);
        assertEquals(
                "+----+------+-----------+----+------------+"   + EOL +
                "|id  |source|line_number|rank|comment_text|"   + EOL +
                "+----+------+-----------+----+------------+"   + EOL +
                "|1   |1     |1          |1   |one         |"   + EOL +
                "|2   |1     |1          |2   |two         |"   + EOL +
                "|3   |1     |1          |3   |three       |"   + EOL +
                "+----+------+-----------+----+------------+",
                selectComments().toString());
        assertEquals("one" + EOL +
                "two" + EOL +
                "three" + EOL,
                DefaultExtractor.commentsAsString(ywdb));
    }    
}
