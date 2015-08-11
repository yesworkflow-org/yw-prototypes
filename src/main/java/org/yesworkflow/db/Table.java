package org.yesworkflow.db;

import static org.jooq.impl.DSL.table;

public class Table {

    public static org.jooq.Table<?> ANNOTATION  = table("annotation");
    public static org.jooq.Table<?> SOURCE_FILE  = table("source_file");
}
