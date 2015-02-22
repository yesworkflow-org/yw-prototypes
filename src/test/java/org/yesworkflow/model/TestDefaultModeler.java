package org.yesworkflow.model;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.exceptions.YWMarkupException;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.model.Channel;
import org.yesworkflow.model.Program;
import org.yesworkflow.model.Workflow;
import org.yesworkflow.util.YesWorkflowTestCase;

public class TestDefaultModeler extends YesWorkflowTestCase {

	Extractor extractor = null;
    Modeler modeler = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new DefaultExtractor(super.stdoutStream, super.stderrStream);
        modeler = new DefaultModeler(super.stdoutStream, super.stderrStream);
    }
    
    public void testExtract_GetModel_OneProgram() throws Exception {
        
        String source = 
                "# @begin script"	+ EOL +
                "  some code"		+ EOL +
                "# @end script"		+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .commentDelimiter("#")
        		.source(reader)
                .extract()
                .getAnnotations();
        
        Program program = modeler.annotations(annotations)
                                 .model()
                                 .getModel();
        
        assertFalse(program instanceof Workflow);
        assertEquals("script", program.beginAnnotation.name);
        assertEquals("script", program.endAnnotation.name);
        assertEquals(0, program.inPorts.length);
        assertEquals(0, program.outPorts.length);
    }

    public void testExtract_GetModel_WorkflowWithOneProgram() throws Exception {
        
        String source = 
                "# @begin script"		+ EOL +
                "#   @begin program"	+ EOL +
                "#   @end program"		+ EOL +
                "# @end script"			+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .commentDelimiter("#")
        		.source(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = (Workflow)modeler.annotations(annotations)
                                             .model()
                                             .getModel();

        assertEquals("script", workflow.beginAnnotation.name);
        assertEquals("script", workflow.endAnnotation.name);
        assertEquals(0, workflow.inPorts.length);
        assertEquals(0, workflow.outPorts.length);
        assertEquals(1, workflow.programs.length);
        assertEquals(0, workflow.channels.length);
        
        Program program = workflow.programs[0];
        assertFalse(program instanceof Workflow);
        assertEquals("program", program.beginAnnotation.name);
    }

    
   public void testExtract_GetModel_WorkflowWithOneProgram_MissingFinalEnd() throws Exception {
        
        String source = 
                "# @begin script"       + EOL +
                "#   @begin program"    + EOL +
                "#   @end program"      + EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .commentDelimiter("#")
        		.source(reader)
        		.extract()
                .getAnnotations();

        Exception caughtException = null;
        try {
            modeler.annotations(annotations)
                   .model()
                   .getModel();
        } catch (YWMarkupException e) {
            caughtException = e;
        }

        assertNotNull(caughtException);
        assertEquals("ERROR: No @end comment paired with '@begin script'" + EOL, caughtException.getMessage());
    }
   
   public void testExtract_GetModel_WorkflowWithOneProgram_MissingBothEnds() throws Exception {
       
       String source = 
               "# @begin script"       + EOL +
               "#   @begin program"    + EOL;

       BufferedReader reader = new BufferedReader(new StringReader(source));
       
       List<Annotation> annotations = extractor
                .commentDelimiter("#")
       			.source(reader)
       		    .extract()
                .getAnnotations();

       
       Exception caughtException = null;
       try {
       
           modeler.annotations(annotations)
                  .model()
                  .getModel();
       
       } catch (YWMarkupException e) {
           caughtException = e;
       }

       assertNotNull(caughtException);
       assertEquals(
               "ERROR: No @end comment paired with '@begin program'"  + EOL +
               "ERROR: No @end comment paired with '@begin script'"   + EOL, 
               caughtException.getMessage()
       );
   }   
   
    
    public void testExtract_GetModel_WorkflowWithTwoPrograms() throws Exception {
        
        String source = 
                "# @begin script"		+ EOL +
                "#   @begin program0"	+ EOL +
                "#   @end program0"		+ EOL +
                "#   @begin program1"	+ EOL +
                "#   @end program1"		+ EOL +
                "# @end script"			+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .commentDelimiter("#")
        		.source(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = (Workflow)modeler.annotations(annotations)
                                             .model()
                                             .getModel();
        
        assertEquals("script", workflow.beginAnnotation.name);
        assertEquals("script", workflow.endAnnotation.name);
        assertEquals(0, workflow.inPorts.length);
        assertEquals(0, workflow.outPorts.length);
        assertEquals(2, workflow.programs.length);
        assertEquals(0, workflow.channels.length);

        Program program0 = workflow.programs[0];
        assertFalse(program0 instanceof Workflow);
        assertEquals("program0", program0.beginAnnotation.name);        
        assertEquals("program0", program0.endAnnotation.name);
        assertEquals(0, program0.inPorts.length);
        assertEquals(0, program0.outPorts.length);

        Program program1 = workflow.programs[1];
        assertFalse(program1 instanceof Workflow);
        assertEquals("program1", program1.beginAnnotation.name);        
        assertEquals("program1", program1.endAnnotation.name);
        assertEquals(0, program1.inPorts.length);
        assertEquals(0, program1.outPorts.length);
    }

    public void testExtract_GetModel_WorkflowWithSubworkflow() throws Exception {
        
        String source = 
                "# @begin workflow"			+ EOL +
                "#   @begin subworkflow"	+ EOL +
                "#     @begin program"		+ EOL +
                "#     @end program"		+ EOL +
                "#   @end subworkflow"		+ EOL +
                "# @end workflow"			+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .commentDelimiter("#")
        		.source(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = (Workflow)modeler.annotations(annotations)
                                             .model()
                                             .getModel();

        
        assertEquals("workflow", workflow.beginAnnotation.name);
        assertEquals("workflow", workflow.endAnnotation.name);
        assertEquals(0, workflow.inPorts.length);
        assertEquals(0, workflow.outPorts.length);        
        assertEquals(1, workflow.programs.length);
        assertEquals(0, workflow.channels.length);
        
        Workflow subworkflow = (Workflow)workflow.programs[0];
        assertEquals("subworkflow", subworkflow.beginAnnotation.name);
        assertEquals("subworkflow", subworkflow.endAnnotation.name);
        assertEquals(0, subworkflow.inPorts.length);
        assertEquals(0, subworkflow.outPorts.length);        
        assertEquals(1, subworkflow.programs.length);
        assertEquals(0, subworkflow.channels.length);
        
        Program program = subworkflow.programs[0];
        assertFalse(program instanceof Workflow);
        assertEquals("program", program.beginAnnotation.name);
        assertEquals("program", program.endAnnotation.name);
        assertEquals(0, program.inPorts.length);
        assertEquals(0, program.outPorts.length);     
    }
    
    public void testExtract_GetModel_OneProgramInAndOut() throws Exception {
        
        String source = 
                "# @begin script"	+ EOL +
                "# @in x"			+ EOL +
                "# @in y"			+ EOL +
                "# @out z"			+ EOL +
                "  some code"		+ EOL +
                "# @end script"		+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .commentDelimiter("#")
        		.source(reader)
                .extract()
                .getAnnotations();

        Program program = modeler.annotations(annotations)
                                 .model()
                                 .getModel();
        
        assertFalse(program instanceof Workflow);
        assertEquals("script", program.beginAnnotation.name);
        assertEquals("script", program.endAnnotation.name);
        assertEquals(2, program.inPorts.length);
        assertEquals(1, program.outPorts.length);
    }
    
    public void testExtract_GetModel_TwoProgramsWithOneChannel() throws Exception {
        
        String source = 
                "# @begin script"		+ EOL +
                "#   @begin program0"	+ EOL +
                "#	 @out channel"		+ EOL +
                "#   @end program0"		+ EOL +                
                "#   @begin program1"	+ EOL +
                "#	 @in channel"		+ EOL +
                "#   @end program1"		+ EOL +
                "# @end script"			+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .commentDelimiter("#")
        		.source(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = (Workflow)modeler.annotations(annotations)
                                             .model()
                                             .getModel();
        
        assertEquals("script", workflow.beginAnnotation.name);
        assertEquals("script", workflow.endAnnotation.name);
        assertEquals(0, workflow.inPorts.length);
        assertEquals(0, workflow.outPorts.length);
        assertEquals(2, workflow.programs.length);
        assertEquals(1, workflow.channels.length);

        Program program0 = workflow.programs[0];
        assertFalse(program0 instanceof Workflow);
        assertEquals("program0", program0.beginAnnotation.name);
        assertEquals("program0", program0.endAnnotation.name);
        assertEquals(0, program0.inPorts.length);
        assertEquals(1, program0.outPorts.length);

        Program program1 = workflow.programs[1];
        assertFalse(program1 instanceof Workflow);
        assertEquals("program1", program1.beginAnnotation.name);
        assertEquals("program1", program1.endAnnotation.name);
        assertEquals(1, program1.inPorts.length);
        assertEquals(0, program1.outPorts.length);
        
        Channel channel = workflow.channels[0];
        assertEquals("program0", channel.sourceProgram.beginAnnotation.name);
        assertEquals("channel", channel.sourcePort.flowAnnotation.name);
        assertEquals("program1", channel.sinkProgram.beginAnnotation.name);
        assertEquals("channel", channel.sinkPort.flowAnnotation.name);
    }
    
    public void testExtract_GetModel_ThreeProgramsMultipleChannels() throws Exception {
        
        String source = 
                "# @begin script"		+ EOL +
                "#"						+ EOL +
                "#   @begin program0"	+ EOL +
                "#	 @out channel0"		+ EOL +
                "#	 @out channel1"		+ EOL +
                "#   @end program0"		+ EOL +                
                "#"						+ EOL +
                "#   @begin program1"	+ EOL +
                "#	 @in channel0"		+ EOL +
                "#   @end program1"		+ EOL +
                "#"						+ EOL +
                "#   @begin program2"	+ EOL +
                "#	 @in channel1"		+ EOL +
                "#   @end program2"		+ EOL +
                "#"						+ EOL +
                "# @end script"			+ EOL;

        BufferedReader reader = new BufferedReader(new StringReader(source));
        
        List<Annotation> annotations = extractor
                .commentDelimiter("#")
        		.source(reader)
                .extract()
                .getAnnotations();

        Workflow workflow = (Workflow)modeler.annotations(annotations)
                                             .model()
                                             .getModel();
        
        assertEquals("script", workflow.beginAnnotation.name);
        assertEquals("script", workflow.endAnnotation.name);
        assertEquals(0, workflow.inPorts.length);
        assertEquals(0, workflow.outPorts.length);
        assertEquals(3, workflow.programs.length);
        assertEquals(2, workflow.channels.length);

        Program program0 = workflow.programs[0];
        assertFalse(program0 instanceof Workflow);
        assertEquals("program0", program0.beginAnnotation.name);
        assertEquals("program0", program0.endAnnotation.name);
        assertEquals(0, program0.inPorts.length);
        assertEquals(2, program0.outPorts.length);

        Program program1 = workflow.programs[1];
        assertFalse(program1 instanceof Workflow);
        assertEquals("program1", program1.beginAnnotation.name);
        assertEquals("program1", program1.endAnnotation.name);
        assertEquals(1, program1.inPorts.length);
        assertEquals(0, program1.outPorts.length);

        Program program2 = workflow.programs[2];
        assertFalse(program2 instanceof Workflow);
        assertEquals("program2", program2.beginAnnotation.name);
        assertEquals("program2", program2.endAnnotation.name);
        assertEquals(1, program1.inPorts.length);
        assertEquals(0, program1.outPorts.length);
        
        Channel channel0 = workflow.channels[0];
        assertEquals("program0", channel0.sourceProgram.beginAnnotation.name);
        assertEquals("channel0", channel0.sourcePort.flowAnnotation.name);
        assertEquals("program1", channel0.sinkProgram.beginAnnotation.name);
        assertEquals("channel0", channel0.sinkPort.flowAnnotation.name);

        Channel channel1 = workflow.channels[1];
        assertEquals("program0", channel1.sourceProgram.beginAnnotation.name);
        assertEquals("channel1", channel1.sourcePort.flowAnnotation.name);
        assertEquals("program2", channel1.sinkProgram.beginAnnotation.name);
        assertEquals("channel1", channel1.sinkPort.flowAnnotation.name);
    }
   
   
   public void testExtract_GetCommentLines_OneComment_Hash() throws Exception {
       
       String source = "# @begin main" + EOL;
       
       BufferedReader reader = new BufferedReader(new StringReader(source));

       List<Annotation> annotations = extractor
               	.commentDelimiter("#")
               	.source(reader)
               	.extract()
               	.getAnnotations();
       
       Exception caughtException = null;
       try {
           
           modeler.annotations(annotations)
                  .model()
                  .getModel();
           
       } catch (YWMarkupException e) {
           caughtException = e;
       }
       
       assertNotNull(caughtException);
       assertEquals("ERROR: No @end comment paired with '@begin main'" + EOL, caughtException.getMessage());
       
       List<String> commentLines = extractor.getLines();
       assertEquals(1,commentLines.size());
       assertEquals("@begin main", commentLines.get(0));
       assertEquals("", super.stdoutBuffer.toString());
       assertEquals("", super.stderrBuffer.toString());
   }
   
   public void testExtract_GetComments_OneBeginComment() throws Exception {
       
       String source = "# @begin main" + EOL;
       
       BufferedReader reader = new BufferedReader(new StringReader(source));
       
       List<Annotation> annotations = extractor
               	.commentDelimiter("#")
               	.source(reader)                                         
               	.extract()
               	.getAnnotations();
       
       Exception caughtException = null;
       try {
       
           modeler.annotations(annotations)
                  .model()
                  .getModel();

       } catch (YWMarkupException e) {
           caughtException = e;
       }
       
       assertNotNull(caughtException);
       assertEquals("ERROR: No @end comment paired with '@begin main'" + EOL, caughtException.getMessage());
       
//       assertEquals(1, comments.size());
//       BeginComment comment = (BeginComment) comments.get(0);
//       assertEquals("main", comment.programName);
//       assertNull(comment.description);
   }
}