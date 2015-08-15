package org.yesworkflow.extract;

import static org.yesworkflow.db.Column.*;

import java.io.IOException;
import java.sql.SQLException;

import org.jooq.Record;
import org.jooq.Result;
import org.yesworkflow.Language;
import org.yesworkflow.LanguageModel;
import org.yesworkflow.YesWorkflowTestCase;
import org.yesworkflow.db.Table;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.util.FileIO;

public class TestCommentMatcher_SAS extends YesWorkflowTestCase {

    private CommentMatcher matcher;
    private YesWorkflowDB ywdb = null;

    @SuppressWarnings("unchecked")
    private Result<Record> selectCode() {
        return ywdb.jooq().select(ID, SOURCE_ID, LINE_NUMBER, LINE)
                          .from(Table.CODE)
                          .orderBy(SOURCE_ID, LINE_NUMBER)
                          .fetch();
    }

    @SuppressWarnings("unchecked")
    private Result<Record> selectComment() {
        return ywdb.jooq().select(ID, SOURCE_ID, LINE_NUMBER, RANK, TEXT)
                          .from(Table.COMMENT)
                          .orderBy(SOURCE_ID, LINE_NUMBER, RANK)
                          .fetch();
    }
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.ywdb = YesWorkflowDB.createInMemoryDB();
        LanguageModel lm = new LanguageModel(Language.SAS);
        Long sourceId = ywdb.insertSource("__reader__");
        matcher = new CommentMatcher(this.ywdb, sourceId, lm);
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

    public void test_SAS_BlankSource_MultiLine()  throws IOException, SQLException {
        String source = "           "  + EOL +
                        "           "  + EOL +
                        "           "  + EOL;
        String comments = matcher.getCommentsAsString(source);
        assertEquals("", comments);
        
        assertEquals(
                "+----+---------+-----------+-----------+"  + EOL +
                "|id  |source_id|line_number|line       |"  + EOL +
                "+----+---------+-----------+-----------+"  + EOL +
                "|1   |1        |1          |           |"  + EOL +
                "|2   |1        |2          |           |"  + EOL +
                "|3   |1        |3          |           |"  + EOL +
                "+----+---------+-----------+-----------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        
        assertEquals(0, ywdb.getRowCount(Table.COMMENT));
    }
    
    public void test_SAS_OneFullLineComment_JavaStyle()  throws IOException {
        String source = "  /* a comment */ ";
        String comments = matcher.getCommentsAsString(source);
        assertEquals("a comment"    + EOL, comments);
                
        assertEquals(
                "+----+---------+-----------+------------------+"  + EOL +
                "|id  |source_id|line_number|line              |"  + EOL +
                "+----+---------+-----------+------------------+"  + EOL +
                "|1   |1        |1          |  /* a comment */ |"  + EOL +
                "+----+---------+-----------+------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));

        assertEquals(
                "+----+---------+-----------+----+---------+"   + EOL +
                "|id  |source_id|line_number|rank|text     |"   + EOL +
                "+----+---------+-----------+----+---------+"   + EOL +
                "|1   |1        |1          |1   |a comment|"   + EOL +
                "+----+---------+-----------+----+---------+",
                FileIO.localizeLineEndings(selectComment().toString()));
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
