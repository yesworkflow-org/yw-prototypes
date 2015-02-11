package org.yesworkflow.extract;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.yesworkflow.LanguageModel;
import org.yesworkflow.LanguageModel.Language;
import org.yesworkflow.comments.BeginComment;
import org.yesworkflow.comments.Comment;
import org.yesworkflow.comments.EndComment;
import org.yesworkflow.comments.InComment;
import org.yesworkflow.comments.OutComment;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestDefaultExtractor extends YesWorkflowTestCase {

    DefaultExtractor extractor = null;
    LanguageModel languageModel = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        languageModel = new LanguageModel(Language.PYTHON);
        extractor = new DefaultExtractor(super.stdoutStream, super.stderrStream);
        extractor.languageModel(languageModel);
    }

    public void testExtract_BlankLine() throws Exception {
        
        String source = "  " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .extract();
        
        List<String> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
    }

    public void testExtract_BlankComment() throws Exception {
        
        String source = "#  " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .extract();
        
        List<String> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
    }
    
    public void testExtract_NonComment() throws Exception {
        
        String source = "not a comment " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .extract();
        
        List<String> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
   }
    

    public void testExtract_NonYWComment() throws Exception {
        
        String source = "# a comment " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .extract();
        
        List<String> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
    }
    
    public void testExtract_NonYWComment_WithAtSymbol() throws Exception {
        
        String source = "# a comment with an @ symbol in it " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .extract();
        
        List<String> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
    }

    public void testExtract_GetCommentLines_MultipleComments_Hash() throws Exception {
        
        String source = 
                "## @begin step   " + EOL +
                "  some code "      + EOL +
                "   # @in x  "      + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                " #    @out y"      + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                " ##    @end step"  + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .extract();
        
        List<String> commentLines = extractor.getLines();
        assertEquals(4,commentLines.size());
        assertEquals("@begin step", commentLines.get(0));
        assertEquals("@in x", commentLines.get(1));
        assertEquals("@out y", commentLines.get(2));
        assertEquals("@end step", commentLines.get(3));
    }

    public void testExtract_GetCommentLines_MultipleComments_Slash() throws Exception {
        
        languageModel = new LanguageModel();
        languageModel.singleDelimiter("//");
        extractor = new DefaultExtractor(super.stdoutStream, super.stderrStream);
        extractor.languageModel(languageModel);

        String source = 
                "// @begin step   " + EOL +
                "  some code "      + EOL +
                "   // @in x  "     + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                " //    @out y"     + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                " //    @end step"  + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .extract();
        
        List<String> commentLines = extractor.getLines();
        assertEquals(4, commentLines.size());
        assertEquals("@begin step", commentLines.get(0));
        assertEquals("@in x", commentLines.get(1));
        assertEquals("@out y", commentLines.get(2));
        assertEquals("@end step", commentLines.get(3));
    }

    public void testExtract_GetComments_MultipleComments() throws Exception {
        
        String source = 
                "## @begin step   " + EOL +
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
                 .extract();
        
        List<Comment> comments = extractor.getComments();

        assertEquals(4, comments.size());
        
        BeginComment begin = (BeginComment) comments.get(0);
        assertEquals("step", begin.programName);
        assertNull(begin.description);

        InComment in = (InComment) comments.get(1);
        assertEquals("x", in.name);
        assertNull(in.description);

        OutComment out = (OutComment) comments.get(2);
        assertEquals("y", out.name);
        assertNull(out.description);

        EndComment end = (EndComment) comments.get(3);
        assertEquals("step", end.programName);
        assertNull(end.description);
    }
    
    public void testExtract_GetComments_SamplePyScript() throws Exception {
        
        extractor.sourcePath("src/main/resources/example.py")
                 .extract();
        
        List<Comment> comments = extractor.getComments();

        assertEquals(22, comments.size());
        
        BeginComment begin0 = (BeginComment) comments.get(0);
        assertEquals("main", begin0.programName);
        assertNull(begin0.description);
        
        InComment in1 = (InComment) comments.get(1);
        assertEquals("LandWaterMask_Global_CRUNCEP.nc", in1.name);
        assertNull(in1.description);

        InComment in2 = (InComment) comments.get(2);
        assertEquals("NEE_first_year.nc", in2.name);
        assertNull(in2.description);
        
        OutComment out3 = (OutComment) comments.get(3);
        assertEquals("result_simple.pdf", out3.name);
        assertNull(out3.description);

        BeginComment begin4 = (BeginComment) comments.get(4);
        assertEquals("fetch_mask", begin4.programName);
        assertNull(begin4.description);
        
        InComment in5 = (InComment) comments.get(5);
        assertEquals("\"LandWaterMask_Global_CRUNCEP.nc\"", in5.name);
        assertEquals("input_mask_file", in5.alias);
        assertNull(in5.description);

        OutComment out6 = (OutComment) comments.get(6);
        assertEquals("mask", out6.name);
        assertEquals("land_water_mask", out6.alias);
        assertNull(out6.description);
        
        EndComment end7 = (EndComment) comments.get(7);
        assertEquals("fetch_mask", end7.programName);
        assertNull(end7.description);
        
        BeginComment begin8 = (BeginComment) comments.get(8);
        assertEquals("load_data", begin8.programName);
        assertNull(begin8.description);

        InComment in9 = (InComment) comments.get(9);
        assertEquals("\"CLM4_BG1_V1_Monthly_NEE.nc4\"", in9.name);
        assertEquals("input_data_file", in9.alias);
        assertEquals(null, in9.description);

        OutComment out10 = (OutComment) comments.get(10);
        assertEquals("data", out10.name);
        assertEquals("NEE_data", out10.alias);
        assertNull(out10.description);
        
        EndComment end11 = (EndComment) comments.get(11);
        assertEquals("load_data", end11.programName);
        assertNull(end11.description);        
    }
}