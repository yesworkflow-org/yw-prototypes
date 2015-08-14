package org.yesworkflow.db;

import static org.jooq.impl.DSL.field;

import org.jooq.Field;

@SuppressWarnings("rawtypes")
public class Column {

    public static Field DESCRIPTION     = field("description");
    public static Field ID              = field("id");
    public static Field LINE_NUMBER     = field("line_number");
    public static Field PATH            = field("path");
    public static Field QUALIFIES       = field("qualifies");
    public static Field SOURCE_ID       = field("source_id");
    public static Field TAG             = field("tag");
    public static Field KEYWORD         = field("keyword");
    public static Field VALUE           = field("value");
    
    public static class SOURCE {
        public static Field ID          = field("source.id");
        public static Field PATH        = field("source.path");
    }
    
    public static class ANNOTATION {
        public static Field ID          = field("annotation.id");
        public static Field SOURCE_ID   = field("annotation.source_id");
    }
}
