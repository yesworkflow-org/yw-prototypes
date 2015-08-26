
/******************************************* SQLite Notes ************************************************* 

- Primary key columns are of type 'INTEGER' so that they are aliases for the builtin 64-bit 'rowid' column.
  See http://www.sqlite.org/lang_createtable.html#rowid
  
- TEXT columns that generally would be of type 'char' or 'varchar' are given the type 'TEXT' to reflect
  the dynamic storage of data values in SQLite. See http://www.sqlite.org/datatype3.html
  
**************************************************************************************************/

CREATE TABLE source (
    id                  INTEGER         NOT NULL        PRIMARY KEY,
    path                TEXT            NULL
);

CREATE TABLE source_line (
    id                  INTEGER         NOT NULL        PRIMARY KEY,
    source              INTEGER         NOT NULL        REFERENCES source(id),
    line_number         INTEGER         NOT NULL,
    line_text           TEXT            NOT NULL
);

CREATE TABLE comment (
    id                  INTEGER         NOT NULL        PRIMARY KEY,
    source              INTEGER         NOT NULL        REFERENCES source(id),
    line_number         INTEGER         NOT NULL,
    rank_in_line        INTEGER         NOT NULL,
    comment_text        VARCHAR         NOT NULL
);

CREATE TABLE annotation (
    id                  INTEGER         NOT NULL        PRIMARY KEY,
    qualifies           INTEGER         NULL            REFERENCES annotation(id),
    comment             INTEGER         NOT NULL        REFERENCES comment(id),
    rank_in_comment     INTEGER         NOT NULL,
    tag                 TEXT            NOT NULL,
    keyword             TEXT            NOT NULL,
    value               TEXT            NOT NULL,
    description         TEXT            NULL
);

CREATE TABLE program_block (
    id                  INTEGER         NOT NULL        PRIMARY KEY,
    in_program_block    INTEGER         NULL            REFERENCES program_block(id),
    begin_annotation    INTEGER         NULL            REFERENCES annotation(id),
    end_annotation      INTEGER         NULL            REFERENCES annotation(id),
    name                TEXT            NOT NULL        DEFAULT '',
    qualified_name      TEXT            NOT NULL        DEFAULT '',
    is_workflow         INTEGER         NOT NULL        DEFAULT 0,
    is_function         INTEGER         NOT NULL        DEFAULT 0
);

CREATE TABLE data (
    id                  INTEGER         NOT NULL        PRIMARY KEY,
    in_program_block    INTEGER         NULL,
    name                TEXT            NOT NULL,
    qualified_name      TEXT            NOT NULL
);

CREATE TABLE port (
    id                  INTEGER         NOT NULL        PRIMARY KEY,
    port_annotation     INTEGER         NULL            REFERENCES annotation(id),
    on_program_block    INTEGER         NOT NULL        REFERENCES program_block(id),
    data                INTEGER         NOT NULL        REFERENCES data(id),
    name                TEXT            NOT NULL,
    qualified_name      TEXT            NOT NULL,
    alias               TEXT            NULL,
    uri_template        TEXT            NULL,
    is_inport           INTEGER         NOT NULL,
    is_outport          INTEGER         NOT NULL
);

CREATE TABLE channel (
    id                  INTEGER         NOT NULL        PRIMARY KEY,
    out_port            INTEGER         NOT NULL        REFERENCES port(id),
    in_port             INTEGER         NOT NULL        REFERENCES port(id),
    data                INTEGER         NOT NULL        REFERENCES data(id),
    is_inflow           INTEGER         NOT NULL,
    is_outflow          INTEGER         NOT NULL
);

CREATE TABLE uri_variable (
    id                  INTEGER         NOT NULL        PRIMARY KEY,
    port                INTEGER         NOT NULL        REFERENCES port(id),
    name                TEXT            NOT NULL
);

CREATE TABLE resource (
    id                  INTEGER         NOT NULL        PRIMARY KEY,
    data                INTEGER         NOT NULL        REFERENCES data(id),
    uri                 TEXT            NOT NULL
);


CREATE TABLE uri_variable_value (
    uri_variable        INTEGER         NOT NULL        REFERENCES uri_variable(id),
    resource            INTEGER         NOT NULL        REFERENCES resource(id),
    value               TEXT            NOT NULL
);