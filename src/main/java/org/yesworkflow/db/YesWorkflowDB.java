package org.yesworkflow.db;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.LogManager;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.yesworkflow.util.FileIO;
import org.yesworkflow.db.Table;

import static org.yesworkflow.db.Column.*;

@SuppressWarnings("unchecked")
public abstract class YesWorkflowDB {

    static {
        // disable display of jOOQ logo
        LogManager.getLogManager().reset();
    }

    protected Connection connection;
    protected Statement statement;
    protected DSLContext jooq;
        
    public static YesWorkflowDB createInstance() throws Exception {
        return createInMemoryDB();
    }

    public static YesWorkflowDB createInMemoryDB() throws Exception {
        return YesWorkflowH2DB.createInMemoryDB();
//        return YesWorkflowSQLiteDB.createInMemoryDB();
    }

    public static YesWorkflowDB openFileDB(Path dbFilePath) throws Exception {
        return YesWorkflowSQLiteDB.openFileDB(dbFilePath);
    }

    public Long getLong(Object value) {
        return (Long)value; 
    }

    public Long getLongValue(Record record, Field<?> field) {
        return getLong(record.getValue(field));
    }
    
    public YesWorkflowDB(Connection connection) throws SQLException {
        this.connection = connection;
        this.statement = connection.createStatement();
    }

    public void close() throws SQLException {
        statement.close();
        connection.close();
    }
    
    protected int createDBTables(String createTablesScriptPath) throws Exception {
        String sqlScript = FileIO.readTextFileOnClasspath(createTablesScriptPath);
        int statementCount = executeSqlScript(sqlScript);
        return statementCount;
    }
    
    public int executeSqlScript(String sqlScript) 
            throws SQLException {
        
        int statementCount = 0;

        for (String sqlStatement : sqlScript.split(";")) {
            if (sqlStatement.trim().length() > 0) {
                statement.execute(sqlStatement);
                statementCount++;
            }
        }

        return statementCount;
    }

    public DSLContext jooq() {
        return jooq;
    }

    public long getGeneratedId() throws SQLException {
        ResultSet resultSet = statement.getGeneratedKeys();
        resultSet.next();
        return resultSet.getLong(1);
    }
    
    public Long insertSource(String path) throws SQLException {

       jooq.insertInto(Table.SOURCE)
           .set(PATH, path)
           .execute();
        
        return getGeneratedId();
    }

    public Long insertSourceLine(Long sourceId, Long lineNumber, String lineText) throws SQLException {

        jooq.insertInto(Table.SOURCE_LINE)
            .set(SOURCE_ID, sourceId)
            .set(LINE_NUMBER, lineNumber)
            .set(LINE_TEXT, lineText)
            .execute();
        
        return getGeneratedId();
    }

    public Long insertComment(Long sourceId, Long lineNumber, 
                              Long rankInLine, String commentText) throws SQLException {

        jooq.insertInto(Table.COMMENT)
            .set(SOURCE_ID, sourceId)
            .set(LINE_NUMBER, lineNumber)
            .set(RANK_IN_LINE, rankInLine)
            .set(COMMENT_TEXT, commentText)
            .execute();
        
        return getGeneratedId();
    }
    
    public Long insertAnnotation(Long qualifiedAnnotationId, long commentId,
                                 long rankInComment, String tag, String keyword, 
                                 String value, String description) throws SQLException {

        jooq.insertInto(Table.ANNOTATION)
            .set(QUALIFIES, qualifiedAnnotationId)
            .set(COMMENT_ID, commentId)
            .set(RANK_IN_COMMENT, rankInComment)
            .set(TAG, tag)
            .set(KEYWORD, keyword)
            .set(VALUE, value)
            .set(DESCRIPTION, description)
            .execute();
        
        return getGeneratedId();
    }

    public Long insertDefaultProgramBlock(Long inProgramBlockId) throws SQLException {

        jooq.insertInto(Table.PROGRAM_BLOCK)
            .set(IN_PROGRAM_BLOCK, inProgramBlockId)
            .execute();

        return getGeneratedId();
    }

    public Long insertProgramBlock(Long inProgramBlockId, Long beginAnnotationId, Long endAnnotationId,
                              String name, String qualifiedName, 
                              boolean isWorkflow, boolean isFunction) throws SQLException {

        jooq.insertInto(Table.PROGRAM_BLOCK)
            .set(IN_PROGRAM_BLOCK, inProgramBlockId)
            .set(BEGIN_ANNOTATION_ID, beginAnnotationId)
            .set(END_ANNOTATION_ID, endAnnotationId)
            .set(NAME, name)
            .set(QUALIFIED_NAME, qualifiedName)
            .set(IS_WORKFLOW, isWorkflow)
            .set(IS_FUNCTION, isFunction)
            .execute();

        return getGeneratedId();
    }

    public void updateProgramBlock(long id, Long beginAnnotationId, Long endAnnotationId,
                              String name, String qualifiedName, 
                              boolean isWorkflow, boolean isFunction) {

        jooq.update(Table.PROGRAM_BLOCK)
            .set(BEGIN_ANNOTATION_ID, beginAnnotationId)
            .set(END_ANNOTATION_ID, endAnnotationId)
            .set(NAME, name)
            .set(QUALIFIED_NAME, qualifiedName)
            .set(IS_WORKFLOW, isWorkflow)
            .set(IS_FUNCTION, isFunction)
            .where(ID.equal(id))            
            .execute();
    }

    public Long insertData(String name, String qualifiedName, Long inProgramBlockId) throws SQLException {

        jooq.insertInto(Table.DATA)
            .set(IN_PROGRAM_BLOCK, inProgramBlockId)
            .set(NAME, name)
            .set(QUALIFIED_NAME, qualifiedName)
            .execute();
        
        return getGeneratedId();
    }
    
    public Long insertPort(String name, String qualifiedName, Long programId) throws SQLException {

        jooq.insertInto(Table.DATA)
            .set(NAME, name)
            .set(QUALIFIED_NAME, qualifiedName)
            .set(PROGRAM_ID, programId)
            .execute();
        
        return getGeneratedId();
    }

    
    public int getRowCount(org.jooq.Table<?> T) throws SQLException {
        
        return (int)jooq.selectCount()
                        .from(T)
                        .fetchOne()
                        .getValue(0);
    }

    public boolean hasTable(org.jooq.Table<?> T) throws SQLException {
        Exception caught = null;
        try {
            getRowCount(T);
        } catch(Exception e) {
            caught = e;
        }
        return caught == null;
    }
}
