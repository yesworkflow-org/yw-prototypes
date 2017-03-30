package org.yesworkflow.db;

import static org.jooq.impl.DSL.field;

import org.jooq.Field;

@SuppressWarnings("rawtypes")
public class Column {

    public static Field BEGIN_ANNOTATION_ID = field("begin_annotation");
    public static Field COMMENT_ID          = field("comment");
    public static Field COMMENT_TEXT        = field("comment_text");
    public static Field DATA_ID             = field("data");
    public static Field DESCRIPTION         = field("description");
    public static Field DIRECTION           = field("direction");
    public static Field END_ANNOTATION_ID   = field("end_annotation");
    public static Field ID                  = field("id");
    public static Field IN_PROGRAM_BLOCK    = field("in_program_block");
    public static Field IS_FUNCTION         = field("is_function");;
    public static Field IS_WORKFLOW         = field("is_workflow");
    public static Field KEYWORD             = field("keyword");
    public static Field LINE_TEXT           = field("line_text");
    public static Field LINE_NUMBER         = field("line_number");
    public static Field NAME                = field("name");
    public static Field OBJECT_ID           = field("object");
    public static Field ON_PROGRAM_BLOCK    = field("on_program_block");
    public static Field PATH                = field("path");
    public static Field PREDICATE           = field("predicate");
    public static Field PROGRAM_ID          = field("in_program_block");
    public static Field PORT_ANNOTATION_ID  = field("port_annotation");
    public static Field QUALIFIED_NAME      = field("qualified_name");
    public static Field QUALIFIES           = field("qualifies");
    public static Field RANK_IN_LINE        = field("rank_in_line");
    public static Field RANK_IN_COMMENT     = field("rank_in_comment");
    public static Field SUBJECT_ID          = field("subject");
    public static Field SOURCE_ID           = field("source");
    public static Field TAG                 = field("tag");
    public static Field URI_TEMPLATE        = field("uri_template");
    public static Field VALUE               = field("value");
    
    public static class DATA {
        public static Field ID                  = field("data.id");
        public static Field IN_PROGRAM_BLOCK    = field("data.in_program_block");
        public static Field NAME                = field("data.name");
    }
    
    public static class SOURCE {
        public static Field ID                  = field("source.id");
        public static Field PATH                = field("source.path");
    }

    public static class SOURCE_LINE {
        public static Field ID              = field("source_line.id");
        public static Field SOURCE_ID       = field("source_line.source");
        public static Field LINE_NUMBER     = field("source_line.line_number");
        public static Field LINE_TEXT       = field("source_line.line_text");
    }

    public static class COMMENT {
        public static Field ID              = field("comment.id");
        public static Field SOURCE_ID       = field("comment.source");
        public static Field LINE_NUMBER     = field("comment.line_number");
        public static Field RANK_IN_LINE    = field("comment.rank_in_line");
        public static Field COMMENT_TEXT    = field("comment.comment_text");
    }
    
    public static class ASSERTION {
        public static Field ID                  = field("assertion.id");
        public static Field ON_PROGRAM_BLOCK    = field("assertion.in_program_block");
        public static Field SUBJECT_ID          = field("assertion.subject");
        public static Field PREDICATE           = field("assertion.predicate");
        public static Field OBJECT_ID           = field("assertion.object");
    }
    
    public static class ANNOTATION {
        public static Field ID                  = field("annotation.id");
        public static Field QUALIFIES           = field("annotation.qualifies");
        public static Field COMMENT_ID          = field("annotation.comment");
        public static Field RANK_IN_COMMENT     = field("annotation.rank_in_comment");
        public static Field TAG                 = field("annotation.tag");
        public static Field KEYWORD             = field("annotation.keyword");
        public static Field VALUE               = field("annotation.value");
        public static Field DESCRIPTION         = field("annotation.description");
    }

    public static class PROGRAM_BLOCK {
        public static Field ID                  = field("program_block.id");
        public static Field ABOUT_PROGRAM       = field("program_block.about_program");
        public static Field BEGIN_ANNOTATION_ID = field("program_block.begin_annotation");
        public static Field END_ANNOTATION_ID   = field("program_block.end_annotation");
        public static Field NAME                = field("program_block.name");
        public static Field QUALIFIED_NAME      = field("program_block.qualified_name");
        public static Field IS_WORKFLOW         = field("program_block.is_workflow");
        public static Field IS_FUNCTION         = field("program_block.is_function");
    }
    
    public static class PORT {
        public static Field ID                  = field("port.id");
        public static Field PORT_ANNOTATION_ID  = field("port.port_annotation");
        public static Field ON_PROGRAM_BLOCK    = field("port.on_program_block");
        public static Field DATA_ID             = field("port.data");
        public static Field NAME                = field("port.name");
        public static Field QUALIFIED_NAME      = field("port.qualified_name");
        public static Field URI_TEMPLATE        = field("port.uri_template");
        public static Field DIRECTION           = field("port.direction");
    }
}
