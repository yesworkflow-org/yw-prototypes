package org.yesworkflow.graph.tests;

import org.yesworkflow.graph.CommentVisibility;
import org.yesworkflow.graph.GraphView;
import org.yesworkflow.graph.ParamVisibility;
import org.yesworkflow.graph.PortLayout;
import org.yesworkflow.graph.TitlePosition;

public class TestDotGrapher_CombinedView extends DotGrapherTestCase {
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testResourceDirectory = "src/test/resources/org/yesworkflow/graph/TestDotGrapher_CombinedView/";
        grapher.configure("view", GraphView.COMBINED_VIEW)
               .configure("dotcomments", CommentVisibility.ON)
               .configure("params", ParamVisibility.SHOW)
               .configure("titleposition", TitlePosition.HIDE)
               .configure("portlayout", PortLayout.RELAX);
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

    public void testDotGrapher_CombinedView_TwoChannels_OneProgram_OneInOneParamOneOut_HideParams() throws Exception {
        grapher.configure("params", ParamVisibility.HIDE);
        String src = "twoChannels_OneProgram_OneInOneParamOneOut";
        assertEquals(expectedGraph(src + "_HideParams"), actualGraph(src));
        assertEquals("", stderrBuffer.toString());  
    }
    
    public void testDotGrapher_CombinedView_TwoChannels_OneProgram_OneInOneParamOneOut_ReduceParams() throws Exception {
        grapher.configure("params", ParamVisibility.REDUCE);
        String src = "twoChannels_OneProgram_OneInOneParamOneOut";
        assertEquals(expectedGraph(src + "_ReduceParams"), actualGraph(src));
        assertEquals("", stderrBuffer.toString());  
    }

    public void testDotGrapher_CombinedView_SamplePyScript() throws Exception {
         String src = "examplePyScript";
         assertEquals(expectedGraph(src), actualGraph(src));  
     }
}
