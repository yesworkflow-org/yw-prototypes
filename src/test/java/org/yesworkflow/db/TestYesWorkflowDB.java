package org.yesworkflow.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;


import org.yesworkflow.db.Table;
import org.yesworkflow.util.FileIO;

import static org.yesworkflow.db.Column.*;
import static org.jooq.impl.DSL.field;

import org.jooq.Result;
import org.yesworkflow.YesWorkflowTestCase;

@SuppressWarnings("unchecked")
public class TestYesWorkflowDB extends YesWorkflowTestCase {
    
    private Path testDirectoryPath;
    private Path testDBFilePath;
    private YesWorkflowDB ywdb;
    private boolean useInMemoryDB = true;
    
    private Long[] sourceId = new Long[4];
    private Long[] codeId = new Long[5];
    private Long[] commentId = new Long[5];
    private Long[] annotationId = new Long[5];
    private Long[] programId = new Long[3];
    
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
        codeId[1] = ywdb.insertSourceLine(sourceId[1], 1L, "# @begin prog1");
        codeId[2] = ywdb.insertSourceLine(sourceId[1], 2L, "# @end prog1");
        codeId[3] = ywdb.insertSourceLine(sourceId[2], 1L, "# @begin prog2");
        codeId[4] = ywdb.insertSourceLine(sourceId[2], 2L, "# @end prog2");
    }
    
    private void insertComments() throws SQLException {
        commentId[1] = ywdb.insertComment(sourceId[1], 1L, 1L, "@begin prog1");
        commentId[2] = ywdb.insertComment(sourceId[1], 2L, 1L, "@end prog1");
        commentId[3] = ywdb.insertComment(sourceId[2], 1L, 1L, "@begin prog2");
        commentId[4] = ywdb.insertComment(sourceId[2], 2L, 1L, "@end prog2");
    }
    
    private void insertAnnotations() throws SQLException {
        annotationId[1] = ywdb.insertAnnotation(null, commentId[1], "BEGIN", "@begin", "prog1", null);
        annotationId[2] = ywdb.insertAnnotation(null, commentId[2], "END",   "@end",   "prog1", null);
        annotationId[3] = ywdb.insertAnnotation( null, commentId[3], "BEGIN", "@begin", "prog2", null);
        annotationId[4] = ywdb.insertAnnotation( null, commentId[4], "END",   "@end",   "prog2", null);
    }

    private void insertPrograms() throws SQLException {
        programId[1] = ywdb.insertProgram(null, annotationId[1], annotationId[2], "prog1", "prog1", false, false);
        programId[2] = ywdb.insertProgram(null, annotationId[3], annotationId[4], "prog2", "prog2", false, false);
    }
    
    private void insertDefaultPrograms() throws SQLException {
        programId[1] = ywdb.insertDefaultProgram(null);
        programId[2] = ywdb.insertDefaultProgram(null);
    }

    private void updatePrograms() throws SQLException {
         ywdb.updateProgram(programId[1], annotationId[1], annotationId[2], "prog1", "prog1", false, false);
         ywdb.updateProgram(programId[2], annotationId[3], annotationId[4], "prog2", "prog2", false, false);
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
            "+----+---------+-----------+--------------+"   + EOL +
            "|id  |source_id|line_number|line_text     |"   + EOL +
            "+----+---------+-----------+--------------+"   + EOL +
            "|1   |1        |1          |# @begin prog1|"   + EOL +
            "|2   |1        |2          |# @end prog1  |"   + EOL +
            "|3   |2        |1          |# @begin prog2|"   + EOL +
            "|4   |2        |2          |# @end prog2  |"   + EOL +
            "+----+---------+-----------+--------------+",
            FileIO.localizeLineEndings(r.toString()));
        
        Result r2 = ywdb.jooq.select(SOURCE_LINE.ID, SOURCE.PATH, LINE_NUMBER, LINE_TEXT)
                .from(Table.SOURCE_LINE)
                .join(Table.SOURCE).on(SOURCE.ID.equal(SOURCE_LINE.SOURCE_ID))
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
        
        Result r1 = ywdb.jooq.select(ID, SOURCE_ID, LINE_NUMBER, RANK_ON_LINE, TEXT)
                            .from(Table.COMMENT)
                            .fetch();
        
        assertEquals(
            "+----+---------+-----------+------------+------------+"    + EOL +
            "|id  |source_id|line_number|rank_on_line|text        |"    + EOL +
            "+----+---------+-----------+------------+------------+"    + EOL +
            "|1   |1        |1          |1           |@begin prog1|"    + EOL +
            "|2   |1        |2          |1           |@end prog1  |"    + EOL +
            "|3   |2        |1          |1           |@begin prog2|"    + EOL +
            "|4   |2        |2          |1           |@end prog2  |"    + EOL +
            "+----+---------+-----------+------------+------------+",
            FileIO.localizeLineEndings(r1.toString()));

        Result r2 = ywdb.jooq.select(COMMENT.ID, PATH, LINE_NUMBER, RANK_ON_LINE, TEXT)
                    .from(Table.COMMENT)
                    .join(Table.SOURCE).on(SOURCE_ID.equal(SOURCE.ID))
                    .fetch();
        
        assertEquals(
            "+----------+-----+-----------+------------+------------+"    + EOL +
            "|comment.id|path |line_number|rank_on_line|text        |"    + EOL +
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
        
        Result r1 = ywdb.jooq.select(ID, QUALIFIES, COMMENT_ID, TAG, KEYWORD, VALUE, DESCRIPTION)
                .from(Table.ANNOTATION)
                .fetch();

        assertEquals(
            "+----+---------+----------+-----+-------+-----+-----------+"    + EOL +
            "|id  |qualifies|comment_id|tag  |keyword|value|description|"    + EOL +
            "+----+---------+----------+-----+-------+-----+-----------+"    + EOL +
            "|1   |{null}   |1         |BEGIN|@begin |prog1|{null}     |"    + EOL +
            "|2   |{null}   |2         |END  |@end   |prog1|{null}     |"    + EOL +
            "|3   |{null}   |3         |BEGIN|@begin |prog2|{null}     |"    + EOL +
            "|4   |{null}   |4         |END  |@end   |prog2|{null}     |"    + EOL +
            "+----+---------+----------+-----+-------+-----+-----------+",
            FileIO.localizeLineEndings(r1.toString()));
    
        Result r2 = ywdb.jooq.select(ANNOTATION.ID, QUALIFIES, SOURCE.PATH, LINE_NUMBER, 
                                     COMMENT.RANK_ON_LINE, COMMENT.TEXT, TAG, KEYWORD, 
                                     VALUE, DESCRIPTION)
                             .from(Table.ANNOTATION)
                             .join(Table.COMMENT).on(ANNOTATION.COMMENT_ID.equal(COMMENT.ID))
                             .join(Table.SOURCE).on(COMMENT.SOURCE_ID.equal(SOURCE.ID))
                             .fetch();
        assertEquals(
            "+-------------+---------+-----------+-----------+--------------------+------------+-----+-------+-----+-----------+"    + EOL +
            "|annotation.id|qualifies|source.path|line_number|comment.rank_on_line|comment.text|tag  |keyword|value|description|"    + EOL +
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
        insertPrograms();
        
        assertEquals(2, ywdb.getRowCount(Table.PROGRAM));
        
        Result r1 = ywdb.jooq.select(ID, PARENT_ID, BEGIN_ID, END_ID, NAME,
                                     QUALIFIED_NAME, IS_WORKFLOW, IS_FUNCTION)
                .from(Table.PROGRAM)
                .fetch();

        assertEquals(
            "+----+---------+--------+------+-----+--------------+-----------+-----------+" + EOL +
            "|id  |parent_id|begin_id|end_id|name |qualified_name|is_workflow|is_function|" + EOL +
            "+----+---------+--------+------+-----+--------------+-----------+-----------+" + EOL +
            "|1   |{null}   |1       |2     |prog1|prog1         |0          |0          |" + EOL +
            "|2   |{null}   |3       |4     |prog2|prog2         |0          |0          |" + EOL +
            "+----+---------+--------+------+-----+--------------+-----------+-----------+",
            FileIO.localizeLineEndings(r1.toString()));
    }
    
    @SuppressWarnings("rawtypes")
    public void testInsertDefaultProgram() throws Exception {
        
        insertSources();
        insertComments();
        insertAnnotations();
        insertDefaultPrograms();
        
        assertEquals(2, ywdb.getRowCount(Table.PROGRAM));
        
        Result r1 = ywdb.jooq.select(ID, PARENT_ID, BEGIN_ID, END_ID, NAME, 
                                     QUALIFIED_NAME, IS_WORKFLOW, IS_FUNCTION)
                .from(Table.PROGRAM)
                .fetch();

        assertEquals(
            "+----+---------+--------+------+----+--------------+-----------+-----------+" + EOL +
            "|id  |parent_id|begin_id|end_id|name|qualified_name|is_workflow|is_function|" + EOL +
            "+----+---------+--------+------+----+--------------+-----------+-----------+" + EOL +
            "|1   |{null}   |{null}  |{null}|    |              |0          |0          |" + EOL +
            "|2   |{null}   |{null}  |{null}|    |              |0          |0          |" + EOL +
            "+----+---------+--------+------+----+--------------+-----------+-----------+",
            FileIO.localizeLineEndings(r1.toString()));
    }
    
    public void testUpdateProgram() throws Exception {
        
        insertSources();
        insertComments();
        insertAnnotations();
        insertDefaultPrograms();
        updatePrograms();
        
        assertEquals(2, ywdb.getRowCount(Table.PROGRAM));
        
        Result<?> r1 = ywdb.jooq.select(ID, PARENT_ID, BEGIN_ID, END_ID, NAME, QUALIFIED_NAME, IS_WORKFLOW, IS_FUNCTION)
                                .from(Table.PROGRAM)
                                .fetch();

        assertEquals(
            "+----+---------+--------+------+-----+--------------+-----------+-----------+" + EOL +
            "|id  |parent_id|begin_id|end_id|name |qualified_name|is_workflow|is_function|" + EOL +
            "+----+---------+--------+------+-----+--------------+-----------+-----------+" + EOL +
            "|1   |{null}   |1       |2     |prog1|prog1         |0          |0          |" + EOL +
            "|2   |{null}   |3       |4     |prog2|prog2         |0          |0          |" + EOL +
            "+----+---------+--------+------+-----+--------------+-----------+-----------+",
            FileIO.localizeLineEndings(r1.toString()));
    }
}