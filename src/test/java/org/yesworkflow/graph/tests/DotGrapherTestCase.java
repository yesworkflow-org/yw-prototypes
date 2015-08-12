package org.yesworkflow.graph.tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.graph.DotGrapher;
import org.yesworkflow.graph.Grapher;
import org.yesworkflow.model.DefaultModeler;
import org.yesworkflow.model.Modeler;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.YesWorkflowTestCase;

public abstract class DotGrapherTestCase extends YesWorkflowTestCase {
    
    private YesWorkflowDB ywdb = null;
    protected Extractor extractor = null;
    protected Modeler modeler = null;
    protected Grapher grapher = null;
    protected String testResourceDirectory = "";
    
    public void setUp() throws Exception {
        
        super.setUp();
        
        ywdb = YesWorkflowDB.createInMemoryDB();
        extractor = new DefaultExtractor(this.ywdb, super.stdoutStream, super.stderrStream);
        modeler = new DefaultModeler(super.stdoutStream, super.stderrStream);
        grapher = new DotGrapher(super.stdoutStream, super.stderrStream);
    }
    
    protected String actualGraph(String name) throws Exception {
         
        String script = super.readTextFile(testResourceDirectory + name + ".in");
        BufferedReader reader = new BufferedReader(new StringReader(script));

        List<Annotation> annotations = extractor
                 .configure("comment", "#")
                 .reader(reader)
                 .extract()
                 .getAnnotations();
         
        Workflow workflow = (Workflow)modeler.annotations(annotations)
                                              .model()
                                              .getModel().program;

        grapher.workflow(workflow)
                .graph();
         
        return grapher.toString();
    }
         
    protected String expectedGraph(String name) throws IOException {
        return readTextFile(testResourceDirectory + name + ".gv");
    }     
}
