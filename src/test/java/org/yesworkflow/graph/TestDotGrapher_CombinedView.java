package org.yesworkflow.graph;

import java.io.BufferedReader;
import java.io.StringReader;

import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestDotGrapher_CombinedView extends YesWorkflowTestCase {

    DefaultExtractor extractor = null;
    DotGrapher grapher = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new DefaultExtractor(super.stdoutStream, super.stderrStream);
        grapher = new DotGrapher();
    }

    public void testDotGrapher_CombinedView_TwoProgramsOneChannel() throws Exception {
        
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
               .view(GraphView.COMBINED_VIEW)
               .graph();
        
        String dotString = grapher.toString();

        assertEquals(
            "digraph Workflow {"                                                                        + EOL +
            "rankdir=LR"                                                                                + EOL +
            "node[shape=box style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1 label=\"\"]"           + EOL +
            "node1 [label=\"program0\"]"                                                                + EOL +
            "node2 [label=\"program1\"]"                                                                + EOL +
            "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1 label=\"\"]"   + EOL +
            "node3 [label=\"channel\"]"                                                                 + EOL +
            "node1 -> node3"                                                                            + EOL +
            "node3 -> node2"                                                                            + EOL +
            "}"                                                                                         + EOL,
            dotString);
    }    
    
  public void testDotGrapher_CombinedView_TwoChannels_OneProgram_OneInOneOut() throws Exception {
      
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
             .view(GraphView.COMBINED_VIEW)
             .graph();
      
      String dotString = grapher.toString();

      assertEquals(
          "digraph Workflow {"                                                                      + EOL +
          "rankdir=LR"                                                                              + EOL +
          "node[shape=box style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1 label=\"\"]"         + EOL +
          "node1 [label=\"program\"]"                                                               + EOL +
          "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1 label=\"\"]" + EOL +
          "node2 [label=\"d\"]"                                                                     + EOL +
          "node3 [label=\"x\"]"                                                                     + EOL +
          "node1 -> node2"                                                                          + EOL +
          "node3 -> node1"                                                                          + EOL +
          "}"                                                                                       + EOL,
          dotString);
  }
  
  
  
  public void testDotGrapher_CombinedView_TwoChannels_OneProgram_TwoInOneOut() throws Exception {
      
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
             .view(GraphView.COMBINED_VIEW)
             .graph();
      
      String dotString = grapher.toString();

      assertEquals(
          "digraph Workflow {"                                                                      + EOL +
          "rankdir=LR"                                                                              + EOL +
          "node[shape=box style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1 label=\"\"]"         + EOL +
          "node1 [label=\"program\"]"                                                               + EOL +
          "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1 label=\"\"]" + EOL +
          "node2 [label=\"d\"]"                                                                     + EOL +
          "node3 [label=\"x\"]"                                                                     + EOL +
          "node4 [label=\"y\"]"                                                                     + EOL +
          "node1 -> node2"                                                                          + EOL +
          "node3 -> node1"                                                                          + EOL +
          "node4 -> node1"                                                                          + EOL +
          "}"                                                                                        + EOL,
          dotString);
      }
         
     public void testDotGrapher_CombinedView_SamplePyScript() throws Exception {
         
         extractor.sourcePath("src/main/resources/example.py")
             .commentCharacter('#')
             .extract();

         Workflow workflow = (Workflow)extractor.getProgram();
    
         grapher.workflow(workflow)
                .view(GraphView.COMBINED_VIEW)
                .graph();
         
         String dotString = grapher.toString();
    
         assertEquals(
                 "digraph Workflow {"                                                                       + EOL +
                 "rankdir=LR"                                                                               + EOL +
                 "node[shape=box style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1 label=\"\"]"          + EOL +
                 "node1 [label=\"fetch_mask\"]"                                                             + EOL +
                 "node2 [label=\"load_data\"]"                                                              + EOL +
                 "node3 [label=\"standardize_with_mask\"]"                                                  + EOL +
                 "node4 [label=\"simple_diagnose\"]"                                                        + EOL +
                 "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1 label=\"\"]"  + EOL +
                 "node5 [label=\"result_NEE_pdf\"]"                                                         + EOL +
                 "node6 [label=\"input_mask_file\"]"                                                        + EOL +
                 "node7 [label=\"input_data_file\"]"                                                        + EOL +
                 "node8 [label=\"NEE_data\"]"                                                               + EOL +
                 "node9 [label=\"land_water_mask\"]"                                                        + EOL +
                 "node10 [label=\"standardized_NEE_data\"]"                                                 + EOL +
                 "node1 -> node9"                                                                           + EOL +
                 "node6 -> node1"                                                                           + EOL +
                 "node2 -> node8"                                                                           + EOL +
                 "node7 -> node2"                                                                           + EOL +
                 "node3 -> node10"                                                                          + EOL +
                 "node8 -> node3"                                                                           + EOL +
                 "node9 -> node3"                                                                           + EOL +
                 "node4 -> node5"                                                                           + EOL +
                 "node10 -> node4"                                                                          + EOL +
                 "}"                                                                                        + EOL,
             dotString);
     }

}
