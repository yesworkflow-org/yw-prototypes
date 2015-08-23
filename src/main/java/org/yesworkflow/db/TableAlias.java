package org.yesworkflow.db;

public class TableAlias {
    
    public static org.jooq.Table<?> BEGIN_ANNOTATION  = Table.ANNOTATION.as("begin_annotation");
    public static org.jooq.Table<?> END_ANNOTATION = Table.ANNOTATION.as("end_annotation");
    public static org.jooq.Table<?> BEGIN_COMMENT = Table.COMMENT.as("begin_comment");
    public static org.jooq.Table<?> END_COMMENT = Table.COMMENT.as("end_comment");
}
