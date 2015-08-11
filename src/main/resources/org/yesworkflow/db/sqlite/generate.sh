#!/usr/bin/env bash

mvn clean
mkdir target

sqlite3 << SQLITE_STDIN_END
.read createtables.sqlite
.save target/empty.sqlite
.exit
SQLITE_STDIN_END

mvn generate-sources