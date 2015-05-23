package org.yesworkflow.extract;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;

import org.yesworkflow.Language;
import org.yesworkflow.LanguageModel;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.YesWorkflowTestCase;

public class TestSkeletonBuilder extends YesWorkflowTestCase {

    DefaultExtractor extractor = null;
    LanguageModel languageModel = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new DefaultExtractor(super.stdoutStream, super.stderrStream);
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
            "# @begin main"                                                         + EOL +
            "# @in LandWaterMask_Global_CRUNCEP.nc  @as input_mask_file"            + EOL +
            "# @in NEE_first_year.nc  @as input_data_file"                          + EOL +
            "# @out result_simple.pdf  @as result_NEE_pdf"                          + EOL +
            ""                                                                      + EOL +
            "#     @begin fetch_mask"                                               + EOL +
            "#     @in \"LandWaterMask_Global_CRUNCEP.nc\"  @as input_mask_file"    + EOL +
            "#     @out mask  @as land_water_mask"                                  + EOL +
            "#     @end fetch_mask"                                                 + EOL +
            ""                                                                      + EOL +
            "#     @begin load_data"                                                + EOL +
            "#     @in \"CLM4_BG1_V1_Monthly_NEE.nc4\"  @as input_data_file"        + EOL +
            "#     @out data  @as NEE_data"                                         + EOL +
            "#     @end load_data"                                                  + EOL +
            ""                                                                      + EOL +
            "#     @begin standardize_with_mask"                                    + EOL +
            "#     @in data  @as NEE_data"                                          + EOL +
            "#     @in mask  @as land_water_mask"                                   + EOL +
            "#     @out data  @as standardized_NEE_data"                            + EOL +
            "#     @end standardize_mask"                                           + EOL +
            ""                                                                      + EOL +
            "#     @begin simple_diagnose"                                          + EOL +
            "#     @in np  @as standardized_NEE_data"                               + EOL +
            "#     @out pp  @as result_NEE_pdf"                                     + EOL +
            "#     @end simple_diagnose"                                            + EOL +
            ""                                                                      + EOL +
            "# @end main"                                                           + EOL,
            skeleton
        );
    }

}