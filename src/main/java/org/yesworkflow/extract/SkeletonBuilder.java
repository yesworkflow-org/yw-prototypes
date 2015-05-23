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
    
    public void add(Annotation annotation) {
        
        // separate consecutive annotations by two spaces if on 
        // the same line, by two new lines if this annotation starts
        // a new block, or by one new line otherwise
        if (sb.length() > 0) {
            if (annotation instanceof Qualification) {
                sb.append("  ");
            } else {
                sb.append(EOL);
                if (annotation instanceof Begin) sb.append(EOL);
            }
        }
        
        // insert a blank line between consecutive End comments
        if (annotation instanceof End && lastAnnotationWasEnd) {
            sb.append(EOL);
        }
        
        // indent further at the start of each non-initial block
        if (annotation instanceof Begin && sb.length() > 0) {
            indent += "    ";
        }

        // insert the current indentation unless annotation is on the same line as previous
        if (!(annotation instanceof Qualification)) {
            sb.append(indent); 
        }
        
        // insert the comment source for the annotation
        sb.append(annotation.comment);
        
        // un-indent after each block ends
        if (annotation instanceof End && indent.length() >= 4) {
            indent = indent.substring(0, indent.length() - 4);
        }
        
        lastAnnotationWasEnd = annotation instanceof End;
    }
    
    public void end() {
        if (sb.length() > 0) sb.append(EOL);
    }
    
    public String toString() {       
        return sb.toString();
    }

}
