package org.yesworkflow.model;

import org.yesworkflow.db.Table;
import static org.yesworkflow.db.Column.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.jooq.Record;
import org.jooq.Result;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.exceptions.YWMarkupException;
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
    
    @SuppressWarnings({ "unchecked" })
    private Result<Record> selectData() {
        
        return ywdb.jooq().select(ID, IN_PROGRAM_BLOCK, NAME, QUALIFIED_NAME)
                          .from(Table.DATA)
                          .fetch();
    }

    @SuppressWarnings({ "unchecked" })
    private Result<Record> selectPort() {
        
        return ywdb.jooq().select(ID, PORT_ANNOTATION_ID, ON_PROGRAM_BLOCK, DATA_ID, NAME, 
                                  QUALIFIED_NAME, URI_TEMPLATE, DIRECTION)
                          .from(Table.PORT)
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

        Model model = modeler.annotations(annotations).model().getModel();
        
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
                "+----+---------------+----------------+----+----+--------------+------------+---------+"   + EOL +
                "|id  |port_annotation|on_program_block|data|name|qualified_name|uri_template|direction|"   + EOL +
                "+----+---------------+----------------+----+----+--------------+------------+---------+"   + EOL +
                "|1   |2              |1               |1   |x   |x             |{null}      |IN       |"   + EOL +
                "|2   |3              |1               |2   |y   |y             |{null}      |IN       |"   + EOL +
                "|3   |4              |1               |3   |z   |z             |{null}      |OUT      |"   + EOL +
                "|4   |2              |2               |1   |x   |script.x      |{null}      |IN       |"   + EOL +
                "|5   |3              |2               |2   |y   |script.y      |{null}      |IN       |"   + EOL +
                "|6   |4              |2               |3   |z   |script.z      |{null}      |OUT      |"   + EOL +
                "+----+---------------+----------------+----+----+--------------+------------+---------+",
                FileIO.localizeLineEndings(selectPort().toString()));
        
        assertEquals(
                "+----+----------------+-------+----------+------+"    + EOL +
                "|id  |in_program_block|subject|predicate |object|"    + EOL +
                "+----+----------------+-------+----------+------+"    + EOL +
                "|1   |{null}          |3      |depends-on|2     |"    + EOL +
                "+----+----------------+-------+----------+------+",
                FileIO.localizeLineEndings(ywdb.selectAssertions().toString()));
        
        Map<String, String> facts = new ModelFacts(ywdb, DefaultModeler.DEFAULT_QUERY_ENGINE, model).build().facts();

        assertEquals(
                "% FACT: assert(program_id, subject_id, predicate, object_id)." + EOL +
                "assert(nil, 3, 'depends-on', 2)."                              + EOL,
                facts.get("assert"));
    }

    
    public void testModelAssertions_AssertDependencyOnTwoInputsOfTwo() throws Exception {
        
        String source = 
                "# @begin script @in x @in y @out z"    + EOL +
                "# @assert z depends-on x y        "    + EOL + 
                "some code                         "    + EOL +
                "# @end script                     "    + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        Model model = modeler.annotations(annotations).model().getModel();
        
        assertEquals(
                "+----+----------------+----------------+--------------+------+--------------+-----------+-----------+" + EOL +
                "|id  |in_program_block|begin_annotation|end_annotation|name  |qualified_name|is_workflow|is_function|" + EOL +
                "+----+----------------+----------------+--------------+------+--------------+-----------+-----------+" + EOL +
                "|1   |{null}          |1               |6             |script|script        |1          |0          |" + EOL + 
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
                "|1   |{null}          |3      |depends-on|1     |"    + EOL +
                "|2   |{null}          |3      |depends-on|2     |"    + EOL +
                "+----+----------------+-------+----------+------+",
                FileIO.localizeLineEndings(ywdb.selectAssertions().toString()));
        
        Map<String, String> facts = new ModelFacts(ywdb, DefaultModeler.DEFAULT_QUERY_ENGINE, model).build().facts();

        assertEquals(
                "% FACT: assert(program_id, subject_id, predicate, object_id)." + EOL +
                "assert(nil, 3, 'depends-on', 1)."                              + EOL +
                "assert(nil, 3, 'depends-on', 2)."                              + EOL,
                facts.get("assert"));
    }
    
    public void testModelAssertions_AssertDependencyOnTwoInputsOfTwoWithTwoAsserts() throws Exception {
        
        String source = 
                "# @begin script @in x @in y @out z"    + EOL +
                "# @assert z depends-on x          "    + EOL + 
                "# @assert z depends-on y          "    + EOL + 
                "some code                         "    + EOL +
                "# @end script                     "    + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        modeler.annotations(annotations).model();
        
        assertEquals(
                "+----+----------------+----------------+--------------+------+--------------+-----------+-----------+" + EOL +
                "|id  |in_program_block|begin_annotation|end_annotation|name  |qualified_name|is_workflow|is_function|" + EOL +
                "+----+----------------+----------------+--------------+------+--------------+-----------+-----------+" + EOL +
                "|1   |{null}          |1               |7             |script|script        |1          |0          |" + EOL + 
                "|2   |1               |1               |7             |script|script.script |0          |0          |" + EOL +
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
                "|1   |{null}          |3      |depends-on|1     |"    + EOL +
                "|2   |{null}          |3      |depends-on|2     |"    + EOL +
                "+----+----------------+-------+----------+------+",
                FileIO.localizeLineEndings(ywdb.selectAssertions().toString()));
    }
    
    public void testModelAssertions_AssertDependencyOneOutputOnAnotherOutput() throws Exception {
        
        String source = 
                "# @begin script @in x @out y @out z"   + EOL +
                "# @assert y depends-on x           "   + EOL +
                "# @assert z depends-on y           "   + EOL +
                "some code                          "   + EOL +
                "# @end script                      "   + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        modeler.annotations(annotations).model();
        
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
                "+----+---------------+----------------+----+----+--------------+------------+---------+"   + EOL +
                "|id  |port_annotation|on_program_block|data|name|qualified_name|uri_template|direction|"   + EOL +
                "+----+---------------+----------------+----+----+--------------+------------+---------+"   + EOL +
                "|1   |2              |1               |1   |x   |x             |{null}      |IN       |"   + EOL +
                "|2   |3              |1               |2   |y   |y             |{null}      |OUT      |"   + EOL +
                "|3   |4              |1               |3   |z   |z             |{null}      |OUT      |"   + EOL +
                "|4   |2              |2               |1   |x   |script.x      |{null}      |IN       |"   + EOL +
                "|5   |3              |2               |2   |y   |script.y      |{null}      |OUT      |"   + EOL +
                "|6   |4              |2               |3   |z   |script.z      |{null}      |OUT      |"   + EOL +
                "+----+---------------+----------------+----+----+--------------+------------+---------+",
                FileIO.localizeLineEndings(selectPort().toString()));
        
        assertEquals(
                "+----+----------------+-------+----------+------+"    + EOL +
                "|id  |in_program_block|subject|predicate |object|"    + EOL +
                "+----+----------------+-------+----------+------+"    + EOL +
                "|1   |{null}          |2      |depends-on|1     |"    + EOL +
                "|2   |{null}          |3      |depends-on|2     |"    + EOL +
                "+----+----------------+-------+----------+------+",
                FileIO.localizeLineEndings(ywdb.selectAssertions().toString()));
    }
    
    public void testModelAssertions_NonexistentObject() throws Exception {
        
        String source = 
                "# @begin script @in x @in y @out z"    + EOL +
                "# @assert z depends-on a          "    + EOL + 
                "some code                         "    + EOL +
                "# @end script                     "    + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();
        
        Exception caught = null;
        try {
            modeler.annotations(annotations).model();
        } catch (YWMarkupException e) {
            caught = e;
        }
        
        assertNotNull(caught);
        assertEquals("Object of assertion 'a' does not exist", caught.getMessage());
    }
    
    public void testModelAssertions_NonexistentSubject_ThrowsException() throws Exception {
        
        String source = 
                "# @begin script @in x @in y @out z"    + EOL +
                "# @assert a depends-on x          "    + EOL + 
                "some code                         "    + EOL +
                "# @end script                     "    + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();
        
        Exception caught = null;
        try {
            modeler.annotations(annotations).model();
        } catch (YWMarkupException e) {
            caught = e;
        }
        
        assertNotNull(caught);
        assertEquals("Subject of assertion 'a' does not exist", caught.getMessage());
    }
    
    public void testModelAssertions_SubjectIsNotOutput_ThrowsException() throws Exception {
        
        String source = 
                "# @begin script @in x @in y @out z"    + EOL +
                "# @assert x depends-on y          "    + EOL + 
                "some code                         "    + EOL +
                "# @end script                     "    + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();
        
        Exception caught = null;
        try {
            modeler.annotations(annotations).model();
        } catch (YWMarkupException e) {
            caught = e;
        }
        
        assertNotNull(caught);
        assertEquals("Subject of assertion 'x' is not an output", caught.getMessage());
    }
}
