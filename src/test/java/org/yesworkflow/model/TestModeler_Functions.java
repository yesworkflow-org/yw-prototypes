package org.yesworkflow.model;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.YesWorkflowTestCase;

public class TestModeler_Functions extends YesWorkflowTestCase {

	Extractor extractor = null;
    Modeler modeler = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new DefaultExtractor(super.stdoutStream, super.stderrStream);
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
        
        assertEquals("script", model.workflow.beginAnnotation.name);
        assertEquals("script", model.workflow.endAnnotation.name);
        assertEquals(0, model.workflow.inPorts.length);
        assertEquals(0, model.workflow.outPorts.length);
        assertEquals(0, model.workflow.channels.length);

        assertEquals(1, model.functions.length);
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

        assertEquals("workflow", model.workflow.beginAnnotation.name);
        assertEquals("workflow", model.workflow.endAnnotation.name);
        assertEquals(0, model.workflow.inPorts.length);
        assertEquals(0, model.workflow.outPorts.length);
        assertEquals(0, model.workflow.channels.length);
        
        assertEquals(1, model.workflow.programs.length);
        assertFalse(model.workflow.programs[0] instanceof Workflow);
        assertEquals("program1", model.workflow.programs[0].beginAnnotation.name);
        
        assertEquals(1, model.functions.length);
        assertEquals("function", model.functions[0].beginAnnotation.name);
        assertEquals(1, model.functions[0].programs.length);
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

        assertEquals("workflow", model.workflow.beginAnnotation.name);
        assertEquals("workflow", model.workflow.endAnnotation.name);
        assertEquals(0, model.workflow.inPorts.length);
        assertEquals(0, model.workflow.outPorts.length);
        assertEquals(0, model.workflow.channels.length);
        
        assertEquals(1, model.workflow.programs.length);
        assertFalse(model.workflow.programs[0] instanceof Workflow);
        assertEquals("program1", model.workflow.programs[0].beginAnnotation.name);
        
        assertEquals(1, model.functions.length);
        assertEquals("function", model.functions[0].beginAnnotation.name);
        assertEquals(2, model.functions[0].programs.length);
        assertEquals("program2", model.functions[0].programs[0].beginAnnotation.name);
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

        assertEquals("workflow", model.workflow.beginAnnotation.name);
        assertEquals("workflow", model.workflow.endAnnotation.name);
        assertEquals(0, model.workflow.inPorts.length);
        assertEquals(0, model.workflow.outPorts.length);
        assertEquals(0, model.workflow.channels.length);
        
        assertEquals(1, model.workflow.programs.length);
        assertFalse(model.workflow.programs[0] instanceof Workflow);
        assertEquals("program1", model.workflow.programs[0].beginAnnotation.name);

        assertEquals(2, model.functions.length);
        assertEquals("function1", model.functions[0].beginAnnotation.name);
        assertEquals(0, model.functions[0].programs.length);
        assertEquals(0, model.functions[0].channels.length);

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

        assertEquals("workflow", model.workflow.beginAnnotation.name);
        assertEquals("workflow", model.workflow.endAnnotation.name);
        assertEquals(0, model.workflow.inPorts.length);
        assertEquals(0, model.workflow.outPorts.length);
        assertEquals(0, model.workflow.channels.length);
        
        assertEquals(1, model.workflow.programs.length);
        assertFalse(model.workflow.programs[0] instanceof Workflow);
        assertEquals("program1", model.workflow.programs[0].beginAnnotation.name);

        assertEquals(2, model.functions.length);
        assertEquals("function1", model.functions[0].beginAnnotation.name);
        assertEquals(0, model.functions[0].programs.length);
        assertEquals(0, model.functions[0].channels.length);

        assertEquals("function2", model.functions[1].beginAnnotation.name);
        assertEquals(0, model.functions[1].programs.length);
        assertEquals(0, model.functions[1].channels.length);
    }

    public void testExtract_NoWorkflow_TwoTopFunctions() throws Exception {
        
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
                             .configure("workflow", "workflow")
                             .model()
                             .getModel();

        assertNull(model.workflow);
        
        assertEquals(2, model.functions.length);
        assertEquals("function1", model.functions[0].beginAnnotation.name);
        assertEquals(0, model.functions[0].programs.length);
        assertEquals(0, model.functions[0].channels.length);

        assertEquals("function2", model.functions[1].beginAnnotation.name);
        assertEquals(0, model.functions[1].programs.length);
        assertEquals(0, model.functions[1].channels.length);
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
        
        assertEquals("workflow", model.workflow.beginAnnotation.name);
        assertEquals(0, model.workflow.inPorts.length);
        assertEquals(0, model.workflow.outPorts.length);
        assertEquals(0, model.workflow.channels.length);
        assertEquals(1, ((Function)(model.workflow)).returnPorts.length);
        assertEquals("result", ((Function)(model.workflow)).returnPorts[0].flowAnnotation.name);
        
        assertEquals(0, model.workflow.programs.length);
        assertEquals(0, model.workflow.functions.length);
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
        
        assertEquals("workflow", model.workflow.beginAnnotation.name);
        assertEquals(0, model.workflow.inPorts.length);
        assertEquals(0, model.workflow.outPorts.length);
        assertEquals(0, model.workflow.channels.length);
        
        assertEquals(0, model.workflow.programs.length);
        assertEquals(1, model.workflow.functions.length);
        assertEquals("function", model.workflow.functions[0].beginAnnotation.name);
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
        
        assertEquals("workflow", model.workflow.beginAnnotation.name);
        assertEquals(0, model.workflow.inPorts.length);
        assertEquals(0, model.workflow.outPorts.length);
        assertEquals(0, model.workflow.channels.length);
        
        assertEquals(0, model.workflow.programs.length);
        assertEquals(1, model.workflow.functions.length);
        assertEquals("function", model.workflow.functions[0].beginAnnotation.name);
    }
}