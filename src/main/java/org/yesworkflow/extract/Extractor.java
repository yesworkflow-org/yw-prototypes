package org.yesworkflow.extract;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.yesworkflow.Language;
import org.yesworkflow.YWStage;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.query.QueryEngineModel;

public interface Extractor extends YWStage {
    Extractor configure(String key, Object value) throws Exception;
    Extractor configure(Map<String, Object> config) throws Exception;
    Extractor reader(Reader reader);
    Extractor extract() throws Exception;
    Language getLanguage();
    List<Comment> getLines();
    List<String> getComments();
    String getSkeleton();
    List<Annotation> getAnnotations();
    String getFacts(QueryEngineModel qem);
}

