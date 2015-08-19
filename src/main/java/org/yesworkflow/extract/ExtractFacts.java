package org.yesworkflow.extract;

import static org.yesworkflow.db.Column.ID;
import static org.yesworkflow.db.Column.PATH;

import java.util.List;

import org.jooq.Record;
import org.jooq.Result;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.annotations.Qualification;
import org.yesworkflow.db.Table;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.query.FactsBuilder;
import org.yesworkflow.query.QueryEngineModel;

public class ExtractFacts {

    private YesWorkflowDB ywdb;
    private final List<Annotation> annotations;
    private String factsString = null;
    private FactsBuilder sourceFileFacts;
    private FactsBuilder annotationFacts;
    private FactsBuilder descriptionFacts;
    private FactsBuilder qualificationFacts;

    public ExtractFacts(YesWorkflowDB ywdb, QueryEngineModel queryEngineModel, List<Annotation> annotations) {
        
        this.ywdb = ywdb;
        this.annotations = annotations;
        
        this.sourceFileFacts  = new FactsBuilder(queryEngineModel, "extract_source", "source_id", "source_path");
        this.annotationFacts  = new FactsBuilder(queryEngineModel, "annotation", "annotation_id", "source_id", "line_number", "tag", "keyword", "value");
        this.descriptionFacts  = new FactsBuilder(queryEngineModel, "annotation_description", "annotation_id", "description");
        this.qualificationFacts = new FactsBuilder(queryEngineModel, "annotation_qualifies", "qualifying_annotation_id", "primary_annotation_id");
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

    @SuppressWarnings("unchecked")
    private void buildSourceFileFacts() {
        
        Result<Record> results = ywdb.jooq().select(ID, PATH)
                                     .from(Table.SOURCE)
                                     .fetch();
        
        for (Record record : results) {
            long id = (long)record.getValue(ID);
            String path = (String)record.getValue(PATH);
            if (path == null) path = "";
            sourceFileFacts.add(id, path);
        }
    }
    
    private void buildAnnotationFacts() {
        
        for (Annotation annotation : annotations) {   
            
            annotationFacts.add(
                    annotation.id, 
                    annotation.sourceId, 
                    annotation.lineNumber, 
                    annotation.tag.toString().toLowerCase(),
                    annotation.keyword, 
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
