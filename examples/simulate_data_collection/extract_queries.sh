#!/usr/bin/env bash
#
# ./run_queries.sh &> run_queries.txt

source $SCRIPT_SETTINGS_FILE

xsb --quietload --noprompt --nofeedback --nobanner << END_XSB_STDIN

['$RULES_DIR/general_rules'].
['$FACTS_DIR/yw_extract_facts'].
['$FACTS_DIR/yw_views'].

set_prolog_flag(unknown, fail).

%-------------------------------------------------------------------------------
banner( 'EQ1',
        'What source files were YW annotations extracted from?',
        'eq1(SourceFile)').
[user].
:- table eq1/1.
eq1(SourceFile) :-
    extract_source(_, SourceFile).
end_of_file.
printall(eq1(_)).
%-------------------------------------------------------------------------------

%-------------------------------------------------------------------------------
banner( 'EQ2',
        'What are the names N of all program blocks?',
        'eq2(ProgramName)').
[user].
:- table eq2/1.
eq2(ProgramName) :-
    annotation(_, _, _, 'begin', _, ProgramName).
end_of_file.
printall(eq2(_)).
%-------------------------------------------------------------------------------

%-------------------------------------------------------------------------------
banner( 'EQ3',
        'What out ports are qualified with URIs',
        'eq3(PortName)').
[user].
:- table eq3/1.
eq3(PortName) :-
    annotation(URI, _, _, 'uri', _, _),
    annotation(OUT, _, _, 'out', _, PortName),
    annotation_qualifies(URI, OUT).
end_of_file.
printall(eq3(_)).
%-------------------------------------------------------------------------------

END_XSB_STDIN
