package org.yesworkflow.annotations;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.yesworkflow.YWKeywords.Tag;
import org.yesworkflow.exceptions.YWMarkupException;

public class Assertion extends Annotation {

    public final String subject;
    public final String predicate;
    public final String[] objects;
    
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
            throw new YWMarkupException("No subject provided to @assert keyword on line " + lineNumber);
        }

        try {
            predicate = commentTokens.nextToken().toLowerCase();
        } catch (NoSuchElementException e) {
            throw new YWMarkupException("No predicate provided to @assert keyword on line " + lineNumber);
        }

        if (!predicate.equals("depends-on")) {
            throw new YWMarkupException("Unrecognized predicate '" + predicate + 
                                        "' given to @assert keyword on line " + lineNumber);
        }
        
        if (!commentTokens.hasMoreTokens()) {
            throw new YWMarkupException("No object provided to @assert keyword on line " + lineNumber);
        }
        
        List<String> objectList = new LinkedList<String>();
        while (commentTokens.hasMoreTokens()) {
            objectList.add(commentTokens.nextToken());
        }
        
        objects = objectList.toArray(new String[1]);
        
        StringBuffer valueBuffer = new StringBuffer();
        valueBuffer.append(subject)
                   .append(" ")
                   .append(predicate);

        for (String obj : objects) {
            valueBuffer.append(" ")
                       .append(obj);
        }
        
        value = valueBuffer.toString();
    }
 }

