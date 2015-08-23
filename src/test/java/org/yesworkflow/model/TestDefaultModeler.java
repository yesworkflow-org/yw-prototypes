package org.yesworkflow.model;

import org.yesworkflow.db.Table;
import static org.yesworkflow.db.Column.*;
import static org.yesworkflow.db.ColumnAlias.*;

import org.yesworkflow.db.TableAlias;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.List;

import org.jooq.Record;
import org.jooq.Result;
import org.yesworkflow.Language;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.annotations.In;
import org.yesworkflow.annotations.Out;
import org.yesworkflow.annotations.Param;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.exceptions.YWMarkupException;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.model.Channel;
import org.yesworkflow.model.Program;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.util.FileIO;
import org.yesworkflow.YesWorkflowTestCase;

public class TestDefaultModeler extends YesWorkflowTestCase {

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
        
        return ywdb.jooq().select(ID, PARENT_ID, BEGIN_ID, END_ID, NAME, 
                                  QUALIFIED_NAME, IS_WORKFLOW, IS_FUNCTION)
                          .from(org.yesworkflow.db.Table.PROGRAM)
                          .fetch();
    }
    
    @SuppressWarnings({ "unchecked" })
    private Result<Record> selectProgramLineNumbers() {

        return ywdb.jooq().select(QUALIFIED_NAME, BEGIN_COMMENT.LINE_NUMBER, END_COMMENT.LINE_NUMBER)
                .from(Table.PROGRAM)
                .join(TableAlias.BEGIN_ANNOTATION).on(BEGIN_ID.equal(BEGIN_ANNOTATION.ID))
                .join(TableAlias.BEGIN_COMMENT).on(BEGIN_ANNOTATION.COMMENT_ID.equal(BEGIN_COMMENT.ID))
                .join(TableAlias.END_ANNOTATION).on(END_ID.equal(END_ANNOTATION.ID))
                .join(TableAlias.END_COMMENT).on(END_COMMENT.ID.equal(END_ANNOTATION.COMMENT_ID))
                .fetch();
    }
    
    
    public void testExtract_GetModel_OneProgram() throws Exception {
        
        String source = 
                "# @begin script"	+ EOL +
                "  some code"		+ EOL +
                "# @end script"		+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();
        
        Program program = modeler.annotations(annotations)
                                 .model()
                                 .getModel()
                                 .program;
        
        assertEquals("script", program.name);
        assertEquals("script", program.beginAnnotation.name);
        assertEquals("script", program.endAnnotation.name);
        assertEquals(0, program.inPorts.length);
        assertEquals(0, program.outPorts.length);
        
        assertEquals(
            "+----+---------+--------+------+------+--------------+-----------+-----------+"    + EOL +
            "|id  |parent_id|begin_id|end_id|name  |qualified_name|is_workflow|is_function|"    + EOL +
            "+----+---------+--------+------+------+--------------+-----------+-----------+"    + EOL +
            "|1   |{null}   |1       |2     |script|script        |0          |0          |"    + EOL +
            "+----+---------+--------+------+------+--------------+-----------+-----------+",
                FileIO.localizeLineEndings(selectPrograms().toString()));
    }

    public void testExtract_GetModel_OneProgram_OneComment() throws Exception {
        
        String source = 
                "# @begin script @end script";

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();
        
        Program program = modeler.annotations(annotations)
                                 .model()
                                 .getModel()
                                 .program;
        
        assertEquals("script", program.name);
        assertEquals("script", program.beginAnnotation.name);
        assertEquals("script", program.endAnnotation.name);
        assertEquals(0, program.inPorts.length);
        assertEquals(0, program.outPorts.length);
        
        assertEquals(
                "+----+---------+--------+------+------+--------------+-----------+-----------+"    + EOL +
                "|id  |parent_id|begin_id|end_id|name  |qualified_name|is_workflow|is_function|"    + EOL +
                "+----+---------+--------+------+------+--------------+-----------+-----------+"    + EOL +
                "|1   |{null}   |1       |2     |script|script        |0          |0          |"    + EOL +
                "+----+---------+--------+------+------+--------------+-----------+-----------+",
                    FileIO.localizeLineEndings(selectPrograms().toString()));
        
        assertEquals(
                "+--------------+-----------------+---------------+"    + EOL +
                "|qualified_name|begin_line_number|end_line_number|"    + EOL +
                "+--------------+-----------------+---------------+"    + EOL +
                "|script        |1                |1              |"    + EOL +
                "+--------------+-----------------+---------------+",
                    FileIO.localizeLineEndings(selectProgramLineNumbers().toString()));

    }

    public void testExtract_GetModel_OneProgram_TwoCommentsOnOneLine() throws Exception {
        
        String source = "/* @begin script */some code /* @end script */";

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("language", Language.JAVA)
                .reader(reader)
                .extract()
                .getAnnotations();
        
        Program program = modeler.annotations(annotations)
                                 .model()
                                 .getModel()
                                 .program;
        
        assertEquals("script", program.name);
        assertEquals("script", program.beginAnnotation.name);
        assertEquals("script", program.endAnnotation.name);
        assertEquals(0, program.inPorts.length);
        assertEquals(0, program.outPorts.length);
        
        assertEquals(
                "+----+---------+--------+------+------+--------------+-----------+-----------+"    + EOL +
                "|id  |parent_id|begin_id|end_id|name  |qualified_name|is_workflow|is_function|"    + EOL +
                "+----+---------+--------+------+------+--------------+-----------+-----------+"    + EOL +
                "|1   |{null}   |1       |2     |script|script        |0          |0          |"    + EOL +
                "+----+---------+--------+------+------+--------------+-----------+-----------+",
                    FileIO.localizeLineEndings(selectPrograms().toString()));
        
        assertEquals(
                "+--------------+-----------------+---------------+"    + EOL +
                "|qualified_name|begin_line_number|end_line_number|"    + EOL +
                "+--------------+-----------------+---------------+"    + EOL +
                "|script        |1                |1              |"    + EOL +
                "+--------------+-----------------+---------------+",
                    FileIO.localizeLineEndings(selectProgramLineNumbers().toString()));
    }

    
    public void testExtract_GetModel_ProgramWithOneSubprogram() throws Exception {
        
        String source = 
                "# @begin script"		+ EOL +
                "#   @begin program"	+ EOL +
                "#   @end program"		+ EOL +
                "# @end script"			+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        Program program = modeler.annotations(annotations)
                                 .model()
                                 .getModel()
                                 .program;
 
        assertEquals("script", program.name);
        assertEquals("script", program.beginAnnotation.name);
        assertEquals("script", program.endAnnotation.name);
        assertEquals(0, program.inPorts.length);
        assertEquals(0, program.outPorts.length);
        assertEquals(1, program.programs.length);
        assertEquals(0, program.channels.length);
        
        Program subprogram = program.programs[0];
        assertEquals("script.program", subprogram.name);
        assertFalse(subprogram instanceof Workflow);
        assertEquals("program", subprogram.beginAnnotation.name);
        
        assertEquals(
                "+----+---------+--------+------+-------+--------------+-----------+-----------+"    + EOL +
                "|id  |parent_id|begin_id|end_id|name   |qualified_name|is_workflow|is_function|"    + EOL +
                "+----+---------+--------+------+-------+--------------+-----------+-----------+"    + EOL +
                "|1   |{null}   |1       |4     |script |script        |0          |0          |"    + EOL +
                "|2   |1        |2       |3     |program|script.program|0          |0          |"    + EOL +
                "+----+---------+--------+------+-------+--------------+-----------+-----------+",
                    FileIO.localizeLineEndings(selectPrograms().toString()));
        
        assertEquals(
                "+--------------+-----------------+---------------+"    + EOL +
                "|qualified_name|begin_line_number|end_line_number|"    + EOL +
                "+--------------+-----------------+---------------+"    + EOL +
                "|script        |1                |4              |"    + EOL +
                "|script.program|2                |3              |"    + EOL +
                "+--------------+-----------------+---------------+",
                    FileIO.localizeLineEndings(selectProgramLineNumbers().toString()));
    }

    public void testExtract_GetModel_WorkflowWithOneProgram_TwoLines() throws Exception {
        
        String source = 
                "# @begin script @begin program"	+ EOL +
                "# @end program  @end script"		+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        Program program = modeler.annotations(annotations)
                                             .model()
                                             .getModel()
                                             .program;

        assertEquals("script", program.name);
        assertEquals("script", program.beginAnnotation.name);
        assertEquals("script", program.endAnnotation.name);
        assertEquals(0, program.inPorts.length);
        assertEquals(0, program.outPorts.length);
        assertEquals(1, program.programs.length);
        assertEquals(0, program.channels.length);
        
        Program subprogram = program.programs[0];
        assertEquals("script.program", subprogram.name);
        assertFalse(subprogram instanceof Workflow);
        assertEquals("program", subprogram.beginAnnotation.name);
        
        assertEquals(
                "+----+---------+--------+------+-------+--------------+-----------+-----------+"    + EOL +
                "|id  |parent_id|begin_id|end_id|name   |qualified_name|is_workflow|is_function|"    + EOL +
                "+----+---------+--------+------+-------+--------------+-----------+-----------+"    + EOL +
                "|1   |{null}   |1       |4     |script |script        |0          |0          |"    + EOL +
                "|2   |1        |2       |3     |program|script.program|0          |0          |"    + EOL +
                "+----+---------+--------+------+-------+--------------+-----------+-----------+",
                    FileIO.localizeLineEndings(selectPrograms().toString()));
        
        assertEquals(
                "+--------------+-----------------+---------------+"    + EOL +
                "|qualified_name|begin_line_number|end_line_number|"    + EOL +
                "+--------------+-----------------+---------------+"    + EOL +
                "|script        |1                |2              |"    + EOL +
                "|script.program|1                |2              |"    + EOL +
                "+--------------+-----------------+---------------+",
                    FileIO.localizeLineEndings(selectProgramLineNumbers().toString()));
    }

    
   public void testExtract_GetModel_WorkflowWithOneProgram_MissingFinalEnd() throws Exception {
        
        String source = 
                "# @begin script"       + EOL +
                "#   @begin program"    + EOL +
                "#   @end program"      + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
        		.extract()
                .getAnnotations();

        Exception caughtException = null;
        try {
            modeler.annotations(annotations)
                   .model();
        } catch (YWMarkupException e) {
            caughtException = e;
        }

        assertNotNull(caughtException);
        assertEquals("ERROR: No @end comment paired with '@begin script'" + EOL, caughtException.getMessage());
    }
   
   public void testExtract_GetModel_WorkflowWithOneProgram_MissingBothEnds() throws Exception {
       
       String source = 
               "# @begin script"       + EOL +
               "#   @begin program"    + EOL;

       BufferedReader reader = new BufferedReader(new StringReader(source));
       
       List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
       		    .extract()
                .getAnnotations();

       
       Exception caughtException = null;
       try {
       
           modeler.annotations(annotations)
                  .model();
       
       } catch (YWMarkupException e) {
           caughtException = e;
       }

       assertNotNull(caughtException);
       assertEquals(
               "ERROR: No @end comment paired with '@begin program'"  + EOL +
               "ERROR: No @end comment paired with '@begin script'"   + EOL, 
               caughtException.getMessage()
       );
   }   
   
    
    public void testExtract_GetModel_ProgramWithTwoSubprograms() throws Exception {
        
        String source = 
                "# @begin script"		+ EOL +
                "#   @begin program0"	+ EOL +
                "#   @end program0"		+ EOL +
                "#   @begin program1"	+ EOL +
                "#   @end program1"		+ EOL +
                "# @end script"			+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        Program program = modeler.annotations(annotations)
                                             .model()
                                             .getModel()
                                             .program;
        
        assertEquals("script", program.name);
        assertEquals("script", program.beginAnnotation.name);
        assertEquals("script", program.endAnnotation.name);
        assertEquals(0, program.inPorts.length);
        assertEquals(0, program.outPorts.length);
        assertEquals(2, program.programs.length);
        assertEquals(0, program.channels.length);

        Program program0 = program.programs[0];
        assertEquals("script.program0", program0.name);
        assertFalse(program0 instanceof Workflow);
        assertEquals("program0", program0.beginAnnotation.name);        
        assertEquals("program0", program0.endAnnotation.name);
        assertEquals(0, program0.inPorts.length);
        assertEquals(0, program0.outPorts.length);

        Program program1 = program.programs[1];
        assertEquals("script.program1", program1.name);
        assertFalse(program1 instanceof Workflow);
        assertEquals("program1", program1.beginAnnotation.name);        
        assertEquals("program1", program1.endAnnotation.name);
        assertEquals(0, program1.inPorts.length);
        assertEquals(0, program1.outPorts.length);
        
        assertEquals(
                "+----+---------+--------+------+--------+---------------+-----------+-----------+"    + EOL +
                "|id  |parent_id|begin_id|end_id|name    |qualified_name |is_workflow|is_function|"    + EOL +
                "+----+---------+--------+------+--------+---------------+-----------+-----------+"    + EOL +
                "|1   |{null}   |1       |6     |script  |script         |0          |0          |"    + EOL +
                "|2   |1        |2       |3     |program0|script.program0|0          |0          |"    + EOL +
                "|3   |1        |4       |5     |program1|script.program1|0          |0          |"    + EOL +
                "+----+---------+--------+------+--------+---------------+-----------+-----------+",
                    FileIO.localizeLineEndings(selectPrograms().toString()));
        
        assertEquals(
                "+---------------+-----------------+---------------+"    + EOL +
                "|qualified_name |begin_line_number|end_line_number|"    + EOL +
                "+---------------+-----------------+---------------+"    + EOL +
                "|script         |1                |6              |"    + EOL +
                "|script.program0|2                |3              |"    + EOL +
                "|script.program1|4                |5              |"    + EOL +
                "+---------------+-----------------+---------------+",
                    FileIO.localizeLineEndings(selectProgramLineNumbers().toString()));
    }

    public void testExtract_GetModel_ProgramWithSubSubprogram() throws Exception {
        
        String source = 
                "# @begin program"			    + EOL +
                "#   @begin subprogram"	        + EOL +
                "#     @begin subsubprogram"    + EOL +
                "#     @end subsubprogram"		+ EOL +
                "#   @end subprogram"		    + EOL +
                "# @end program"			    + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        Program program = modeler.annotations(annotations)
                                 .model()
                                 .getModel()
                                     .program;
        
        assertEquals("program", program.name);
        assertEquals("program", program.beginAnnotation.name);
        assertEquals("program", program.endAnnotation.name);
        assertEquals(0, program.inPorts.length);
        assertEquals(0, program.outPorts.length);        
        assertEquals(1, program.programs.length);
        assertEquals(0, program.channels.length);
        
        Program subprogram = program.programs[0];
        assertEquals("program.subprogram", subprogram.name);
        assertEquals("subprogram", subprogram.beginAnnotation.name);
        assertEquals("subprogram", subprogram.endAnnotation.name);
        assertEquals(0, subprogram.inPorts.length);
        assertEquals(0, subprogram.outPorts.length);        
        assertEquals(1, subprogram.programs.length);
        assertEquals(0, subprogram.channels.length);
        
        Program subsubprogram = subprogram.programs[0];
        assertEquals("program.subprogram.subsubprogram", subsubprogram.name);
        assertFalse(subsubprogram instanceof Workflow);
        assertEquals("subsubprogram", subsubprogram.beginAnnotation.name);
        assertEquals("subsubprogram", subsubprogram.endAnnotation.name);
        assertEquals(0, subsubprogram.inPorts.length);
        assertEquals(0, subsubprogram.outPorts.length);
        
        assertEquals(
            "+----+---------+--------+------+-------------+--------------------------------+-----------+-----------+"   + EOL +
            "|id  |parent_id|begin_id|end_id|name         |qualified_name                  |is_workflow|is_function|"   + EOL +
            "+----+---------+--------+------+-------------+--------------------------------+-----------+-----------+"   + EOL +
            "|1   |{null}   |1       |6     |program      |program                         |0          |0          |"   + EOL +
            "|2   |1        |2       |5     |subprogram   |program.subprogram              |0          |0          |"   + EOL +
            "|3   |2        |3       |4     |subsubprogram|program.subprogram.subsubprogram|0          |0          |"   + EOL +
            "+----+---------+--------+------+-------------+--------------------------------+-----------+-----------+",
            FileIO.localizeLineEndings(selectPrograms().toString()));
    
        assertEquals(
            "+--------------------------------+-----------------+---------------+" + EOL +
            "|qualified_name                  |begin_line_number|end_line_number|" + EOL +
            "+--------------------------------+-----------------+---------------+" + EOL +
            "|program                         |1                |6              |" + EOL +
            "|program.subprogram              |2                |5              |" + EOL +
            "|program.subprogram.subsubprogram|3                |4              |" + EOL +
            "+--------------------------------+-----------------+---------------+",
            FileIO.localizeLineEndings(selectProgramLineNumbers().toString()));
    }
    
    public void testExtract_GetModel_OneProgramTwoInsOneOut() throws Exception {
        
        String source = 
                "# @begin script"	+ EOL +
                "# @in x"			+ EOL +
                "# @in y"			+ EOL +
                "# @out z"			+ EOL +
                "  some code"		+ EOL +
                "# @end script"		+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        Program program = modeler.annotations(annotations)
                                 .model()
                                 .getModel()
                                 .program;
        
        assertEquals("script", program.name);
        assertEquals("script", program.beginAnnotation.name);
        assertEquals("script", program.endAnnotation.name);
        assertEquals(2, program.inPorts.length);
        assertEquals(1, program.outPorts.length);
        assertEquals(In.class, program.inPorts[0].flowAnnotation.getClass());
        assertEquals(In.class, program.inPorts[1].flowAnnotation.getClass());
        assertEquals(Out.class, program.outPorts[0].flowAnnotation.getClass());

        assertEquals(
                "+----+---------+--------+------+------+--------------+-----------+-----------+"    + EOL +
                "|id  |parent_id|begin_id|end_id|name  |qualified_name|is_workflow|is_function|"    + EOL +
                "+----+---------+--------+------+------+--------------+-----------+-----------+"    + EOL +
                "|1   |{null}   |1       |5     |script|script        |0          |0          |"    + EOL +
                "+----+---------+--------+------+------+--------------+-----------+-----------+",
                FileIO.localizeLineEndings(selectPrograms().toString()));
        
        assertEquals(
                "+--------------+-----------------+---------------+"    + EOL +
                "|qualified_name|begin_line_number|end_line_number|"    + EOL +
                "+--------------+-----------------+---------------+"    + EOL +
                "|script        |1                |6              |"    + EOL +
                "+--------------+-----------------+---------------+",
                FileIO.localizeLineEndings(selectProgramLineNumbers().toString()));
    }
    
    public void testExtract_GetModel_OneProgramTwoParamsOneOut() throws Exception {
        
        String source = 
                "# @begin script"   + EOL +
                "# @param x"        + EOL +
                "# @param y"        + EOL +
                "# @out z"          + EOL +
                "  some code"       + EOL +
                "# @end script"     + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        Program program = modeler.annotations(annotations)
                                 .model()
                                 .getModel()
                                 .program;
        
        assertEquals("script", program.name);
        assertEquals("script", program.beginAnnotation.name);
        assertEquals("script", program.endAnnotation.name);
        assertEquals(2, program.inPorts.length);
        assertEquals(1, program.outPorts.length);
        assertEquals(Param.class, program.inPorts[0].flowAnnotation.getClass());
        assertEquals("x", program.inPorts[0].flowAnnotation.binding());
        assertEquals(Param.class, program.inPorts[1].flowAnnotation.getClass());
        assertEquals("y", program.inPorts[1].flowAnnotation.binding());
        assertEquals(Out.class, program.outPorts[0].flowAnnotation.getClass());
        assertEquals("z", program.outPorts[0].flowAnnotation.binding());
        
        assertEquals(
                "+----+---------+--------+------+------+--------------+-----------+-----------+"    + EOL +
                "|id  |parent_id|begin_id|end_id|name  |qualified_name|is_workflow|is_function|"    + EOL +
                "+----+---------+--------+------+------+--------------+-----------+-----------+"    + EOL +
                "|1   |{null}   |1       |5     |script|script        |0          |0          |"    + EOL +
                "+----+---------+--------+------+------+--------------+-----------+-----------+",
                FileIO.localizeLineEndings(selectPrograms().toString()));
        
        assertEquals(
                "+--------------+-----------------+---------------+"    + EOL +
                "|qualified_name|begin_line_number|end_line_number|"    + EOL +
                "+--------------+-----------------+---------------+"    + EOL +
                "|script        |1                |6              |"    + EOL +
                "+--------------+-----------------+---------------+",
                FileIO.localizeLineEndings(selectProgramLineNumbers().toString()));
    }

    public void testExtract_GetModel_OneProgramInAndOut_TwoCommentLines() throws Exception {
        
        String source = 
                "# @begin script @in x @in y @out z"	+ EOL +
                "some code                         "	+ EOL +
                "# @end script                     "	+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        Program program = modeler.annotations(annotations)
                                 .model()
                                 .getModel()
                                 .program;
        
        assertEquals("script", program.name);
        assertEquals("script", program.beginAnnotation.name);
        assertEquals("script", program.endAnnotation.name);
        assertEquals(2, program.inPorts.length);
        assertEquals(1, program.outPorts.length);
        
        assertEquals(
                "+----+---------+--------+------+------+--------------+-----------+-----------+"    + EOL +
                "|id  |parent_id|begin_id|end_id|name  |qualified_name|is_workflow|is_function|"    + EOL +
                "+----+---------+--------+------+------+--------------+-----------+-----------+"    + EOL +
                "|1   |{null}   |1       |5     |script|script        |0          |0          |"    + EOL +
                "+----+---------+--------+------+------+--------------+-----------+-----------+",
                FileIO.localizeLineEndings(selectPrograms().toString()));
        
        assertEquals(
                "+--------------+-----------------+---------------+"    + EOL +
                "|qualified_name|begin_line_number|end_line_number|"    + EOL +
                "+--------------+-----------------+---------------+"    + EOL +
                "|script        |1                |3              |"    + EOL +
                "+--------------+-----------------+---------------+",
                FileIO.localizeLineEndings(selectProgramLineNumbers().toString()));
    }

    
    public void testExtract_GetModel_TwoProgramsWithOneChannel_OutAndIn() throws Exception {
        
        String source = 
                "# @begin script"		+ EOL +
                "#   @begin program0"	+ EOL +
                "#	 @out channel"		+ EOL +
                "#   @end program0"		+ EOL +                
                "#   @begin program1"	+ EOL +
                "#	 @in channel"		+ EOL +
                "#   @end program1"		+ EOL +
                "# @end script"			+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = (Workflow)modeler.annotations(annotations)
                                             .model()
                                             .getModel()
                                             .program;
        
        assertEquals("script", workflow.name);
        assertEquals("script", workflow.beginAnnotation.name);
        assertEquals("script", workflow.endAnnotation.name);
        assertEquals(0, workflow.inPorts.length);
        assertEquals(0, workflow.outPorts.length);
        assertEquals(2, workflow.programs.length);
        assertEquals(1, workflow.channels.length);

        Program program0 = workflow.programs[0];
        assertFalse(program0 instanceof Workflow);
        assertEquals("script.program0", program0.name);
        assertEquals("program0", program0.beginAnnotation.name);
        assertEquals("program0", program0.endAnnotation.name);
        assertEquals(0, program0.inPorts.length);
        assertEquals(1, program0.outPorts.length);

        Program program1 = workflow.programs[1];
        assertEquals("script.program1", program1.name);
        assertFalse(program1 instanceof Workflow);
        assertEquals("program1", program1.beginAnnotation.name);
        assertEquals("program1", program1.endAnnotation.name);
        assertEquals(1, program1.inPorts.length);
        assertEquals(0, program1.outPorts.length);
        
        Channel channel = workflow.channels[0];
        assertEquals("script.program0", channel.sourceProgram.name);
        assertEquals("channel", channel.sourcePort.flowAnnotation.name);
        assertEquals(Out.class, channel.sourcePort.flowAnnotation.getClass());

        assertEquals("script.program1", channel.sinkProgram.name);
        assertEquals("channel", channel.sinkPort.flowAnnotation.name);
        assertEquals(In.class, channel.sinkPort.flowAnnotation.getClass());

        assertEquals(
                "+----+---------+--------+------+--------+---------------+-----------+-----------+"   + EOL +
                "|id  |parent_id|begin_id|end_id|name    |qualified_name |is_workflow|is_function|"   + EOL +
                "+----+---------+--------+------+--------+---------------+-----------+-----------+"   + EOL +
                "|1   |{null}   |1       |8     |script  |script         |1          |0          |"   + EOL +
                "|2   |1        |2       |4     |program0|script.program0|0          |0          |"   + EOL +
                "|3   |1        |5       |7     |program1|script.program1|0          |0          |"   + EOL +
                "+----+---------+--------+------+--------+---------------+-----------+-----------+",
                FileIO.localizeLineEndings(selectPrograms().toString()));
        
        assertEquals(
                "+---------------+-----------------+---------------+"   + EOL +
                "|qualified_name |begin_line_number|end_line_number|"   + EOL +
                "+---------------+-----------------+---------------+"   + EOL +
                "|script         |1                |8              |"   + EOL +
                "|script.program0|2                |4              |"   + EOL +
                "|script.program1|5                |7              |"   + EOL +
                "+---------------+-----------------+---------------+",
                FileIO.localizeLineEndings(selectProgramLineNumbers().toString()));
    }

    public void testExtract_GetModel_TwoProgramsWithOneChannel_OutAndParam() throws Exception {
        
        String source = 
                "# @begin script"       + EOL +
                "#   @begin program0"   + EOL +
                "#   @out channel"      + EOL +
                "#   @end program0"     + EOL +                
                "#   @begin program1"   + EOL +
                "#   @param channel"    + EOL +
                "#   @end program1"     + EOL +
                "# @end script"         + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = (Workflow)modeler.annotations(annotations)
                                             .model()
                                             .getModel()
                                             .program;
        
        assertEquals("script", workflow.name);
        assertEquals("script", workflow.beginAnnotation.name);
        assertEquals("script", workflow.endAnnotation.name);
        assertEquals(0, workflow.inPorts.length);
        assertEquals(0, workflow.outPorts.length);
        assertEquals(2, workflow.programs.length);
        assertEquals(1, workflow.channels.length);

        Program program0 = workflow.programs[0];
        assertFalse(program0 instanceof Workflow);
        assertEquals("script.program0", program0.name);
        assertEquals("program0", program0.beginAnnotation.name);
        assertEquals("program0", program0.endAnnotation.name);
        assertEquals(0, program0.inPorts.length);
        assertEquals(1, program0.outPorts.length);

        Program program1 = workflow.programs[1];
        assertFalse(program1 instanceof Workflow);
        assertEquals("script.program1", program1.name);
        assertEquals("program1", program1.beginAnnotation.name);
        assertEquals("program1", program1.endAnnotation.name);
        assertEquals(1, program1.inPorts.length);
        assertEquals(0, program1.outPorts.length);
        
        Channel channel = workflow.channels[0];
        assertEquals("script.program0", channel.sourceProgram.name);
        assertEquals("channel", channel.sourcePort.flowAnnotation.name);
        assertEquals(Out.class, channel.sourcePort.flowAnnotation.getClass());

        assertEquals("script.program1", channel.sinkProgram.name);
        assertEquals("channel", channel.sinkPort.flowAnnotation.name);
        assertEquals(Param.class, channel.sinkPort.flowAnnotation.getClass());

        assertEquals(
                "+----+---------+--------+------+--------+---------------+-----------+-----------+"   + EOL +
                "|id  |parent_id|begin_id|end_id|name    |qualified_name |is_workflow|is_function|"   + EOL +
                "+----+---------+--------+------+--------+---------------+-----------+-----------+"   + EOL +
                "|1   |{null}   |1       |8     |script  |script         |1          |0          |"   + EOL +
                "|2   |1        |2       |4     |program0|script.program0|0          |0          |"   + EOL +
                "|3   |1        |5       |7     |program1|script.program1|0          |0          |"   + EOL +
                "+----+---------+--------+------+--------+---------------+-----------+-----------+",
                FileIO.localizeLineEndings(selectPrograms().toString()));
        
        assertEquals(
                "+---------------+-----------------+---------------+"   + EOL +
                "|qualified_name |begin_line_number|end_line_number|"   + EOL +
                "+---------------+-----------------+---------------+"   + EOL +
                "|script         |1                |8              |"   + EOL +
                "|script.program0|2                |4              |"   + EOL +
                "|script.program1|5                |7              |"   + EOL +
                "+---------------+-----------------+---------------+",
                FileIO.localizeLineEndings(selectProgramLineNumbers().toString()));
    }

    
   public void testExtract_GetModel_TwoProgramsWithOneChannel_CombinedCommentLines() throws Exception {
        
        String source = 
                "# @begin script                   "	+ EOL +
                "                                  "	+ EOL +
                "#   @begin program0 @out channel  "	+ EOL +
                "    some code in program0         "	+ EOL +
                "#   @end program0                 "    + EOL +
                "                                  "	+ EOL +
                "    some code in script           "    + EOL +
                "                                  "	+ EOL +
                "#   @begin program1 @in channel   "	+ EOL +
                "    some code in program1         "	+ EOL +
                "#   @end program1                 "	+ EOL +
                "                                  "	+ EOL +
                "# @end script                     "	+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = (Workflow)modeler.annotations(annotations)
                                             .model()
                                             .getModel()
                                             .program;
        
        assertEquals("script", workflow.name);
        assertEquals("script", workflow.beginAnnotation.name);
        assertEquals("script", workflow.endAnnotation.name);
        assertEquals(0, workflow.inPorts.length);
        assertEquals(0, workflow.outPorts.length);
        assertEquals(2, workflow.programs.length);
        assertEquals(1, workflow.channels.length);

        Program program0 = workflow.programs[0];
        assertFalse(program0 instanceof Workflow);
        assertEquals("script.program0", program0.name);
        assertEquals("program0", program0.beginAnnotation.name);
        assertEquals("program0", program0.endAnnotation.name);
        assertEquals(0, program0.inPorts.length);
        assertEquals(1, program0.outPorts.length);

        Program program1 = workflow.programs[1];
        assertFalse(program1 instanceof Workflow);
        assertEquals("script.program1", program1.name);
        assertEquals("program1", program1.beginAnnotation.name);
        assertEquals("program1", program1.endAnnotation.name);
        assertEquals(1, program1.inPorts.length);
        assertEquals(0, program1.outPorts.length);
        
        Channel channel = workflow.channels[0];
        assertEquals("script.program0", channel.sourceProgram.name);
        assertEquals("channel", channel.sourcePort.flowAnnotation.name);
        assertEquals("script.program1", channel.sinkProgram.name);
        assertEquals("channel", channel.sinkPort.flowAnnotation.name);

        assertEquals(
                "+----+---------+--------+------+--------+---------------+-----------+-----------+"   + EOL +
                "|id  |parent_id|begin_id|end_id|name    |qualified_name |is_workflow|is_function|"   + EOL +
                "+----+---------+--------+------+--------+---------------+-----------+-----------+"   + EOL +
                "|1   |{null}   |1       |8     |script  |script         |1          |0          |"   + EOL +
                "|2   |1        |2       |4     |program0|script.program0|0          |0          |"   + EOL +
                "|3   |1        |5       |7     |program1|script.program1|0          |0          |"   + EOL +
                "+----+---------+--------+------+--------+---------------+-----------+-----------+",
                    FileIO.localizeLineEndings(selectPrograms().toString()));
        
        assertEquals(
                "+---------------+-----------------+---------------+"   + EOL +
                "|qualified_name |begin_line_number|end_line_number|"   + EOL +
                "+---------------+-----------------+---------------+"   + EOL +
                "|script         |1                |13             |"   + EOL +
                "|script.program0|3                |5              |"   + EOL +
                "|script.program1|9                |11             |"   + EOL +
                "+---------------+-----------------+---------------+",
                    FileIO.localizeLineEndings(selectProgramLineNumbers().toString()));
   }
    
    
    public void testExtract_GetModel_ThreeProgramsMultipleChannels() throws Exception {
        
        String source = 
                "# @begin script"		+ EOL +
                "#"						+ EOL +
                "#   @begin program0"	+ EOL +
                "#	 @out channel0"		+ EOL +
                "#	 @out channel1"		+ EOL +
                "#   @end program0"		+ EOL +                
                "#"						+ EOL +
                "#   @begin program1"	+ EOL +
                "#	 @in channel0"		+ EOL +
                "#   @end program1"		+ EOL +
                "#"						+ EOL +
                "#   @begin program2"	+ EOL +
                "#	 @param channel1"	+ EOL +
                "#   @end program2"		+ EOL +
                "#"						+ EOL +
                "# @end script"			+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = (Workflow)modeler.annotations(annotations)
                                             .model()
                                             .getModel()
                                             .program;
        
        assertEquals("script", workflow.name);
        assertEquals("script", workflow.beginAnnotation.name);
        assertEquals("script", workflow.endAnnotation.name);
        assertEquals(0, workflow.inPorts.length);
        assertEquals(0, workflow.outPorts.length);
        assertEquals(3, workflow.programs.length);
        assertEquals(2, workflow.channels.length);

        Program program0 = workflow.programs[0];
        assertFalse(program0 instanceof Workflow);
        assertEquals("script.program0", program0.name);
        assertEquals("program0", program0.beginAnnotation.name);
        assertEquals("program0", program0.endAnnotation.name);
        assertEquals(0, program0.inPorts.length);
        assertEquals(2, program0.outPorts.length);

        Program program1 = workflow.programs[1];
        assertFalse(program1 instanceof Workflow);
        assertEquals("script.program1", program1.name);
        assertEquals("program1", program1.beginAnnotation.name);
        assertEquals("program1", program1.endAnnotation.name);
        assertEquals(1, program1.inPorts.length);
        assertEquals(0, program1.outPorts.length);

        Program program2 = workflow.programs[2];
        assertFalse(program2 instanceof Workflow);
        assertEquals("script.program2", program2.name);
        assertEquals("program2", program2.beginAnnotation.name);
        assertEquals("program2", program2.endAnnotation.name);
        assertEquals(1, program1.inPorts.length);
        assertEquals(0, program1.outPorts.length);
        
        Channel channel0 = workflow.channels[0];
        assertEquals("script.program0", channel0.sourceProgram.name);
        assertEquals("channel0", channel0.sourcePort.flowAnnotation.name);
        assertEquals("script.program1", channel0.sinkProgram.name);
        assertEquals("channel0", channel0.sinkPort.flowAnnotation.name);
        assertEquals(In.class, channel0.sinkPort.flowAnnotation.getClass());

        Channel channel1 = workflow.channels[1];
        assertEquals("script.program0", channel1.sourceProgram.name);
        assertEquals("channel1", channel1.sourcePort.flowAnnotation.name);
        assertEquals("script.program2", channel1.sinkProgram.name);
        assertEquals("channel1", channel1.sinkPort.flowAnnotation.name);
        assertEquals(Param.class, channel1.sinkPort.flowAnnotation.getClass());
        
        assertEquals(
                "+----+---------+--------+------+--------+---------------+-----------+-----------+"   + EOL +
                "|id  |parent_id|begin_id|end_id|name    |qualified_name |is_workflow|is_function|"   + EOL +
                "+----+---------+--------+------+--------+---------------+-----------+-----------+"   + EOL +
                "|1   |{null}   |1       |12    |script  |script         |1          |0          |"   + EOL +
                "|2   |1        |2       |5     |program0|script.program0|0          |0          |"   + EOL +
                "|3   |1        |6       |8     |program1|script.program1|0          |0          |"   + EOL +
                "|4   |1        |9       |11    |program2|script.program2|0          |0          |"   + EOL +
                "+----+---------+--------+------+--------+---------------+-----------+-----------+",
                    FileIO.localizeLineEndings(selectPrograms().toString()));
        
        assertEquals(
                "+---------------+-----------------+---------------+"   + EOL +
                "|qualified_name |begin_line_number|end_line_number|"   + EOL +
                "+---------------+-----------------+---------------+"   + EOL +
                "|script         |1                |16             |"   + EOL +
                "|script.program0|3                |6              |"   + EOL +
                "|script.program1|8                |10             |"   + EOL +
                "|script.program2|12               |14             |"   + EOL +
                "+---------------+-----------------+---------------+",
                    FileIO.localizeLineEndings(selectProgramLineNumbers().toString()));
    }
   
   
   public void testExtract_GetCommentLines_OneComment_Hash() throws Exception {
       
       String source = "# @begin main" + EOL;
       
       BufferedReader reader = new BufferedReader(new StringReader(source));

       List<Annotation> annotations = extractor
               .configure("comment", "#")
                .reader(reader)
               	.extract()
               	.getAnnotations();
       
       Exception caughtException = null;
       try {
           
           modeler.annotations(annotations)
                  .model();
           
       } catch (YWMarkupException e) {
           caughtException = e;
       }
       
       assertNotNull(caughtException);
       assertEquals("ERROR: No @end comment paired with '@begin main'" + EOL, caughtException.getMessage());
       
       assertEquals("", super.stdoutBuffer.toString());
       assertEquals("", super.stderrBuffer.toString());
   }
   
   public void testExtract_GetComments_OneBeginComment() throws Exception {
       
       String source = "# @begin main" + EOL;
       
       BufferedReader reader = new BufferedReader(new StringReader(source));
       
       List<Annotation> annotations = extractor
               .configure("comment", "#")
                .reader(reader)
               	.extract()
               	.getAnnotations();
       
       Exception caughtException = null;
       try {
       
           modeler.annotations(annotations)
                  .model();

       } catch (YWMarkupException e) {
           caughtException = e;
       }
       
       assertNotNull(caughtException);
       assertEquals("ERROR: No @end comment paired with '@begin main'" + EOL, caughtException.getMessage());
   }
   
    public void testModel_SimulateDataCollection() throws FileNotFoundException, Exception {       

        List<Annotation> annotations = extractor
               .configure("language", "python")
               .reader(new FileReader("src/main/resources/examples/simulate_data_collection/simulate_data_collection.py"))
               .extract()
               .getAnnotations();
        
        modeler.annotations(annotations)
               .model();
    }
}
