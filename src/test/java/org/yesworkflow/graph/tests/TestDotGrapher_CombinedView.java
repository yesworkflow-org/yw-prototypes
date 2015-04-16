package org.yesworkflow.graph.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.graph.CommentVisibility;
import org.yesworkflow.graph.DotGrapher;
import org.yesworkflow.graph.GraphView;
import org.yesworkflow.graph.Grapher;
import org.yesworkflow.model.DefaultModeler;
import org.yesworkflow.model.Modeler;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.YesWorkflowTestCase;

public class TestDotGrapher_CombinedView extends YesWorkflowTestCase {

	Extractor extractor = null;
    Modeler modeler = null;
    Grapher grapher = null;
    
    static final String TEST_RESOURCE_DIR = "src/test/resources/org/yesworkflow/graph/TestDotGrapher_CombinedView/";
    
    @Override
    public void setUp() throws Exception {
        
        super.setUp();
        
        extractor = new DefaultExtractor(super.stdoutStream, super.stderrStream);
        modeler = new DefaultModeler(super.stdoutStream, super.stderrStream);
        grapher = new DotGrapher(super.stdoutStream, super.stderrStream);
        
        grapher.configure("view", GraphView.COMBINED_VIEW)
               .configure("comments", CommentVisibility.HIDE);
    }
    
    public void testDotGrapher_CombinedView_TwoProgramsOneChannel_InOut() throws Exception {
        String src = "twoProgramsOneChannel_InOut";
        assertEquals(expectedGraph(src), actualGraph(src));
        assertEquals("", stderrBuffer.toString());
    }
    
    public void testDotGrapher_CombinedView_TwoProgramsOneChannel_ParamOut() throws Exception {        
        String src = "twoProgramsOneChannel_ParamOut";
        assertEquals(expectedGraph(src), actualGraph(src));
        assertEquals("", stderrBuffer.toString());
    }     
    
    public void testDotGrapher_CombinedView_TwoChannels_OneProgram_OneInOneOut() throws Exception {
        String src = "twoChannels_OneProgram_OneInOneOut";
        assertEquals(expectedGraph(src), actualGraph(src));
        assertEquals("", stderrBuffer.toString());  
    }
  
    public void testDotGrapher_CombinedView_TwoChannels_OneProgram_TwoInOneOut() throws Exception {
        String src = "twoChannels_OneProgram_TwoInOneOut";
        assertEquals(expectedGraph(src), actualGraph(src));
        assertEquals("", stderrBuffer.toString());  
      }

  public void testDotGrapher_CombinedView_TwoChannels_OneProgram_OneInOneParamOneOut() throws Exception {
      String src = "twoChannels_OneProgram_OneInOneParamOneOut";
      assertEquals(expectedGraph(src), actualGraph(src));
      assertEquals("", stderrBuffer.toString());  
      }

     public void testDotGrapher_CombinedView_SamplePyScript() throws Exception {
         String src = "examplePyScript";
         assertEquals(expectedGraph(src), actualGraph(src));  
     }

     private String actualGraph(String name) throws Exception {
         
         String script = super.readTextFile(TEST_RESOURCE_DIR + name + ".in");

         BufferedReader reader = new BufferedReader(new StringReader(script));
         
         List<Annotation> annotations = extractor
                 .configure("comment", "#")
                 .reader(reader)
                 .extract()
                 .getAnnotations();

         Workflow workflow = modeler.annotations(annotations)
                                    .model()
                                    .getWorkflow();

         grapher.workflow(workflow)
                .configure("portlayout", "relax")
                .graph();
         
         return grapher.toString();
    }
     
    
    private String expectedGraph(String name) throws IOException {
        return readTextFile(TEST_RESOURCE_DIR + name + ".gv");
    }     
}
