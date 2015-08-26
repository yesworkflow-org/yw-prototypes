package org.yesworkflow.db;

import static org.jooq.impl.DSL.table;

public class Table {

    public static org.jooq.Table<?> ANNOTATION          = table("annotation");
    public static org.jooq.Table<?> CHANNEL             = table("channel");
    public static org.jooq.Table<?> COMMENT             = table("comment");
    public static org.jooq.Table<?> DATA                = table("data");
    public static org.jooq.Table<?> PORT                = table("port");
    public static org.jooq.Table<?> PROGRAM_BLOCK       = table("program_block");
    public static org.jooq.Table<?> RESOURCE            = table("resource");
    public static org.jooq.Table<?> SOURCE              = table("source");
    public static org.jooq.Table<?> SOURCE_LINE         = table("source_line");
    public static org.jooq.Table<?> URI_VARIABLE        = table("uri_variable");
    public static org.jooq.Table<?> URI_VARIABLE_VALUE  = table("uri_variable_value");
}
