package org.yesworkflow.db;

import static org.jooq.impl.DSL.field;

import org.jooq.Field;

@SuppressWarnings("rawtypes")
public class ColumnAlias {
    
    public static class BEGIN_ANNOTATION {
        public static Field ID = field("begin_annotation.id");
        public static Field COMMENT_ID = field("begin_annotation.comment_id");
    }
    
    public static class END_ANNOTATION {
        public static Field ID = field("end_annotation.id");        
        public static Field COMMENT_ID = field("end_annotation.comment_id");
    }
    
    public static class BEGIN_COMMENT {
        public static Field ID = field("begin_comment.id");
        public static Field LINE_NUMBER = field("begin_comment.line_number").as("begin_line_number");        
    }
    
    public static class END_COMMENT {
        public static Field ID = field("end_comment.id");
        public static Field LINE_NUMBER = field("end_comment.line_number").as("end_line_number");
    }
}
