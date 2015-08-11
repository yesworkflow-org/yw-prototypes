package org.yesworkflow.db;

import java.nio.file.Files;
import java.nio.file.Path;


import static org.jooq.impl.DSL.table;

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
    
    public void testCreateVolatileDB() throws Exception {
        YesWorkflowDB ywdb = YesWorkflowDB.createVolatileDB();
        ywdb.close();
    }

    public void testCreateFileDB() throws Exception {
        YesWorkflowDB ywdb = YesWorkflowDB.openFileDB(testDBFilePath);
        ywdb.close();
    }
    
    public void testCreateDBTables() throws Exception {
//        YesWorkflowDB ywdb = YesWorkflowDB.openFileDB(testDirectoryPath.resolve("schema.db"));
        YesWorkflowDB ywdb = YesWorkflowDB.createVolatileDB();
        assertTrue(ywdb.hasTable(table("source_file")));
        ywdb.close();
    }

    public void testInsertSourceFile() throws Exception {
        YesWorkflowDB ywdb = YesWorkflowDB.createVolatileDB();
        assertEquals(0, ywdb.getRowCount(table("source_file")));
        ywdb.insertSourceFile(1, "source1");
        assertEquals(1, ywdb.getRowCount(table("source_file")));
        ywdb.insertSourceFile(2, "source2");
        assertEquals(2, ywdb.getRowCount(table("source_file")));
        ywdb.insertSourceFile(3, "source3");
        assertEquals(3, ywdb.getRowCount("source_file"));
    }

    public void testInsertAnnotation() throws Exception {
        YesWorkflowDB ywdb = YesWorkflowDB.createVolatileDB();
        ywdb.insertSourceFile(1, "source");
        ywdb.insertAnnotation(1, 1, 1, 10, "BEGIN", "begin", "p", null);
        ywdb.insertAnnotation(2, 1, 1, 20, "END", "end", "p", null);
        assertEquals(1, ywdb.getRowCount("source_file"));
        assertEquals(2, ywdb.getRowCount("annotation"));
        
    }
}
