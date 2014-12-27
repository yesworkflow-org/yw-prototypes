package org.yesworkflow;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.yesworkflow.comments.BeginComment;
import org.yesworkflow.comments.Comment;
import org.yesworkflow.comments.EndComment;
import org.yesworkflow.comments.InComment;
import org.yesworkflow.comments.OutComment;
import org.yesworkflow.model.Program;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestDefaultExtractor extends YesWorkflowTestCase {

    DefaultExtractor extractor = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new DefaultExtractor();
    }

    public void testExtract_BlankLine() throws Exception {
        
        String source = "  " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .commentCharacter('#')
                 .extract();
        
        List<String> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
    }

    public void testExtract_BlankComment() throws Exception {
        
        String source = "#  " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .commentCharacter('#')
                 .extract();
        
        List<String> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
    }
    
    public void testExtract_NonComment() throws Exception {
        
        String source = "# a comment " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .commentCharacter('#')
                 .extract();
        
        List<String> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
    }
    

    public void testExtract_NonYWComment() throws Exception {
        
        String source = "# a comment " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .commentCharacter('#')
                 .extract();
        
        List<String> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
    }
    
    public void testExtract_GetCommentLines_OneComment_Hash() throws Exception {
        
        String source = "# @begin main" + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .commentCharacter('#')
                 .extract();
        
        List<String> commentLines = extractor.getLines();
        assertEquals(1,commentLines.size());
        assertEquals("@begin main", commentLines.get(0));
    }

    public void testExtract_GetCommentLines_MultipleComments_Hash() throws Exception {
        
        String source = 
                "## @begin step   "  + EOL +
                "  some code "      + EOL +
                "   # @in x  "      + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                " #    @out y"      + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .commentCharacter('#')
                 .extract();
        
        List<String> commentLines = extractor.getLines();
        assertEquals(3,commentLines.size());
        assertEquals("@begin step", commentLines.get(0));
        assertEquals("@in x", commentLines.get(1));
        assertEquals("@out y", commentLines.get(2));
    }

    public void testExtract_GetCommentLines_MultipleComments_Slash() throws Exception {
        
        String source = 
                "// @begin step   " + EOL +
                "  some code "      + EOL +
                "   // @in x  "     + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                " //    @out y"     + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .commentCharacter('/')
                 .extract();
        
        List<String> commentLines = extractor.getLines();
        assertEquals(3, commentLines.size());
        assertEquals("@begin step", commentLines.get(0));
        assertEquals("@in x", commentLines.get(1));
        assertEquals("@out y", commentLines.get(2));
    }
    
    public void testExtract_GetComments_OneComment() throws Exception {
        
        String source = "# @begin main" + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .commentCharacter('#')
                 .extract();
        
        List<Comment> comments = extractor.getComments();
        
        assertEquals(1, comments.size());
        BeginComment comment = (BeginComment) comments.get(0);
        assertEquals("main", comment.programName);
        assertNull(comment.description);
    }

    public void testExtract_GetComments_MultipleComments() throws Exception {
        
        String source = 
                "## @begin step   "  + EOL +
                "  some code "      + EOL +
                "   # @in x  "      + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                " #    @out y"      + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                "    #  @end step"  + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .commentCharacter('#')
                 .extract();
        
        List<Comment> comments = extractor.getComments();

        assertEquals(4, comments.size());
        
        BeginComment begin = (BeginComment) comments.get(0);
        assertEquals("step", begin.programName);
        assertNull(begin.description);

        InComment in = (InComment) comments.get(1);
        assertEquals("x", in.binding);
        assertNull(in.description);

        OutComment out = (OutComment) comments.get(2);
        assertEquals("y", out.binding);
        assertNull(out.description);

        EndComment end = (EndComment) comments.get(3);
        assertEquals("step", end.programName);
        assertNull(end.description);
    }
    
    public void testExtract_GetComments_SamplePyScript() throws Exception {
        
        extractor.sourcePath("src/main/resources/example.py")
                 .commentCharacter('#')
                 .extract();
        
        List<Comment> comments = extractor.getComments();

        assertEquals(22, comments.size());
        
        BeginComment begin0 = (BeginComment) comments.get(0);
        assertEquals("main", begin0.programName);
        assertNull(begin0.description);
        
        InComment in1 = (InComment) comments.get(1);
        assertEquals("LandWaterMask_Global_CRUNCEP.nc", in1.binding);
        assertNull(in1.description);

        InComment in2 = (InComment) comments.get(2);
        assertEquals("NEE_first_year.nc", in2.binding);
        assertNull(in2.description);
        
        OutComment out3 = (OutComment) comments.get(3);
        assertEquals("result_simple.pdf", out3.binding);
        assertNull(out3.description);

        BeginComment begin4 = (BeginComment) comments.get(4);
        assertEquals("fetch_mask", begin4.programName);
        assertNull(begin4.description);
        
        InComment in5 = (InComment) comments.get(5);
        assertEquals("\"LandWaterMask_Global_CRUNCEP.nc\"", in5.binding);
        assertEquals("input_mask_file", in5.label);
        assertNull(in5.description);

        OutComment out6 = (OutComment) comments.get(6);
        assertEquals("mask", out6.binding);
        assertEquals("land_water_mask", out6.label);
        assertNull(out6.description);
        
        EndComment end7 = (EndComment) comments.get(7);
        assertEquals("fetch_mask", end7.programName);
        assertNull(end7.description);
        
        BeginComment begin8 = (BeginComment) comments.get(8);
        assertEquals("load_data", begin8.programName);
        assertNull(begin8.description);

        InComment in9 = (InComment) comments.get(9);
        assertEquals("\"CLM4_BG1_V1_Monthly_NEE.nc4\"", in9.binding);
        assertEquals("input", in9.label);
        assertEquals("data file", in9.description);

        OutComment out10 = (OutComment) comments.get(10);
        assertEquals("data", out10.binding);
        assertEquals("NEE_data", out10.label);
        assertNull(out10.description);
        
        EndComment end11 = (EndComment) comments.get(11);
        assertEquals("load_data", end11.programName);
        assertNull(end11.description);        
    }
    
    public void testExtract_GetModel_OneProgram() throws Exception {
        
        String source = 
                "# @begin script"	+ EOL +
                "  some code"		+ EOL +
                "# @end script"		+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .commentCharacter('#')
                 .extract();        
        Program model = extractor.getModel();
        
        assertFalse(model instanceof Workflow);
        assertEquals("script", model.comment.programName);
    }

    public void testExtract_GetModel_WorkflowWithOneProgram() throws Exception {
        
        String source = 
                "# @begin script"		+ EOL +
                "#   @begin program"	+ EOL +
                "#   @end program"		+ EOL +
                "# @end step"			+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .commentCharacter('#')
                 .extract();        
        Workflow model = (Workflow)extractor.getModel();
        
        assertEquals("script", model.comment.programName);
        assertEquals(1, model.programs.size());
        assertEquals(0, model.channels.size());
        
        Program program = model.programs.get(0);
        assertFalse(program instanceof Workflow);
        assertEquals("program", program.comment.programName);
    }

    public void testExtract_GetModel_WorkflowWithTwoPrograms() throws Exception {
        
        String source = 
                "# @begin script"		+ EOL +
                "#   @begin program0"	+ EOL +
                "#   @end program0"		+ EOL +
                "#   @begin program1"	+ EOL +
                "#   @end program1"		+ EOL +
                "# @end step"			+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .commentCharacter('#')
                 .extract();        
        Workflow model = (Workflow)extractor.getModel();
        
        assertEquals("script", model.comment.programName);
        assertEquals(2, model.programs.size());
        assertEquals(0, model.channels.size());

        Program program0 = model.programs.get(0);
        assertFalse(program0 instanceof Workflow);
        assertEquals("program0", program0.comment.programName);        

        Program program1 = model.programs.get(1);
        assertFalse(program1 instanceof Workflow);
        assertEquals("program1", program1.comment.programName);        
    }

    public void testExtract_GetModel_WorkflowWithSubworkflow() throws Exception {
        
        String source = 
                "# @begin workflow"			+ EOL +
                "#   @begin subworkflow"	+ EOL +
                "#     @begin program"		+ EOL +
                "#     @end program"		+ EOL +
                "#   @end subworkflow"		+ EOL +
                "# @end workflow"			+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .commentCharacter('#')
                 .extract();
        
        Workflow model = (Workflow)extractor.getModel();
        assertEquals(1, model.programs.size());
        assertEquals(0, model.channels.size());
        
        Workflow subworkflow = (Workflow)model.programs.get(0);
        assertEquals("subworkflow", subworkflow.comment.programName);
        assertEquals(1, subworkflow.programs.size());
        assertEquals(0, subworkflow.channels.size());
        
        Program program = subworkflow.programs.get(0);
        assertFalse(program instanceof Workflow);
        assertEquals("program", program.comment.programName);
    }
}