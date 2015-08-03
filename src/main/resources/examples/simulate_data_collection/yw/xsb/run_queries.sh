#!/usr/bin/env bash -l
#
# ./run_queries.sh &> run_queries.txt

xsb << END_XSB_STDIN

[rules].
[extractfacts].
[modelfacts].
[reconfacts].
[extract_queries].
[model_queries].
[recon_queries].

printall('eq1(SourceFile) - What source files SF were YW annotations extracted from?', eq1(_)).
printall('eq2(ProgramName) - What are the names N of all program blocks?', eq2(_)).
printall('eq3(PortName) - What out ports are qualified with URIs?', eq3(_)).

printall('mq1(SourceFile,StartLine,EndLine) - Where is the definition of block simulate_data_collection.collect_data_set?', mq1(_,_,_)).
printall('mq2(WorkflowName) - What is the name of the top-level workflow?', mq2(_)).
printall('mq3(FunctionName) - What are the names of any top-level functions?', mq3(_)).
printall('mq4(ProgramName) -  What are the names of the programs comprising the top-level workflow?', mq4(_)).
printall('mq5(DataName) - What are the inputs to the top-level workflow?', mq5(_)).
printall('mq6(DataName) - What data is output by program block simulate_data_collection.collect_data_set?', mq6(_)).
printall('mq7(ProgramName) - What program blocks provide input directly to simulate_data_collection.collect_data_set?', mq7(_)).
printall('mq8(ProgramName) - What programs have input ports that receive data simulate_data_collection[cassette_id]', mq8(_)).
printall('mq9(PortCount) - How many ports read data simulate_data_collection[frame_number]?', mq9(_)).
printall('mq10(DataCount) - How many data are read by more than port in workflow simulate_data_collection?', mq10(_)).
printall('mq11(ProgramName) - What program blocks are immediately downstream of calculate_strategy?', mq11(_)).
printall('mq12(UpstreamProgramName) - What program blocks are immediately upstream of transform_images?', mq12(_)).
printall('mq13(UpstreamProgramName) - What program blocks are upstream of transform_images?', mq13(_)).
printall('mq14(DownstreamProgramName) - What program blocks are anywhere downstream of calculate_strategy?', mq14(_)).
printall('mq15(DownstreamDataName) - What data is immediately downstream of raw_image?', mq15(_)).
printall('mq16(UpstreamDataName) - What data is immediately upstream of raw_image?', mq16(_)).
printall('mq17(DownstreamDataName) - What data is downstream of accepted_sample?', mq17(_)).
printall('mq18(UpstreamDataName) - What data is upstream of raw_image?', mq18(_)).
printall('mq19(UriVariableName) - What URI variables are associated with writes of data simulate_data_collection[corrected_image]?', mq19(_)).
printall('mq20(UpStreamDataName) - What URI variables do data written to raw_image and corrected_image have in common?', mq20(_)).

printall('rq0(VarName, VarValue) - What URI variable values are associated with resource run/data/DRT322/DRT322_11000eV_028.img?', rq0(_,_)).
printall('rq1(Sample) - What samples did the run of the script collect images from?', rq1(_)).
printall('rq2(Energy) - What energies were used during collection of images from sample DRT322?', rq2(_)).
printall('rq3(RawImageFile) - Where is the raw image from which corrected image run/data/DRT322/DRT322_11000eV_028.img is derived?', rq3(_)).
printall('rq4(RawImageFile) - Are there any raw images for which there are no corresponding corrected images?', rq4(_)).
printall('rq5(Cassette) - What cassette held the sample from which run/data/DRT240/DRT240_10000eV_010.img was derived?', rq5(_)).

END_XSB_STDIN


