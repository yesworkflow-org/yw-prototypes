package org.yesworkflow.annotations;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.yesworkflow.YWKeywords.Tag;
import org.yesworkflow.exceptions.YWMarkupException;

public class Assertion extends Annotation {

    public final String subject;
    public final String predicateText;
    public final Predicate predicate;
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
            predicateText = commentTokens.nextToken().toLowerCase();
        } catch (NoSuchElementException e) {
            throw new YWMarkupException("No predicate provided to @assert keyword on line " + lineNumber);
        }

        try {
            predicate = Predicate.toPredicate(predicateText);
        } catch (YWMarkupException e) {            
            throw new YWMarkupException("Unrecognized predicate '" + predicateText + 
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
                   .append(predicateText);

        for (String obj : objects) {
            valueBuffer.append(" ")
                       .append(obj);
        }
        
        value = valueBuffer.toString();
    }
    
    public static enum Predicate {
        
        DOWNSTREAM_OF,
        DEPENDS_ON,
        ACQUIRED_USING,
        DERIVES_FROM,
        TAKES_VALUE_OF,
        IS;
        
        public static Predicate toPredicate(Object predicate) throws YWMarkupException {
            
            if (predicate instanceof Predicate) return (Predicate)predicate;
            
            if (predicate instanceof String) {
                String predicateString = (String)predicate; 
                if (predicateString.equalsIgnoreCase("DOWNSTREAM-OF"))      return Predicate.DOWNSTREAM_OF;
                if (predicateString.equalsIgnoreCase("DEPENDS-ON"))         return Predicate.DEPENDS_ON;
                if (predicateString.equalsIgnoreCase("ACQUIRED-USING"))     return Predicate.ACQUIRED_USING;
                if (predicateString.equalsIgnoreCase("DERIVES-FROM"))       return Predicate.DERIVES_FROM;
                if (predicateString.equalsIgnoreCase("TAKES-VALUE-OF"))     return Predicate.TAKES_VALUE_OF;
                if (predicateString.equalsIgnoreCase("IS"))                 return Predicate.IS;
            }
            
            throw new YWMarkupException("Unrecognized assertion predicate: " + predicate);
        }
    }
 }

