package org.yesworkflow.db;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.LogManager;

import org.jooq.DSLContext;
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
    
    private static YesWorkflowDB globalInstance = null;

    public static YesWorkflowDB getGlobalInstance() throws Exception {
        if (globalInstance == null) {
            globalInstance = YesWorkflowH2DB.createInMemoryDB();
        }
        return globalInstance;
    }

    public static void dropGlobalInstance() throws Exception {
        if (globalInstance != null) {
            globalInstance.close();
            globalInstance = null;
        }
    }
    
    public static YesWorkflowDB createInMemoryDB() throws Exception {
        return YesWorkflowH2DB.createInMemoryDB();
    }

    public static YesWorkflowDB openFileDB(Path dbFilePath) throws Exception {
        return YesWorkflowSQLiteDB.openFileDB(dbFilePath);
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

        try {
            for (String sqlStatement : sqlScript.split(";")) {
                if (sqlStatement.trim().length() > 0) {
                    statement.execute(sqlStatement);
                    statementCount++;
                }
            }
            
        } catch (SQLException e) {
            throw e;
        } finally {
            statement.close();
        }
        
        return statementCount;
    }

    public void insertSourceFile(int id, String sourceFilePath) {

        jooq.insertInto(Table.SOURCE_FILE, ID, PATH)
          .values(id, sourceFilePath)
          .execute();
    }
    
    public void insertAnnotation(int id, int sourceFileId, Integer qualifiedAnnotationId,
            int lineNumber, String tag, String keyword, String value, String description) {

        jooq.insertInto(Table.ANNOTATION,
                        ID, SOURCE_FILE_ID, QUALIFIES, LINE_NUMBER,
                        TAG, KEYWORD, VALUE, DESCRIPTION)
          .values(id, sourceFileId, qualifiedAnnotationId, lineNumber, tag, keyword, value, description)
          .execute();
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
