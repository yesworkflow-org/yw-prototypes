
program_line_range(ProgramName, SourcePath, StartLine, EndLine) :-
        program(_, ProgramName, StartAnnotation, EndAnnotation), 
        annotation(StartAnnotation, SourceId, StartLine, _, _),
        annotation(EndAnnotation, SourceId, EndLine, _, _),
        extract_source(SourceId, SourcePath).
