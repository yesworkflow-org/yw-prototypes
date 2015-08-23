package org.yesworkflow.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class YesWorkflowH2DB extends YesWorkflowDB {
    
    private static String IN_MEMORY_DB_URL = "jdbc:h2:mem:";
    private static String CREATE_TABLES_SCRIPT  = "org/yesworkflow/db/h2/createtables.h2";

    public YesWorkflowH2DB(Connection connection) throws SQLException {
        super(connection);
        this.jooq = DSL.using(this.connection, SQLDialect.H2);
    }
    
    public static YesWorkflowDB createInMemoryDB() throws Exception {
        YesWorkflowDB ywdb = new YesWorkflowH2DB(DriverManager.getConnection(IN_MEMORY_DB_URL));
        ywdb.createDBTables(CREATE_TABLES_SCRIPT);
        return ywdb;
    }
}
