
% FACT: program(program_id, program_name, begin_annotation_id, end_annotation_id).
program(1, 'simulate_data_collection', 1, 80).
program(2, 'initialize_run', 16, 19).
program(3, 'load_screening_results', 20, 26).
program(4, 'calculate_strategy', 27, 35).
program(5, 'log_rejected_sample', 36, 41).
program(6, 'collect_data_set', 42, 54).
program(7, 'transform_images', 55, 69).
program(8, 'log_average_image_intensity', 70, 79).
program(9, 'collect_next_image', 81, 91).
program(10, 'transform_image', 92, 98).

% FACT: workflow(program_id).
workflow(1).

% FACT: function(program_id).
function(9).
function(10).

% FACT: has_sub_program(program_id, subprogram_id).
has_sub_program(1, 2).
has_sub_program(1, 3).
has_sub_program(1, 4).
has_sub_program(1, 5).
has_sub_program(1, 6).
has_sub_program(1, 7).
has_sub_program(1, 8).

% FACT: port(port_id, port_type, port_name, port_annotation_id).
port(1, 'param', 'cassette_id', 2).
port(2, 'param', 'sample_score_cutoff', 3).
port(3, 'in', 'sample_spreadsheet', 4).
port(4, 'in', 'calibration_image', 6).
port(5, 'out', 'corrected_image', 8).
port(6, 'out', 'run_log', 10).
port(7, 'out', 'collection_log', 12).
port(8, 'out', 'rejection_log', 14).
port(9, 'out', 'run_log', 17).
port(10, 'param', 'cassette_id', 21).
port(11, 'in', 'sample_spreadsheet', 22).
port(12, 'out', 'sample_name', 24).
port(13, 'out', 'sample_quality', 25).
port(14, 'param', 'sample_score_cutoff', 28).
port(15, 'in', 'sample_name', 29).
port(16, 'in', 'sample_quality', 30).
port(17, 'out', 'accepted_sample', 31).
port(18, 'out', 'rejected_sample', 32).
port(19, 'out', 'num_images', 33).
port(20, 'out', 'energies', 34).
port(21, 'param', 'cassette_id', 37).
port(22, 'in', 'rejected_sample', 38).
port(23, 'out', 'rejection_log', 39).
port(24, 'param', 'cassette_id', 44).
port(25, 'in', 'accepted_sample', 45).
port(26, 'in', 'num_images', 46).
port(27, 'in', 'energies', 47).
port(28, 'out', 'sample_id', 48).
port(29, 'out', 'energy', 49).
port(30, 'out', 'frame_number', 50).
port(31, 'out', 'raw_image_path', 51).
port(32, 'in', 'sample_id', 57).
port(33, 'in', 'energy', 58).
port(34, 'in', 'frame_number', 59).
port(35, 'in', 'raw_image_path', 60).
port(36, 'in', 'calibration_image', 62).
port(37, 'out', 'corrected_image', 64).
port(38, 'out', 'corrected_image_path', 66).
port(39, 'out', 'total_intensity', 67).
port(40, 'out', 'pixel_count', 68).
port(41, 'param', 'cassette_id', 71).
port(42, 'param', 'sample_id', 72).
port(43, 'param', 'frame_number', 73).
port(44, 'in', 'corrected_image_path', 74).
port(45, 'in', 'total_intensity', 75).
port(46, 'in', 'pixel_count', 76).
port(47, 'out', 'collection_log', 77).
port(48, 'param', 'cassette_id', 82).
port(49, 'param', 'sample_id', 83).
port(50, 'param', 'num_images', 84).
port(51, 'param', 'energies', 85).
port(52, 'param', 'image_path_template', 86).
port(57, 'param', 'raw_image_path', 93).
port(58, 'param', 'corrected_image_path', 94).
port(59, 'param', 'calibration_image_path', 95).

% FACT: port_alias(port_id, alias).
port_alias(31, 'raw_image').
port_alias(35, 'raw_image').

% FACT: port_uri(port_id, uri).
port_uri(3, 'file:cassette_{cassette_id}_spreadsheet.csv').
port_uri(4, 'file:calibration.img').
port_uri(5, 'file:run/data/{}/{}_{}eV_{}.img').
port_uri(6, 'file:run/run_log.txt').
port_uri(7, 'file:run/collected_images.csv').
port_uri(8, 'file:run/rejected_samples.txt').
port_uri(9, 'file:run/run_log.txt').
port_uri(11, 'file:cassette_{cassette_id}_spreadsheet.csv').
port_uri(23, 'file:/run/rejected_samples.txt').
port_uri(31, 'file:run/raw/{cassette_id}/{sample_id}/e{energy}/image_{frame_number}.raw').
port_uri(36, 'file:calibration.img').
port_uri(37, 'file:data/{sample_id}/{sample_id}_{energy}eV_{frame_number}.img').
port_uri(47, 'file:run/collected_images.csv').

% FACT: has_in_port(block_id, port_id).
has_in_port(1, 1).
has_in_port(1, 2).
has_in_port(1, 3).
has_in_port(1, 4).
has_in_port(3, 10).
has_in_port(3, 11).
has_in_port(4, 14).
has_in_port(4, 15).
has_in_port(4, 16).
has_in_port(5, 21).
has_in_port(5, 22).
has_in_port(6, 24).
has_in_port(6, 25).
has_in_port(6, 26).
has_in_port(6, 27).
has_in_port(7, 32).
has_in_port(7, 33).
has_in_port(7, 34).
has_in_port(7, 35).
has_in_port(7, 36).
has_in_port(8, 41).
has_in_port(8, 42).
has_in_port(8, 43).
has_in_port(8, 44).
has_in_port(8, 45).
has_in_port(8, 46).
has_in_port(9, 48).
has_in_port(9, 49).
has_in_port(9, 50).
has_in_port(9, 51).
has_in_port(9, 52).
has_in_port(10, 57).
has_in_port(10, 58).
has_in_port(10, 59).

% FACT: has_out_port(block_id, port_id).
has_out_port(1, 5).
has_out_port(1, 6).
has_out_port(1, 7).
has_out_port(1, 8).
has_out_port(2, 9).
has_out_port(3, 12).
has_out_port(3, 13).
has_out_port(4, 17).
has_out_port(4, 18).
has_out_port(4, 19).
has_out_port(4, 20).
has_out_port(5, 23).
has_out_port(6, 28).
has_out_port(6, 29).
has_out_port(6, 30).
has_out_port(6, 31).
has_out_port(7, 37).
has_out_port(7, 38).
has_out_port(7, 39).
has_out_port(7, 40).
has_out_port(8, 47).

% FACT: channel(channel_id, binding).
channel(1, 'corrected_image').
channel(2, 'run_log').
channel(3, 'collection_log').
channel(4, 'rejection_log').
channel(5, 'cassette_id').
channel(6, 'cassette_id').
channel(7, 'cassette_id').
channel(8, 'cassette_id').
channel(9, 'sample_spreadsheet').
channel(10, 'sample_score_cutoff').
channel(11, 'sample_name').
channel(12, 'sample_quality').
channel(13, 'rejected_sample').
channel(14, 'accepted_sample').
channel(15, 'num_images').
channel(16, 'energies').
channel(17, 'sample_id').
channel(18, 'sample_id').
channel(19, 'energy').
channel(20, 'frame_number').
channel(21, 'frame_number').
channel(22, 'raw_image').
channel(23, 'calibration_image').
channel(24, 'corrected_image_path').
channel(25, 'total_intensity').
channel(26, 'pixel_count').

% FACT: port_connects_to_channel(port_id, channel_id).
port_connects_to_channel(37, 1).
port_connects_to_channel(5, 1).
port_connects_to_channel(9, 2).
port_connects_to_channel(6, 2).
port_connects_to_channel(47, 3).
port_connects_to_channel(7, 3).
port_connects_to_channel(23, 4).
port_connects_to_channel(8, 4).
port_connects_to_channel(1, 5).
port_connects_to_channel(10, 5).
port_connects_to_channel(1, 6).
port_connects_to_channel(21, 6).
port_connects_to_channel(1, 7).
port_connects_to_channel(24, 7).
port_connects_to_channel(1, 8).
port_connects_to_channel(41, 8).
port_connects_to_channel(3, 9).
port_connects_to_channel(11, 9).
port_connects_to_channel(2, 10).
port_connects_to_channel(14, 10).
port_connects_to_channel(12, 11).
port_connects_to_channel(15, 11).
port_connects_to_channel(13, 12).
port_connects_to_channel(16, 12).
port_connects_to_channel(18, 13).
port_connects_to_channel(22, 13).
port_connects_to_channel(17, 14).
port_connects_to_channel(25, 14).
port_connects_to_channel(19, 15).
port_connects_to_channel(26, 15).
port_connects_to_channel(20, 16).
port_connects_to_channel(27, 16).
port_connects_to_channel(28, 17).
port_connects_to_channel(32, 17).
port_connects_to_channel(28, 18).
port_connects_to_channel(42, 18).
port_connects_to_channel(29, 19).
port_connects_to_channel(33, 19).
port_connects_to_channel(30, 20).
port_connects_to_channel(34, 20).
port_connects_to_channel(30, 21).
port_connects_to_channel(43, 21).
port_connects_to_channel(31, 22).
port_connects_to_channel(35, 22).
port_connects_to_channel(4, 23).
port_connects_to_channel(36, 23).
port_connects_to_channel(38, 24).
port_connects_to_channel(44, 24).
port_connects_to_channel(39, 25).
port_connects_to_channel(45, 25).
port_connects_to_channel(40, 26).
port_connects_to_channel(46, 26).

% FACT: uri_variable(uri_variable_id, variable_name, port_id).
uri_variable(1, 'cassette_id', 3).
uri_variable(2, 'cassette_id', 11).
uri_variable(3, 'cassette_id', 31).
uri_variable(4, 'frame_number', 31).
uri_variable(5, 'sample_id', 31).
uri_variable(6, 'energy', 31).
uri_variable(7, 'frame_number', 37).
uri_variable(8, 'sample_id', 37).
uri_variable(9, 'energy', 37).
