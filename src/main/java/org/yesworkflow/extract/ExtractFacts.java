package org.yesworkflow.extract;

import static org.yesworkflow.db.Column.ID;
import static org.yesworkflow.db.Column.PATH;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Record;
import org.jooq.Result;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.annotations.Qualification;
import org.yesworkflow.db.Table;
import org.yesworkflow.db.YesWorkflowDB;
import org.yesworkflow.query.DataExportBuilder;
import org.yesworkflow.query.QueryEngine;

public class ExtractFacts {

    private YesWorkflowDB ywdb;
    private final List<Annotation> annotations;
    private Map<String,String> facts = new LinkedHashMap<String,String>();
    private DataExportBuilder sourceFileFacts;
    private DataExportBuilder annotationFacts;
    private DataExportBuilder qualificationFacts;

    public ExtractFacts(YesWorkflowDB ywdb, QueryEngine queryEngine, List<Annotation> annotations) throws IOException {
        
        this.ywdb = ywdb;
        this.annotations = annotations;
        
        this.sourceFileFacts  = DataExportBuilder.create(queryEngine, "extract_source", "source_id", "source_path");
        this.annotationFacts  = DataExportBuilder.create(queryEngine, "annotation", "annotation_id", "source_id", "line_number", "tag", "keyword", "value");
        this.qualificationFacts = DataExportBuilder.create(queryEngine, "annotation_qualifies", "qualifying_annotation_id", "primary_annotation_id");
    }

    public ExtractFacts build() throws IOException {
                
        buildSourceFileFacts();
        buildAnnotationFacts();
        
        facts.put(sourceFileFacts.name, sourceFileFacts.toString());
        facts.put(annotationFacts.name, annotationFacts.toString());
        facts.put(qualificationFacts.name, qualificationFacts.toString());
        
        return this;
    }

    public Map<String,String> facts() {
        return facts;
    }

    @SuppressWarnings("unchecked")
    private void buildSourceFileFacts() throws IOException {
        
        Result<Record> results = ywdb.jooq().select(ID, PATH)
                                     .from(Table.SOURCE)
                                     .fetch();
        
        for (Record record : results) {
            long id = ywdb.getLongValue(record, ID);
            String path = (String)record.getValue(PATH);
            if (path == null) path = "";
            sourceFileFacts.addRow(id, path);
        }
    }
    
    private void buildAnnotationFacts() throws IOException {
        
        for (Annotation annotation : annotations) {   
            
            annotationFacts.addRow(
                    annotation.id, 
                    annotation.sourceId, 
                    annotation.lineNumber, 
                    annotation.tag.toString().toLowerCase(),
                    annotation.keyword, 
                    annotation.value()
            );
            
            if (annotation instanceof Qualification) {
                qualificationFacts.addRow(
                        annotation.id, 
                        ((Qualification)annotation).primaryAnnotation.id
                );
            }
        }
    }
}
