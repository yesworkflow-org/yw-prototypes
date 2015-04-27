
% FACT: program(program_id, program_name).
program(1, 'simulate_data_collection').
program(2, 'initialize_run').
program(3, 'load_screening_results').
program(4, 'calculate_strategy').
program(5, 'log_rejected_sample').
program(6, 'collect_data_set').
program(7, 'transform_images').
program(8, 'log_average_image_intensity').
program(9, 'collect_next_image').
program(10, 'transform_image').

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

% FACT: port(port_id, port_type, variable_name).
port(1, 'param', 'cassette_id').
port(2, 'param', 'sample_score_cutoff').
port(3, 'in', 'sample_spreadsheet').
port(4, 'in', 'calibration_image').
port(5, 'out', 'corrected_image').
port(6, 'out', 'run_log').
port(7, 'out', 'collection_log').
port(8, 'out', 'rejection_log').
port(9, 'out', 'run_log').
port(10, 'param', 'cassette_id').
port(11, 'in', 'sample_spreadsheet').
port(12, 'out', 'sample_name').
port(13, 'out', 'sample_quality').
port(14, 'param', 'sample_score_cutoff').
port(15, 'in', 'sample_name').
port(16, 'in', 'sample_quality').
port(17, 'out', 'accepted_sample').
port(18, 'out', 'rejected_sample').
port(19, 'out', 'num_images').
port(20, 'out', 'energies').
port(21, 'param', 'cassette_id').
port(22, 'param', 'rejected_sample').
port(23, 'out', 'rejection_log').
port(24, 'param', 'cassette_id').
port(25, 'param', 'accepted_sample').
port(26, 'param', 'num_images').
port(27, 'param', 'energies').
port(28, 'out', 'sample_id').
port(29, 'out', 'energy').
port(30, 'out', 'frame_number').
port(31, 'out', 'raw_image_path').
port(32, 'param', 'sample_id').
port(33, 'param', 'energy').
port(34, 'param', 'frame_number').
port(35, 'in', 'raw_image_path').
port(36, 'in', 'calibration_image').
port(37, 'out', 'corrected_image').
port(38, 'out', 'corrected_image_path').
port(39, 'out', 'total_intensity').
port(40, 'out', 'pixel_count').
port(41, 'param', 'cassette_id').
port(42, 'param', 'sample_id').
port(43, 'param', 'frame_number').
port(44, 'in', 'total_intensity').
port(45, 'in', 'pixel_count').
port(46, 'in', 'corrected_image_path').
port(47, 'out', 'collection_log').
port(48, 'param', 'cassette_id').
port(49, 'param', 'sample_id').
port(50, 'param', 'num_images').
port(51, 'param', 'energies').
port(52, 'param', 'image_path_template').
port(57, 'param', 'raw_image_path').
port(58, 'param', 'corrected_image_path').
port(59, 'param', 'calibration_image_path').

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
channel(24, 'total_intensity').
channel(25, 'pixel_count').
channel(26, 'corrected_image_path').

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
port_connects_to_channel(39, 24).
port_connects_to_channel(44, 24).
port_connects_to_channel(40, 25).
port_connects_to_channel(45, 25).
port_connects_to_channel(38, 26).
port_connects_to_channel(46, 26).
