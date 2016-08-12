"""Workflow for collecting diffraction data from high quality crystals in a cassette."""

import csv
import sys
import math
import optparse
import os
import time
from datetime import datetime

"""
@begin simulate_data_collection @desc Workflow for collecting diffraction data from high quality crystals in a cassette.
@param cassette_id @desc The unique ID of the cassette containing the crystals.
@param sample_score_cutoff @desc The minimum quality score required of crystals.
@param data_redundancy @desc The desired redundancy of the data sets collected.
@in sample_spreadsheet @desc CSV file giving quality score for each crystal.
@in calibration_image_file @as calibration_image @desc File used to correct raw diffraction images.
@out corrected_image @desc The corrected diffraction images collected on all crystals.
@out run_log
@out collection_log
@out rejection_log
"""

def simulate_data_collection(cassette_id, sample_score_cutoff, data_redundancy, calibration_image_file):

    """
    @begin initialize_run @desc Create run directory and initialize log files.
    @param cassette_id
    @param sample_score_cutoff
    @out run_log @uri file:run/run_log.txt
                 @log {timestamp} Processing samples in cassette {cassette_id}
                 @log Sample quality cutoff: {sample_score_cutoff}
    """
    if not os.path.exists('run'):
        os.makedirs('run')
    for filepath in ['run/run_log.txt', 'run/collected_images.csv', 'run/rejected_samples.txt']:
        if os.path.exists(filepath):
            os.remove(filepath)

    with run_logger(log_file_name="run/run_log.txt") as run_log:
        run_log.write("Processing samples in cassette " + cassette_id)
        run_log.write("Sample quality cutoff: " + str(sample_score_cutoff))
        """
        @end initialize_run
        """

        """
        @begin load_screening_results @desc Load sample information from spreadsheet.
        @param cassette_id
        @in sample_spreadsheet_file @as sample_spreadsheet @uri file:cassette_{cassette_id}_spreadsheet.csv
        @out sample_name @out sample_quality
        @out run_log @uri file:run/run_log.txt
                     @log {timestamp} Sample {sample_id} had score of {sample_quality}
        """
        sample_spreadsheet_file = 'cassette_{0}_spreadsheet.csv'.format(cassette_id)
        for sample_name, sample_quality in spreadsheet_rows(sample_spreadsheet_file):
            run_log.write("Sample {0} had score of {1}".format(sample_name, sample_quality))
            """
            @end load_screening_results
            """

            """
            @begin calculate_strategy @desc Reject unsuitable crystals and compute \n best data sets to collect for accepted crystals.
            @param sample_score_cutoff @param data_redundancy @param sample_name @param sample_quality
            @out accepted_sample @out rejected_sample @out num_images @out energies
            """
            accepted_sample, rejected_sample, num_images, energies = calculate_strategy(sample_name, sample_quality, sample_score_cutoff, data_redundancy)
            """
            @end calculate_strategy
            """

            """
            @begin log_rejected_sample @desc Record which samples were rejected.
            @param cassette_id  @param rejected_sample
			@out rejection_log @uri file:run/rejected_samples.txt @log Rejected sample {rejected_sample} in cassette {cassette_id}"""
            if (rejected_sample is not None):
                run_log.write("Rejected sample {0}".format(rejected_sample))
                with open('run/rejected_samples.txt', 'at') as rejection_log:
                    rejection_log.write("Rejected sample {0} in cassette {1}\n".format(rejected_sample, cassette_id))
                continue
            """
            @end log_rejected_sample
            """

            """
            @begin collect_data_set @desc Collect data set using the given data collection parameters.
            @param cassette_id  @param num_images  @param accepted_sample @param energies
            @out sample_id @desc The crystal that the diffraction image was collected from.
            @out energy @desc Energy (in eV) at which the diffraction image was collected.
            @out frame_number @desc Index of diffraction image within data set.
            @out raw_image_file @as raw_image @desc Path of file storing the raw diffraction image.
                @uri file:run/raw/{cassette_id}/{sample_id}/e{energy}/image_{frame_number}.raw
            @out run_log @uri file:run/run_log.txt
                         @log {timestamp} Collecting data set for sample {sample_id}
                         @log {timestamp} Collecting image {raw_image_path}

            """
            run_log.write("Collecting data set for sample {0}".format(accepted_sample))
            sample_id = accepted_sample
            for energy, frame_number, intensity, raw_image_file in collect_next_image(cassette_id, sample_id, num_images, energies, 'run/raw/{cassette_id}/{sample_id}/e{energy}/image_{frame_number:03d}.raw'):
                run_log.write("Collecting image {0}".format(raw_image_file))
                """
                @end collect_data_set
                """

                """
                @begin transform_images @desc Correct raw image using the detector calibration image.
                @param sample_id  @param energy  @param frame_number
                @in raw_image_file @as raw_image
                @in calibration_image_file @as calibration_image
                @out corrected_image_file @as corrected_image @uri file:run/data/{sample_id}/{sample_id}_{energy}eV_{frame_number}.img
                @out total_intensity  @out pixel_count
                @out run_log @uri file:run/run_log.txt
                             @log {timestamp} Wrote transformed image {corrected_image_path}
                """
                (total_intensity, pixel_count, corrected_image_file) = transform_image(raw_image_file, 'run/data/{0}/{0}_{1}eV_{2:03d}.img'.format(sample_id, energy, frame_number), calibration_image_file)
                run_log.write("Wrote transformed image {0}".format(corrected_image_file))
                """
                @end transform_images
                """

                """
                @begin log_average_image_intensity @desc Record statistics about each diffraction image.
                @param cassette_id @param sample_id @param frame_number @param total_intensity @param pixel_count
                @in corrected_image_file @AS corrected_image
                @out collection_log @uri file:run/collected_images.csv
                @log {cassette_id},{sample_id},{energy},{average_intensity},{corrected_image_path}

                """
                average_intensity = float(total_intensity) / float(pixel_count)
                with open('run/collected_images.csv', 'at') as collection_log_file:
                    collection_log = csv.writer(collection_log_file, lineterminator='\n')
                    collection_log.writerow([cassette_id, sample_id, energy, average_intensity, corrected_image_file])
                """
                @end log_average_image_intensity
                """

"""
@end simulate_data_collection
"""

"""
@begin calculate_strategy
@param sample_name
@param sample_quality
@param sample_score_cutoff
@param data_redundancy
@return accepted_sample
@return rejected_sample
@return num_images
@return energies
"""
def calculate_strategy(sample_name, sample_quality, sample_score_cutoff, data_redundancy):
    if sample_quality >= sample_score_cutoff:
        accepted_sample = sample_name
        rejected_sample = None
        num_images = int(sample_quality * data_redundancy) + 2
        num_energies = 1 + int(sample_quality/sample_score_cutoff) if sample_score_cutoff > 0 else 5
        energies = [10000, 11000, 12000, 13000, 14000][0:num_energies-1]
    else:
        accepted_sample = None
        rejected_sample = sample_name
        num_images = 0
        energies = []
    return accepted_sample, rejected_sample, num_images, energies
"""
@end calculate_strategy
"""

"""
@begin collect_next_image
@param cassette_id
@param sample_id
@param num_images
@param energies
@param image_path_template
@return energy
@return frame_number
@return intensity
@return raw_image_path
"""
def collect_next_image(cassette_id, sample_id, num_images, energies, image_path_template):
    for energy in energies:
        for frame_number in range(1, num_images + 1):
            raw_image_path = image_path_template.format(cassette_id=cassette_id, sample_id=sample_id, energy=energy, frame_number=frame_number)
            with new_image_file(raw_image_path) as raw_image:
                intensity = int(math.floor(math.floor(energy / (frame_number + 1)) % math.sqrt(energy)))
                raw_image.write_values(10 * [intensity])
            yield energy, frame_number, intensity, raw_image_path
"""
@end collect_next_image
"""

"""
@begin transform_image
@param raw_image_path
@param corrected_image_path
@param calibration_image_path
@return total_intensity
@return pixel_count
@return corrected_image_path
"""
def transform_image(raw_image_path, corrected_image_path, calibration_image_path):

    with open(raw_image_path, 'rt') as raw_image, open(calibration_image_path, 'rt') as calibration_image, new_image_file(corrected_image_path) as corrected_image:

        pixel_count = 0
        total_intensity = 0
        for line in raw_image:
            raw_value = int(line)
            correction = int(calibration_image.readline())
            adjusted_value = raw_value - correction
            corrected_value = adjusted_value if adjusted_value >= 0 else 0
            corrected_image.write(corrected_value)
            total_intensity += corrected_value
            pixel_count += 1

    return total_intensity, pixel_count, corrected_image_path
"""
@end transform_image
"""

def spreadsheet_rows(spreadsheet_file_name):
    with open(spreadsheet_file_name, 'rt') as screening_results:
        sample_results = csv.DictReader(screening_results)
        for sample in sample_results:
            yield sample['id'], int(sample['score'])

class run_logger:

    def __init__(self, terminal=sys.stdout, log_file_name=None):
        self.log_file = open(log_file_name, 'wt') if log_file_name is not None else None
        self.terminal = terminal

    def __enter__(self):
        return self

    def write(self, message):
        current_time = time.time()
        timestamp = datetime.fromtimestamp(current_time).strftime('%Y-%m-%d %H:%M:%S')
        log_message = "{0} {1}\n".format(timestamp, message)
        for log in (self.log_file, self.terminal):
            if (log is not None):
                log.write(log_message)

    def __exit__(self, type, value, traceback):
        if self.log_file is not None:
            self.log_file.close()

class new_image_file:

    def __init__(self, image_path):
        image_dir = os.path.dirname(image_path)
        if not os.path.isdir(image_dir):
            os.makedirs(image_dir)
        self.image_file = open(image_path, 'wt')

    def __enter__(self):
        return self

    def write(self, value):
        self.image_file.write(str(value))
        self.image_file.write('\n')

    def write_values(self, values):
        for value in values:
            self.write(value)

    def name(self):
        return self.image_file.name

    def __exit__(self, type, value, traceback):
        self.image_file.close()


if __name__ == '__main__':

    # define command line options
    parser = optparse.OptionParser()
    cassette_id = None
    parser.add_option("-o", "--cutoff",
                      type='float',
                      dest="sample_score_cutoff",
                      help="Minimum quality score required of crystals (default=0)",
                      default=0)
    parser.add_option("-r", "--redundancy",
                      type='float',
                      dest="data_redundancy",
                      help='The desired redundancy of the data sets collected (default=1)',
                      default=1)
    parser.add_option("-c", "--calibration",
                      type='string',
                      dest="calibration_file",
                      help='Calibration file for transforming raw images (default=calibration.img)',
                      default='calibration.img')

    parser.set_usage("python simulate_data_collection.py <cassette_id> [options]")

    # parse command line options
    (options, args) = parser.parse_args()

    # validate options
    if len(args) != 1:
        print("\n***** ERROR: Required argument cassette_id was not provided *****\n")
        parser.print_help()
        exit()

    # run the simulation using the provided options
    simulate_data_collection(args[0], options.sample_score_cutoff, options.data_redundancy, options.calibration_file)
