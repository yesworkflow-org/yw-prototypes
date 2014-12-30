package org.yesworkflow.graph;

import java.io.BufferedReader;
import java.io.StringReader;

import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestDotGrapher extends YesWorkflowTestCase {

    DefaultExtractor extractor = null;
    DotGrapher grapher = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new DefaultExtractor();
        grapher = new DotGrapher();
    }

    public void testDotGrapher_TwoProgramsOneChannel() throws Exception {
        
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
               .type(GraphType.DATA_FLOW_GRAPH)
               .graph();
        
        String dotString = grapher.toString();

        assertEquals(
            "digraph Workflow {"                                        + EOL +
            "rankdir=LR"                                                + EOL +
            "node1 [label=\"program0\",shape=box,peripheries=1];"       + EOL +
            "node2 [label=\"program1\",shape=box,peripheries=1];"       + EOL +
            "node1 -> node2 [label=\"channel\"];"                       + EOL +
            "}"                                                         + EOL,
            dotString);
    }
 
 public void testDotGrapher_ThreeProgramsTwoChannel() throws Exception {
     
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
            .type(GraphType.DATA_FLOW_GRAPH)
            .graph();
     
     String dotString = grapher.toString();

     assertEquals(
         "digraph Workflow {"                                       + EOL +
         "rankdir=LR"                                               + EOL +
         "node1 [label=\"program0\",shape=box,peripheries=1];"      + EOL +
         "node2 [label=\"program1\",shape=box,peripheries=1];"      + EOL +
         "node3 [label=\"program2\",shape=box,peripheries=1];"      + EOL +
         "node1 -> node2 [label=\"channel0\"];"                     + EOL +
         "node1 -> node3 [label=\"channel1\"];"                     + EOL +
         "}"                                                        + EOL,
         dotString);
     }
 
     public void testDotGrapher_SamplePyScript() throws Exception {
         
         extractor.sourcePath("src/main/resources/example.py")
             .commentCharacter('#')
             .extract();

         Workflow workflow = (Workflow)extractor.getProgram();
    
         grapher.workflow(workflow)
                .type(GraphType.DATA_FLOW_GRAPH)
                .graph();
         
         String dotString = grapher.toString();
    
         assertEquals(
             "digraph Workflow {"                                                   + EOL +
             "rankdir=LR"                                                           + EOL +
             "node1 [label=\"fetch_mask\",shape=box,peripheries=1];"                + EOL +
             "node2 [label=\"load_data\",shape=box,peripheries=1];"                 + EOL +
             "node3 [label=\"standardize_with_mask\",shape=box,peripheries=1];"     + EOL +
             "node4 [label=\"simple_diagnose\",shape=box,peripheries=1];"           + EOL +
             "node1 -> node3 [label=\"land_water_mask\"];"                          + EOL +
             "node3 -> node4 [label=\"standardized_NEE_data\"];"                    + EOL +
             "node2 -> node3 [label=\"NEE_data\"];"                                 + EOL +
             "}"                                                                    + EOL,
             dotString);
     }
}
