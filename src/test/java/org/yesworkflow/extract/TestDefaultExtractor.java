package org.yesworkflow.extract;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.yesworkflow.Language;
import org.yesworkflow.LanguageModel;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.End;
import org.yesworkflow.annotations.In;
import org.yesworkflow.annotations.Out;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.YesWorkflowTestCase;

public class TestDefaultExtractor extends YesWorkflowTestCase {

    YesWorkflowDB ywdb = null;
    DefaultExtractor extractor = null;
    LanguageModel languageModel = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.ywdb = YesWorkflowDB.createVolatileDB();
        extractor = new DefaultExtractor(this.ywdb, super.stdoutStream, super.stderrStream);
        extractor.configure("language", Language.PYTHON);
    }

    public void testExtract_BlankLine() throws Exception {
        
        String source = "  " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<SourceLine> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
    }

    public void testExtract_BlankComment() throws Exception {
        
        String source = "#  " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<SourceLine> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
    }
    
    public void testExtract_NonComment() throws Exception {
        
        String source = "not a comment " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<SourceLine> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
   }
    

    public void testExtract_NonYWComment() throws Exception {
        
        String source = "# a comment " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<SourceLine> commentLines = extractor.getLines();
        assertEquals(0,commentLines.size());
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
    }
    
    public void testExtract_NonYWComment_WithAtSymbol() throws Exception {
        
        String source = "# a comment with an @ symbol in it " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<SourceLine> commentLines = extractor.getLines();
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
        
        extractor.reader(reader)
                 .extract();
        
        List<SourceLine> commentLines = extractor.getLines();
        assertEquals(4,commentLines.size());
        assertEquals("@begin step", commentLines.get(0).text);
        assertEquals("@in x", commentLines.get(1).text);
        assertEquals("@out y", commentLines.get(2).text);
        assertEquals("@end step", commentLines.get(3).text);
    }

    public void testExtract_GetCommentLines_MultipleComments_Slash() throws Exception {
        
        extractor = new DefaultExtractor(this.ywdb, super.stdoutStream, super.stderrStream);
        extractor.configure("comment", "//");

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
        
        extractor.reader(reader)
                 .extract();
        
        List<SourceLine> commentLines = extractor.getLines();
        assertEquals(4, commentLines.size());
        assertEquals("@begin step", commentLines.get(0).text);
        assertEquals("@in x", commentLines.get(1).text);
        assertEquals("@out y", commentLines.get(2).text);
        assertEquals("@end step", commentLines.get(3).text);
    }

    public void testExtract_GetComments_MultipleComments() throws Exception {
        
        String source = 
                "## @begin step   " + EOL +
                "  some code "      + EOL +
                "   # @in x  "      + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                " #    @param y"    + EOL +
                "     more code"    + EOL +
                "     more code"    + EOL +
                "    #  @end step"  + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<String> comments = extractor.getComments();

        assertEquals(4, comments.size());
        assertEquals("@begin step", comments.get(0));
        assertEquals("@in x", comments.get(1));
        assertEquals("@param y", comments.get(2));
        assertEquals("@end step", comments.get(3));
    }
    
    public void testExtract_GetComments_MultipleComments_WithAliasesOnSameLines() throws Exception {
        
        String source = 
                "## @begin step   " 	   + EOL +
                "  some code "      	   + EOL +
                "   # @in x @as horiz "    + EOL +
                "     more code"    	   + EOL +
                "     more code"    	   + EOL +
                " #    @param y @as vert"  + EOL +
                "     more code"    	   + EOL +
                "     more code"    	   + EOL +
                "    #  @end step"  	   + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<String> comments = extractor.getComments();

        assertEquals(6, comments.size());
        assertEquals("@begin step", comments.get(0));
        assertEquals("@in x", comments.get(1));
        assertEquals("@as horiz", comments.get(2));
        assertEquals("@param y", comments.get(3));
        assertEquals("@as vert", comments.get(4));
        assertEquals("@end step", comments.get(5));        
    }
    
    
    public void testExtract_GetComments_MultipleComments_WithAliasesOnDifferentLines() throws Exception {
        
        String source = 
                "## @begin step   " 	+ EOL +
                "  some code "      	+ EOL +
                "   # @in x" 			+ EOL +
                "    # @as horiz "		+ EOL +
                "     more code"    	+ EOL +
                "     more code"    	+ EOL +
                " #    @param y  "		+ EOL +
                "  #@as vert"			+ EOL +
                "     more code"    	+ EOL +
                "     more code"    	+ EOL +
                "    #  @end step"  	+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<String> comments = extractor.getComments();

        assertEquals(6, comments.size());
        assertEquals("@begin step", comments.get(0));
        assertEquals("@in x", comments.get(1));
        assertEquals("@as horiz", comments.get(2));
        assertEquals("@param y", comments.get(3));
        assertEquals("@as vert", comments.get(4));
        assertEquals("@end step", comments.get(5));        
    }
    
    public void testExtract_GetComments_MultipleCommentsOnOneLine() throws Exception {
        
        String source = "# @begin step @in x @as horiz @param y @as vert @end step";

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.reader(reader)
                 .extract();
        
        List<String> comments = extractor.getComments();

        assertEquals(6, comments.size());
        assertEquals("@begin step", comments.get(0));
        assertEquals("@in x", comments.get(1));
        assertEquals("@as horiz", comments.get(2));
        assertEquals("@param y", comments.get(3));
        assertEquals("@as vert", comments.get(4));
        assertEquals("@end step", comments.get(5));    
    }

    public void testExtract_GetAnnotations_MultipleComments() throws Exception {
        
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
        
        extractor.reader(reader)
                 .extract();
        
        List<Annotation> annotations = extractor.getAnnotations();

        assertEquals(4, annotations.size());
        
        Begin begin = (Begin) annotations.get(0);
        assertEquals("step", begin.name);
        assertNull(begin.description());

        In in = (In) annotations.get(1);
        assertEquals("x", in.name);
        assertEquals("x", in.binding());
        assertNull(in.description());

        Out out = (Out) annotations.get(2);
        assertEquals("y", out.name);
        assertEquals("y", out.binding());
        assertNull(out.description());

        End end = (End) annotations.get(3);
        assertEquals("step", end.name);
        assertNull(end.description());
    }
}