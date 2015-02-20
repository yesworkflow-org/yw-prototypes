package org.yesworkflow.extract;

import java.io.Reader;
import java.util.List;

import org.yesworkflow.LanguageModel;
import org.yesworkflow.LanguageModel.Language;
import org.yesworkflow.YWStage;
import org.yesworkflow.comments.Comment;

public interface Extractor extends YWStage {
    Extractor languageModel(LanguageModel languageModel);
    Extractor commentDelimiter(String c);
	Extractor source(Reader reader);
    Extractor extract() throws Exception;
    Language getLanguage();
    List<String> getLines();
    List<Comment> getComments();
}

