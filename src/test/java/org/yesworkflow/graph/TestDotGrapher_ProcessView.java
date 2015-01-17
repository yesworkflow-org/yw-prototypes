package org.yesworkflow.graph;

import java.io.BufferedReader;
import java.io.StringReader;

import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestDotGrapher_ProcessView extends YesWorkflowTestCase {

    DefaultExtractor extractor = null;
    DotGrapher grapher = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new DefaultExtractor(super.stdoutStream, super.stderrStream);
        grapher = new DotGrapher();
    }

    public void testDotGrapher_ProcessView_TwoProgramsOneChannel() throws Exception {
        
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
               .view(GraphView.PROCESS_CENTRIC_VIEW)
               .graph();
        
        String dotString = grapher.toString();

        assertEquals(
            "digraph Workflow {"                                                    + EOL +
            "rankdir=LR"                                                            + EOL +
            "node[shape=box style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1]"  + EOL +
            "node1 [label=\"program0\"]"                                            + EOL +
            "node2 [label=\"program1\"]"                                            + EOL +
            "node1 -> node2 [label=\"channel\"]"                                    + EOL +
            "}"                                                                     + EOL,
            dotString);
    }    
    
    public void testDotGrapher_ProcessView_TwoChannels_OneProgram_OneInOneOut() throws Exception {
        
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
               .view(GraphView.PROCESS_CENTRIC_VIEW)
               .graph();
        
        String dotString = grapher.toString();

        assertEquals(
            "digraph Workflow {"                                                                + EOL +
            "rankdir=LR"                                                                        + EOL +
            "node[shape=box style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1]"              + EOL +
            "node1 [label=\"program\"]"                                                         + EOL +
            "node[shape=circle style=\"filled\" fillcolor=\"#FFFFFF\" peripheries=1 width=0.1]" + EOL +
            "node2 [label=\"\"]"                                                                + EOL +
            "node3 [label=\"\"]"                                                                + EOL +
            "node1 -> node3 [label=\"d\"]"                                                      + EOL +
            "node2 -> node1 [label=\"x\"]"                                                      + EOL +
            "}"                                                                                 + EOL,
            dotString);
    }  
  
  public void testDotGrapher_ProcessView_TwoChannels_OneProgram_TwoInOneOut() throws Exception {
      
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
             .view(GraphView.PROCESS_CENTRIC_VIEW)
             .graph();
      
      String dotString = grapher.toString();

      assertEquals(
          "digraph Workflow {"                                                                  + EOL +
          "rankdir=LR"                                                                          + EOL +
          "node[shape=box style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1]"                + EOL +
          "node1 [label=\"program\"]"                                                           + EOL +
          "node[shape=circle style=\"filled\" fillcolor=\"#FFFFFF\" peripheries=1 width=0.1]"   + EOL +
          "node2 [label=\"\"]"                                                                  + EOL +
          "node3 [label=\"\"]"                                                                  + EOL +
          "node4 [label=\"\"]"                                                                  + EOL +
          "node1 -> node4 [label=\"d\"]"                                                        + EOL +
          "node2 -> node1 [label=\"x\"]"                                                        + EOL +
          "node3 -> node1 [label=\"y\"]"                                                        + EOL +
          "}"                                                                                   + EOL,
          dotString);
  }
    
  public void testDotGrapher_ProcessView_TwoChannels_OneProgram_TwoInOneOut_ExtraIn() throws Exception {
      
      String source = 
          "# @begin script"       + EOL +
          "# @in x"               + EOL +
          "# @in y"               + EOL +
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
             .view(GraphView.PROCESS_CENTRIC_VIEW)
             .graph();
      
      String dotString = grapher.toString();

      assertEquals(
          "digraph Workflow {"                                                                  + EOL +
          "rankdir=LR"                                                                          + EOL +
          "node[shape=box style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1]"                + EOL +
          "node1 [label=\"program\"]"                                                           + EOL +
          "node[shape=circle style=\"filled\" fillcolor=\"#FFFFFF\" peripheries=1 width=0.1]"   + EOL +
          "node2 [label=\"\"]"                                                                  + EOL +
          "node3 [label=\"\"]"                                                                  + EOL +
          "node1 -> node3 [label=\"d\"]"                                                        + EOL +
          "node2 -> node1 [label=\"x\"]"                                                        + EOL +
          "}"                                                                                   + EOL,
          dotString);

      assertEquals("WARNING: No nested @in port and no workflow @out port for nested @out 'y' in workflow 'script'" + EOL, super.stderrBuffer.toString());
  }
  
  public void testDotGrapher_ProcessView_TwoChannels_OneProgram_TwoInOneOut_ExtraOut() throws Exception {
      
      String source = 
          "# @begin script"       + EOL +
          "# @in x"               + EOL +
          "# @out c"              + EOL +
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
             .view(GraphView.PROCESS_CENTRIC_VIEW)
             .graph();
      
      String dotString = grapher.toString();

      assertEquals(
          "digraph Workflow {"                                                                  + EOL +
          "rankdir=LR"                                                                          + EOL +
          "node[shape=box style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1]"                + EOL +
          "node1 [label=\"program\"]"                                                           + EOL +
          "node[shape=circle style=\"filled\" fillcolor=\"#FFFFFF\" peripheries=1 width=0.1]"   + EOL +
          "node2 [label=\"\"]"                                                                  + EOL +
          "node3 [label=\"\"]"                                                                  + EOL +
          "node1 -> node3 [label=\"d\"]"                                                        + EOL +
          "node2 -> node1 [label=\"x\"]"                                                        + EOL +
          "}"                                                                                   + EOL,
          dotString);
      
      assertEquals("WARNING: No nested @out port and no workflow @in port for nested @in 'c' on 'script'" + EOL, super.stderrBuffer.toString());
  }
  
 public void testDotGrapher_ProcessView_ThreeProgramsTwoChannel() throws Exception {
     
     String source = 
             "# @begin script"       + EOL +
             "#"                     + EOL +
             "#   @begin program0"   + EOL +
             "#   @out channel0"     + EOL +
             "#   @out channel1"     + EOL +
             "#   @end program0"     + EOL +                
             "#"                     + EOL +
             "#   @begin program1"   + EOL +
             "#   @in channel0"      + EOL +
             "#   @end program1"     + EOL +
             "#"                     + EOL +
             "#   @begin program2"   + EOL +
             "#   @in channel1"      + EOL +
             "#   @end program2"     + EOL +
             "#"                     + EOL +
             "# @end script"         + EOL;

     BufferedReader reader = new BufferedReader(new StringReader(source));
     
     extractor.sourceReader(reader)
              .commentCharacter('#')
              .extract();
     Workflow workflow = (Workflow)extractor.getProgram();

     grapher.workflow(workflow)
            .view(GraphView.PROCESS_CENTRIC_VIEW)
            .graph();
     
     String dotString = grapher.toString();

     assertEquals(
         "digraph Workflow {"                                                               + EOL +
         "rankdir=LR"                                                                       + EOL +
         "node[shape=box style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1]"             + EOL +
         "node1 [label=\"program0\"]"                                                       + EOL +
         "node2 [label=\"program1\"]"                                                       + EOL +
         "node3 [label=\"program2\"]"                                                       + EOL +
         "node1 -> node2 [label=\"channel0\"]"                                              + EOL +
         "node1 -> node3 [label=\"channel1\"]"                                              + EOL +
         "}"                                                                                + EOL,
         dotString);
     }
 
 public void testDotGrapher_ProcessView_NestedSubworkflow() throws Exception {
     
     String source = 
             "# @begin workflow"            + EOL +
             "# @in workflowInput"          + EOL +
             "# @out workflowOutput"        + EOL +
             "#"                            + EOL +
             "#   @begin program0"          + EOL +
             "#   @in workflowInput"        + EOL +
             "#   @out channel0"            + EOL +
             "#   @end program0"            + EOL +                
             "#"                            + EOL +
             "#   @begin subWorkflow"       + EOL +
             "#   @in channel0"             + EOL +
             "#   @out channel3"            + EOL +
             "#"                            + EOL +
             "#     @begin program2"        + EOL +
             "#     @in channel1"           + EOL +
             "#     @out channel2"          + EOL +
             "#     @end program2"          + EOL +
             "#"                            + EOL +
             "#     @begin program3"        + EOL +
             "#     @in channel2"           + EOL +
             "#     @out channel3"          + EOL +
             "#     @end program3"          + EOL +                
             "#"                            + EOL +
             "#   @end subWorkflow"         + EOL +
             "#"                            + EOL +
             "#   @begin program4"          + EOL +
             "#   @in channel3"             + EOL +
             "#   @out workflowOutput"      + EOL +
             "#   @end program4"            + EOL +
             "#"                            + EOL +
             "# @end workflow"              + EOL;

     BufferedReader reader = new BufferedReader(new StringReader(source));
     
     extractor.sourceReader(reader)
              .commentCharacter('#')
              .extract();
     Workflow workflow = (Workflow)extractor.getProgram();

     grapher.workflow(workflow)
            .view(GraphView.PROCESS_CENTRIC_VIEW)
            .graph();
     
     String dotString = grapher.toString();

     assertEquals(
         "digraph Workflow {"                                                                   + EOL +
         "rankdir=LR"                                                                           + EOL +
         "node[shape=box style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1]"                 + EOL +
         "node1 [label=\"program0\"]"                                                           + EOL +
         "node2 [label=\"subWorkflow\"]"                                                        + EOL +
         "node3 [label=\"program4\"]"                                                           + EOL +
         "node[shape=circle style=\"filled\" fillcolor=\"#FFFFFF\" peripheries=1 width=0.1]"    + EOL +
         "node4 [label=\"\"]"                                                                   + EOL +
         "node5 [label=\"\"]"                                                                   + EOL +
         "node3 -> node5 [label=\"workflowOutput\"]"                                            + EOL +
         "node4 -> node1 [label=\"workflowInput\"]"                                             + EOL +
         "node1 -> node2 [label=\"channel0\"]"                                                  + EOL +
         "node2 -> node3 [label=\"channel3\"]"                                                  + EOL +
         "}"                                                                                    + EOL,
         dotString);
     }
 
     public void testDotGrapher_ProcessView_SamplePyScript() throws Exception {
         
         extractor.sourcePath("src/main/resources/example.py")
             .commentCharacter('#')
             .extract();

         Workflow workflow = (Workflow)extractor.getProgram();
    
         grapher.workflow(workflow)
                .view(GraphView.PROCESS_CENTRIC_VIEW)
                .graph();
         
         String dotString = grapher.toString();
    
         assertEquals(
                 "digraph Workflow {"                                                                   + EOL +
                 "rankdir=LR"                                                                           + EOL +
                 "node[shape=box style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1]"                 + EOL +
                 "node1 [label=\"fetch_mask\"]"                                                         + EOL +
                 "node2 [label=\"load_data\"]"                                                          + EOL +
                 "node3 [label=\"standardize_with_mask\"]"                                              + EOL +
                 "node4 [label=\"simple_diagnose\"]"                                                    + EOL +
                 "node[shape=circle style=\"filled\" fillcolor=\"#FFFFFF\" peripheries=1 width=0.1]"    + EOL +
                 "node5 [label=\"\"]"                                                                   + EOL +
                 "node6 [label=\"\"]"                                                                   + EOL +
                 "node7 [label=\"\"]"                                                                   + EOL +
                 "node4 -> node7 [label=\"result_NEE_pdf\"]"                                            + EOL +
                 "node5 -> node1 [label=\"input_mask_file\"]"                                           + EOL +
                 "node6 -> node2 [label=\"input_data_file\"]"                                           + EOL +
                 "node2 -> node3 [label=\"NEE_data\"]"                                                  + EOL +
                 "node1 -> node3 [label=\"land_water_mask\"]"                                           + EOL +
                 "node3 -> node4 [label=\"standardized_NEE_data\"]"                                     + EOL +
                 "}"                                                                                    + EOL,
             dotString);
     }
}
