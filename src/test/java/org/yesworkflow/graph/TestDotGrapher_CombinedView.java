package org.yesworkflow.graph;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.yesworkflow.comments.Comment;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.model.DefaultModeler;
import org.yesworkflow.model.Modeler;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestDotGrapher_CombinedView extends YesWorkflowTestCase {

    Extractor extractor = null;
    Modeler modeler = null;
    Grapher grapher = null;
    
    static final String TEST_RESOURCE_DIR = "org/yesworkflow/graph/";
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new DefaultExtractor(super.stdoutStream, super.stderrStream);
        modeler = new DefaultModeler(super.stdoutStream, super.stderrStream);
        grapher = new DotGrapher(super.stdoutStream, super.stderrStream);
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
        
        List<Comment> comments = extractor.sourceReader(reader)
                .commentCharacter('#')
                .extract()
                .getComments();

        Workflow workflow = (Workflow)modeler.comments(comments)
                                             .model()
                                             .getModel();

        grapher.workflow(workflow)
               .view(GraphView.COMBINED_VIEW)
               .enableComments(false)
               .graph();
        
        String dotString = grapher.toString();

        assertEquals(
            "digraph Workflow {"                                                                        + EOL +
            "rankdir=LR"                                                                                + EOL +
            "graph[fontname=Courier]"                                                                   + EOL +
            "node[fontname=Courier]"                                                                    + EOL +
            "node[shape=box3d style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1 label=\"\"]"         + EOL +
            "node1 [label=\"program0\"]"                                                                + EOL +
            "node2 [label=\"program1\"]"                                                                + EOL +
            "node[fontname=Helvetica]"                                                                  + EOL +
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
      
      
      List<Comment> comments = extractor.sourceReader(reader)
              .commentCharacter('#')
              .extract()
              .getComments();

      Workflow workflow = (Workflow)modeler.comments(comments)
                                           .model()
                                           .getModel();

      grapher.workflow(workflow)
             .view(GraphView.COMBINED_VIEW)
             .enableComments(false)
             .graph();
      
      String dotString = grapher.toString();

      assertEquals(
          "digraph Workflow {"                                                                      + EOL +
          "rankdir=LR"                                                                              + EOL +
          "graph[fontname=Courier]"                                                                 + EOL +
          "node[fontname=Courier]"                                                                  + EOL +
          "node[shape=box3d style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1 label=\"\"]"       + EOL +
          "node1 [label=\"program\"]"                                                               + EOL +
          "node[fontname=Helvetica]"                                                                + EOL +
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
      
      List<Comment> comments = extractor.sourceReader(reader)
              .commentCharacter('#')
              .extract()
              .getComments();

      Workflow workflow = (Workflow)modeler.comments(comments)
                                           .model()
                                           .getModel();

      grapher.workflow(workflow)
             .view(GraphView.COMBINED_VIEW)
             .enableComments(false)
             .graph();
      
      String dotString = grapher.toString();

      assertEquals(
          "digraph Workflow {"                                                                      + EOL +
          "rankdir=LR"                                                                              + EOL +
          "graph[fontname=Courier]"                                                                 + EOL +
          "node[fontname=Courier]"                                                                  + EOL +
          "node[shape=box3d style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1 label=\"\"]"       + EOL +
          "node1 [label=\"program\"]"                                                               + EOL +
          "node[fontname=Helvetica]"                                                                + EOL +
          "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1 label=\"\"]" + EOL +
          "node2 [label=\"d\"]"                                                                     + EOL +
          "node3 [label=\"x\"]"                                                                     + EOL +
          "node4 [label=\"y\"]"                                                                     + EOL +
          "node1 -> node2"                                                                          + EOL +
          "node3 -> node1"                                                                          + EOL +
          "node4 -> node1"                                                                          + EOL +
          "}"                                                                                       + EOL,
          dotString);
      }
         
     public void testDotGrapher_CombinedView_SamplePyScript() throws Exception {
         
         List<Comment> comments = extractor.sourcePath("src/main/resources/example.py")
                 .commentCharacter('#')
                 .extract()
                 .getComments();

         Workflow workflow = (Workflow)modeler.comments(comments)
                                              .model()
                                              .getModel();
    
         grapher.workflow(workflow)
                .view(GraphView.COMBINED_VIEW)
                .enableComments(false)
                .graph();
         
         String dotString = grapher.toString();
    
         assertEquals(
                 "digraph Workflow {"                                                                       + EOL +
                 "rankdir=LR"                                                                               + EOL +
                 "graph[fontname=Courier]"                                                                  + EOL +
                 "node[fontname=Courier]"                                                                   + EOL +
                "node[shape=box3d style=\"filled\" fillcolor=\"#CCFFCC\" peripheries=1 label=\"\"]"         + EOL +
                 "node1 [label=\"fetch_mask\"]"                                                             + EOL +
                 "node2 [label=\"load_data\"]"                                                              + EOL +
                 "node3 [label=\"standardize_with_mask\"]"                                                  + EOL +
                 "node4 [label=\"simple_diagnose\"]"                                                        + EOL +
                 "node[fontname=Helvetica]"                                                                 + EOL +
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
