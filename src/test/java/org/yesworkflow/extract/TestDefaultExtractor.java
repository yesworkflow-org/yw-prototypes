package org.yesworkflow.extract;

import static org.yesworkflow.db.Column.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.jooq.Result;
import org.yesworkflow.Language;
import org.yesworkflow.LanguageModel;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.End;
import org.yesworkflow.annotations.In;
import org.yesworkflow.annotations.Out;
import org.yesworkflow.db.Table;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.db.Column.ANNOTATION;
import org.yesworkflow.db.Column.SOURCE;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.util.FileIO;
import org.yesworkflow.YesWorkflowTestCase;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TestDefaultExtractor extends YesWorkflowTestCase {

    YesWorkflowDB ywdb = null;
    DefaultExtractor extractor = null;
    LanguageModel languageModel = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.ywdb = YesWorkflowDB.createInMemoryDB();
        extractor = new DefaultExtractor(this.ywdb, super.stdoutStream, super.stderrStream);
        extractor.configure("language", Language.PYTHON);
    }

    private Result selectSourceFile() {
        
        return ywdb.jooq().select(ID, PATH)
                          .from(Table.SOURCE)
                          .fetch();
    }

    private Result joinAnnotationWithSourceFile() {
        
        return ywdb.jooq().select(ANNOTATION.ID, PATH, QUALIFIES, LINE_NUMBER, TAG, KEYWORD, VALUE, DESCRIPTION)
                          .from(Table.ANNOTATION)
                          .join(Table.SOURCE)
                          .on(ANNOTATION.SOURCE_ID.equal(SOURCE.ID))
                          .fetch();
    }
    
    public void testExtract_BlankLine() throws Exception {
        
        String source = "  " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<CommentLine> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
        
        assertEquals(
            "+----+------+"     + EOL +
            "|id  |path  |"     + EOL +
            "+----+------+"     + EOL +
            "|1   |{null}|"     + EOL +
            "+----+------+", 
            FileIO.localizeLineEndings(selectSourceFile().toString()));

        assertEquals(0, ywdb.getRowCount(Table.ANNOTATION));
    }

    public void testExtract_BlankComment() throws Exception {
        
        String source = "#  " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<CommentLine> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
        
        assertEquals(
            "+----+------+"     + EOL +
            "|id  |path  |"     + EOL +
            "+----+------+"     + EOL +
            "|1   |{null}|"     + EOL +
            "+----+------+", 
        FileIO.localizeLineEndings(selectSourceFile().toString()));

        assertEquals(0, ywdb.getRowCount(Table.ANNOTATION));
    }
    
    public void testExtract_NonComment() throws Exception {
        
        String source = "not a comment " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<CommentLine> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());

        assertEquals(
            "+----+------+"     + EOL +
            "|id  |path  |"     + EOL +
            "+----+------+"     + EOL +
            "|1   |{null}|"     + EOL +
            "+----+------+", 
        FileIO.localizeLineEndings(selectSourceFile().toString()));

        assertEquals(0, ywdb.getRowCount(Table.ANNOTATION));
    }
    

    public void testExtract_NonYWComment() throws Exception {
        
        String source = "# a comment " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<CommentLine> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());

        assertEquals(
            "+----+------+"     + EOL +
            "|id  |path  |"     + EOL +
            "+----+------+"     + EOL +
            "|1   |{null}|"     + EOL +
            "+----+------+", 
        FileIO.localizeLineEndings(selectSourceFile().toString()));

        assertEquals(0, ywdb.getRowCount(Table.ANNOTATION));
    }
    
    public void testExtract_NonYWComment_WithAtSymbol() throws Exception {
        
        String source = "# a comment with an @ symbol in it " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<CommentLine> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());

        assertEquals(
                "+----+------+"     + EOL +
                "|id  |path  |"     + EOL +
                "+----+------+"     + EOL +
                "|1   |{null}|"     + EOL +
                "+----+------+", 
            FileIO.localizeLineEndings(selectSourceFile().toString()));

            assertEquals(0, ywdb.getRowCount(Table.ANNOTATION));    
    }

    public void testExtract_GetCommentLines_MultipleComments_Hash() throws Exception {
        
        String source = 
                "## @begin step   " + EOL +
                "  some code "      + EOL +
                "   # @in x  "      + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                " #    @out y"      + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                " ##    @end step"  + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<CommentLine> commentLines = extractor.getLines();
        assertEquals(4,commentLines.size());
        assertEquals("@begin step", commentLines.get(0).text);
        assertEquals("@in x", commentLines.get(1).text);
        assertEquals("@out y", commentLines.get(2).text);
        assertEquals("@end step", commentLines.get(3).text);
        
        assertEquals(
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+"  + EOL +
            "|annotation.id|path  |qualifies|line_number|tag  |keyword|value|description|"  + EOL +
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+"  + EOL +
            "|1            |{null}|{null}   |1          |BEGIN|@begin |step |{null}     |"  + EOL +
            "|2            |{null}|{null}   |3          |IN   |@in    |x    |{null}     |"  + EOL +
            "|3            |{null}|{null}   |6          |OUT  |@out   |y    |{null}     |"  + EOL +
            "|4            |{null}|{null}   |9          |END  |@end   |step |{null}     |"  + EOL +
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+", 
            FileIO.localizeLineEndings(joinAnnotationWithSourceFile().toString()));
    }

    public void testExtract_GetCommentLines_MultipleComments_Slash() throws Exception {
        
        extractor = new DefaultExtractor(this.ywdb, super.stdoutStream, super.stderrStream);
        extractor.configure("comment", "//");

        String source = 
                "// @begin step   " + EOL +
                "  some code "      + EOL +
                "   // @in x  "     + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                " //    @out y"     + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                " //    @end step"  + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<CommentLine> commentLines = extractor.getLines();
        assertEquals(4, commentLines.size());
        assertEquals("@begin step", commentLines.get(0).text);
        assertEquals("@in x", commentLines.get(1).text);
        assertEquals("@out y", commentLines.get(2).text);
        assertEquals("@end step", commentLines.get(3).text);

        assertEquals(
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+"  + EOL +
            "|annotation.id|path  |qualifies|line_number|tag  |keyword|value|description|"  + EOL +
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+"  + EOL +
            "|1            |{null}|{null}   |1          |BEGIN|@begin |step |{null}     |"  + EOL +
            "|2            |{null}|{null}   |3          |IN   |@in    |x    |{null}     |"  + EOL +
            "|3            |{null}|{null}   |6          |OUT  |@out   |y    |{null}     |"  + EOL +
            "|4            |{null}|{null}   |9          |END  |@end   |step |{null}     |"  + EOL +
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+", 
            FileIO.localizeLineEndings(joinAnnotationWithSourceFile().toString()));
    }

    public void testExtract_GetComments_MultipleComments() throws Exception {
        
        String source = 
                "## @begin step   " + EOL +
                "  some code "      + EOL +
                "   # @in x  "      + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                " #    @param y"    + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                "    #  @end step"  + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<String> comments = extractor.getComments();

        assertEquals(4, comments.size());
        assertEquals("@begin step", comments.get(0));
        assertEquals("@in x", comments.get(1));
        assertEquals("@param y", comments.get(2));
        assertEquals("@end step", comments.get(3));

        assertEquals(
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+"  + EOL +
            "|annotation.id|path  |qualifies|line_number|tag  |keyword|value|description|"  + EOL +
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+"  + EOL +
            "|1            |{null}|{null}   |1          |BEGIN|@begin |step |{null}     |"  + EOL +
            "|2            |{null}|{null}   |3          |IN   |@in    |x    |{null}     |"  + EOL +
            "|3            |{null}|{null}   |6          |PARAM|@param |y    |{null}     |"  + EOL +
            "|4            |{null}|{null}   |9          |END  |@end   |step |{null}     |"  + EOL +
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+", 
            FileIO.localizeLineEndings(joinAnnotationWithSourceFile().toString()));
    }
    
    public void testExtract_GetComments_MultipleComments_WithAliasesOnSameLines() throws Exception {
        
        String source = 
                "## @begin step   " 	   + EOL +
                "  some code "      	   + EOL +
                "   # @in x @as horiz "    + EOL +
                "     more code"    	   + EOL +
                "     more code"    	   + EOL +
                " #    @param y @as vert"  + EOL +
                "     more code"    	   + EOL +
                "     more code"    	   + EOL +
                "    #  @end step"  	   + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<String> comments = extractor.getComments();

        assertEquals(6, comments.size());
        assertEquals("@begin step", comments.get(0));
        assertEquals("@in x", comments.get(1));
        assertEquals("@as horiz", comments.get(2));
        assertEquals("@param y", comments.get(3));
        assertEquals("@as vert", comments.get(4));
        assertEquals("@end step", comments.get(5));        

        assertEquals(
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+"  + EOL +
            "|annotation.id|path  |qualifies|line_number|tag  |keyword|value|description|"  + EOL +
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+"  + EOL +
            "|1            |{null}|{null}   |1          |BEGIN|@begin |step |{null}     |"  + EOL +
            "|2            |{null}|{null}   |3          |IN   |@in    |x    |{null}     |"  + EOL +
            "|3            |{null}|2        |3          |AS   |@as    |horiz|{null}     |"  + EOL +
            "|4            |{null}|{null}   |6          |PARAM|@param |y    |{null}     |"  + EOL +
            "|5            |{null}|4        |6          |AS   |@as    |vert |{null}     |"  + EOL +
            "|6            |{null}|{null}   |9          |END  |@end   |step |{null}     |"  + EOL +
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+", 
            FileIO.localizeLineEndings(joinAnnotationWithSourceFile().toString()));
    }
    
    
    public void testExtract_GetComments_MultipleComments_WithAliasesOnDifferentLines() throws Exception {
        
        String source = 
                "## @begin step   " 	+ EOL +
                "  some code "      	+ EOL +
                "   # @in x" 			+ EOL +
                "    # @as horiz "		+ EOL +
                "     more code"    	+ EOL +
                "     more code"    	+ EOL +
                " #    @param y  "		+ EOL +
                "  #@as vert"			+ EOL +
                "     more code"    	+ EOL +
                "     more code"    	+ EOL +
                "    #  @end step"  	+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<String> comments = extractor.getComments();

        assertEquals(6, comments.size());
        assertEquals("@begin step", comments.get(0));
        assertEquals("@in x", comments.get(1));
        assertEquals("@as horiz", comments.get(2));
        assertEquals("@param y", comments.get(3));
        assertEquals("@as vert", comments.get(4));
        assertEquals("@end step", comments.get(5));        

        assertEquals(
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+"  + EOL +
            "|annotation.id|path  |qualifies|line_number|tag  |keyword|value|description|"  + EOL +
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+"  + EOL +
            "|1            |{null}|{null}   |1          |BEGIN|@begin |step |{null}     |"  + EOL +
            "|2            |{null}|{null}   |3          |IN   |@in    |x    |{null}     |"  + EOL +
            "|3            |{null}|2        |4          |AS   |@as    |horiz|{null}     |"  + EOL +
            "|4            |{null}|{null}   |7          |PARAM|@param |y    |{null}     |"  + EOL +
            "|5            |{null}|4        |8          |AS   |@as    |vert |{null}     |"  + EOL +
            "|6            |{null}|{null}   |11         |END  |@end   |step |{null}     |"  + EOL +
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+", 
            FileIO.localizeLineEndings(joinAnnotationWithSourceFile().toString()));
    }
    
    public void testExtract_GetComments_MultipleCommentsOnOneLine() throws Exception {
        
        String source = "# @begin step @in x @as horiz @param y @as vert @end step";

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<String> comments = extractor.getComments();

        assertEquals(6, comments.size());
        assertEquals("@begin step", comments.get(0));
        assertEquals("@in x", comments.get(1));
        assertEquals("@as horiz", comments.get(2));
        assertEquals("@param y", comments.get(3));
        assertEquals("@as vert", comments.get(4));
        assertEquals("@end step", comments.get(5));
        
        assertEquals(
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+"  + EOL +
            "|annotation.id|path  |qualifies|line_number|tag  |keyword|value|description|"  + EOL +
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+"  + EOL +
            "|1            |{null}|{null}   |1          |BEGIN|@begin |step |{null}     |"  + EOL +
            "|2            |{null}|{null}   |1          |IN   |@in    |x    |{null}     |"  + EOL +
            "|3            |{null}|2        |1          |AS   |@as    |horiz|{null}     |"  + EOL +
            "|4            |{null}|{null}   |1          |PARAM|@param |y    |{null}     |"  + EOL +
            "|5            |{null}|4        |1          |AS   |@as    |vert |{null}     |"  + EOL +
            "|6            |{null}|{null}   |1          |END  |@end   |step |{null}     |"  + EOL +
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+", 
            FileIO.localizeLineEndings(joinAnnotationWithSourceFile().toString()));
        
        
    }

    public void testExtract_GetAnnotations_MultipleComments() throws Exception {
        
        String source = 
                "## @begin step   " + EOL +
                "  some code "      + EOL +
                "   # @in x  "      + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                " #    @out y"      + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                "    #  @end step"  + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<Annotation> annotations = extractor.getAnnotations();

        assertEquals(4, annotations.size());
        
        Begin begin = (Begin) annotations.get(0);
        assertEquals("step", begin.name);
        assertNull(begin.description());

        In in = (In) annotations.get(1);
        assertEquals("x", in.name);
        assertEquals("x", in.binding());
        assertNull(in.description());

        Out out = (Out) annotations.get(2);
        assertEquals("y", out.name);
        assertEquals("y", out.binding());
        assertNull(out.description());

        End end = (End) annotations.get(3);
        assertEquals("step", end.name);
        assertNull(end.description());

        assertEquals(
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+"  + EOL +
            "|annotation.id|path  |qualifies|line_number|tag  |keyword|value|description|"  + EOL +
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+"  + EOL +
            "|1            |{null}|{null}   |1          |BEGIN|@begin |step |{null}     |"  + EOL +
            "|2            |{null}|{null}   |3          |IN   |@in    |x    |{null}     |"  + EOL +
            "|3            |{null}|{null}   |6          |OUT  |@out   |y    |{null}     |"  + EOL +
            "|4            |{null}|{null}   |9          |END  |@end   |step |{null}     |"  + EOL +
            "+-------------+------+---------+-----------+-----+-------+-----+-----------+", 
            FileIO.localizeLineEndings(joinAnnotationWithSourceFile().toString()));    
    }
}