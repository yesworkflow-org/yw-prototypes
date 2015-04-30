
% RULE: program_line_range
% May be used to look up source file location of a particular program block: 
% path to source file, starting line number, and ending line number.
program_line_range(ProgramName, SourcePath, StartLine, EndLine) :-
        program(_, ProgramName, StartAnnotation, EndAnnotation), 
        annotation(StartAnnotation, SourceId, StartLine, _, _),
        annotation(EndAnnotation, SourceId, EndLine, _, _),
        extract_source(SourceId, SourcePath).

% RULE: uri_variable_value
% Used to obtain values observed for a variable in the URI template
% for the given port on the given program
uri_variable_value(VariableValue, ProgramName, PortName, UriVariableName) :-
    uri_variable_value(_, UriVariableId, VariableValue),
    uri_variable(UriVariableId, UriVariableName, PortId),
    port(PortId, _, PortName, _),
    has_out_port(ProgramId, PortId),
    program(ProgramId, ProgramName, _, _).

% RULE: uri_variable_unique_values
% Used to obtain list of unique values observed for a variable in the URI template
uri_variable_unique_values(UniqueValues, ProgramName, PortName, UriVariableName) :-
    findall(Value, uri_variable_value(Value, ProgramName, PortName, UriVariableName), ValueList),
    sort(ValueList, UniqueValues).

