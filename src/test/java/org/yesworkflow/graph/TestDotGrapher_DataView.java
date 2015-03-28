package org.yesworkflow.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.model.DefaultModeler;
import org.yesworkflow.model.Modeler;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.YesWorkflowTestCase;

public class TestDotGrapher_DataView extends YesWorkflowTestCase {

	Extractor extractor = null;
    Modeler modeler = null;
    Grapher grapher = null;
    Map<String,Object> config = null;
    
    static final String TEST_RESOURCE_DIR = "org/yesworkflow/graph/";
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new DefaultExtractor(super.stdoutStream, super.stderrStream);
        modeler = new DefaultModeler(super.stdoutStream, super.stderrStream);
        grapher = new DotGrapher(super.stdoutStream, super.stderrStream);
        config = new HashMap<String,Object>();
        
        config.put("view", GraphView.DATA_CENTRIC_VIEW);
        config.put("comments", CommentVisibility.HIDE);
        grapher.configure(config);
    }
    

    public void testDotGrapher_DataView_TwoProgramsOneChannel_OneInOneOut() throws Exception {
        
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
        
        List<Annotation> annotations = extractor
                .configure("commentDelimiter", "#")
        		.source(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = modeler.annotations(annotations)
                                   .model()
                                   .getWorkflow();

        grapher.workflow(workflow)
               .graph();
        
        String dotString = grapher.toString();

        assertEquals(
            "digraph Workflow {"                                                                        + EOL +
            "rankdir=LR"                                                                                + EOL +
            "graph[fontname=Courier]"                                                                   + EOL +
            "edge[fontname=Courier]"                                                                    + EOL +
            "node[fontname=Helvetica]"                                                                  + EOL +
            "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1 label=\"\"]"   + EOL +
            "node1 [label=\"channel\"]"                                                                 + EOL +
            "}"                                                                                         + EOL,
            dotString);
    }

    
    public void testDotGrapher_DataView_TwoProgramsOneChannel_OneParamOneOut() throws Exception {
        
        String source = 
            "# @begin script"       + EOL +
            "#"                     + EOL +
            "#   @begin program0"   + EOL +
            "#   @out channel"      + EOL +
            "#   @end program0"     + EOL +                
            "#"                     + EOL +
            "#   @begin program1"   + EOL +
            "#   @param channel"    + EOL +
            "#   @end program1"     + EOL +
            "#"                     + EOL +
            "# @end script"         + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("commentDelimiter", "#")
                .source(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = modeler.annotations(annotations)
                                   .model()
                                   .getWorkflow();

        grapher.workflow(workflow)
               .graph();
        
        String dotString = grapher.toString();

        assertEquals(
            "digraph Workflow {"                                                                        + EOL +
            "rankdir=LR"                                                                                + EOL +
            "graph[fontname=Courier]"                                                                   + EOL +
            "edge[fontname=Courier]"                                                                    + EOL +
            "node[fontname=Helvetica]"                                                                  + EOL +
            "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1 label=\"\"]"   + EOL +
            "node1 [label=\"channel\"]"                                                                 + EOL +
            "}"                                                                                         + EOL,
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
      
      List<Annotation> annotations = extractor
              .configure("commentDelimiter", "#")
    		  .source(reader)
              .extract()
              .getAnnotations();

      Workflow workflow = modeler.annotations(annotations)
                                 .model()
                                 .getWorkflow();

      grapher.workflow(workflow)
             .graph();

      
      String dotString = grapher.toString();

      assertEquals(
          "digraph Workflow {"                                                                      + EOL +
          "rankdir=LR"                                                                              + EOL +
          "graph[fontname=Courier]"                                                                 + EOL +
          "edge[fontname=Courier]"                                                                  + EOL +
          "node[fontname=Helvetica]"                                                                + EOL +
          "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1 label=\"\"]" + EOL +
          "node1 [label=\"d\"]"                                                                     + EOL +
          "node2 [label=\"x\"]"                                                                     + EOL +
          "node2 -> node1 [label=\"program\"]"                                                      + EOL +
          "}"                                                                                       + EOL,
          dotString);
  }
  
    public void testDotGrapher_DataView_TwoChannels_OneProgram_OneInOneParam() throws Exception {
        
        String source = 
            "# @begin script"       + EOL +
            "# @param x"            + EOL +
            "# @out d"              + EOL +
            "#"                     + EOL +
            "#   @begin program"    + EOL +
            "#   @param x"          + EOL +
            "#   @out d"            + EOL +
            "#   @end program"      + EOL +                
            "#"                     + EOL +
            "# @end script"         + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("commentDelimiter", "#")
                .source(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = modeler.annotations(annotations)
                                   .model()
                                   .getWorkflow();
        
        grapher.workflow(workflow)
               .graph();

        
        String dotString = grapher.toString();

        assertEquals(
            "digraph Workflow {"                                                                      + EOL +
            "rankdir=LR"                                                                              + EOL +
            "graph[fontname=Courier]"                                                                 + EOL +
            "edge[fontname=Courier]"                                                                  + EOL +
            "node[fontname=Helvetica]"                                                                + EOL +
            "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1 label=\"\"]" + EOL +
            "node1 [label=\"d\"]"                                                                     + EOL +
            "node2 [label=\"x\"]"                                                                     + EOL +
            "node2 -> node1 [label=\"program\"]"                                                      + EOL +
            "}"                                                                                       + EOL,
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
      
      List<Annotation> annotations = extractor
              .configure("commentDelimiter", "#")
    		  .source(reader)
              .extract()
              .getAnnotations();

      Workflow workflow = modeler.annotations(annotations)
                                 .model()
                                 .getWorkflow();

      grapher.workflow(workflow)
             .graph();
      
      String dotString = grapher.toString();

      assertEquals(
          "digraph Workflow {"                                                                      + EOL +
          "rankdir=LR"                                                                              + EOL +
          "graph[fontname=Courier]"                                                                 + EOL +
          "edge[fontname=Courier]"                                                                  + EOL +
          "node[fontname=Helvetica]"                                                                + EOL +
          "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1 label=\"\"]" + EOL +
          "node1 [label=\"d\"]"                                                                     + EOL +
          "node2 [label=\"x\"]"                                                                     + EOL +
          "node3 [label=\"y\"]"                                                                     + EOL +
          "node2 -> node1 [label=\"program\"]"                                                      + EOL +
          "node3 -> node1 [label=\"program\"]"                                                      + EOL +
          "}"                                                                                       + EOL,
          dotString);
    }

    public void testDotGrapher_DataView_TwoChannels_OneProgram_OneInOneParamOneOut() throws Exception {
        
        String source = 
            "# @begin script"       + EOL +
            "# @in x"               + EOL +
            "# @param y"            + EOL +
            "# @out d"              + EOL +
            "#"                     + EOL +
            "#   @begin program"    + EOL +
            "#   @in x"             + EOL +
            "#   @param y"          + EOL +
            "#   @out d"            + EOL +
            "#   @end program"      + EOL +                
            "#"                     + EOL +
            "# @end script"         + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .configure("commentDelimiter", "#")
                .source(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = modeler.annotations(annotations)
                                   .model()
                                   .getWorkflow();

        grapher.workflow(workflow)
               .graph();
        
        String dotString = grapher.toString();

        assertEquals(
            "digraph Workflow {"                                                                      + EOL +
            "rankdir=LR"                                                                              + EOL +
            "graph[fontname=Courier]"                                                                 + EOL +
            "edge[fontname=Courier]"                                                                  + EOL +
            "node[fontname=Helvetica]"                                                                + EOL +
            "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1 label=\"\"]" + EOL +
            "node1 [label=\"d\"]"                                                                     + EOL +
            "node2 [label=\"x\"]"                                                                     + EOL +
            "node3 [label=\"y\"]"                                                                     + EOL +
            "node2 -> node1 [label=\"program\"]"                                                      + EOL +
            "node3 -> node1 [label=\"program\"]"                                                      + EOL +
            "}"                                                                                       + EOL,
            dotString);
    }

    public void testDotGrapher_DataView_SamplePyScript() throws Exception {
         
         List<Annotation> annotations = extractor
                 .configure("commentDelimiter", "#")
        		 .source(new BufferedReader(new FileReader("src/main/resources/example.py")))
                 .extract()
                 .getAnnotations();

         Workflow workflow = modeler.annotations(annotations)
                                    .model()
                                    .getWorkflow();
    
         grapher.workflow(workflow)
                .graph();
         
         String dotString = grapher.toString();
    
         assertEquals(
                 "digraph Workflow {"                                                                       + EOL +
                 "rankdir=LR"                                                                               + EOL +
                 "graph[fontname=Courier]"                                                                  + EOL +
                 "edge[fontname=Courier]"                                                                   + EOL +
                 "node[fontname=Helvetica]"                                                                 + EOL +
                 "node[shape=box style=\"rounded,filled\" fillcolor=\"#FFFFCC\" peripheries=1 label=\"\"]"  + EOL +
                 "node1 [label=\"result_NEE_pdf\"]"                                                         + EOL +
                 "node2 [label=\"input_mask_file\"]"                                                        + EOL +
                 "node3 [label=\"input_data_file\"]"                                                        + EOL +
                 "node4 [label=\"NEE_data\"]"                                                               + EOL +
                 "node5 [label=\"land_water_mask\"]"                                                        + EOL +
                 "node6 [label=\"standardized_NEE_data\"]"                                                  + EOL +
                 "node2 -> node5 [label=\"fetch_mask\"]"                                                    + EOL +
                 "node3 -> node4 [label=\"load_data\"]"                                                     + EOL +
                 "node4 -> node6 [label=\"standardize_with_mask\"]"                                         + EOL +
                 "node5 -> node6 [label=\"standardize_with_mask\"]"                                         + EOL +
                 "node6 -> node1 [label=\"simple_diagnose\"]"                                               + EOL +
                 "}"                                                                                        + EOL,
             dotString);
     }
}
