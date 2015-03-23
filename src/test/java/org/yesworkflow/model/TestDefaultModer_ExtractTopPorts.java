package org.yesworkflow.model;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.yesworkflow.LanguageModel;
import org.yesworkflow.LanguageModel.Language;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.model.Program;
import org.yesworkflow.YesWorkflowTestCase;

public class TestDefaultModer_ExtractTopPorts extends YesWorkflowTestCase {
    
    public void testDefaultModeler_ExtractTopPorts() throws Exception {
        
        BufferedReader reader = new BufferedReader(new StringReader(
                "% @begin script"       + EOL +
                "% @in x @as horiz"     + EOL +
                "% @in y @as vert"      + EOL +
                "% @out d @as dist"     + EOL +
                "  some Matlab code"    + EOL +
                "% @end script"         + EOL
        ));
        
        LanguageModel lm = new LanguageModel(Language.MATLAB);
        List<Annotation> annotations = new DefaultExtractor()
                                           .languageModel(lm)
                                           .source(reader)
                                           .extract()
                                           .getAnnotations();

        Program program = new DefaultModeler()
                              .annotations(annotations)
                              .model()
                              .getModel();

        Port[] inPorts = program.inPorts;
        Port[] outPorts = program.outPorts;
        
        assertEquals(2, inPorts.length);
        assertEquals(1, outPorts.length);
        assertEquals("horiz", inPorts[0].flowAnnotation.binding());
        assertEquals("vert", inPorts[1].flowAnnotation.binding());
        assertEquals("dist", outPorts[0].flowAnnotation.binding());
    }
}