package org.yesworkflow.graph;

import org.yesworkflow.graph.CommentVisibility;
import org.yesworkflow.graph.GraphView;
import org.yesworkflow.graph.LayoutDirection;
import org.yesworkflow.graph.ParamVisibility;
import org.yesworkflow.graph.PortLayout;
import org.yesworkflow.graph.TitlePosition;

public class TestDotGrapher_ProcessView extends DotGrapherTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();        
        testResourceDirectory = "src/test/resources/org/yesworkflow/graph/TestDotGrapher_ProcessView/";
        grapher.configure("view", GraphView.PROCESS_CENTRIC_VIEW)
               .configure("dotcomments", CommentVisibility.ON)
               .configure("params", ParamVisibility.SHOW)
               .configure("titleposition", TitlePosition.HIDE);
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

    public void test_OneProgram_TwoChannels_OneInOneOut_VerticalLayout() throws Exception {  
        grapher.configure("layout", LayoutDirection.TB);
        assertEquals(expectedGraph("oneProgram_TwoChannels_OneInOneOut_VerticalLayout"), 
                     actualGraph("oneProgram_TwoChannels_OneInOneOut"));
        assertEquals("", stderrBuffer.toString());
    }

    public void test_OneProgram_TwoChannels_OneInOneOut_HideParams() throws Exception {  
        grapher.configure("params", ParamVisibility.HIDE);
        assertEquals(expectedGraph("oneProgram_TwoChannels_OneInOneOut_HideParams"), 
                     actualGraph("oneProgram_TwoChannels_OneInOneOut"));
        assertEquals("", stderrBuffer.toString());
    }
    
    public void test_OneProgram_TwoChannels_OneParamOneOut() throws Exception {
        String src = "oneProgram_TwoChannels_OneParamOneOut";
        assertEquals(expectedGraph(src), actualGraph(src));
        assertEquals("", stderrBuffer.toString());
    }

    public void test_OneProgram_TwoChannels_OneParamOneOut_HideParams() throws Exception {
        grapher.configure("params", ParamVisibility.HIDE);
        assertEquals(expectedGraph("oneProgram_TwoChannels_OneParamOneOut_HideParams"), 
                     actualGraph("oneProgram_TwoChannels_OneParamOneOut"));
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

    public void test_OneProgram_TwoChannels_OneParamOneInOneOut_HideParams() throws Exception {
        grapher.configure("params", ParamVisibility.HIDE);
        assertEquals(expectedGraph("oneProgram_TwoChannels_OneParamOneInOneOut_HideParams"), 
                     actualGraph("oneProgram_TwoChannels_OneParamOneInOneOut"));
        assertEquals("", stderrBuffer.toString());
    }
    
    public void test_OneProgram_TwoInOneOut_ExtraIn() throws Exception {      
        String src = "oneProgram_TwoInOneOut_ExtraIn";
        assertEquals(expectedGraph(src), actualGraph(src));
    }
  
    public void test_OneProgram_TwoChannels_OneInOneOut_ExtraOut() throws Exception {
        String src = "oneProgram_TwoChannels_OneInOneOut_ExtraOut";
        assertEquals(expectedGraph(src), actualGraph(src));   
    }
  
     public void test_ThreePrograms_TwoChannels() throws Exception {
         String src = "threePrograms_TwoChannels";
         assertEquals(expectedGraph(src), actualGraph(src));  
     }
 
     public void test_NestedSubworkflow_Top() throws Exception {
         assertEquals(expectedGraph("nestedSubworkflow"), actualGraph("nestedSubworkflow"));  
     }

     public void test_NestedSubworkflow_Sub() throws Exception {
         grapher.configure("subworkflow", "workflow.subWorkflow");
         assertEquals(expectedGraph("nestedSubworkflow_sub"), actualGraph("nestedSubworkflow"));  
     }

     public void test_DoublyNestedSubworkflow_Top() throws Exception {
         assertEquals(expectedGraph("doublyNestedSubworkflow"), actualGraph("doublyNestedSubworkflow"));  
     }

     public void test_DoublyNestedSubworkflow_Sub() throws Exception {
         grapher.configure("subworkflow", "workflow.subWorkflow");
         assertEquals(expectedGraph("doublyNestedSubworkflow_sub"), actualGraph("doublyNestedSubworkflow"));
     }

     public void test_DoublyNestedSubworkflow_SubSub() throws Exception {
         grapher.configure("subworkflow", "workflow.subWorkflow.subSubWorkflow");
         assertEquals(expectedGraph("doublyNestedSubworkflow_subSub"), actualGraph("doublyNestedSubworkflow"));  
     }

     public void test_ExamplePyScript() throws Exception {
         grapher.configure("portlayout", PortLayout.RELAX);
         String src = "examplePyScript";
         assertEquals(expectedGraph(src), actualGraph(src));  
     }
}
