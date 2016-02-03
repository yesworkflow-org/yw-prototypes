package org.yesworkflow.db;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import org.yesworkflow.db.Table;
import org.yesworkflow.YesWorkflowTestCase;

public class TestYesWorkflowDBViews extends YesWorkflowTestCase {
    
    private Path testDirectoryPath;
    private Path testDBFilePath;
    private YesWorkflowDB ywdb;
    private boolean useInMemoryDB = true;
    private Long[] programBlockId = new Long[20];
    
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

    @SuppressWarnings("unused")
    private void insertNestedProgramBlocks() throws SQLException {
        programBlockId[1] = ywdb.insertProgramBlock(null, null, null, "A", "A", false, false);
        programBlockId[2] = ywdb.insertProgramBlock(programBlockId[1], null, null, "B", "A.B", false, false);
        programBlockId[3] = ywdb.insertProgramBlock(programBlockId[1], null, null, "C", "A.C", false, false);
        programBlockId[4] = ywdb.insertProgramBlock(programBlockId[3], null, null, "D", "A.C.D", false, false);
        programBlockId[5] = ywdb.insertProgramBlock(programBlockId[4], null, null, "E", "A.C.D.E", false, false);
        programBlockId[6] = ywdb.insertProgramBlock(null, null, null, "F", "F", false, false);
        programBlockId[7] = ywdb.insertProgramBlock(programBlockId[6], null, null, "G", "F.G", false, false);
        programBlockId[8] = ywdb.insertProgramBlock(programBlockId[7], null, null, "H", "F.G.H", false, false);
        programBlockId[9] = ywdb.insertProgramBlock(programBlockId[8], null, null, "I", "F.G.H.I", false, false);
        programBlockId[10] = ywdb.insertProgramBlock(programBlockId[8], null, null, "J", "F.G.H.J", false, false);
    }
    
    public void testCreateDBTables() throws Exception {
        assertTrue(ywdb.hasTable(Table.SOURCE));
    }
    
//    public void testContainedProgramBlocks() throws SQLException, IOException {
//        
//        insertNestedProgramBlocks();
//        
//        Result<?> r = ywdb.jooq.select(ID, IN_PROGRAM_BLOCK, QUALIFIED_NAME, field("root"))
//                               .from(View.CONTAINED_PROGRAM_BLOCKS)
//                               .where(field("root").equal(1))
//                               .fetch();
//        
//        assertEquals(
//                "",
//                FileIO.localizeLineEndings(r.toString()));
//    }
}