package org.yesworkflow.model;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yesworkflow.Language;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.YesWorkflowTestCase;

public class TestDefaultModer_ExtractTopPorts extends YesWorkflowTestCase {
    
    public void testDefaultModeler_ExtractTopPorts() throws Exception {
        
        BufferedReader reader = new BufferedReader(new StringReader(
                "    %  @begin script           " + EOL +
                "    %  @in x @as horiz         " + EOL +
                "    %  @in y @as vert          " + EOL +
                "    %  @out d @as dist         " + EOL +
                "                               " + EOL +
                "    some Matlab code           " + EOL +
                "                               " + EOL +
                "    % @end script              " + EOL
        ));

        Map<String,Object> config = new HashMap<String,Object>();
        config.put("language", Language.MATLAB);
        List<Annotation> annotations = new DefaultExtractor()
                                           .configure(config)
                                           .reader(reader)
                                           .extract()
                                           .getAnnotations();

        Workflow workflow = new DefaultModeler()
                                .annotations(annotations)
                                .model()
                                .getWorkflow();

        Port[] inPorts = workflow.inPorts;
        Port[] outPorts = workflow.outPorts;
        
        assertEquals(2, inPorts.length);
        assertEquals(1, outPorts.length);
        assertEquals("horiz", inPorts[0].flowAnnotation.binding());
        assertEquals("vert", inPorts[1].flowAnnotation.binding());
        assertEquals("dist", outPorts[0].flowAnnotation.binding());
    }
}