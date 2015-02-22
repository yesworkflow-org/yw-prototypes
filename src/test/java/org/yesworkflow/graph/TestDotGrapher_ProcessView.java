package org.yesworkflow.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.List;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.model.DefaultModeler;
import org.yesworkflow.model.Modeler;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestDotGrapher_ProcessView extends YesWorkflowTestCase {

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
        
        List<Annotation> annotations = extractor
                .commentDelimiter("#")
        		.source(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = (Workflow)modeler.annotations(annotations)
                                             .model()
                                             .getModel();

        grapher.workflow(workflow)
               .view(GraphView.PROCESS_CENTRIC_VIEW)
               .enableComments(false)
               .graph();
        
        assertEquals(
            readTextFileOnClasspath(TEST_RESOURCE_DIR + "testDotGrapher_ProcessView_TwoProgramsOneChannel.gv"),
            grapher.toString()
        );
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
        
        List<Annotation> annotations = extractor
                .commentDelimiter("#")
        		.source(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = (Workflow)modeler.annotations(annotations)
                                             .model()
                                             .getModel();

        grapher.workflow(workflow)
               .view(GraphView.PROCESS_CENTRIC_VIEW)
               .enableComments(false)
               .graph();
        
        assertEquals(
            readTextFileOnClasspath(TEST_RESOURCE_DIR + "testDotGrapher_ProcessView_TwoChannels_OneProgram_OneInOneOut.gv"),
            grapher.toString()
        );
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
          
          List<Annotation> annotations = extractor
                  .commentDelimiter("#")
                  .source(reader)
                  .extract()
                  .getAnnotations();

          Workflow workflow = (Workflow)modeler.annotations(annotations)
                                               .model()
                                               .getModel();
        
          grapher.workflow(workflow)
                 .view(GraphView.PROCESS_CENTRIC_VIEW)
                 .enableComments(false)
                 .graph();
          
          assertEquals(
              readTextFileOnClasspath(TEST_RESOURCE_DIR + "testDotGrapher_ProcessView_TwoChannels_OneProgram_TwoInOneOut.gv"),
              grapher.toString()
          );
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
      
      List<Annotation> annotations = extractor
    		  .commentDelimiter("#")
      		  .source(reader)
      		  .extract()
              .getAnnotations();

      Workflow workflow = (Workflow)modeler.annotations(annotations)
                                           .model()
                                           .getModel();

      grapher.workflow(workflow)
             .view(GraphView.PROCESS_CENTRIC_VIEW)
             .enableComments(false)
             .graph();
      
      assertEquals(
          readTextFileOnClasspath(TEST_RESOURCE_DIR + "testDotGrapher_ProcessView_TwoChannels_OneProgram_TwoInOneOut_ExtraIn.gv"),
          grapher.toString()
      );

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
      
      List<Annotation> annotations = extractor
    		  .commentDelimiter("#")
      		  .source(reader)
      		  .extract()
              .getAnnotations();

      Workflow workflow = (Workflow)modeler.annotations(annotations)
                                           .model()
                                           .getModel();
      
      grapher.workflow(workflow)
             .view(GraphView.PROCESS_CENTRIC_VIEW)
             .enableComments(false)
             .graph();
      
      assertEquals(
          readTextFileOnClasspath(TEST_RESOURCE_DIR + "testDotGrapher_ProcessView_TwoChannels_OneProgram_TwoInOneOut_ExtraOut.gv"),
          grapher.toString()
      );
      
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
     
     List<Annotation> annotations = extractor
             .commentDelimiter("#")
     		 .source(reader)
             .extract()
             .getAnnotations();

     Workflow workflow = (Workflow)modeler.annotations(annotations)
                                          .model()
                                          .getModel();

     grapher.workflow(workflow)
            .view(GraphView.PROCESS_CENTRIC_VIEW)
            .enableComments(false)
            .graph();
     
     assertEquals(
         readTextFileOnClasspath(TEST_RESOURCE_DIR + "testDotGrapher_ProcessView_ThreeProgramsTwoChannel.gv"),
         grapher.toString()
     );
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
             "#     @in channel0"           + EOL +
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
     
         List<Annotation> annotations = extractor
                 .commentDelimiter("#")
         		 .source(reader)
                 .extract()
                 .getAnnotations();

         Workflow workflow = (Workflow)modeler.annotations(annotations)
                                              .model()
                                              .getModel();

             grapher.workflow(workflow)
                    .view(GraphView.PROCESS_CENTRIC_VIEW)
                    .enableComments(false)
                    .graph();
     
             assertEquals(
                 readTextFileOnClasspath(TEST_RESOURCE_DIR + "testDotGrapher_ProcessView_NestedSubworkflow.gv"),
                 grapher.toString()
             );
     }      
 
     public void testDotGrapher_ProcessView_SamplePyScript() throws Exception {
         
        List<Annotation> annotations = extractor
                .commentDelimiter("#")
                .source(new BufferedReader(new FileReader("src/main/resources/example.py")))
                .extract()
                .getAnnotations();

        Workflow workflow = (Workflow)modeler.annotations(annotations)
                                             .model()
                                             .getModel();
    
         grapher.workflow(workflow)
                .view(GraphView.PROCESS_CENTRIC_VIEW)
                .enableComments(false)
                .graph();
         
         assertEquals(
             readTextFileOnClasspath(TEST_RESOURCE_DIR + "testDotGrapher_ProcessView_SamplePyScript.gv"),
             grapher.toString()
         );
     }
}
