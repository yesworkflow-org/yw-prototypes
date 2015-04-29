package org.yesworkflow.extract;

import java.util.List;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.annotations.Qualification;
import org.yesworkflow.query.FactsBuilder;

public class ExtractFacts {

    private final List<Annotation> annotations;
    private final List<String> sources;
    private String factsString = null;
    
    private FactsBuilder sourceFileFacts  = new FactsBuilder("extract_source", "source_id", "source_path");
    private FactsBuilder annotationFacts  = new FactsBuilder("annotation", "annotation_id", "source_id", "line_number", "annotation_tag", "annotation_value");
    private FactsBuilder descriptionFacts  = new FactsBuilder("annotation_description", "annotation_id", "annotation_description");
    private FactsBuilder qualificationFacts = new FactsBuilder("annotation_qualifies", "qualifying_annotation_id", "primary_annotation_id");
    
    public ExtractFacts(List<String> sources, List<Annotation> annotations) {
        this.sources = sources;
        this.annotations = annotations;
    }

    public ExtractFacts build() {        
                
        buildSourceFileFacts();
        buildAnnotationFacts();
        
        StringBuilder sb = new StringBuilder();
        sb.append(sourceFileFacts)
          .append(annotationFacts)
          .append(descriptionFacts)
          .append(qualificationFacts);
        
        factsString = sb.toString();
        
        return this;
    }

    public String toString() {
        return factsString;
    }
    
    private void buildSourceFileFacts() {
        int nextSourceId = 1;
        for (String source : sources) {
            sourceFileFacts.add(nextSourceId++, source);
        }
    }
    
    private void buildAnnotationFacts() {
        
        for (Annotation annotation : annotations) {   
            
            annotationFacts.add(
                    annotation.id, 
                    annotation.line.sourceId, 
                    annotation.line.lineNumber, 
                    annotation.tag, 
                    annotation.name
            );
            
            if (annotation.description() != null) {
                descriptionFacts.add(
                        annotation.id, 
                        annotation.description()
                );
            }
            
            if (annotation instanceof Qualification) {
                qualificationFacts.add(
                        annotation.id, 
                        ((Qualification)annotation).primaryAnnotation.id
                );
            }
        }
    }
}
