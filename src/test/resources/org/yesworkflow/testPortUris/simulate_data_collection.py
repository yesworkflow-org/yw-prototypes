# @begin simulate_data_dollection
# @param cassette_id
# @in screening_results
# @in calibration_image
# @out corrected_image
# @out collection_log
# @out rejection_log


    # @begin load_screening_results
    # @param cassette_id
    # @in screening_results @uri file:screening_results_{cassette_id}.csv
    # @out sample_name
    # @out sample_quality
    # @end load_screening_results

    # @begin calculate_strategy
    # @in sample_name
    # @in sample_quality
    # @out accepted_sample
    # @out rejected_sample
    # @out num_images
    # @out energies
    # @end calculate_strategy

    # @begin collect_data_set
    # @param accepted_sample
    # @param num_images
    # @param energies
    # @out sample_id
    # @out energy
    # @out frame_number
    # @out raw_image @uri file:raw/{sample_id}/e_{energy}/image_{frame_number}.raw
    # @end collect_data_set

    # @begin transform_image
    # @param sample_id
    # @param energy
    # @param frame_number
    # @in calibration_image @uri file:calibration.img
    # @in raw_image
    # @out corrected_image @uri file:data/{sample_id}/image_{energy}_{frame_number}.img
    # @end transform_image

    # @begin log_average_image_intensity
    # @param cassette_id
    # @in corrected_image
    # @param sample_id
    # @param frame_number
    # @out collection_log @uri file:/{cassette_id}/collectedImages.txt
    # @end log_average_image_intensity

    # @begin log_rejected_sample
    # @param cassette_id
    # @param rejected_sample
    # @out rejection_log @uri file:/{cassette_id}/rejectedSamples.txt
    # @end log_rejected_sample


# @end simulate_data_dollection