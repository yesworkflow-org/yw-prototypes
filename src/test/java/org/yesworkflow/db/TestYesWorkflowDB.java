package org.yesworkflow.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import static org.yesworkflow.db.jooq.Tables.SOURCE_FILE;
import static org.yesworkflow.db.jooq.Tables.ANNOTATION;

import org.yesworkflow.YesWorkflowTestCase;

public class TestYesWorkflowDB extends YesWorkflowTestCase {
    
    private Path testDirectoryPath;
    private Path testDBFilePath;
    
    @Override
    public void setUp() throws Exception {
        testDirectoryPath = getTestDirectory("TestYesWorkflowDB");
        testDBFilePath = testDirectoryPath.resolve("test.db");
        if (testDBFilePath.toFile().exists()) {
            Files.delete(testDBFilePath);
        }
    }
    
    public void testCreateVolatileDB() throws SQLException {
        YesWorkflowDB.createVolatileDB();
    }

    public void testCreateFileDB() throws Exception {
        YesWorkflowDB.openFileDB(testDBFilePath);
    }

    
    public void testCreateDBTables() throws Exception {
//        YesWorkflowDB db = YesWorkflowDB.openFileDB(testDirectoryPath.resolve("schema.db"));
        YesWorkflowDB db = YesWorkflowDB.createVolatileDB();
        assertFalse(db.hasTable(SOURCE_FILE));
        db.createDBTables();
        assertTrue(db.hasTable(SOURCE_FILE));
        db.close();
    }

    public void testInsertSourceFile() throws Exception {
        YesWorkflowDB db = YesWorkflowDB.createVolatileDB();
        db.createDBTables();
        assertEquals(0, db.getRowCount(SOURCE_FILE));
        assertEquals(1, db.insertSourceFile("source1"));
        assertEquals(1, db.getRowCount(SOURCE_FILE));
        assertEquals(2, db.insertSourceFile("source2"));
        assertEquals(2, db.getRowCount(SOURCE_FILE));
        assertEquals(3, db.insertSourceFile("source3"));
        assertEquals(3, db.getRowCount(SOURCE_FILE));
    }

    public void testInsertAnnotation() throws Exception {
        YesWorkflowDB db = YesWorkflowDB.createVolatileDB();
        db.createDBTables();
        int sourceId = db.insertSourceFile("source");
        db.insertAnnotation(sourceId, null, 10, "BEGIN", "begin", "p", null);
        db.insertAnnotation(sourceId, null, 20, "END", "end", "p", null);
        assertEquals(1, db.getRowCount(SOURCE_FILE));
        assertEquals(2, db.getRowCount(ANNOTATION));
        
    }
}
