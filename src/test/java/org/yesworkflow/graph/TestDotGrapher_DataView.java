package org.yesworkflow.graph;

import org.yesworkflow.graph.CommentVisibility;
import org.yesworkflow.graph.GraphView;
import org.yesworkflow.graph.ParamVisibility;
import org.yesworkflow.graph.PortLayout;
import org.yesworkflow.graph.TitlePosition;

public class TestDotGrapher_DataView extends DotGrapherTestCase {

    @Override
    public void setUp() throws Exception {        
        super.setUp();
        testResourceDirectory = "src/test/resources/org/yesworkflow/graph/TestDotGrapher_DataView/";
        grapher.configure("view", GraphView.DATA_CENTRIC_VIEW)
               .configure("dotcomments", CommentVisibility.ON)
               .configure("params", ParamVisibility.SHOW)
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
        grapher.configure("portlayout", PortLayout.RELAX);
        String src = "twoChannels_OneProgram_TwoInOneOut";
        assertEquals(expectedGraph(src), actualGraph(src));
        assertEquals("", stderrBuffer.toString());
    }

    public void testDotGrapher_DataView_TwoChannels_OneProgram_OneInOneParamOneOut_ShowParams() throws Exception {
        grapher.configure("params", ParamVisibility.SHOW);
        grapher.configure("portlayout", PortLayout.RELAX);
        assertEquals(expectedGraph("twoChannels_OneProgram_OneInOneParamOneOut_ShowParams"), 
                     actualGraph("twoChannels_OneProgram_OneInOneParamOneOut"));        
        assertEquals("", stderrBuffer.toString());
    }

    public void testDotGrapher_DataView_TwoChannels_OneProgram_OneInOneParamOneOut_HideParams() throws Exception {
        grapher.configure("params", ParamVisibility.HIDE);
        grapher.configure("portlayout", PortLayout.RELAX);
        assertEquals(expectedGraph("twoChannels_OneProgram_OneInOneParamOneOut_HideParams"), 
                     actualGraph("twoChannels_OneProgram_OneInOneParamOneOut"));        
        assertEquals("", stderrBuffer.toString());
    }

    public void testDotGrapher_DataView_TwoChannels_OneProgram_OneInOneParamOneOut_ReduceParams() throws Exception {
        grapher.configure("params", ParamVisibility.REDUCE);        
        grapher.configure("portlayout", PortLayout.RELAX);
        assertEquals(expectedGraph("twoChannels_OneProgram_OneInOneParamOneOut_ReduceParams"), 
                     actualGraph("twoChannels_OneProgram_OneInOneParamOneOut"));        
        assertEquals("", stderrBuffer.toString());
    }
}
