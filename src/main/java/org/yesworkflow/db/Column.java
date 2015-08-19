package org.yesworkflow.db;

import static org.jooq.impl.DSL.field;

import org.jooq.Field;

@SuppressWarnings("rawtypes")
public class Column {

    public static Field COMMENT_ID      = field("comment_id");
    public static Field DESCRIPTION     = field("description");
    public static Field ID              = field("id");
    public static Field KEYWORD_START   = field("keyword_start");
    public static Field LINE            = field("line");
    public static Field LINE_NUMBER     = field("line_number");
    public static Field PATH            = field("path");
    public static Field QUALIFIES       = field("qualifies");
    public static Field RANK            = field("rank");
    public static Field SOURCE_ID       = field("source_id");
    public static Field TAG             = field("tag");
    public static Field TEXT            = field("text");
    public static Field KEYWORD         = field("keyword");
    public static Field VALUE           = field("value");
    
    public static class SOURCE {
        public static Field ID              = field("source.id");
        public static Field PATH            = field("source.path");
    }

    public static class COMMENT {
        public static Field ID              = field("comment.id");
        public static Field SOURCE_ID       = field("comment.source_id");
        public static Field LINE_NUMBER     = field("comment.line_number");
        public static Field RANK            = field("comment.rank");
        public static Field TEXT            = field("comment.text");        
        public static Field KEYWORD_START   = field("comment.keyword_start");
    }
    
    public static class ANNOTATION {
        public static Field ID              = field("annotation.id");
        public static Field QUALIFIES       = field("annotation.qualifies");
        public static Field COMMENT_ID      = field("annotation.comment_id");
        public static Field TAG             = field("annotation.tag");
        public static Field KEYWORD         = field("annotation.keyword");
        public static Field VALUE           = field("annotation.value");
        public static Field DESCRIPTION     = field("annotation.description");
    }
}
