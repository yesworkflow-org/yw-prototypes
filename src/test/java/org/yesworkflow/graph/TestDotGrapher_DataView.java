package org.yesworkflow.graph;

import java.io.BufferedReader;
import java.io.StringReader;

import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestDotGrapher_DataView extends YesWorkflowTestCase {

    DefaultExtractor extractor = null;
    DotGrapher grapher = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new DefaultExtractor(super.stdoutStream, super.stderrStream);
        grapher = new DotGrapher();
    }

    public void testDotGrapher_DataView_TwoProgramsOneChannel() throws Exception {
        
        String source = 
            "# @begin script"       + EOL +
            "#"                     + EOL +
            "#   @begin program0"   + EOL +
            "#   @out channel"      + EOL +
            "#   @end program0"     + EOL +                
            "#"                     + EOL +
            "#   @begin program1"   + EOL +
            "#   @in channel"       + EOL +
            "#   @end program1"     + EOL +
            "#"                     + EOL +
            "# @end script"         + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        extractor.sourceReader(reader)
                 .commentCharacter('#')
                 .extract();
        Workflow workflow = (Workflow)extractor.getProgram();

        grapher.workflow(workflow)
               .view(GraphView.DATA_CENTRIC_VIEW)
               .graph();
        
        String dotString = grapher.toString();

        assertEquals(
            "digraph Workflow {"                                                            + EOL +
            "rankdir=LR"                                                                    + EOL +
            "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1]"  + EOL +
            "node1 [label=\"channel\"]"                                                     + EOL +
            "}"                                                                             + EOL,
            dotString);
    }
        
    public void testDotGrapher_DataView_TwoChannels_OneProgram_OneInOneOut() throws Exception {
      
      String source = 
          "# @begin script"       + EOL +
          "# @in x"               + EOL +
          "# @out d"              + EOL +
          "#"                     + EOL +
          "#   @begin program"    + EOL +
          "#   @in x"             + EOL +
          "#   @out d"            + EOL +
          "#   @end program"      + EOL +                
          "#"                     + EOL +
          "# @end script"         + EOL;

      BufferedReader reader = new BufferedReader(new StringReader(source));
      
      extractor.sourceReader(reader)
               .commentCharacter('#')
               .extract();
      Workflow workflow = (Workflow)extractor.getProgram();

      grapher.workflow(workflow)
             .view(GraphView.DATA_CENTRIC_VIEW)
             .graph();
      
      String dotString = grapher.toString();

      assertEquals(
          "digraph Workflow {"                                                              + EOL +
          "rankdir=LR"                                                                      + EOL +
          "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1]"    + EOL +
          "node1 [label=\"d\"]"                                                             + EOL +
          "node2 [label=\"x\"]"                                                             + EOL +
          "node2 -> node1 [label=\"program\"]"                                              + EOL +
          "}"                                                                               + EOL,
          dotString);
  }
  

  public void testDotGrapher_DataView_TwoChannels_OneProgram_TwoInOneOut() throws Exception {
      
      String source = 
          "# @begin script"       + EOL +
          "# @in x"               + EOL +
          "# @in y"               + EOL +
          "# @out d"              + EOL +
          "#"                     + EOL +
          "#   @begin program"    + EOL +
          "#   @in x"             + EOL +
          "#   @in y"             + EOL +
          "#   @out d"            + EOL +
          "#   @end program"      + EOL +                
          "#"                     + EOL +
          "# @end script"         + EOL;

      BufferedReader reader = new BufferedReader(new StringReader(source));
      
      extractor.sourceReader(reader)
               .commentCharacter('#')
               .extract();
      Workflow workflow = (Workflow)extractor.getProgram();

      grapher.workflow(workflow)
             .view(GraphView.DATA_CENTRIC_VIEW)
             .graph();
      
      String dotString = grapher.toString();

      assertEquals(
          "digraph Workflow {"                                                                  + EOL +
          "rankdir=LR"                                                                          + EOL +
          "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1]"        + EOL +
          "node1 [label=\"d\"]"                                                                 + EOL +
          "node2 [label=\"x\"]"                                                                 + EOL +
          "node3 [label=\"y\"]"                                                                 + EOL +
          "node2 -> node1 [label=\"program\"]"                                                  + EOL +
          "node3 -> node1 [label=\"program\"]"                                                  + EOL +
          "}"                                                                                   + EOL,
          dotString);
  }
  

     public void testDotGrapher_DataView_SamplePyScript() throws Exception {
         
         extractor.sourcePath("src/main/resources/example.py")
             .commentCharacter('#')
             .extract();

         Workflow workflow = (Workflow)extractor.getProgram();
    
         grapher.workflow(workflow)
                .view(GraphView.DATA_CENTRIC_VIEW)
                .graph();
         
         String dotString = grapher.toString();
    
         assertEquals(
                 "digraph Workflow {"                                                                   + EOL +
                 "rankdir=LR"                                                                           + EOL +
                 "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1]"         + EOL +
                 "node1 [label=\"result_NEE_pdf\"]"                                                     + EOL +
                 "node2 [label=\"input_mask_file\"]"                                                    + EOL +
                 "node3 [label=\"input_data_file\"]"                                                    + EOL +
                 "node4 [label=\"NEE_data\"]"                                                           + EOL +
                 "node5 [label=\"land_water_mask\"]"                                                    + EOL +
                 "node6 [label=\"standardized_NEE_data\"]"                                              + EOL +
                 "node2 -> node5 [label=\"fetch_mask\"]"                                                + EOL +
                 "node3 -> node4 [label=\"load_data\"]"                                                 + EOL +
                 "node4 -> node6 [label=\"standardize_with_mask\"]"                                     + EOL +
                 "node5 -> node6 [label=\"standardize_with_mask\"]"                                     + EOL +
                 "node6 -> node1 [label=\"simple_diagnose\"]"                                           + EOL +
                 "}"                                                                                    + EOL,
             dotString);
     }
}
