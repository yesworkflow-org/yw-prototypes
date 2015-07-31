#!/usr/bin/env bash -l
#
# ./run_queries.sh &> run_queries.txt

xsb << END_XSB_STDIN

% Load facts and rules.
[extractfacts].
[modelfacts].
[reconfacts].
[rules].
[extract_queries].
[model_queries].
[recon_queries].

write('EQ1: What source files SF were YW annotations extracted from?').
eq1(SourceFile).
.

write('EQ2: What are the names N of all program blocks?').
eq2(ProgramName).
.
.
.
.
.
.
.
.
.
.

write('EQ3: What out ports are qualified with URIs?').
eq3(PortName).
.
.
.
.
.

write('MQ1: Where is the definition of block simulate_data_collection.collect_data_set?').
mq1(SourceFile, StartLine, EndLine).
.

write('MQ2: What is the name of the top-level workflow?').
mq2(WorkflowName).
.

write('MQ3: What are the names of any top-level functions?').
mq3(FunctionName).
.
.

write('MQ4:  What are the names of the programs comprising the top-level workflow?').
mq4(ProgramName).
.
.
.
.
.
.
.

write('MQ5: What are the inputs to the top-level workflow?').
mq5(InputPortName).
.
.
.
.



write('MQ6: What data is output by program block simulate_data_collection.collect_data_set?').
mq6(DataName).
.
.
.
.

write('MQ7: What program blocks provide input directly to simulate_data_collection.collect_data_set?').
mq7(ProgramName).
.

write('MQ8: What programs have input ports that receive data simulate_data_collection[cassette_id]').
mq8(ProgramName).
.
.
.
.


write('MQ9: How many ports read data simulate_data_collection[frame_number]?').
mq9(Ports).
.

write('MQ10: How many data are read by more than port in workflow simulate_data_collection?').
mq10(Ports).
.

write('MQ11: What program blocks are immediately downstream of calculate_strategy?').
mq11(ProgramName).
.
.

write('MQ12: What program blocks are immediately upstream of transform_images?').
mq12(UpstreamProgramName).
.

write('MQ13: What program blocks are upstream of transform_images?').
mq13(UpstreamProgramName).
.
.
.

write('MQ14: What program blocks are anywhere downstream of calculate_strategy?').
mq14(DownstreamProgramName).
.
.
.
.

write('MQ15: What data is immediately downstream of raw_image?').
mq15(DownstreamDataName).
.
.
.
.

write('MQ16: What data is immediately upstream of raw_image?').
mq16(UpstreamDataName).
.
.
.
.

write('MQ17: What data is downstream of accepted_sample?').
mq17(DownstreamDataName).
.
.
.
.
.
.
.
.
.

write('MQ18: What data is upstream of raw_image?').
mq18(UpstreamDataName).
.
.
.
.
.
.
.
.

write('MQ19: What URI variables are associated with writes of data simulate_data_collection[corrected_image]?').
mq19(UriVariableName).
.
.
.

write('MQ20: What URI variables do data written to raw_image and corrected_image have in common?').
mq20(UpStreamDataName).
.
.
.


write('RQ0: What URI variable values are associated with resource run/data/DRT322/DRT322_11000eV_028.img?').
rq0(VarName, VarValue).
.
.
.

write('RQ1: What samples did the run of the script collect images from?').
rq1(Sample).
.
.

write('RQ2: What energies were used during collection of images from sample DRT322?').
rq2(Energy).
.
.

write('RQ3: Where is the raw image from which corrected image run/data/DRT322/DRT322_11000eV_028.img is derived?').
rq3(RawImageFile).
.

write('Q4: Are there any raw images for which there are no corresponding corrected images?').
rq4(RawImageFile).
.

write('RQ5: What cassette held the sample from which run/data/DRT240/DRT240_10000eV_010.img was derived?').
rq5(Cassette).
.


END_XSB_STDIN


