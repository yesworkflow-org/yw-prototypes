package org.yesworkflow.model;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.YesWorkflowTestCase;

public class TestModeler_Functions extends YesWorkflowTestCase {

    private YesWorkflowDB ywdb = null;
	Extractor extractor = null;
    Modeler modeler = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.ywdb = YesWorkflowDB.createInMemoryDB();
        extractor = new DefaultExtractor(this.ywdb, super.stdoutStream, super.stderrStream);
        modeler = new DefaultModeler(super.stdoutStream, super.stderrStream);
        
        extractor.configure("comment", "#");
    }
    
    public void testExtract_Workflow_OneTopFunction() throws Exception {
        
        String source = 
                "# @begin script"	    + EOL +
                "  some code"		    + EOL +
                "# @end script"         + EOL +
                ""                      + EOL +
                "# @begin function"     + EOL +
                "  some more code"      + EOL +
                "# @end function"       + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor.reader(reader)
                                                .extract()
                                                .getAnnotations();

        
        Model model = modeler.annotations(annotations)
                                 .model()
                                 .getModel();
        
        assertEquals("script", model.program.name);
        assertEquals("script", model.program.beginAnnotation.name);
        assertEquals("script", model.program.endAnnotation.name);
        assertEquals(0, model.program.inPorts.length);
        assertEquals(0, model.program.outPorts.length);
        assertEquals(0, model.program.channels.length);

        assertEquals(1, model.functions.length);
        assertEquals("function", model.functions[0].name);
        assertEquals("function", model.functions[0].beginAnnotation.name);
        assertEquals("function", model.functions[0].endAnnotation.name);
        assertEquals(0, model.functions[0].channels.length);
    }

    public void testExtract_WorkflowOneProgram_TopFunctionOneProgram() throws Exception {
        
        String source = 
                "# @begin workflow"		+ EOL +
                "#   @begin program1"	+ EOL +
                "#   @end program1"		+ EOL +
                "# @end workflow"		+ EOL +
                "#"                     + EOL +
                "# @begin function"     + EOL +
                "#   @begin program2"   + EOL +
                "#   @end program2"     + EOL +
                "# @end function"       + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor.reader(reader)
                                                .extract()
                                                .getAnnotations();

        Model model = modeler.annotations(annotations)
                             .model()
                             .getModel();

        assertEquals("workflow", model.program.name);
        assertEquals("workflow", model.program.beginAnnotation.name);
        assertEquals("workflow", model.program.endAnnotation.name);
        assertEquals(0, model.program.inPorts.length);
        assertEquals(0, model.program.outPorts.length);
        assertEquals(0, model.program.channels.length);
        
        assertEquals(1, model.program.programs.length);
        assertFalse(model.program.programs[0] instanceof Workflow);
        assertEquals("workflow.program1", model.program.programs[0].name);
        assertEquals("program1", model.program.programs[0].beginAnnotation.name);
        
        assertEquals(1, model.functions.length);
        assertEquals("function", model.functions[0].name);
        assertEquals("function", model.functions[0].beginAnnotation.name);
        assertEquals(1, model.functions[0].programs.length);
        assertEquals("function.program2", model.functions[0].programs[0].name);
        assertEquals("program2", model.functions[0].programs[0].beginAnnotation.name);
        assertEquals(0, model.functions[0].channels.length);
    }

    public void testExtract_WorkflowOneProgram_TopFunctionTwoPrograms() throws Exception {
        
        String source = 
                "# @begin workflow"     + EOL +
                "#   @begin program1"   + EOL +
                "#   @end program1"     + EOL +
                "# @end workflow"       + EOL +
                "#"                     + EOL +
                "# @begin function"     + EOL +
                "#   @begin program2"   + EOL +
                "#   @out channel1"     + EOL +
                "#   @end program2"     + EOL +
                "#   @begin program3"   + EOL +
                "#   @in channel1"      + EOL +
                "#   @end program3"     + EOL +
                "# @end function"       + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor.reader(reader)
                                                .extract()
                                                .getAnnotations();


        Model model = modeler.annotations(annotations)
                             .model()
                             .getModel();

        assertEquals("workflow", model.program.name);
        assertEquals("workflow", model.program.beginAnnotation.name);
        assertEquals("workflow", model.program.endAnnotation.name);
        assertEquals(0, model.program.inPorts.length);
        assertEquals(0, model.program.outPorts.length);
        assertEquals(0, model.program.channels.length);
        
        assertEquals(1, model.program.programs.length);
        assertFalse(model.program.programs[0] instanceof Workflow);
        assertEquals("workflow.program1", model.program.programs[0].name);
        assertEquals("program1", model.program.programs[0].beginAnnotation.name);
        
        assertEquals(1, model.functions.length);
        assertEquals("function", model.functions[0].name);
        assertEquals("function", model.functions[0].beginAnnotation.name);
        assertEquals(2, model.functions[0].programs.length);
        assertEquals("function.program2", model.functions[0].programs[0].name);
        assertEquals("program2", model.functions[0].programs[0].beginAnnotation.name);
        assertEquals("function.program3", model.functions[0].programs[1].name);
        assertEquals("program3", model.functions[0].programs[1].beginAnnotation.name);
        assertEquals(1, model.functions[0].channels.length);
        assertEquals("channel1", model.functions[0].channels[0].sourcePort.flowAnnotation.binding());        
    }

    public void testExtract_WorkflowOneProgram_TwoTopFunctions_BothAfterWorkflow() throws Exception {
        
        String source = 
                "# @begin workflow"     + EOL +
                "#   @begin program1"   + EOL +
                "#   @end program1"     + EOL +
                "# @end workflow"       + EOL +
                "#"                     + EOL +
                "# @begin function1"    + EOL +
                "# @end function1"      + EOL +
                "#"                     + EOL +
                "# @begin function2"    + EOL +
                "# @end function2"      + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor.reader(reader)
                .extract()
                .getAnnotations();

        Model model = modeler.annotations(annotations)
                             .model()
                             .getModel();

        assertEquals("workflow", model.program.name);
        assertEquals("workflow", model.program.beginAnnotation.name);
        assertEquals("workflow", model.program.endAnnotation.name);
        assertEquals(0, model.program.inPorts.length);
        assertEquals(0, model.program.outPorts.length);
        assertEquals(0, model.program.channels.length);
        
        assertEquals(1, model.program.programs.length);
        assertFalse(model.program.programs[0] instanceof Workflow);
        assertEquals("workflow.program1", model.program.programs[0].name);
        assertEquals("program1", model.program.programs[0].beginAnnotation.name);

        assertEquals(2, model.functions.length);
        assertEquals("function1", model.functions[0].name);
        assertEquals("function1", model.functions[0].beginAnnotation.name);
        assertEquals(0, model.functions[0].programs.length);
        assertEquals(0, model.functions[0].channels.length);

        assertEquals("function2", model.functions[1].name);
        assertEquals("function2", model.functions[1].beginAnnotation.name);
        assertEquals(0, model.functions[1].programs.length);
        assertEquals(0, model.functions[1].channels.length);
    }


    public void testExtract_WorkflowOneProgram_TwoTopFunctions_BeforeAndAfterWorkflow() throws Exception {
        
        String source = 
                "# @begin function1"    + EOL +
                "# @end function1"      + EOL +
                "#"                     + EOL +
                "# @begin workflow"     + EOL +
                "#   @begin program1"   + EOL +
                "#   @end program1"     + EOL +
                "# @end workflow"       + EOL +
                "#"                     + EOL +
                "# @begin function2"    + EOL +
                "# @end function2"      + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor.reader(reader)
                                                .extract()
                                                .getAnnotations();


        Model model = modeler.annotations(annotations)
                             .configure("workflow", "workflow")
                             .model()
                             .getModel();

        assertEquals("workflow", model.program.name);
        assertEquals("workflow", model.program.beginAnnotation.name);
        assertEquals("workflow", model.program.endAnnotation.name);
        assertEquals(0, model.program.inPorts.length);
        assertEquals(0, model.program.outPorts.length);
        assertEquals(0, model.program.channels.length);
        
        assertEquals(1, model.program.programs.length);
        assertFalse(model.program.programs[0] instanceof Workflow);
        assertEquals("workflow.program1", model.program.programs[0].name);
        assertEquals("program1", model.program.programs[0].beginAnnotation.name);

        assertEquals(2, model.functions.length);
        assertEquals("function1", model.functions[0].name);
        assertEquals("function1", model.functions[0].beginAnnotation.name);
        assertEquals(0, model.functions[0].programs.length);
        assertEquals(0, model.functions[0].channels.length);

        assertEquals("function2", model.functions[1].name);
        assertEquals("function2", model.functions[1].beginAnnotation.name);
        assertEquals(0, model.functions[1].programs.length);
        assertEquals(0, model.functions[1].channels.length);
    }

    public void testExtract_MissingNamedWorkflow_TwoTopFunctions() throws Exception {
        
        String source = 
                "# @begin function1"    + EOL +
                "# @end function1"      + EOL +
                "#"                     + EOL +
                "# @begin function2"    + EOL +
                "# @end function2"      + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor.reader(reader)
                                                .extract()
                                                .getAnnotations();
    
        Exception caught = null;
        try {
            modeler.annotations(annotations)
                  .configure("workflow", "workflow")
                  .model()
                  .getModel();
        } catch(Exception e) {
            caught = e;
        }
        assertNotNull(caught);
        assertEquals("No workflow named 'workflow' found in source.", caught.getMessage());
    }    
    
    public void testExtract_NoWorkflowNamed_TwoTopFunctions() throws Exception {
        
        String source = 
                "# @begin function1"    + EOL +
                "# @end function1"      + EOL +
                "#"                     + EOL +
                "# @begin function2"    + EOL +
                "# @end function2"      + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor.reader(reader)
                                                .extract()
                                                .getAnnotations();

        Model model = modeler.annotations(annotations)
                             .model()
                             .getModel();

        assertEquals("function1", model.program.name);
        assertEquals("function1", model.program.beginAnnotation.name);
        assertEquals(0, model.program.programs.length);
        assertEquals(0, model.program.channels.length);

        assertEquals(1, model.functions.length);
        assertEquals("function2", model.functions[0].name);
        assertEquals("function2", model.functions[0].beginAnnotation.name);
        assertEquals(0, model.functions[0].programs.length);
        assertEquals(0, model.functions[0].channels.length);
    }

    public void testExtract_TopWorkflowWithReturn() throws Exception {
        
        String source = 
                "# @begin workflow"    + EOL +
                "#   @return result"   + EOL +
                "# @end workflow"      + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor.reader(reader)
                                                .extract()
                                                .getAnnotations();

        Model model = modeler.annotations(annotations)
                             .configure("workflow", "workflow")
                             .model()
                             .getModel();

        assertEquals(0, model.functions.length);
        
        assertEquals("workflow", model.program.name);
        assertEquals("workflow", model.program.beginAnnotation.name);
        assertEquals(0, model.program.inPorts.length);
        assertEquals(0, model.program.outPorts.length);
        assertEquals(0, model.program.channels.length);
        assertEquals(1, ((Function)(model.program)).returnPorts.length);
        assertEquals("result", ((Function)(model.program)).returnPorts[0].flowAnnotation.name);
        
        assertEquals(0, model.program.programs.length);
        assertEquals(0, model.program.functions.length);
    }

    public void testExtract_FunctionInWorkflow() throws Exception {
        
        String source = 
                "# @begin workflow"    + EOL +
                "#   @begin function"  + EOL +
                "#   @return result"   + EOL +
                "#   @end function"    + EOL +
                "# @end workflow"      + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor.reader(reader)
                                                .extract()
                                                .getAnnotations();

        Model model = modeler.annotations(annotations)
                             .configure("workflow", "workflow")
                             .model()
                             .getModel();

        assertEquals(0, model.functions.length);
        
        assertEquals("workflow", model.program.name);
        assertEquals("workflow", model.program.beginAnnotation.name);
        assertEquals(0, model.program.inPorts.length);
        assertEquals(0, model.program.outPorts.length);
        assertEquals(0, model.program.channels.length);
        
        assertEquals(0, model.program.programs.length);
        assertEquals(1, model.program.functions.length);
        assertEquals("workflow.function", model.program.functions[0].name);
        assertEquals("function", model.program.functions[0].beginAnnotation.name);
    }

    public void testExtract_FunctionWithReturnInWorkflow() throws Exception {
        
        String source = 
                "# @begin workflow"    + EOL +
                "#   @begin function"  + EOL +
                "#   @return result"   + EOL +
                "#   @end function"    + EOL +
                "# @end workflow"      + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor.reader(reader)
                                                .extract()
                                                .getAnnotations();

        Model model = modeler.annotations(annotations)
                             .configure("workflow", "workflow")
                             .model()
                             .getModel();

        assertEquals(0, model.functions.length);
        
        assertEquals("workflow", model.program.name);
        assertEquals("workflow", model.program.beginAnnotation.name);
        assertEquals(0, model.program.inPorts.length);
        assertEquals(0, model.program.outPorts.length);
        assertEquals(0, model.program.channels.length);
        
        assertEquals(0, model.program.programs.length);
        assertEquals(1, model.program.functions.length);
        assertEquals("workflow.function", model.program.functions[0].name);
        assertEquals("function", model.program.functions[0].beginAnnotation.name);
    }
}