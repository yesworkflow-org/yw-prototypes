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







%-------------------------------------------------------------------------------
banner( 'YW_Q1',
        'What is the name and description of the workflow implemented by the script?',
        'yw_q1(WorkflowName, Description)').
[user].
:- table yw_q1/2.
yw_q1(WorkflowName, Description) :-
    yw_workflow_script(WorkflowId, WorkflowName, _,_),
    yw_description(program, WorkflowId, _, Description).
end_of_file.
printall(yw_q1(_,_)).
%-------------------------------------------------------------------------------


%-------------------------------------------------------------------------------
banner( 'YW_Q2',
        'What workflow steps comprise the top-level workflow?',
        'yw_q2(StepName, Description)').
[user].
:- table yw_q2/2.
yw_q2(StepName, Description) :-
    yw_workflow_script(WorkflowId,_,_,_),
    yw_workflow_step(StepId, StepName, WorkflowId, _, _, _),
    yw_description(program, StepId, _, Description).
end_of_file.
printall(yw_q2(_,_)).
%-------------------------------------------------------------------------------


%-------------------------------------------------------------------------------
banner( 'YW_Q3',
        'Where is the definition of workflow step emphasize_greeting?',
        'yw_q3(SourceFile, StartLine, EndLine)').
[user].
:- table yw_q3/3.
yw_q3(SourceFile, StartLine, EndLine) :-
    yw_workflow_script(WorkflowId,_,_,_),
    yw_workflow_step(_, 'emphasize_greeting', WorkflowId, SourceId, StartLine, EndLine),
    yw_source_file(SourceId, SourceFile).
end_of_file.
printall(yw_q3(_,_,_)).
%-------------------------------------------------------------------------------


%-------------------------------------------------------------------------------
banner( 'YW_Q4',
        'What are the names and descriptions of any outputs of the workflow?',
        'yw_q4(OutputName, Description)').
[user].
:- table yw_q4/2.
yw_q4(OutputName, Description) :-
    yw_workflow_script(WorkflowId,_,_,_),
    yw_step_output(WorkflowId, _, _, PortId, _,_, OutputName),
    yw_description(port, PortId, _, Description).
end_of_file.
printall(yw_q4(_,_)).
%-------------------------------------------------------------------------------


%-------------------------------------------------------------------------------
banner( 'YW_Q5',
        'What data flows from the emphasize_greeting workflow step to the print_greeting step?',
        'yw_q5(DataName)').
[user].
:- table yw_q5/1.
yw_q5(DataName) :-
    yw_flow(_, 'emphasize_greeting', _, _,  _, DataName, _, _, _, 'print_greeting').
end_of_file.
printall(yw_q5(_)).
%-------------------------------------------------------------------------------


%-------------------------------------------------------------------------------
banner( 'NW_Q1',
        'What functions are called from the top level of the script?',
        'nw_q1(FunctionName)').
[user].
:- table nw_q1/1.
nw_q1(FunctionName) :-
    nw_function_activation(_, _, FunctionName, _, ScriptActivation),
    nw_script_activation(_, _, ScriptActivation, _).
end_of_file.
printall(nw_q1(_)).
%-------------------------------------------------------------------------------


%-------------------------------------------------------------------------------
banner( 'NW_Q2',
        'What variable values are passed to greet_user() from the top of the script?',
        'nw_q2(Variable, Value)').
[user].
:- table nw_q2/2.
nw_q2(VariableName, Value) :-
    nw_script_activation(_, _, ScriptActivationId, _),
    nw_function_activation(ActivationId, _, 'greet_user', _, ScriptActivationId),
    nw_function_argument(ActivationId, _, _, _, Value, variable, VariableName, _).
end_of_file.
printall(nw_q2(_,_)).
%-------------------------------------------------------------------------------


%-------------------------------------------------------------------------------
banner( 'NW_Q3',
        'What literal values are passed to the greeting argument of greet_user() from the top of the script?',
        'nw_q3(Literal)').
[user].
:- table nw_q3/1.
nw_q3(Value) :-
    nw_script_activation(_, _, ScriptActivationId, _),
    nw_function_activation(ActivationId, _, 'greet_user', _, ScriptActivationId),
    nw_function_argument(ActivationId, _, _, 'greeting', Value, literal, nil, nil).
end_of_file.
printall(nw_q3(_)).
%-------------------------------------------------------------------------------


%-------------------------------------------------------------------------------
banner( 'YW_NW_Q1',
        'What Python variables carries values of emphasized_greeting into the print_greeting workflow step?',
        'yw_nw_q1(VariableId, VariableName, VariableValue)').
[user].
:- table yw_nw_q1/3.
yw_nw_q1(VariableId, VariableName, VariableValue) :-
    yw_flow(_, _, _, _, _, 'emphasized_greeting', PortId, _, _, 'print_greeting'),
    nw_variable_for_yw_in_port(VariableId, VariableName, VariableValue, _, _, _, PortId, _, _, _).

end_of_file.
printall(yw_nw_q1(_,_,_)).
%-------------------------------------------------------------------------------


%-------------------------------------------------------------------------------
%banner( 'YW_NW_Q2',
%        'What values are emitted by the displayed_greeting output of the print_greeting step?',
%        'yw_nw_q2(OutputValue)').
%[user].
%:- table yw_nw_q1/3.
%yw_nw_q1(VariableValue) :-
%    yw_step_output(_, 'print_greeting', _, PortId, _, _, 'displayed_greeting'),
%    nw_variable_for_yw_out_port(VariableId, VariableName, VariableValue, _, _, PortId, _, _, _).
%
%end_of_file.
%printall(yw_nw_q1(_,_,_)).
%-------------------------------------------------------------------------------


END_XSB_STDIN
