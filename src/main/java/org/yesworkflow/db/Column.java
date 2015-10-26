package org.yesworkflow.db;

import static org.jooq.impl.DSL.field;

import org.jooq.Field;

@SuppressWarnings("rawtypes")
public class Column {

    public static Field ALIAS               = field("alias");
    public static Field BEGIN_ANNOTATION_ID = field("begin_annotation");
    public static Field COMMENT_ID          = field("comment");
    public static Field COMMENT_TEXT        = field("comment_text");
    public static Field DESCRIPTION         = field("description");
    public static Field END_ANNOTATION_ID   = field("end_annotation");
    public static Field ID                  = field("id");
    public static Field IN_PROGRAM_BLOCK    = field("in_program_block");
    public static Field IN_CODE_BLOCK       = field("in_code_block");
    public static Field INPUT_OR_OUTPUT     = field("input_or_output");
    public static Field IS_WORKFLOW         = field("is_workflow");
    public static Field IS_FUNCTION         = field("is_function");
    public static Field KEYWORD             = field("keyword");
    public static Field LINE_TEXT           = field("line_text");
    public static Field LINE_NUMBER         = field("line_number");
    public static Field NAME                = field("name");
    public static Field PATH                = field("path");
    public static Field PROGRAM_ID          = field("in_program_block");
    public static Field QUALIFIED_NAME      = field("qualified_name");
    public static Field QUALIFIES           = field("qualifies");
    public static Field RANK_IN_LINE        = field("rank_in_line");
    public static Field RANK_IN_COMMENT     = field("rank_in_comment");
    public static Field SOURCE_ID           = field("source");
    public static Field TAG                 = field("tag");
    public static Field URI                 = field("uri");
    public static Field VALUE               = field("value");
    public static Field VARIABLE            = field("variable");
    public static Field BEGIN_LINE          = field("begin_line");
    public static Field END_LINE            = field("end_line");
    public static Field INPUT               = field("input");
    public static Field OUTPUT              = field("output");

    public static class DATA {
        public static Field ID                  = field("data.id");
        public static Field IN_PROGRAM_BLOCK    = field("data.in_program_block");
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
        public static Field IN_PROGRAM_BLOCK    = field("program_block.in_program_block");
        public static Field BEGIN_ANNOTATION_ID = field("program_block.begin_annotation");
        public static Field END_ANNOTATION_ID   = field("program_block.end_annotation");
        public static Field NAME                = field("program_block.name");
        public static Field QUALIFIED_NAME      = field("program_block.qualified_name");
        public static Field IS_WORKFLOW         = field("program_block.is_workflow");
        public static Field IS_FUNCTION         = field("program_block.is_function");
    }

    public static class CODE_BLOCK {
        public static Field ID                  = field("code_block.id");
        public static Field BEGIN_LINE          = field("code_block.begin_annotation");
        public static Field END_LINE            = field("code_block.end_annotation");
        public static Field NAME                = field("code_block.name");
        public static Field INPUT               = field("code_block.input");
        public static Field OUTPUT              = field("code_block.output");
    }

    public static class SIGNATURE {
        public static Field ID                  = field("signature.id");
        public static Field INPUT_OR_OUTPUT     = field("signature.input_or_output");
        public static Field VARIABLE            = field("signature.variable");
        public static Field ALIAS               = field("signature.alias");
        public static Field URI                 = field("signature.uri");
        public static Field IN_CODE_BLOCK       = field("signature.in_code_output");
    }
}
