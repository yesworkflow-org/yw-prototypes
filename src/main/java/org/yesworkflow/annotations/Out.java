package org.yesworkflow.annotations;

import java.util.LinkedList;
import java.util.List;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.YWKeywords.Tag;

public class Out extends Flow {
    
    protected List<Log> logAnnotations = new LinkedList<Log>();
    
    public Out(Long id,Long sourceId, Long lineNumber, String comment) throws Exception {        
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.OUT);
    }

    public Out(Long id, Long sourceId, Long lineNumber, String comment, Tag tag) throws Exception {
        super(id, sourceId, lineNumber, comment, tag);
    }    

    @Override
    public Flow qualifyWith(Qualification qualification) throws Exception {
        
        if (qualification instanceof Log) {
            this.logAnnotations.add((Log)qualification);
        } else {
            super.qualifyWith(qualification);
        }
        
        return this;
    }    
    
    public List<Log> logAnnotations() {
        return logAnnotations;
    }
}
