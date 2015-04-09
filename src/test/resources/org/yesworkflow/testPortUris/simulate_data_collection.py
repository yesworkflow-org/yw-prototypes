import csv
import sys
import math
import os

"""
@begin simulate_data_collection
@param cassette_id 
@param sample_score_cutoff
@in spreadsheet_file 
@in calibration_image
@out corrected_image 
@out collection_log 
@out rejection_log
"""

def simulate_data_collection(cassette_id, sample_score_cutoff):

    print "Processing samples in cassette " + cassette_id
    print "Sample quality cutoff:" + str(sample_score_cutoff)

    with open('rejectedSamples.txt', 'wt') as rejection_log, \
         open('collectedImages.txt', 'wt') as collection_log_file:
        collection_log = csv.writer(collection_log_file)
        collection_log.writerow(['cassette', 'sample', 'energy', 'frame', 'file'])

        """
        @begin load_screening_results
        @param cassette_id
        @in spreadsheet_file @uri file:cassette_{cassette_id}_spreadsheet.csv
        @out sample_name 
        @out sample_quality
        """
        spreadsheet_file = 'cassette_{0}_spreadsheet.csv'.format(cassette_id)
        for sample_name, sample_quality in spreadsheet_rows(spreadsheet_file):
            print "Sample {0} had score of {1}".format(sample_name, sample_quality)
            """ @end load_screening_results """

            """
            @begin calculate_strategy
            @param sample_score_cutoff
            @in sample_name 
            @in sample_quality
            @out accepted_sample 
            @out rejected_sample 
            @out num_images 
            @out energies
            """
            if sample_quality >= sample_score_cutoff: 
                accepted_sample = sample_name
                rejected_sample = None
                num_images = sample_quality + 2
                energies = [10000, 11000, 12000, 13000, 14000][:sample_quality/sample_score_cutoff]
            else:
                accepted_sample = None
                rejected_sample = sample_name            
            """ @end calculate_strategy """
        
            """
            @begin log_rejected_sample 
            @param cassette_id 
            @param rejected_sample
            @out rejection_log 
            @uri file:/{cassette_id}/rejectedSamples.txt
            """
            if (rejected_sample is not None):
                rejection_log.write("Rejected sample {0} in cassette {1}\n"
                                    .format(rejected_sample, cassette_id))
                continue
            """ @end log_rejected_sample """
    
            """ 
            @begin collect_data_set
            @param accepted_sample 
            @param num_images 
            @param energies
            @out sample_id 
            @out energy 
            @out frame_number
            @out raw_image @uri file:raw/{sample_id}/e_{energy}/image_{frame_number}.raw            
            """
            sample_id = accepted_sample
            for energy, frame_number, intensity in collect_next_frame(num_images, energies):

                raw_image_directory = 'raw/{0}/e_{1}/'.format(sample_id, energy)
                raw_image_name = 'image_{0}.raw'.format(frame_number)
                with new_image_file(raw_image_directory, raw_image_name) as raw_image:
                    raw_image.write_values(10 * [intensity])
                    """ @end collect_data_set """
        
                """
                @begin transform_image
                @param sample_id 
                @param energy 
                @param frame_number
                @in calibration_image @uri file:calibration.img 
                @in raw_image
                @out corrected_image @uri file:data/{sample_id}/image_{energy}_{frame_number}.img
                """
                corrected_image_directory = 'data/{0}'.format(sample_id)
                corrected_image_name = 'image_{0}_{1}.img'.format(energy, frame_number)
                with open(raw_image_directory + "/" + raw_image_name, 'rt') as raw_image, \
                     open("calibration.img", 'rt') as calibration_image, \
                     new_image_file(corrected_image_directory, corrected_image_name) as corrected_image:
                    
                    for line in raw_image:
                        raw_value = int(line)
                        correction = int(calibration_image.readline())
                        corrected_value = raw_value - correction    
                        corrected_image.write(corrected_value)
                """ @end transform_image """

                """
                @begin log_average_image_intensity
                @param cassette_id 
                @param sample_id 
                @param frame_number
                @in corrected_image
                @out collection_log 
                @uri file:/{cassette_id}/collectedImages.txt
                """
                collection_log.writerow([cassette_id, sample_id, energy, frame_number, corrected_image.name()])
                """ @end log_average_image_intensity """
    
"""
@end simulate_data_collection    
"""

def collect_next_frame(num_images, energies):
    for energy in energies:
        for frame_number in range(num_images):
            intensity = int((energy / (frame_number + 1)) % math.sqrt(energy))
            yield energy, frame_number, intensity

def spreadsheet_rows(spreadsheet_file_name):
    with open(spreadsheet_file_name, 'rt') as screening_results:
        sample_results = csv.DictReader(screening_results)
        for sample in sample_results:
            yield sample['id'], int(sample['score'])

class new_image_file:
    
    def __init__(self, directory_name, file_name):
        if not os.path.isdir(directory_name):
            os.makedirs(directory_name)
        self.image_file = open(directory_name + "/" + file_name, 'wt')
        
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
    cassette_id = sys.argv[1]
    sample_score_cutoff = int(sys.argv[2])
    simulate_data_collection(cassette_id, sample_score_cutoff)
    