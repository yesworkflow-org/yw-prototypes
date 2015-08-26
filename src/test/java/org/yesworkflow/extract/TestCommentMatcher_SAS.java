package org.yesworkflow.extract;

import static org.yesworkflow.db.Column.*;

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
        return ywdb.jooq().select(ID, SOURCE_ID, LINE_NUMBER, LINE_TEXT)
                          .from(Table.SOURCE_LINE)
                          .orderBy(SOURCE_ID, LINE_NUMBER)
                          .fetch();
    }

    @SuppressWarnings("unchecked")
    private Result<Record> selectComment() {
        return ywdb.jooq().select(ID, SOURCE_ID, LINE_NUMBER, RANK_IN_LINE, COMMENT_TEXT)
                          .from(Table.COMMENT)
                          .orderBy(SOURCE_ID, LINE_NUMBER, RANK_IN_LINE)
                          .fetch();
    }
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.ywdb = YesWorkflowDB.createInMemoryDB();
        LanguageModel lm = new LanguageModel(Language.SAS);
        matcher = new CommentMatcher(this.ywdb, lm);
    }

    public void test_SAS_EmptySource()  throws Exception {
        String source = "";
        matcher.extractComments(source);
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_BlankSource_OneLine()  throws Exception {
        String source = "           ";
        matcher.extractComments(source);
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_BlankSource_MultiLine()  throws Exception, SQLException {
        String source = "           "  + EOL +
                        "           "  + EOL +
                        "           "  + EOL;
        matcher.extractComments(source);
        assertEquals("", DefaultExtractor.commentsAsString(ywdb));
        
        assertEquals(
                "+----+------+-----------+-----------+"  + EOL +
                "|id  |source|line_number|line_text  |"  + EOL +
                "+----+------+-----------+-----------+"  + EOL +
                "|1   |1     |1          |           |"  + EOL +
                "|2   |1     |2          |           |"  + EOL +
                "|3   |1     |3          |           |"  + EOL +
                "+----+------+-----------+-----------+",
                FileIO.localizeLineEndings(selectCode().toString()));
        
        assertEquals(0, ywdb.getRowCount(Table.COMMENT));
    }
    
    public void test_SAS_OneFullLineComment_JavaStyle()  throws Exception {
        String source = "  /* a comment */ ";
        matcher.extractComments(source);
        assertEquals("a comment"    + EOL, DefaultExtractor.commentsAsString(ywdb));
                
        assertEquals(
                "+----+------+-----------+------------------+"  + EOL +
                "|id  |source|line_number|line_text         |"  + EOL +
                "+----+------+-----------+------------------+"  + EOL +
                "|1   |1     |1          |  /* a comment */ |"  + EOL +
                "+----+------+-----------+------------------+",
                FileIO.localizeLineEndings(selectCode().toString()));

        assertEquals(
                "+----+------+-----------+------------+------------+"  + EOL +
                "|id  |source|line_number|rank_in_line|comment_text|"  + EOL +
                "+----+------+-----------+------------+------------+"  + EOL +
                "|1   |1     |1          |1           |a comment   |"  + EOL +
                "+----+------+-----------+------------+------------+",
                FileIO.localizeLineEndings(selectComment().toString()));
    }

    public void test_SAS_OneFullLineComment_SASStyle()  throws Exception {
        String source = "  * a comment ; ";
        matcher.extractComments(source);
        assertEquals("a comment"    + EOL, DefaultExtractor.commentsAsString(ywdb));
    }

    
    public void test_SAS_TwoFullLineComment_JavaStyle()  throws Exception {
        String source = "  /* a comment */ " + EOL +
                        "  /* another comment */ ";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_TwoFullLineComment_SASStyle()  throws Exception {
        String source = "  * a comment ; " + EOL +
                        "  * another comment ; ";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_TwoLineComment_JavaStyle()  throws Exception {
        String source = "  /* a comment "           + EOL +
                        "     another comment */ "  + EOL;        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_TwoLineComment_SASStyle()  throws Exception {
        String source = "  * a comment "           + EOL +
                        "     another comment ; "  + EOL;        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_SAS_TwoSeparatedComments_JavaStyle()  throws Exception {
        String source = "  /* a comment    */  "    + EOL +
                        "  some code"               + EOL +
                        "  /* another comment*/";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_TwoSeparatedComments_SASStyle()  throws Exception {
        String source = "  * a comment    ;  "    + EOL +
                        "  some code"               + EOL +
                        "  * another comment;";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_TwoMultilineComments_JavaStyle()  throws Exception {
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
    
    public void test_SAS_TwoMultilineComments_SASStyle()  throws Exception {
        String source = "  * a comment    "        + EOL +
                        "      on two lines ;"     + EOL +
                        "  some code"              + EOL +
                        "  * another comment     " + EOL +
                        "     on two lines  ; "    + EOL;        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "on two lines"     + EOL +
                     "another comment"  + EOL +
                     "on two lines"     + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_TwoMultilineComments_WithBlankCommentLines_JavaStyle()  throws Exception {
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

    public void test_SAS_TwoMultilineComments_WithBlankCommentLines_SASStyle()  throws Exception {
        String source = "  * a comment    "        + EOL +
                        "                ;"        + EOL +
                        "  some code"              + EOL +
                        "  * another comment     " + EOL +
                        "                      ; " + EOL;        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }   
    
    public void test_SAS_MixedCodeAndOneLineComments_Delimited_JavaStyle() throws Exception {
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
    
    public void test_SAS_MixedCodeAndOneLineComments_Delimited_SASStyle() throws Exception {
        String source = "  some initial code"           + EOL +
                        "  a second line of code "      + EOL +
                        "  * a comment  ;   "           + EOL +
                        "  some more code"              + EOL +
                        "  * another comment   ;"       + EOL +
                        "  a final bit of code";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_SAS_MixedCodeAndOneLineComments_MultilineComments_JavaStyle() throws Exception {
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
    
    public void test_SAS_MixedCodeAndOneLineComments_MultilineComments_SASStyle() throws Exception {
        String source = "  some initial code"           + EOL +
                        "  a second line of code "      + EOL +
                        "  * a comment   "              + EOL +
                        "     on two lines ; "          + EOL +
                        "  some more code"              + EOL +
                        "  * another comment   "        + EOL +
                        "     this one is on "          + EOL +
                        "  three lines;"                + EOL +
                        "  a final bit of code";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "on two lines"     + EOL +
                     "another comment"  + EOL +
                     "this one is on"   + EOL +
                     "three lines"      + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }
   
    public void test_SAS_OnePartialLineComment_JavaStyle()  throws Exception {
        String source = "  some code /* a comment*/ ";
        matcher.extractComments(source);
        assertEquals("a comment" + EOL, DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_OnePartialLineComment_SASStyle()  throws Exception {
        String source = "  some code * a comment; ";
        matcher.extractComments(source);
        assertEquals("a comment" + EOL, DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_OnePartialLineComment_SpansTwoLines_JavaStyle()  throws Exception {
        String source = "  some code /* a comment "                 + EOL +
                        "  the rest of the comment */ more code"    + EOL;
        matcher.extractComments(source);
        assertEquals("a comment" + EOL +
                     "the rest of the comment" + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_OnePartialLineComment_SpansTwoLines_SASStyle()  throws Exception {
        String source = "  some code * a comment "                 + EOL +
                        "  the rest of the comment ; more code"    + EOL;
        matcher.extractComments(source);
        assertEquals("a comment" + EOL +
                     "the rest of the comment" + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_TwoPartialLineComment_JavaStyle()  throws Exception {
        String source = "  some code /* a comment */" + EOL +
                        "  some more code  /* another comment */";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_TwoPartialLineComment_SASStyle()  throws Exception {
        String source = "  some code * a comment ;" + EOL +
                        "  some more code  * another comment ;";        
        matcher.extractComments(source);
        assertEquals("a comment"        + EOL +
                     "another comment"  + EOL, 
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_TwoPartialLineComments_JavaStyle()  throws Exception {
        String source = "  code /* a comment */ more code /* another comment */" + EOL;
        matcher.extractComments(source);
        assertEquals("a comment" + EOL +
                     "another comment" + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_TwoPartialLineComments_SASStyle()  throws Exception {
        String source = "  code * a comment ; more code * another comment ;" + EOL;
        matcher.extractComments(source);
        assertEquals("a comment" + EOL +
                     "another comment" + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_TwoPartialLineComments_MixedStyle()  throws Exception {
        String source = "  code * a comment */ more code ; /* another comment ; */" + EOL;
        matcher.extractComments(source);
        assertEquals("a comment */ more code" + EOL +
                     "another comment ;" + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_SAS_ThreeCommentsOnOneLine_JavaStyle()  throws Exception {
        String source = " /* one */ /* two */ /* three */" + EOL;
        matcher.extractComments(source);
        assertEquals("one" + EOL +
                     "two" + EOL +
                     "three" + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }
    
    public void test_SAS_ThreeCommentsOnOneLine_MixedStyles()  throws Exception {
        String source = " /* one */ /* two ; /* three */" + EOL;
        matcher.extractComments(source);
        assertEquals("one" + EOL +
                     "two ; /* three" + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }

    public void test_SAS_ThreeCommentsOnOneLine_SASStyle()  throws Exception {
        String source = " * one ; * two ; * three ;" + EOL;
        matcher.extractComments(source);
        assertEquals("one" + EOL +
                     "two" + EOL +
                     "three" + EOL,
                     DefaultExtractor.commentsAsString(ywdb));
    }    
}
