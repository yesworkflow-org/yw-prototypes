package org.yesworkflow.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.jooq.impl.DSL.field;

import org.yesworkflow.db.Table;
import org.yesworkflow.util.FileIO;

import static org.yesworkflow.db.Column.*;

import org.yesworkflow.db.View;
import org.jooq.Result;
import org.yesworkflow.YesWorkflowTestCase;

@SuppressWarnings("unchecked")
public class TestYesWorkflowDB extends YesWorkflowTestCase {
    
    private Path testDirectoryPath;
    private Path testDBFilePath;
    private YesWorkflowDB ywdb;
    private boolean useInMemoryDB = true;
    
    private Long[] sourceId = new Long[4];
    private Long[] sourceLineId = new Long[5];
    private Long[] commentId = new Long[5];
    private Long[] annotationId = new Long[5];
    private Long[] programBlockId = new Long[3];
    private Long[] codeBlockId = new Long[3];
    private Long[] signatureId = new Long[3];
    
    @Override
    public void setUp() throws Exception {
        if (useInMemoryDB) {
            ywdb = YesWorkflowDB.createInMemoryDB();
        } else {
            testDirectoryPath = getTestDirectory("TestYesWorkflowDB");
            testDBFilePath = testDirectoryPath.resolve("test.db");
            if (testDBFilePath.toFile().exists()) {
                Files.delete(testDBFilePath);
            }
            ywdb = YesWorkflowDB.openFileDB(testDBFilePath);
        }
    }

    @Override
    public void tearDown() throws SQLException {
        ywdb.close();
    }
    
    private void insertSources() throws SQLException {
        sourceId[1] = ywdb.insertSource("path1");
        sourceId[2] = ywdb.insertSource("path2");
        sourceId[3] = ywdb.insertSource("path3");
    }

    private void insertSourceLines() throws SQLException {
        sourceLineId[1] = ywdb.insertSourceLine(sourceId[1], 1L, "# @begin prog1");
        sourceLineId[2] = ywdb.insertSourceLine(sourceId[1], 2L, "# @end prog1");
        sourceLineId[3] = ywdb.insertSourceLine(sourceId[2], 1L, "# @begin prog2");
        sourceLineId[4] = ywdb.insertSourceLine(sourceId[2], 2L, "# @end prog2");
    }
    
    private void insertComments() throws SQLException {
        commentId[1] = ywdb.insertComment(sourceId[1], 1L, 1L, "@begin prog1");
        commentId[2] = ywdb.insertComment(sourceId[1], 2L, 1L, "@end prog1");
        commentId[3] = ywdb.insertComment(sourceId[2], 1L, 1L, "@begin prog2");
        commentId[4] = ywdb.insertComment(sourceId[2], 2L, 1L, "@end prog2");
    }
    
    private void insertAnnotations() throws SQLException {
        annotationId[1] = ywdb.insertAnnotation(null, commentId[1], 1L, "BEGIN", "@begin", "prog1", null);
        annotationId[2] = ywdb.insertAnnotation(null, commentId[2], 1L, "END",   "@end",   "prog1", null);
        annotationId[3] = ywdb.insertAnnotation( null, commentId[3], 1L, "BEGIN", "@begin", "prog2", null);
        annotationId[4] = ywdb.insertAnnotation( null, commentId[4], 1L, "END",   "@end",   "prog2", null);
    }

    private void insertProgramBlocks() throws SQLException {
        programBlockId[1] = ywdb.insertProgramBlock(null, annotationId[1], annotationId[2], "prog1", "prog1", false, false);
        programBlockId[2] = ywdb.insertProgramBlock(null, annotationId[3], annotationId[4], "prog2", "prog2", false, false);
    }

    private void insertCodeBlocks() throws SQLException {
        codeBlockId[1] = ywdb.insertCodeBlock(1L, 2L, "prog1", null);
        codeBlockId[2] = ywdb.insertCodeBlock(3L, 4L, "prog2", null);
    }

    private void insertSignature() throws SQLException {
        signatureId[1] = ywdb.insertSignature("IN", "x", "x_value", null, "prog1");
        signatureId[2] = ywdb.insertSignature("OUT", "y", "y_value", null, "prog1");
    }

    private void insertDefaultProgramBlocks() throws SQLException {
        programBlockId[1] = ywdb.insertDefaultProgramBlock(null);
        programBlockId[2] = ywdb.insertDefaultProgramBlock(null);
    }

    private void updateProgramBlocks() throws SQLException {
         ywdb.updateProgramBlock(programBlockId[1], annotationId[1], annotationId[2], "prog1", "prog1", false, false);
         ywdb.updateProgramBlock(programBlockId[2], annotationId[3], annotationId[4], "prog2", "prog2", false, false);
    }

    public void testCreateDBTables() throws Exception {
        assertTrue(ywdb.hasTable(Table.SOURCE));
    }
    
    @SuppressWarnings("rawtypes")
    public void testInsertSource() throws Exception {
        
        insertSources();
        assertEquals(3, ywdb.getRowCount(Table.SOURCE));
        
        Result r = ywdb.jooq.select(ID, PATH)
                            .from(Table.SOURCE)
                            .fetch();
        
        assertEquals(
            "+----+-----+"  + EOL +
            "|id  |path |"  + EOL +
            "+----+-----+"  + EOL +
            "|1   |path1|"  + EOL +
            "|2   |path2|"  + EOL +
            "|3   |path3|"  + EOL +
            "+----+-----+",
            FileIO.localizeLineEndings(r.toString()));
    }
    
    @SuppressWarnings("rawtypes")
    public void testInsertSourceLine() throws Exception {
        
        insertSources();
        insertSourceLines();
        
        assertEquals(4, ywdb.getRowCount(Table.SOURCE_LINE));
        
        Result r = ywdb.jooq.select(ID, SOURCE_ID, LINE_NUMBER, LINE_TEXT)
                            .from(Table.SOURCE_LINE)
                            .fetch();
        
        assertEquals(
            "+----+------+-----------+--------------+"   + EOL + 
            "|id  |source|line_number|line_text     |"   + EOL + 
            "+----+------+-----------+--------------+"   + EOL + 
            "|1   |1     |1          |# @begin prog1|"   + EOL + 
            "|2   |1     |2          |# @end prog1  |"   + EOL + 
            "|3   |2     |1          |# @begin prog2|"   + EOL + 
            "|4   |2     |2          |# @end prog2  |"   + EOL + 
            "+----+------+-----------+--------------+",
            FileIO.localizeLineEndings(r.toString()));
        
        Result r2 = ywdb.jooq.select(SOURCE_LINE.ID, SOURCE.PATH, LINE_NUMBER, LINE_TEXT)
                             .from(Table.SOURCE_LINE)
                             .join(Table.SOURCE).on(SOURCE_LINE.SOURCE_ID.equal(SOURCE.ID))
                             .fetch();
    
        assertEquals(
            "+--------------+-----------+-----------+--------------+"   + EOL + 
            "|source_line.id|source.path|line_number|line_text     |"   + EOL + 
            "+--------------+-----------+-----------+--------------+"   + EOL + 
            "|1             |path1      |1          |# @begin prog1|"   + EOL + 
            "|2             |path1      |2          |# @end prog1  |"   + EOL + 
            "|3             |path2      |1          |# @begin prog2|"   + EOL + 
            "|4             |path2      |2          |# @end prog2  |"   + EOL + 
            "+--------------+-----------+-----------+--------------+",
            FileIO.localizeLineEndings(r2.toString()));
    }

    @SuppressWarnings("rawtypes")
    public void testInsertComment() throws Exception {
        
        insertSources();
        insertComments();
        
        assertEquals(4, ywdb.getRowCount(Table.COMMENT));
        
        Result r1 = ywdb.jooq.select(ID, SOURCE_ID, LINE_NUMBER, RANK_IN_LINE, COMMENT_TEXT)
                             .from(Table.COMMENT)
                             .fetch();
        
        assertEquals(
            "+----+------+-----------+------------+------------+"    + EOL +
            "|id  |source|line_number|rank_in_line|comment_text|"    + EOL +
            "+----+------+-----------+------------+------------+"    + EOL +
            "|1   |1     |1          |1           |@begin prog1|"    + EOL +
            "|2   |1     |2          |1           |@end prog1  |"    + EOL +
            "|3   |2     |1          |1           |@begin prog2|"    + EOL +
            "|4   |2     |2          |1           |@end prog2  |"    + EOL +
            "+----+------+-----------+------------+------------+",
            FileIO.localizeLineEndings(r1.toString()));

        Result r2 = ywdb.jooq.select(COMMENT.ID, PATH, LINE_NUMBER, RANK_IN_LINE, COMMENT_TEXT)
                             .from(Table.COMMENT)
                             .join(Table.SOURCE).on(SOURCE_ID.equal(SOURCE.ID))
                             .fetch();
                    
        assertEquals(
            "+----------+-----+-----------+------------+------------+"    + EOL +
            "|comment.id|path |line_number|rank_in_line|comment_text|"    + EOL +
            "+----------+-----+-----------+------------+------------+"    + EOL +
            "|1         |path1|1          |1           |@begin prog1|"    + EOL +
            "|2         |path1|2          |1           |@end prog1  |"    + EOL +
            "|3         |path2|1          |1           |@begin prog2|"    + EOL +
            "|4         |path2|2          |1           |@end prog2  |"    + EOL +
            "+----------+-----+-----------+------------+------------+",
            FileIO.localizeLineEndings(r2.toString()));
    }
    
    @SuppressWarnings("rawtypes")
    public void testInsertAnnotation() throws Exception {
        
        insertSources();
        insertComments();
        insertAnnotations();
        
        assertEquals(4, ywdb.getRowCount(Table.ANNOTATION));
        
        Result r1 = ywdb.jooq.select(ID, QUALIFIES, COMMENT_ID, RANK_IN_COMMENT, TAG, KEYWORD, VALUE, DESCRIPTION)
                             .from(Table.ANNOTATION)
                             .fetch();

        assertEquals(
            "+----+---------+-------+---------------+-----+-------+-----+-----------+"   + EOL +
            "|id  |qualifies|comment|rank_in_comment|tag  |keyword|value|description|"   + EOL +
            "+----+---------+-------+---------------+-----+-------+-----+-----------+"   + EOL +
            "|1   |{null}   |1      |1              |BEGIN|@begin |prog1|{null}     |"   + EOL +
            "|2   |{null}   |2      |1              |END  |@end   |prog1|{null}     |"   + EOL +
            "|3   |{null}   |3      |1              |BEGIN|@begin |prog2|{null}     |"   + EOL +
            "|4   |{null}   |4      |1              |END  |@end   |prog2|{null}     |"   + EOL +
            "+----+---------+-------+---------------+-----+-------+-----+-----------+",
            FileIO.localizeLineEndings(r1.toString()));
    
        Result r2 = ywdb.jooq.select(ANNOTATION.ID, QUALIFIES, SOURCE.PATH, LINE_NUMBER, 
                                     COMMENT.RANK_IN_LINE, COMMENT.COMMENT_TEXT.as(field("comment_text")), TAG, KEYWORD, 
                                     VALUE, DESCRIPTION)
                             .from(Table.ANNOTATION)
                             .join(Table.COMMENT).on(ANNOTATION.COMMENT_ID.equal(COMMENT.ID))
                             .join(Table.SOURCE).on(COMMENT.SOURCE_ID.equal(SOURCE.ID))
                             .fetch();
        assertEquals(
            "+-------------+---------+-----------+-----------+--------------------+------------+-----+-------+-----+-----------+"    + EOL +
            "|annotation.id|qualifies|source.path|line_number|comment.rank_in_line|comment_text|tag  |keyword|value|description|"    + EOL +
            "+-------------+---------+-----------+-----------+--------------------+------------+-----+-------+-----+-----------+"    + EOL +
            "|1            |{null}   |path1      |1          |1                   |@begin prog1|BEGIN|@begin |prog1|{null}     |"    + EOL +
            "|2            |{null}   |path1      |2          |1                   |@end prog1  |END  |@end   |prog1|{null}     |"    + EOL +
            "|3            |{null}   |path2      |1          |1                   |@begin prog2|BEGIN|@begin |prog2|{null}     |"    + EOL +
            "|4            |{null}   |path2      |2          |1                   |@end prog2  |END  |@end   |prog2|{null}     |"    + EOL +
            "+-------------+---------+-----------+-----------+--------------------+------------+-----+-------+-----+-----------+",
            FileIO.localizeLineEndings(r2.toString()));
    }
    
    @SuppressWarnings("rawtypes")
    public void testInsertProgram() throws Exception {
        
        insertSources();
        insertComments();
        insertAnnotations();
        insertProgramBlocks();
        
        assertEquals(2, ywdb.getRowCount(Table.PROGRAM_BLOCK));
        
        Result r1 = ywdb.jooq.select(ID, IN_PROGRAM_BLOCK, BEGIN_ANNOTATION_ID, END_ANNOTATION_ID, NAME,
                                     QUALIFIED_NAME, IS_WORKFLOW, IS_FUNCTION)
                             .from(Table.PROGRAM_BLOCK)
                             .fetch();

        assertEquals(
            "+----+----------------+----------------+--------------+-----+--------------+-----------+-----------+"  + EOL +
            "|id  |in_program_block|begin_annotation|end_annotation|name |qualified_name|is_workflow|is_function|"  + EOL +
            "+----+----------------+----------------+--------------+-----+--------------+-----------+-----------+"  + EOL +
            "|1   |{null}          |1               |2             |prog1|prog1         |0          |0          |"  + EOL +
            "|2   |{null}          |3               |4             |prog2|prog2         |0          |0          |"  + EOL +
            "+----+----------------+----------------+--------------+-----+--------------+-----------+-----------+",
            FileIO.localizeLineEndings(r1.toString()));
    }

    @SuppressWarnings("rawtypes")
    public void testInsertCodeBlock() throws Exception {

        insertSources();
        insertComments();
        insertAnnotations();
        insertCodeBlocks();

        assertEquals(2, ywdb.getRowCount(Table.CODE_BLOCK));

        Result r1 = ywdb.jooq.select(ID, BEGIN_LINE, END_LINE, NAME, DESCRIPTION)
                .from(Table.CODE_BLOCK)
                .fetch();

        assertEquals(
                "+----+----------+--------+-----+-----------+"  + EOL +
                "|id  |begin_line|end_line|name |description|"  + EOL +
                "+----+----------+--------+-----+-----------+"  + EOL +
                "|1   |1         |2       |prog1|{null}     |"  + EOL +
                "|2   |3         |4       |prog2|{null}     |"  + EOL +
                "+----+----------+--------+-----+-----------+",
                FileIO.localizeLineEndings(r1.toString()));
    }

    @SuppressWarnings("rawtypes")
    public void testInsertSignature() throws Exception {

        insertSources();
        insertComments();
        insertAnnotations();
        insertSignature();

        assertEquals(2, ywdb.getRowCount(Table.SIGNATURE));

        Result r1 = ywdb.jooq.select(ID, INPUT_OR_OUTPUT, VARIABLE, ALIAS, URI, IN_CODE_BLOCK)
                .from(Table.SIGNATURE)
                .fetch();

        assertEquals(
                "+----+---------------+--------+-------+------+-------------+"  + EOL +
                "|id  |input_or_output|variable|alias  |uri   |in_code_block|"  + EOL +
                "+----+---------------+--------+-------+------+-------------+"  + EOL +
                "|1   |IN             |x       |x_value|{null}|prog1        |"  + EOL +
                "|2   |OUT            |y       |y_value|{null}|prog1        |"  + EOL +
                "+----+---------------+--------+-------+------+-------------+",
                FileIO.localizeLineEndings(r1.toString()));
    }
    
    @SuppressWarnings("rawtypes")
    public void testInsertDefaultProgram() throws Exception {
        
        insertSources();
        insertComments();
        insertAnnotations();
        insertDefaultProgramBlocks();
        
        assertEquals(2, ywdb.getRowCount(Table.PROGRAM_BLOCK));
        
        Result r1 = ywdb.jooq.select(ID, IN_PROGRAM_BLOCK, BEGIN_ANNOTATION_ID, END_ANNOTATION_ID,
                                     NAME, QUALIFIED_NAME, IS_WORKFLOW, IS_FUNCTION)
                             .from(Table.PROGRAM_BLOCK)
                             .fetch();

        assertEquals(
            "+----+----------------+----------------+--------------+----+--------------+-----------+-----------+"   + EOL +
            "|id  |in_program_block|begin_annotation|end_annotation|name|qualified_name|is_workflow|is_function|"   + EOL +
            "+----+----------------+----------------+--------------+----+--------------+-----------+-----------+"   + EOL +
            "|1   |{null}          |{null}          |{null}        |    |              |0          |0          |"   + EOL +
            "|2   |{null}          |{null}          |{null}        |    |              |0          |0          |"   + EOL +
            "+----+----------------+----------------+--------------+----+--------------+-----------+-----------+",
            FileIO.localizeLineEndings(r1.toString()));
    }
    
    public void testUpdateProgram() throws Exception {
        
        insertSources();
        insertComments();
        insertAnnotations();
        insertDefaultProgramBlocks();
        updateProgramBlocks();
        
        assertEquals(2, ywdb.getRowCount(Table.PROGRAM_BLOCK));
        
        Result<?> r1 = ywdb.jooq.select(ID, IN_PROGRAM_BLOCK, BEGIN_ANNOTATION_ID, END_ANNOTATION_ID, 
                                        NAME, QUALIFIED_NAME, IS_WORKFLOW, IS_FUNCTION)
                                .from(Table.PROGRAM_BLOCK)
                                .fetch();

        assertEquals(
            "+----+----------------+----------------+--------------+-----+--------------+-----------+-----------+"  + EOL +
            "|id  |in_program_block|begin_annotation|end_annotation|name |qualified_name|is_workflow|is_function|"  + EOL +
            "+----+----------------+----------------+--------------+-----+--------------+-----------+-----------+"  + EOL +
            "|1   |{null}          |1               |2             |prog1|prog1         |0          |0          |"  + EOL +
            "|2   |{null}          |3               |4             |prog2|prog2         |0          |0          |"  + EOL +
            "+----+----------------+----------------+--------------+-----+--------------+-----------+-----------+",
            FileIO.localizeLineEndings(r1.toString()));
    }
}