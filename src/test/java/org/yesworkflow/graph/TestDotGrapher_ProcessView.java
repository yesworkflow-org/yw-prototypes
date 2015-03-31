package org.yesworkflow.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.model.DefaultModeler;
import org.yesworkflow.model.Modeler;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.YesWorkflowTestCase;

public class TestDotGrapher_ProcessView extends YesWorkflowTestCase {

	Extractor extractor = null;
    Modeler modeler = null;
    Grapher grapher = null;
    Map<String,Object> config = null;
    
    static final String TEST_RESOURCE_DIR = "src/test/resources/org/yesworkflow/graph/";
    
    @Override
    public void setUp() throws Exception {
        
        super.setUp();
        
        extractor = new DefaultExtractor(super.stdoutStream, super.stderrStream);
        modeler = new DefaultModeler(super.stdoutStream, super.stderrStream);
        grapher = new DotGrapher(super.stdoutStream, super.stderrStream);

        grapher.configure("view", GraphView.PROCESS_CENTRIC_VIEW)
               .configure("comments", CommentVisibility.HIDE);
    }
    
    public void test_TwoProgramsOneChannel_In() throws Exception {
        String src = "twoProgramsOneChannel_In";
        assertEquals(expectedGraph(src), actualGraph(src));
        assertEquals("", stderrBuffer.toString());
    }    
    
    public void test_OneProgram_TwoChannels_OneInOneOut() throws Exception {  
        String src = "oneProgram_TwoChannels_OneInOneOut";
        assertEquals(expectedGraph(src), actualGraph(src));
        assertEquals("", stderrBuffer.toString());
    }
    
    public void test_OneProgram_TwoChannels_OneParamOneOut() throws Exception {
        String src = "oneProgram_TwoChannels_OneParamOneOut";
        assertEquals(expectedGraph(src), actualGraph(src));
        assertEquals("", stderrBuffer.toString());
    }
  
    public void test_OneProgram_ThreeChannels_TwoInOneOut() throws Exception {
        String src = "oneProgram_ThreeChannels_TwoInOneOut";
        assertEquals(expectedGraph(src), actualGraph(src));
        assertEquals("", stderrBuffer.toString());
    }

    public void test_OneProgram_TwoChannels_OneParamOneInOneOut() throws Exception {
        String src = "oneProgram_TwoChannels_OneParamOneInOneOut";
        assertEquals(expectedGraph(src), actualGraph(src));
        assertEquals("", stderrBuffer.toString());
    }
    
    public void test_OneProgram_TwoInOneOut_ExtraIn() throws Exception {      
        String src = "oneProgram_TwoInOneOut_ExtraIn";
        assertEquals(expectedGraph(src), actualGraph(src));
        assertEquals(
            "WARNING: No nested @in port and no workflow @out port for nested @out 'y' in workflow 'script'" + EOL, 
            stderrBuffer.toString());
    }
  
    public void test_OneProgram__TwoChannels_OneInOneOut_ExtraOut() throws Exception {
        String src = "oneProgram__TwoChannels_OneInOneOut_ExtraOut";
        assertEquals(expectedGraph(src), actualGraph(src));      
        assertEquals(
            "WARNING: No nested @out port and no workflow @in port for nested @in 'c' on 'script'" + EOL,
            stderrBuffer.toString());
    }
  
     public void test_ThreePrograms_TwoChannels() throws Exception {
         String src = "threePrograms_TwoChannels";
         assertEquals(expectedGraph(src), actualGraph(src));  
     }
 
     public void test_NestedSubworkflow() throws Exception {
         String src = "nestedSubworkflow";
         assertEquals(expectedGraph(src), actualGraph(src));  
     }
 
     public void test_ExamplePyScript() throws Exception {
         String src = "examplePyScript";
         assertEquals(expectedGraph(src), actualGraph(src));  
     }

     private String actualGraph(String name) throws Exception {
          
          String script = super.readTextFile(TEST_RESOURCE_DIR + name + ".in");

          BufferedReader reader = new BufferedReader(new StringReader(script));
          
          List<Annotation> annotations = extractor
                  .configure("comment", "#")
                  .source(reader)
                  .extract()
                  .getAnnotations();

          Workflow workflow = modeler.annotations(annotations)
                                     .model()
                                     .getWorkflow();

          grapher.workflow(workflow)
                 .graph();
          
          return grapher.toString();
     }
      
     
     private String expectedGraph(String name) throws IOException {
         return readTextFile(TEST_RESOURCE_DIR + name + ".gv");
     }
}
