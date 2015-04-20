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
    
    public void testExtract_GetModel_OneProgram() throws Exception {
        
        String source = 
                "# @begin script"	    + EOL +
                "  some code"		    + EOL +
                "# @end script"         + EOL +
                ""                      + EOL +
                "# @begin function"     + EOL +
                "  some more code"      + EOL +
                "# @end function"       + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .reader(reader)
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

    public void testExtract_GetModel_WorkflowOneProgram_FunctionOneProgram() throws Exception {
        
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
        
        List<Annotation> annotations = extractor
                .reader(reader)
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

    public void testExtract_GetModel_WorkflowOneProgram_FunctionTwoPrograms() throws Exception {
        
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
        
        List<Annotation> annotations = extractor
                .reader(reader)
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

    public void testExtract_GetModel_WorkflowOneProgram_TwoFunctions_BothAfterWorkflow() throws Exception {
        
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
        
        List<Annotation> annotations = extractor
                .reader(reader)
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


    public void testExtract_GetModel_WorkflowOneProgram_TwoFunctions_BeforeAndAfterWorkflow() throws Exception {
        
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
        
        List<Annotation> annotations = extractor
                .reader(reader)
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

    public void testExtract_GetModel_NoWorkflow_TwoFunctions() throws Exception {
        
        String source = 
                "# @begin function1"    + EOL +
                "# @end function1"      + EOL +
                "#"                     + EOL +
                "# @begin function2"    + EOL +
                "# @end function2"      + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .reader(reader)
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
    
 //    public void testExtract_GetModel_WorkflowWithSubworkflow() throws Exception {
//        
//        String source = 
//                "# @begin workflow"			+ EOL +
//                "#   @begin subworkflow"	+ EOL +
//                "#     @begin program"		+ EOL +
//                "#     @end program"		+ EOL +
//                "#   @end subworkflow"		+ EOL +
//                "# @end workflow"			+ EOL;
//
//        BufferedReader reader = new BufferedReader(new StringReader(source));
//        
//        List<Annotation> annotations = extractor
//                .reader(reader)
//                .extract()
//                .getAnnotations();
//
//        Workflow workflow = (Workflow)modeler.annotations(annotations)
//                                             .model()
//                                             .getWorkflow();
//
//        
//        assertEquals("workflow", workflow.beginAnnotation.name);
//        assertEquals("workflow", workflow.endAnnotation.name);
//        assertEquals(0, workflow.inPorts.length);
//        assertEquals(0, workflow.outPorts.length);        
//        assertEquals(1, workflow.programs.length);
//        assertEquals(0, workflow.channels.length);
//        
//        Workflow subworkflow = (Workflow)workflow.programs[0];
//        assertEquals("subworkflow", subworkflow.beginAnnotation.name);
//        assertEquals("subworkflow", subworkflow.endAnnotation.name);
//        assertEquals(0, subworkflow.inPorts.length);
//        assertEquals(0, subworkflow.outPorts.length);        
//        assertEquals(1, subworkflow.programs.length);
//        assertEquals(0, subworkflow.channels.length);
//        
//        Program program = subworkflow.programs[0];
//        assertFalse(program instanceof Workflow);
//        assertEquals("program", program.beginAnnotation.name);
//        assertEquals("program", program.endAnnotation.name);
//        assertEquals(0, program.inPorts.length);
//        assertEquals(0, program.outPorts.length);     
//    }
//    
//    public void testExtract_GetModel_OneProgramTwoInsOneOut() throws Exception {
//        
//        String source = 
//                "# @begin script"	+ EOL +
//                "# @in x"			+ EOL +
//                "# @in y"			+ EOL +
//                "# @out z"			+ EOL +
//                "  some code"		+ EOL +
//                "# @end script"		+ EOL;
//
//        BufferedReader reader = new BufferedReader(new StringReader(source));
//        
//        List<Annotation> annotations = extractor
//                .reader(reader)
//                .extract()
//                .getAnnotations();
//
//        Program program = modeler.annotations(annotations)
//                                 .model()
//                                 .getWorkflow();
//        
//        assertEquals("script", program.beginAnnotation.name);
//        assertEquals("script", program.endAnnotation.name);
//        assertEquals(2, program.inPorts.length);
//        assertEquals(1, program.outPorts.length);
//        assertEquals(In.class, program.inPorts[0].flowAnnotation.getClass());
//        assertEquals(In.class, program.inPorts[1].flowAnnotation.getClass());
//        assertEquals(Out.class, program.outPorts[0].flowAnnotation.getClass());
//    }
//    
//    public void testExtract_GetModel_OneProgramTwoParamsOneOut() throws Exception {
//        
//        String source = 
//                "# @begin script"   + EOL +
//                "# @param x"        + EOL +
//                "# @param y"        + EOL +
//                "# @out z"          + EOL +
//                "  some code"       + EOL +
//                "# @end script"     + EOL;
//
//        BufferedReader reader = new BufferedReader(new StringReader(source));
//        
//        List<Annotation> annotations = extractor
//                .reader(reader)
//                .extract()
//                .getAnnotations();
//
//        Program program = modeler.annotations(annotations)
//                                 .model()
//                                 .getWorkflow();
//        
//        assertEquals("script", program.beginAnnotation.name);
//        assertEquals("script", program.endAnnotation.name);
//        assertEquals(2, program.inPorts.length);
//        assertEquals(1, program.outPorts.length);
//        assertEquals(Param.class, program.inPorts[0].flowAnnotation.getClass());
//        assertEquals("x", program.inPorts[0].flowAnnotation.binding());
//        assertEquals(Param.class, program.inPorts[1].flowAnnotation.getClass());
//        assertEquals("y", program.inPorts[1].flowAnnotation.binding());
//        assertEquals(Out.class, program.outPorts[0].flowAnnotation.getClass());
//        assertEquals("z", program.outPorts[0].flowAnnotation.binding());
//    }
//
//    public void testExtract_GetModel_OneProgramInAndOut_TwoCommentLines() throws Exception {
//        
//        String source = 
//                "# @begin script @in x @in y @out z"	+ EOL +
//                "some code                         "	+ EOL +
//                "# @end script                     "	+ EOL;
//
//        BufferedReader reader = new BufferedReader(new StringReader(source));
//        
//        List<Annotation> annotations = extractor
//                .reader(reader)
//                .extract()
//                .getAnnotations();
//
//        Program program = modeler.annotations(annotations)
//                                 .model()
//                                 .getWorkflow();
//        
//        assertEquals("script", program.beginAnnotation.name);
//        assertEquals("script", program.endAnnotation.name);
//        assertEquals(2, program.inPorts.length);
//        assertEquals(1, program.outPorts.length);
//    }
//
//    
//    public void testExtract_GetModel_TwoProgramsWithOneChannel_OutAndIn() throws Exception {
//        
//        String source = 
//                "# @begin script"		+ EOL +
//                "#   @begin program0"	+ EOL +
//                "#	 @out channel"		+ EOL +
//                "#   @end program0"		+ EOL +                
//                "#   @begin program1"	+ EOL +
//                "#	 @in channel"		+ EOL +
//                "#   @end program1"		+ EOL +
//                "# @end script"			+ EOL;
//
//        BufferedReader reader = new BufferedReader(new StringReader(source));
//        
//        List<Annotation> annotations = extractor
//                .reader(reader)
//                .extract()
//                .getAnnotations();
//
//        Workflow workflow = (Workflow)modeler.annotations(annotations)
//                                             .model()
//                                             .getWorkflow();
//        
//        assertEquals("script", workflow.beginAnnotation.name);
//        assertEquals("script", workflow.endAnnotation.name);
//        assertEquals(0, workflow.inPorts.length);
//        assertEquals(0, workflow.outPorts.length);
//        assertEquals(2, workflow.programs.length);
//        assertEquals(1, workflow.channels.length);
//
//        Program program0 = workflow.programs[0];
//        assertFalse(program0 instanceof Workflow);
//        assertEquals("program0", program0.beginAnnotation.name);
//        assertEquals("program0", program0.endAnnotation.name);
//        assertEquals(0, program0.inPorts.length);
//        assertEquals(1, program0.outPorts.length);
//
//        Program program1 = workflow.programs[1];
//        assertFalse(program1 instanceof Workflow);
//        assertEquals("program1", program1.beginAnnotation.name);
//        assertEquals("program1", program1.endAnnotation.name);
//        assertEquals(1, program1.inPorts.length);
//        assertEquals(0, program1.outPorts.length);
//        
//        Channel channel = workflow.channels[0];
//        assertEquals("program0", channel.sourceProgram.beginAnnotation.name);
//        assertEquals("channel", channel.sourcePort.flowAnnotation.name);
//        assertEquals(Out.class, channel.sourcePort.flowAnnotation.getClass());
//
//        assertEquals("program1", channel.sinkProgram.beginAnnotation.name);
//        assertEquals("channel", channel.sinkPort.flowAnnotation.name);
//        assertEquals(In.class, channel.sinkPort.flowAnnotation.getClass());
//
//    }
//
//    public void testExtract_GetModel_TwoProgramsWithOneChannel_OutAndParam() throws Exception {
//        
//        String source = 
//                "# @begin script"       + EOL +
//                "#   @begin program0"   + EOL +
//                "#   @out channel"      + EOL +
//                "#   @end program0"     + EOL +                
//                "#   @begin program1"   + EOL +
//                "#   @param channel"    + EOL +
//                "#   @end program1"     + EOL +
//                "# @end script"         + EOL;
//
//        BufferedReader reader = new BufferedReader(new StringReader(source));
//        
//        List<Annotation> annotations = extractor
//                .reader(reader)
//                .extract()
//                .getAnnotations();
//
//        Workflow workflow = (Workflow)modeler.annotations(annotations)
//                                             .model()
//                                             .getWorkflow();
//        
//        assertEquals("script", workflow.beginAnnotation.name);
//        assertEquals("script", workflow.endAnnotation.name);
//        assertEquals(0, workflow.inPorts.length);
//        assertEquals(0, workflow.outPorts.length);
//        assertEquals(2, workflow.programs.length);
//        assertEquals(1, workflow.channels.length);
//
//        Program program0 = workflow.programs[0];
//        assertFalse(program0 instanceof Workflow);
//        assertEquals("program0", program0.beginAnnotation.name);
//        assertEquals("program0", program0.endAnnotation.name);
//        assertEquals(0, program0.inPorts.length);
//        assertEquals(1, program0.outPorts.length);
//
//        Program program1 = workflow.programs[1];
//        assertFalse(program1 instanceof Workflow);
//        assertEquals("program1", program1.beginAnnotation.name);
//        assertEquals("program1", program1.endAnnotation.name);
//        assertEquals(1, program1.inPorts.length);
//        assertEquals(0, program1.outPorts.length);
//        
//        Channel channel = workflow.channels[0];
//        assertEquals("program0", channel.sourceProgram.beginAnnotation.name);
//        assertEquals("channel", channel.sourcePort.flowAnnotation.name);
//        assertEquals(Out.class, channel.sourcePort.flowAnnotation.getClass());
//
//        assertEquals("program1", channel.sinkProgram.beginAnnotation.name);
//        assertEquals("channel", channel.sinkPort.flowAnnotation.name);
//        assertEquals(Param.class, channel.sinkPort.flowAnnotation.getClass());
//
//    }
//
//    
//   public void testExtract_GetModel_TwoProgramsWithOneChannel_CombinedCommentLines() throws Exception {
//        
//        String source = 
//                "# @begin script                   "	+ EOL +
//                "                                  "	+ EOL +
//                "#   @begin program0 @out channel  "	+ EOL +
//                "    some code in program0         "	+ EOL +
//                "#   @end program0                 "    + EOL +
//                "                                  "	+ EOL +
//                "    some code in script           "    + EOL +
//                "                                  "	+ EOL +
//                "#   @begin program1 @in channel   "	+ EOL +
//                "    some code in program1         "	+ EOL +
//                "#   @end program1                 "	+ EOL +
//                "                                  "	+ EOL +
//                "# @end script                     "	+ EOL;
//
//        BufferedReader reader = new BufferedReader(new StringReader(source));
//        
//        List<Annotation> annotations = extractor
//                .reader(reader)
//                .extract()
//                .getAnnotations();
//
//        Workflow workflow = (Workflow)modeler.annotations(annotations)
//                                             .model()
//                                             .getWorkflow();
//        
//        assertEquals("script", workflow.beginAnnotation.name);
//        assertEquals("script", workflow.endAnnotation.name);
//        assertEquals(0, workflow.inPorts.length);
//        assertEquals(0, workflow.outPorts.length);
//        assertEquals(2, workflow.programs.length);
//        assertEquals(1, workflow.channels.length);
//
//        Program program0 = workflow.programs[0];
//        assertFalse(program0 instanceof Workflow);
//        assertEquals("program0", program0.beginAnnotation.name);
//        assertEquals("program0", program0.endAnnotation.name);
//        assertEquals(0, program0.inPorts.length);
//        assertEquals(1, program0.outPorts.length);
//
//        Program program1 = workflow.programs[1];
//        assertFalse(program1 instanceof Workflow);
//        assertEquals("program1", program1.beginAnnotation.name);
//        assertEquals("program1", program1.endAnnotation.name);
//        assertEquals(1, program1.inPorts.length);
//        assertEquals(0, program1.outPorts.length);
//        
//        Channel channel = workflow.channels[0];
//        assertEquals("program0", channel.sourceProgram.beginAnnotation.name);
//        assertEquals("channel", channel.sourcePort.flowAnnotation.name);
//        assertEquals("program1", channel.sinkProgram.beginAnnotation.name);
//        assertEquals("channel", channel.sinkPort.flowAnnotation.name);
//    }
//    
//    
//    public void testExtract_GetModel_ThreeProgramsMultipleChannels() throws Exception {
//        
//        String source = 
//                "# @begin script"		+ EOL +
//                "#"						+ EOL +
//                "#   @begin program0"	+ EOL +
//                "#	 @out channel0"		+ EOL +
//                "#	 @out channel1"		+ EOL +
//                "#   @end program0"		+ EOL +                
//                "#"						+ EOL +
//                "#   @begin program1"	+ EOL +
//                "#	 @in channel0"		+ EOL +
//                "#   @end program1"		+ EOL +
//                "#"						+ EOL +
//                "#   @begin program2"	+ EOL +
//                "#	 @param channel1"	+ EOL +
//                "#   @end program2"		+ EOL +
//                "#"						+ EOL +
//                "# @end script"			+ EOL;
//
//        BufferedReader reader = new BufferedReader(new StringReader(source));
//        
//        List<Annotation> annotations = extractor
//                .reader(reader)
//                .extract()
//                .getAnnotations();
//
//        Workflow workflow = (Workflow)modeler.annotations(annotations)
//                                             .model()
//                                             .getWorkflow();
//        
//        assertEquals("script", workflow.beginAnnotation.name);
//        assertEquals("script", workflow.endAnnotation.name);
//        assertEquals(0, workflow.inPorts.length);
//        assertEquals(0, workflow.outPorts.length);
//        assertEquals(3, workflow.programs.length);
//        assertEquals(2, workflow.channels.length);
//
//        Program program0 = workflow.programs[0];
//        assertFalse(program0 instanceof Workflow);
//        assertEquals("program0", program0.beginAnnotation.name);
//        assertEquals("program0", program0.endAnnotation.name);
//        assertEquals(0, program0.inPorts.length);
//        assertEquals(2, program0.outPorts.length);
//
//        Program program1 = workflow.programs[1];
//        assertFalse(program1 instanceof Workflow);
//        assertEquals("program1", program1.beginAnnotation.name);
//        assertEquals("program1", program1.endAnnotation.name);
//        assertEquals(1, program1.inPorts.length);
//        assertEquals(0, program1.outPorts.length);
//
//        Program program2 = workflow.programs[2];
//        assertFalse(program2 instanceof Workflow);
//        assertEquals("program2", program2.beginAnnotation.name);
//        assertEquals("program2", program2.endAnnotation.name);
//        assertEquals(1, program1.inPorts.length);
//        assertEquals(0, program1.outPorts.length);
//        
//        Channel channel0 = workflow.channels[0];
//        assertEquals("program0", channel0.sourceProgram.beginAnnotation.name);
//        assertEquals("channel0", channel0.sourcePort.flowAnnotation.name);
//        assertEquals("program1", channel0.sinkProgram.beginAnnotation.name);
//        assertEquals("channel0", channel0.sinkPort.flowAnnotation.name);
//        assertEquals(In.class, channel0.sinkPort.flowAnnotation.getClass());
//
//        Channel channel1 = workflow.channels[1];
//        assertEquals("program0", channel1.sourceProgram.beginAnnotation.name);
//        assertEquals("channel1", channel1.sourcePort.flowAnnotation.name);
//        assertEquals("program2", channel1.sinkProgram.beginAnnotation.name);
//        assertEquals("channel1", channel1.sinkPort.flowAnnotation.name);
//        assertEquals(Param.class, channel1.sinkPort.flowAnnotation.getClass());
//    }
//   
//   
//   public void testExtract_GetCommentLines_OneComment_Hash() throws Exception {
//       
//       String source = "# @begin main" + EOL;
//       
//       BufferedReader reader = new BufferedReader(new StringReader(source));
//
//       List<Annotation> annotations = extractor
//                .reader(reader)
//               	.extract()
//               	.getAnnotations();
//       
//       Exception caughtException = null;
//       try {
//           
//           modeler.annotations(annotations)
//                  .model()
//                  .getWorkflow();
//           
//       } catch (YWMarkupException e) {
//           caughtException = e;
//       }
//       
//       assertNotNull(caughtException);
//       assertEquals("ERROR: No @end comment paired with '@begin main'" + EOL, caughtException.getMessage());
//       
//       List<String> commentLines = extractor.getLines();
//       assertEquals(1,commentLines.size());
//       assertEquals("@begin main", commentLines.get(0));
//       assertEquals("", super.stdoutBuffer.toString());
//       assertEquals("", super.stderrBuffer.toString());
//   }
//   
//   public void testExtract_GetComments_OneBeginComment() throws Exception {
//       
//       String source = "# @begin main" + EOL;
//       
//       BufferedReader reader = new BufferedReader(new StringReader(source));
//       
//       List<Annotation> annotations = extractor
//                .reader(reader)
//               	.extract()
//               	.getAnnotations();
//       
//       Exception caughtException = null;
//       try {
//       
//           modeler.annotations(annotations)
//                  .model()
//                  .getWorkflow();
//
//       } catch (YWMarkupException e) {
//           caughtException = e;
//       }
//       
//       assertNotNull(caughtException);
//       assertEquals("ERROR: No @end comment paired with '@begin main'" + EOL, caughtException.getMessage());
//   }
}