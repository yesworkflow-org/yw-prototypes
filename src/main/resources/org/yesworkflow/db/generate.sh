#!/usr/local/bin/bash
sqlite3 << SQLITE_STDIN_END
.read createtables.sql
.save schema.db
.exit
SQLITE_STDIN_END