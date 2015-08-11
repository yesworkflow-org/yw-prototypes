package org.yesworkflow.db;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

import org.jooq.Field;
import org.jooq.Table;

@SuppressWarnings("rawtypes")
public class Tables {

    public static Table<?> ANNOTATION   = table("annotation");
    public static Table<?> SOURCE_FILE  = table("source_file");
    
    public static Field DESCRIPTION     = field("description");
    public static Field ID              = field("id");
    public static Field LINE_NUMBER     = field("line_number");
    public static Field PATH            = field("path");
    public static Field QUALIFIES       = field("qualifies");
    public static Field SOURCE_FILE_ID  = field("source_file_id");
    public static Field TAG             = field("tag");
    public static Field KEYWORD         = field("keyword");
    public static Field VALUE           = field("value");
}
