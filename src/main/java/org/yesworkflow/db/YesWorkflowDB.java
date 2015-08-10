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
import org.yesworkflow.db.jooq.tables.Annotation;
import org.yesworkflow.db.jooq.tables.SourceFile;
import org.yesworkflow.util.FileIO;

import static org.yesworkflow.db.jooq.Tables.SOURCE_FILE;
import static org.yesworkflow.db.jooq.Tables.ANNOTATION;

public class YesWorkflowDB {

    private static String tableCreationSqlFile  = "org/yesworkflow/db/createtables.sql";
    
    private final Connection connection;
    private final Statement statement;
    private final DSLContext db;
    
    public YesWorkflowDB(Connection connection) throws SQLException {
        this.connection = connection;
        this.statement = connection.createStatement();
        this.db = DSL.using(this.connection, SQLDialect.SQLITE);
    }
    
    public static YesWorkflowDB createVolatileDB() throws SQLException {
        YesWorkflowDB db = new YesWorkflowDB(DriverManager.getConnection("jdbc:sqlite::memory:"));
        return db;
    }

    public static YesWorkflowDB openFileDB(Path dbFilePath) throws Exception {
        
        if (!(dbFilePath.toFile().exists())) {
            Path parentDirectory = dbFilePath.getParent();
            if (Files.exists(parentDirectory)) {
                if (! Files.isDirectory(parentDirectory)) {
                    throw new Exception("Cannot create " + dbFilePath + " because " + parentDirectory + " is not a directory");
                }
            } else {
                Files.createDirectories(parentDirectory);
            }
        }
        
        YesWorkflowDB db = new YesWorkflowDB(DriverManager.getConnection("jdbc:sqlite:" + dbFilePath));
        return db;
    }

    public void close() throws SQLException {
        statement.close(); 
        connection.close();
    }
    
    public int createDBTables() throws Exception {
        String sqlScript = FileIO.readTextFileOnClasspath(tableCreationSqlFile);
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
    
    public int insertSourceFile(String sourceFilePath) {    
        SourceFile SF = SOURCE_FILE;
        return db.insertInto(SF, SF.PATH)
                 .values(sourceFilePath)
                 .returning(SF.ID)
                 .fetchOne()
                 .getValue(SF.ID);
    }

    public int insertAnnotation(int sourceFileId, Integer qualifiedAnnotationId,
                                int lineNumber, String tag, String keyword,
                                String value, String description) {
        Annotation A = ANNOTATION;
        return db.insertInto(A, A.SOURCE_FILE_ID, A.QUALIFIES, A.LINE_NUMBER,
                                A.TAG, A.KEYWORD, A.VALUE, A.DESCRIPTION)
                 .values(sourceFileId, qualifiedAnnotationId, lineNumber, 
                         tag, keyword, value, description)
                 .returning(A.ID)
                 .fetchOne()
                 .getValue(A.ID);
    }

    
    public int getRowCount(Table<?> T) throws SQLException {
        
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
