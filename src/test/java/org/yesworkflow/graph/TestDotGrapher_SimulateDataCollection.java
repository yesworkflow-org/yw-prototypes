package org.yesworkflow.graph;

import org.yesworkflow.graph.CommentVisibility;
import org.yesworkflow.graph.GraphView;
import org.yesworkflow.graph.ParamVisibility;
import org.yesworkflow.graph.TitlePosition;

public class TestDotGrapher_SimulateDataCollection extends DotGrapherTestCase {

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

    }    
}
