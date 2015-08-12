package org.yesworkflow.extract;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;

import org.yesworkflow.Language;
import org.yesworkflow.LanguageModel;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.YesWorkflowTestCase;

public class TestSkeletonBuilder extends YesWorkflowTestCase {

    private YesWorkflowDB ywdb = null;
    DefaultExtractor extractor = null;
    LanguageModel languageModel = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        ywdb = YesWorkflowDB.createInMemoryDB();
        extractor = new DefaultExtractor(this.ywdb, super.stdoutStream, super.stderrStream);
        extractor.configure("language", Language.PYTHON);
    }

    public void testSkeletonBuilder_EmptySource() throws Exception {
        
        String source = "";
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        String skeleton = extractor.reader(reader)
                                   .extract()
                                   .getSkeleton();
        
        assertTrue(skeleton.isEmpty());
        
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
    }

    public void testSkeletonBuilder_BlankLine() throws Exception {
        
        String source = "  " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        String skeleton = extractor.reader(reader)
                                   .extract()
                                   .getSkeleton();
        
        assertTrue(skeleton.isEmpty());
        
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
    }

    public void testSkeletonBuilder_BlankComment() throws Exception {
        
        String source = "#  " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        String skeleton = extractor.reader(reader)
                .extract()
                .getSkeleton();

        assertTrue(skeleton.isEmpty());
        
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
    }
    
    public void testSkeletonBuilder_NonComment() throws Exception {
        
        String source = "not a comment " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        String skeleton = extractor.reader(reader)
                .extract()
                .getSkeleton();

        assertTrue(skeleton.isEmpty());

        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
   }
    

    public void testSkeletonBuilder_NonYWComment() throws Exception {
        
        String source = "# a comment " + EOL;
        
        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        String skeleton = extractor.reader(reader)
                .extract()
                .getSkeleton();

        assertTrue(skeleton.isEmpty());
        
        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("WARNING: No YW comments found in source code." + EOL, super.stderrBuffer.toString());
    }
    
    public void testSkeletonBuilder_GetCommentLines_MultipleComments_Hash() throws Exception {
        
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
        
        String skeleton = extractor.reader(reader)
                                   .extract()
                                   .getSkeleton();

        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("", super.stderrBuffer.toString());

        assertEquals(
                "# @begin step"     + EOL + 
                "# @in x"           + EOL +
                "# @out y"          + EOL +
                "# @end step"       + EOL,
                skeleton);
    }

    public void testSkeletonBuilder_AliasesOnSameLines() throws Exception {
        
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
        
        String skeleton = extractor.reader(reader)
                .extract()
                .getSkeleton();

        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("", super.stderrBuffer.toString());

        assertEquals(
            "# @begin step"                 + EOL + 
            "# @in x  @as horiz"            + EOL +
            "# @param y  @as vert"          + EOL +
            "# @end step"                   + EOL,
            skeleton
        );
    }
    
    public void testSkeletonBuilder_AliasesOnDifferentLines() throws Exception {
        
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
        
        String skeleton = extractor.reader(reader)
                .extract()
                .getSkeleton();

        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("", super.stderrBuffer.toString());

        assertEquals(
            "# @begin step"                 + EOL + 
            "# @in x  @as horiz"            + EOL +
            "# @param y  @as vert"          + EOL +
            "# @end step"                   + EOL,
            skeleton
        );     
    }
    
    public void testSkeletonBuilder_MultipleCommentsOnOneLine() throws Exception {
        
        String source = "# @begin step @in x @as horiz @param y @as vert @end step";

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        String skeleton = extractor.reader(reader)
                .extract()
                .getSkeleton();

        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("", super.stderrBuffer.toString());

        assertEquals(
            "# @begin step"                 + EOL + 
            "# @in x  @as horiz"            + EOL +
            "# @param y  @as vert"          + EOL +
            "# @end step"                   + EOL,
            skeleton
        ); 
    }
    
    public void testSkeletonBuilder_GetComments_SamplePyScript() throws Exception {
        
        String skeleton = extractor.reader(new FileReader("src/main/resources/example.py"))
                                   .extract()                
                                   .getSkeleton();

        assertEquals("", super.stdoutBuffer.toString());
        assertEquals("", super.stderrBuffer.toString());

        assertEquals(
            "# @BEGIN main"                                                                                         + EOL +
            "# @PARAM db_pth"                                                                                       + EOL +
            "# @PARAM fmodel"                                                                                       + EOL +
            "# @IN input_mask_file  @URI file:{db_pth}/land_water_mask/LandWaterMask_Global_CRUNCEP.nc"             + EOL +
            "# @IN input_data_file  @URI file:{db_pth}/NEE_first_year.nc"                                           + EOL +
            "# @OUT result_NEE_pdf  @URI file:result_NEE.pdf"                                                       + EOL +
            ""                                                                                                      + EOL +
            "#     @BEGIN fetch_mask"                                                                               + EOL +
            "#     @PARAM db_pth"                                                                                   + EOL +
            "#     @IN g  @AS input_mask_file  @URI file:{db_pth}/land_water_mask/LandWaterMask_Global_CRUNCEP.nc"  + EOL +
            "#     @OUT mask  @AS land_water_mask"                                                                  + EOL +
            "#     @END fetch_mask"                                                                                 + EOL +
            ""                                                                                                      + EOL +
            "#     @BEGIN load_data"                                                                                + EOL +
            "#     @PARAM db_pth"                                                                                   + EOL +
            "#     @IN input_data_file  @URI file:{db_pth}/NEE_first_year.nc"                                       + EOL +
            "#     @OUT data  @AS NEE_data"                                                                         + EOL +
            "#     @END load_data"                                                                                  + EOL +
            ""                                                                                                      + EOL +
            "#     @BEGIN standardize_with_mask"                                                                    + EOL +
            "#     @IN data  @AS NEE_data"                                                                          + EOL +
            "#     @IN mask  @AS land_water_mask"                                                                   + EOL +
            "#     @OUT data  @AS standardized_NEE_data"                                                            + EOL +
            "#     @END standardize_with_mask"                                                                      + EOL +
            ""                                                                                                      + EOL +
            "#     @BEGIN simple_diagnose"                                                                          + EOL +
            "#     @PARAM fmodel"                                                                                   + EOL +
            "#     @IN data  @AS standardized_NEE_data"                                                             + EOL +
            "#     @OUT pp  @AS result_NEE_pdf  @URI file:result_NEE.pdf"                                           + EOL +
            "#     @END simple_diagnose"                                                                            + EOL +
            ""                                                                                                      + EOL +
            "# @END main"                                                                                           + EOL,
            skeleton
        );
    }

}