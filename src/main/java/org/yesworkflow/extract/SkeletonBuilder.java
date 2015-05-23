package org.yesworkflow.extract;

import org.yesworkflow.annotations.Annotation;
import org.yesworkflow.annotations.Begin;
import org.yesworkflow.annotations.End;
import org.yesworkflow.annotations.Qualification;

public class SkeletonBuilder {

    public static final String EOL = System.getProperty("line.separator");

    private StringBuilder sb = new StringBuilder();
    private String indent = "";
    private boolean lastAnnotationWasEnd = false;
    
    public SkeletonBuilder(String commentDelimiter) {
        this.indent = commentDelimiter;
    }
    
    public void add(Annotation annotation, String comment) {
        
        
        if (sb.length() > 0) {
            if (annotation instanceof Qualification) {
                sb.append("  ");
            } else {
                sb.append(EOL);
                if (annotation instanceof Begin) sb.append(EOL);
            }
        }
        
        if (annotation instanceof End && lastAnnotationWasEnd) {
            sb.append(EOL);
        }
        
        if (annotation instanceof Begin && sb.length() > 0) {
            indent += "    ";
        }

        if (!(annotation instanceof Qualification)) {
            sb.append(indent); 
        }        
        
        sb.append(comment);
        
        if (annotation instanceof End && indent.length() >= 4) {
            indent = indent.substring(0, indent.length() - 4);
        }
        
        lastAnnotationWasEnd = annotation instanceof End;
    }
    
    public String toString() {
        return sb.toString();
    }

}
