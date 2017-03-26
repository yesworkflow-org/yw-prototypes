package org.yesworkflow.model;

import org.yesworkflow.db.Table;
import static org.yesworkflow.db.Column.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.jooq.Record;
import org.jooq.Result;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.util.FileIO;
import org.yesworkflow.YesWorkflowTestCase;

public class TestDefaultModelerAssertions extends YesWorkflowTestCase {

    private YesWorkflowDB ywdb = null;
	Extractor extractor = null;
    Modeler modeler = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.ywdb = YesWorkflowDB.createInMemoryDB();
        extractor = new DefaultExtractor(this.ywdb, super.stdoutStream, super.stderrStream);
        modeler = new DefaultModeler(this.ywdb, super.stdoutStream, super.stderrStream);
    }
    
    @SuppressWarnings({ "unchecked" })
    private Result<Record> selectPrograms() {
        
        return ywdb.jooq().select(ID, IN_PROGRAM_BLOCK, BEGIN_ANNOTATION_ID, END_ANNOTATION_ID, NAME, 
                                  QUALIFIED_NAME, IS_WORKFLOW, IS_FUNCTION)
                          .from(Table.PROGRAM_BLOCK)
                          .fetch();
    }

    @SuppressWarnings("unchecked")
    private Result<Record> selectAssertions() {
        
        return ywdb.jooq().select(ID, PROGRAM_ID, SUBJECT_ID, PREDICATE, OBJECT_ID)
                          .from(Table.ASSERTION)
                          .fetch();
    }
    
    @SuppressWarnings({ "unchecked" })
    private Result<Record> selectData() {
        
        return ywdb.jooq().select(ID, IN_PROGRAM_BLOCK, NAME, QUALIFIED_NAME)
                          .from(Table.DATA)
                          .fetch();
    }
    

    public void testModelAssertions_AssertDependencyOnOneInputOfTwo() throws Exception {
        
        String source = 
                "# @begin script @in x @in y @out z"	+ EOL +
                "# @assert z depends-on y          "    + EOL + 
                "some code                         "	+ EOL +
                "# @end script                     "	+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        modeler.annotations(annotations).model();
        
        assertEquals(
                "+----+----------------+----------------+--------------+------+--------------+-----------+-----------+"	+ EOL +
                "|id  |in_program_block|begin_annotation|end_annotation|name  |qualified_name|is_workflow|is_function|" + EOL +
                "+----+----------------+----------------+--------------+------+--------------+-----------+-----------+" + EOL +
                "|1   |{null}          |1               |6             |script|script        |1          |0          |"	+ EOL + 
                "|2   |1               |1               |6             |script|script.script |0          |0          |" + EOL +
                "+----+----------------+----------------+--------------+------+--------------+-----------+-----------+",
                FileIO.localizeLineEndings(selectPrograms().toString()));
        
        assertEquals(
                "+----+----------------+----+--------------+"    + EOL +
                "|id  |in_program_block|name|qualified_name|"    + EOL +
                "+----+----------------+----+--------------+"    + EOL +
                "|1   |{null}          |x   |x             |"    + EOL +
                "|2   |{null}          |y   |y             |"    + EOL +
                "|3   |{null}          |z   |z             |"    + EOL +
                "+----+----------------+----+--------------+",
                FileIO.localizeLineEndings(selectData().toString()));
        
        assertEquals(
                "+----+----------------+-------+----------+------+"    + EOL +
                "|id  |in_program_block|subject|predicate |object|"    + EOL +
                "+----+----------------+-------+----------+------+"    + EOL +
                "|1   |{null}          |3      |depends-on|2     |"    + EOL +
                "+----+----------------+-------+----------+------+",
                FileIO.localizeLineEndings(selectAssertions().toString()));
    }
    
    
}
