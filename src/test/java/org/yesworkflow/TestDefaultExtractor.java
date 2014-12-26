package org.yesworkflow;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.yesworkflow.util.YesWorkflowTestCase;

public class TestDefaultExtractor extends YesWorkflowTestCase {

    DefaultExtractor extractor = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new DefaultExtractor();
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

}
