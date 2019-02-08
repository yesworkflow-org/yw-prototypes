package org.yesworkflow.extract;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.yesworkflow.Language;
import org.yesworkflow.YWStage;
import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.config.Configurable;

public interface Extractor extends YWStage, Configurable {
    Extractor configure(String key, Object value) throws Exception;
    Extractor configure(Map<String, Object> config) throws Exception;
    Extractor reader(Reader reader);
    Extractor extract() throws Exception;
    Language getLanguage();
    String getSkeleton();
    List<String> getSourceCodeList();
    List<String> getSourcePaths();
    List<Annotation> getAnnotations();
    Map<String, String> getFacts() throws IOException;
}

