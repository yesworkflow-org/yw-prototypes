
% RULE: program_line_range
% May be used to look up source file location of a particular program block: 
% path to source file, starting line number, and ending line number.
program_line_range(ProgramName, SourcePath, StartLine, EndLine) :-
        program(_, ProgramName, StartAnnotation, EndAnnotation), 
        annotation(StartAnnotation, SourceId, StartLine, _, _),
        annotation(EndAnnotation, SourceId, EndLine, _, _),
        extract_source(SourceId, SourcePath).

% RULE has_port
% Used to determine the program associated with any port, in or out.
has_port(ProgramId, PortId) :-
    has_out_port(ProgramId, PortId); has_in_port(ProgramId, PortId).

% RULE: uri_variable_value
% Used to obtain values observed for a variable in the URI template
% for the given port on the given program
uri_variable_value(VariableValue, ProgramName, PortName, UriVariableName) :-
    uri_variable_value(_, UriVariableId, VariableValue),
    uri_variable(UriVariableId, UriVariableName, PortId),
    port(PortId, _, PortName, _),
    has_port(ProgramId, PortId),
    program(ProgramId, ProgramName, _, _).

% RULE: uri_variable_unique_values
% Used to obtain list of unique values observed for a variable in the URI template
uri_variable_unique_values(UniqueValues, ProgramName, PortName, UriVariableName) :-
    findall(Value, 
            uri_variable_value(Value, ProgramName, PortName, UriVariableName), 
            ValueList),
    sort(ValueList, UniqueValues).

% RULE: program_output_resource
% Used to obtain id of resources output by a program
program_output_resource(ProgramName, ResourceId) :-
    program(ProgramId, ProgramName, _, _),
    has_out_port(ProgramId, PortId),
    port_connects_to_channel(PortId, ChannelId),
    resource_channel(ResourceId, ChannelId).
  
% RULE: program_output_uri
% Used to obtain URIs of resources output by a program
program_output_uri(ProgramName, ResourceUri) :-
    program_output_resource(ProgramName, ResourceId),
    resource(ResourceId, ResourceUri).

% RULE: program_port_with_uri_variable
% Used to obtain ports on a program that use the given variable in its URI template
program_port_with_uri_variable(ProgramName, UriVariableName, PortId) :-
    program(ProgramId, ProgramName, _, _),
    has_port(ProgramId, PortId),
    uri_variable(_, UriVariableName, PortId).

% RULE: program_port_with_uri_variables
% Used to obtain ports on a program that use all of the given variables in its URI template
program_port_with_uri_variables(ProgramName, UriVariableNameList, PortId) :-
    program(ProgramId, ProgramName, _, _),
    has_port(ProgramId, PortId),
    forall(member(VariableName, UriVariableNameList), uri_variable(_, VariableName, PortId)).
 
% RULE: resource_with_variable_value
% Used to obtain ids of resources which include a specific variable value in its expanded URI template
resource_with_variable_value(PortId, VariableName, VariableValue, ResourceId) :-
    uri_variable(VariableId, VariableName, PortId),
    uri_variable_value(ResourceId, VariableId, VariableValue).

% RULE: variable_value_for_uris_with_other_variable_value
% Used to obtain values of a variable that occur in an expanded uri template 
% where another variables takes a specific constant value
variable_value_for_uris_with_other_variable_value(ProgramName, ConstantName, ConstantValue, VariableName, VariableValue) :-
    program_port_with_uri_variables(ProgramName, [ConstantName, VariableName], PortId),
    resource_with_variable_value(PortId, ConstantName, ConstantValue, ResourceId),
    uri_variable(VariableId, VariableName, PortId),
    uri_variable_value(ResourceId, VariableId, VariableValue).

% RULE: variable_value_for_uris_with_other_variable_value
% Used to obtain the list of unique values taken by a variable that occurs in an expanded uri template 
% where another variable takes a specific constant value
variable_values_for_uris_with_other_variable_value(ProgramName, ConstantName, ConstantValue, VariableName, UniqueValues) :-
    findall(VariableValue, 
            variable_value_for_uris_with_other_variable_value(ProgramName, ConstantName, ConstantValue, VariableName, VariableValue), 
            ValueList),
    sort(ValueList, UniqueValues).
