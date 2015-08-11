package org.yesworkflow.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.yesworkflow.util.FileIO;

import static org.yesworkflow.db.Tables.*;

@SuppressWarnings("unchecked")
public class YesWorkflowDB {

    private static String CREATE_TABLES_SCRIPT_H2  = "org/yesworkflow/db/h2/createtables.h2";
    private static String CREATE_TABLES_SCRIPT_SQLITE  = "org/yesworkflow/db/sqlite/createtables.sqlite";
    private static String createTablesScript = CREATE_TABLES_SCRIPT_H2;

    private static String IN_MEMORY_DB_URL_H2 = "jdbc:h2:mem:";
    private static String IN_MEMORY_DB_URL_SQLITE = "jdbc:sqlite::memory:";
    private static String inMemoryDbUrl = IN_MEMORY_DB_URL_H2;
    
    private final Connection connection;
    private final Statement statement;
    private final DSLContext db;
    
    public YesWorkflowDB(Connection connection) throws SQLException {
        this.connection = connection;
        this.statement = connection.createStatement();
        this.db = DSL.using(this.connection, SQLDialect.H2);
    }
    
    public static YesWorkflowDB createVolatileDB() throws Exception {
        YesWorkflowDB ywdb = new YesWorkflowDB(DriverManager.getConnection(inMemoryDbUrl));
        ywdb.createDBTables();
        return ywdb;
    }

    public static YesWorkflowDB openFileDB(Path dbFilePath) throws Exception {
        
        YesWorkflowDB ywdb;
        
        if (!(dbFilePath.toFile().exists())) {
            Path parentDirectory = dbFilePath.getParent();
            if (Files.exists(parentDirectory)) {
                if (! Files.isDirectory(parentDirectory)) {
                    throw new Exception("Cannot create " + dbFilePath + " because " + parentDirectory + " is not a directory");
                }
            } else {
                Files.createDirectories(parentDirectory);
            }
            ywdb = new YesWorkflowDB(DriverManager.getConnection("jdbc:sqlite:" + dbFilePath));
            ywdb.createDBTables();
        } else {
            ywdb = new YesWorkflowDB(DriverManager.getConnection("jdbc:sqlite:" + dbFilePath));
        }
        
        return ywdb;
    }

    public void close() throws SQLException {
        statement.close(); 
        connection.close();
    }
    
    public int createDBTables() throws Exception {
        String sqlScript = FileIO.readTextFileOnClasspath(createTablesScript);
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

        db.insertInto(SOURCE_FILE, ID, PATH)
          .values(id, sourceFilePath)
          .execute();
    }
    
    public void insertAnnotation(int id, int sourceFileId, Integer qualifiedAnnotationId,
            int lineNumber, String tag, String keyword, String value, String description) {
        
        db.insertInto(ANNOTATION,
                      ID, SOURCE_FILE_ID, QUALIFIES, LINE_NUMBER,
                      TAG, KEYWORD, VALUE, DESCRIPTION)
          .values(id, sourceFileId, qualifiedAnnotationId, lineNumber, tag, keyword, value, description)
          .execute();
    }

    
    public int getRowCount(Table<?> T) throws SQLException {
        
        return (int)db.selectCount()
                      .from(T)
                      .fetchOne()
                      .getValue(0);
    }

    public int getRowCount(String T) throws SQLException {
        
        return (int)db.selectCount()
                      .from(T)
                      .fetchOne()
                      .getValue(0);
    }
    
    public boolean hasTable(Table<?> T) throws SQLException {
        Exception caught = null;
        try {
            getRowCount(T);
        } catch(Exception e) {
            caught = e;
        }
        return caught == null;
    }
}
