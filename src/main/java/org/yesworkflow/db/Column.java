package org.yesworkflow.db;

import static org.jooq.impl.DSL.field;

import org.jooq.Field;

@SuppressWarnings("rawtypes")
public class Column {

    public static Field BEGIN_ID        = field("begin_id");
    public static Field COMMENT_ID      = field("comment_id");
    public static Field DESCRIPTION     = field("description");
    public static Field END_ID          = field("end_id");
    public static Field ID              = field("id");
    public static Field IS_WORKFLOW     = field("is_workflow");
    public static Field IS_FUNCTION     = field("is_function");
    public static Field KEYWORD         = field("keyword");
    public static Field LINE            = field("line");
    public static Field LINE_NUMBER     = field("line_number");
    public static Field NAME            = field("name");
    public static Field PARENT_ID       = field("parent_id");
    public static Field PATH            = field("path");
    public static Field PROGRAM_ID      = field("program_id");
    public static Field QUALIFIED_NAME  = field("qualified_name");
    public static Field QUALIFIES       = field("qualifies");
    public static Field RANK            = field("rank");
    public static Field SOURCE_ID       = field("source_id");
    public static Field TAG             = field("tag");
    public static Field TEXT            = field("text");
    public static Field VALUE           = field("value");
    
    public static class SOURCE {
        public static Field ID              = field("source.id");
        public static Field PATH            = field("source.path");
    }

    public static class CODE {
        public static Field ID              = field("code.id");
        public static Field SOURCE_ID       = field("code.source_id");
        public static Field LINE_NUMBER     = field("code.line_number");
        public static Field LINE            = field("code.line");
    }

    public static class COMMENT {
        public static Field ID              = field("comment.id");
        public static Field SOURCE_ID       = field("comment.source_id");
        public static Field LINE_NUMBER     = field("comment.line_number");
        public static Field RANK            = field("comment.rank");
        public static Field TEXT            = field("comment.text");
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

    public static class PROGRAM {
        public static Field ID              = field("program.id");
        public static Field PARENT_ID       = field("program.parent_id");
        public static Field BEGIN_ID        = field("program.begin_id");
        public static Field END_ID          = field("program.end_id");
        public static Field NAME            = field("program.name");
        public static Field QUALIFIED_NAME  = field("program.qualified_name");
        public static Field IS_WORKFLOW     = field("program.is_workflow");
        public static Field IS_FUNCTION     = field("program.is_function");
        public static Field BEGIN_ANNOTATION_ID = field("begin_annotation.id");
    }
}
