package org.yesworkflow.extract;

public class SourceLine {

    public final Integer lineId;
    public final Integer sourceId;
    public final Integer lineNumber;
    public final String text;
    
    public SourceLine(Integer lineId, Integer sourceId, Integer lineNumber, String text) {
        this.lineId = lineId;
        this.sourceId = sourceId;
        this.lineNumber = lineNumber;
        this.text = text;
    }
}
