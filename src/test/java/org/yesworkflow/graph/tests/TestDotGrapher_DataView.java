package org.yesworkflow.graph.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.graph.CommentVisibility;
import org.yesworkflow.graph.DotGrapher;
import org.yesworkflow.graph.GraphView;
import org.yesworkflow.graph.Grapher;
import org.yesworkflow.graph.ParamVisibility;
import org.yesworkflow.graph.TitlePosition;
import org.yesworkflow.model.DefaultModeler;
import org.yesworkflow.model.Modeler;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.YesWorkflowTestCase;

public class TestDotGrapher_DataView extends YesWorkflowTestCase {

	Extractor extractor = null;
    Modeler modeler = null;
    Grapher grapher = null;
    Map<String,Object> config = null;
    
    static final String TEST_RESOURCE_DIR = "src/test/resources/org/yesworkflow/graph/TestDotGrapher_DataView/";
    
    @Override
    public void setUp() throws Exception {
        
        super.setUp();
        
        extractor = new DefaultExtractor(super.stdoutStream, super.stderrStream);
        modeler = new DefaultModeler(super.stdoutStream, super.stderrStream);
        grapher = new DotGrapher(super.stdoutStream, super.stderrStream);
        config = new HashMap<String,Object>();
        
        grapher.configure("view", GraphView.DATA_CENTRIC_VIEW)
               .configure("comments", CommentVisibility.HIDE)
               .configure("titleposition", TitlePosition.HIDE);
    }    

    public void testDotGrapher_DataView_TwoProgramsOneChannel_OneInOneOut() throws Exception {
        
        String src = "twoProgramsOneChannel_OneInOneOut";
        assertEquals(expectedGraph(src), actualGraph(src));
        assertEquals("", stderrBuffer.toString());
    }
    
    public void testDotGrapher_DataView_TwoProgramsOneChannel_OneParamOneOut_ShowParams() throws Exception {
      
        grapher.configure("params", ParamVisibility.SHOW);
        
        assertEquals(expectedGraph("twoProgramsOneChannel_OneParamOneOut_ShowParams"), 
                     actualGraph("twoProgramsOneChannel_OneParamOneOut"));
        assertEquals("", stderrBuffer.toString());
    }

    public void testDotGrapher_DataView_TwoProgramsOneChannel_OneParamOneOut_HideParams() throws Exception {

        grapher.configure("params", ParamVisibility.HIDE);
        
        assertEquals(expectedGraph("twoProgramsOneChannel_OneParamOneOut_HideParams"), 
                     actualGraph("twoProgramsOneChannel_OneParamOneOut"));        
        assertEquals("", stderrBuffer.toString());
    }

    public void testDotGrapher_DataView_TwoChannels_OneProgram_OneInOneOut() throws Exception {
      
        String src = "twoChannels_OneProgram_OneInOneOut";
        assertEquals(expectedGraph(src), actualGraph(src));        
        assertEquals("", stderrBuffer.toString());
  }
  
    public void testDotGrapher_DataView_TwoChannels_OneProgram_OneParamOneOut_ShowParams() throws Exception {
        
        grapher.configure("params", ParamVisibility.SHOW);
        
        assertEquals(expectedGraph("twoChannels_OneProgram_OneParamOneOut_ShowParams"),
                     actualGraph("twoChannels_OneProgram_OneParamOneOut"));        
        assertEquals("", stderrBuffer.toString());
    }
    
   public void testDotGrapher_DataView_TwoChannels_OneProgram_OneParamOneOut_HideParams() throws Exception {
        
        grapher.configure("params", ParamVisibility.HIDE);
        
        assertEquals(expectedGraph("twoChannels_OneProgram_OneParamOneOut_HideParams"), 
                     actualGraph("twoChannels_OneProgram_OneParamOneOut"));        
        assertEquals("", stderrBuffer.toString());
    }

   public void testDotGrapher_DataView_TwoChannels_OneProgram_OneParamOneOut_ReduceParams() throws Exception {
       
       grapher.configure("params", ParamVisibility.REDUCE);
       
       assertEquals(expectedGraph("twoChannels_OneProgram_OneParamOneOut_ReduceParams"), 
                    actualGraph("twoChannels_OneProgram_OneParamOneOut"));        
       assertEquals("", stderrBuffer.toString());
   }

   
    public void testDotGrapher_DataView_TwoChannels_OneProgram_TwoInOneOut() throws Exception {
        String src = "twoChannels_OneProgram_TwoInOneOut";
        assertEquals(expectedGraph(src), actualGraph(src));
        assertEquals("", stderrBuffer.toString());
    }

    public void testDotGrapher_DataView_TwoChannels_OneProgram_OneInOneParamOneOut_ShowParams() throws Exception {
        grapher.configure("params", ParamVisibility.SHOW);
        
        assertEquals(expectedGraph("twoChannels_OneProgram_OneInOneParamOneOut_ShowParams"), 
                     actualGraph("twoChannels_OneProgram_OneInOneParamOneOut"));        
        assertEquals("", stderrBuffer.toString());
    }

    public void testDotGrapher_DataView_TwoChannels_OneProgram_OneInOneParamOneOut_HideParams() throws Exception {
        grapher.configure("params", ParamVisibility.HIDE);
        
        assertEquals(expectedGraph("twoChannels_OneProgram_OneInOneParamOneOut_HideParams"), 
                     actualGraph("twoChannels_OneProgram_OneInOneParamOneOut"));        
        assertEquals("", stderrBuffer.toString());
    }

    public void testDotGrapher_DataView_TwoChannels_OneProgram_OneInOneParamOneOut_ReduceParams() throws Exception {
        grapher.configure("params", ParamVisibility.REDUCE);
        
        assertEquals(expectedGraph("twoChannels_OneProgram_OneInOneParamOneOut_ReduceParams"), 
                     actualGraph("twoChannels_OneProgram_OneInOneParamOneOut"));        
        assertEquals("", stderrBuffer.toString());
    }

    
    private String actualGraph(String name) throws Exception {
        
        String script = super.readTextFile(TEST_RESOURCE_DIR + name + ".in");

        BufferedReader reader = new BufferedReader(new StringReader(script));
        
        List<Annotation> annotations = extractor
                .configure("comment", "#")
                .reader(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = (Workflow)modeler.annotations(annotations)
                                             .model()
                                             .getModel()
                                             .program;

        grapher.workflow(workflow)
               .graph();
        
        return grapher.toString();
   }
   
   private String expectedGraph(String name) throws IOException {
       return readTextFile(TEST_RESOURCE_DIR + name + ".gv");
   }
}
