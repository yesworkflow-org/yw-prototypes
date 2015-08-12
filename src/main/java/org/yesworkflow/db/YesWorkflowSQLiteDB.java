package org.yesworkflow.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class YesWorkflowSQLiteDB extends YesWorkflowDB {

    private static String IN_MEMORY_DB_URL = "jdbc:sqlite::memory:";
    private static String CREATE_TABLES_SCRIPT  = "org/yesworkflow/db/sqlite/createtables.sqlite";

    public YesWorkflowSQLiteDB(Connection connection) throws SQLException {
        super(connection);
        this.jooq = DSL.using(this.connection, SQLDialect.SQLITE);
    }
    
    public static YesWorkflowDB createVolatileDB() throws Exception {
        YesWorkflowDB ywdb = new YesWorkflowH2DB(DriverManager.getConnection(IN_MEMORY_DB_URL));
        ywdb.createDBTables(CREATE_TABLES_SCRIPT);
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
            ywdb = new YesWorkflowSQLiteDB(DriverManager.getConnection("jdbc:sqlite:" + dbFilePath));
            ywdb.createDBTables(CREATE_TABLES_SCRIPT);
        } else {
            ywdb = new YesWorkflowSQLiteDB(DriverManager.getConnection("jdbc:sqlite:" + dbFilePath));
        }
        
        return ywdb;
    }
}
