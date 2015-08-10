package org.yesworkflow.recon;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.extract.DefaultExtractor;
import org.yesworkflow.extract.Extractor;
import org.yesworkflow.model.DefaultModeler;
import org.yesworkflow.model.Model;
import org.yesworkflow.model.Modeler;
import org.yesworkflow.YesWorkflowTestCase;

public class TestDefaultReconstructor extends YesWorkflowTestCase {

	Extractor extractor = null;
    Modeler modeler = null;
    Reconstructor reconstructor = null;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new DefaultExtractor(super.stdoutStream, super.stderrStream);
        modeler = new DefaultModeler(super.stdoutStream, super.stderrStream);
        reconstructor = new DefaultReconstructor(super.stdoutStream, super.stderrStream);
    }
   
    public void testRecon_SimulateDataCollection() throws FileNotFoundException, Exception {

        List<Annotation> annotations = extractor
               .configure("language", "python")
               .reader(new FileReader("src/main/resources/examples/simulate_data_collection/simulate_data_collection.py"))
               .extract()
               .getAnnotations();
        
        Model model = modeler.annotations(annotations)
                             .model()
                             .getModel();
        
        Run run = new Run(model, "src/main/resources/examples/simulate_data_collection");
        
        reconstructor.run(run)
                     .recon()
                     .getFacts();
    }
}
