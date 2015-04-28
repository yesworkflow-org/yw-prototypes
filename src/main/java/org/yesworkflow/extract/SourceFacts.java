package org.yesworkflow.extract;

import java.util.List;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.query.FactsBuilder;

public class SourceFacts {

    private final List<Annotation> annotations;
    private String factsString = null;
    
    private FactsBuilder sourcefileFacts  = new FactsBuilder("sourcefile", "source_id", "file_path");
    private FactsBuilder annotationFacts  = new FactsBuilder("annotation", "annotation_id", "annotation_tag", "annotation_value", "annotation_description");
    private FactsBuilder qualificationFacts = new FactsBuilder("qualification", "primary_annotation_id", "qualifying_annotation_id");
    private FactsBuilder annotationLocationFacts = new FactsBuilder ("location", "annotation_id", "source_id", "line_number");
    
    public SourceFacts(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public SourceFacts build() {        
        
        StringBuilder sb = new StringBuilder();
        sb.append(sourcefileFacts);

        factsString = sb.toString();
        
        return this;
    }

        
    public String toString() {
        return factsString;
    }
}
