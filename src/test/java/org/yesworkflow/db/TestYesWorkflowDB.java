package org.yesworkflow.db;

import java.nio.file.Files;
import java.nio.file.Path;

import org.yesworkflow.db.Table;

import static org.yesworkflow.db.Column.*;

import org.jooq.Result;
import org.yesworkflow.YesWorkflowTestCase;

@SuppressWarnings("unchecked")
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
        assertTrue(ywdb.hasTable(Table.SOURCE_FILE));
        ywdb.close();
    }

    @SuppressWarnings("rawtypes")
    public void testInsertSourceFile() throws Exception {
        YesWorkflowDB ywdb = YesWorkflowDB.createVolatileDB();
        assertEquals(0, ywdb.getRowCount(Table.SOURCE_FILE));
        ywdb.insertSourceFile(1, "source1");
        assertEquals(1, ywdb.getRowCount(Table.SOURCE_FILE));
        ywdb.insertSourceFile(2, "source2");
        assertEquals(2, ywdb.getRowCount(Table.SOURCE_FILE));
        ywdb.insertSourceFile(3, "source3");
        assertEquals(3, ywdb.getRowCount(Table.SOURCE_FILE));
        
        Result r = ywdb.jooq.select(ID, PATH)
                            .from(Table.SOURCE_FILE)
                            .fetch();
        
        assertEquals(3, r.size());
        assertEquals(1, (int) r.getValue(0, ID));
        assertEquals("source1", (String) r.getValue(0, PATH));
        assertEquals(2, (int) r.getValue(1, ID));
        assertEquals("source2", (String) r.getValue(1, PATH));
        assertEquals(3, (int) r.getValue(2, ID));
        assertEquals("source3", (String) r.getValue(2, PATH));
    }

    @SuppressWarnings("rawtypes")
    public void testInsertAnnotation() throws Exception {
        
        YesWorkflowDB ywdb = YesWorkflowDB.createVolatileDB();
        
        ywdb.insertSourceFile(1, "source1");
        ywdb.insertSourceFile(2, "source2");
        ywdb.insertAnnotation(1, 1, 1, 10, "BEGIN", "begin", "p", null);
        ywdb.insertAnnotation(2, 1, 1, 20, "END",   "end",   "p", null);
        ywdb.insertAnnotation(3, 2, 1, 5,  "BEGIN", "begin", "q", null);
        ywdb.insertAnnotation(4, 2, 1, 15, "END",   "end",   "q", null);
        
        assertEquals(2, ywdb.getRowCount(Table.SOURCE_FILE));
        assertEquals(4, ywdb.getRowCount(Table.ANNOTATION));

        Result r = ywdb.jooq.select(ANNOTATION.ID, PATH, LINE_NUMBER, TAG, KEYWORD, VALUE)
                            .from(Table.ANNOTATION)
                            .join(Table.SOURCE_FILE)
                            .on(ANNOTATION.SOURCE_FILE_ID.equal(SOURCE_FILE.ID))
                            .fetch();

        assertEquals(4, r.size());

        assertEquals(1, r.getValue(0, ANNOTATION.ID));
        assertEquals("source1", r.getValue(0, PATH));
        assertEquals(10, r.getValue(0, LINE_NUMBER));
        assertEquals("begin", r.getValue(0, KEYWORD));
        
        assertEquals(4, r.getValue(3, ANNOTATION.ID));
        assertEquals("source2", r.getValue(3, PATH));
        assertEquals(15, r.getValue(3, LINE_NUMBER));
        assertEquals("end", r.getValue(3, KEYWORD));
        
        assertEquals(
            "+-------------+-------+-----------+-----+-------+-----+"   + EOL +
            "|annotation.id|path   |line_number|tag  |keyword|value|"   + EOL +
            "+-------------+-------+-----------+-----+-------+-----+"   + EOL +
            "|1            |source1|10         |BEGIN|begin  |p    |"   + EOL +
            "|2            |source1|20         |END  |end    |p    |"   + EOL +
            "|3            |source2|5          |BEGIN|begin  |q    |"   + EOL +
            "|4            |source2|15         |END  |end    |q    |"   + EOL +
            "+-------------+-------+-----------+-----+-------+-----+",
            r.toString());
    }
}