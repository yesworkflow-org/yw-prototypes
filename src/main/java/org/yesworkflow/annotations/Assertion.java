package org.yesworkflow.annotations;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.yesworkflow.YWKeywords.Tag;
import org.yesworkflow.exceptions.YWMarkupException;

public class Assertion extends Annotation {

    public final String subject;
    public final String predicate;
    public final String object;
    
    public Assertion(Long id, Long sourceId, Long lineNumber, String comment) throws Exception {
        this(id, sourceId, lineNumber, comment, Tag.ASSERT);
    }

    public Assertion(Long id, Long sourceId, Long lineNumber, String comment, Tag tag) throws Exception {
        super(id, sourceId, lineNumber, comment, tag);
        
        StringTokenizer commentTokens = new StringTokenizer(comment);
        commentTokens.nextToken();
        
        try {
            subject = commentTokens.nextToken();
        } catch (NoSuchElementException e) {
            throw new YWMarkupException("No subject provided to @ASSERT keyword on line " + lineNumber);
        }

        try {
            predicate = commentTokens.nextToken();
        } catch (NoSuchElementException e) {
            throw new YWMarkupException("No predicate provided to @ASSERT keyword on line " + lineNumber);
        }
        
        try {
            object = commentTokens.nextToken();
        } catch (NoSuchElementException e) {
            throw new YWMarkupException("No object provided for predicate @ASSERT keyword on line " + lineNumber);
        }
    }
 }

