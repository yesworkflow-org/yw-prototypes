# @begin SimulateDataCollection
# @in screeningResults
# @out correctedImage
# @out collectionLog
# @out rejectionLog


    # @begin LoadScreeningResults
    # @in screeningResults
    # @out sampleName
    # @out sampleQuality
    # @end LoadScreeningResults

    # @begin CalculateStrategy
    # @param sampleName
    # @param sampleQuality
    # @out acceptedSample
    # @out rejectedSample
    # @out numImages
    # @out energies
    # @end CalculateStrategy

    # @begin CollectDataSet
    # @param acceptedSample
    # @param numImages
    # @param energies
    # @out sampleId
    # @out energy
    # @out frameNumber
    # @out rawImage @uri file:data/{sampleId}/images/raw/{energy}/image_{frameNumber}.raw
    # @end CollectDataSet

    # @begin TransformImage
    # @param sampleId
    # @param energy
    # @param frameNumber
    # @in rawImage
    # @out correctedImage @uri file:data/{sampleId}/image_{energy}_{frameNumber}.img
    # @end TransformImage

    # @begin LogAverageImageIntensity
    # @in correctedImage
    # @param sampleId
    # @param frameNumber
    # @out collectionLog @uri file:/collectedImages.txt
    # @end LogAverageSpotIntensity

    # @begin LogRejectedSamples
    # @in rejectedSample
    # @out rejectionLog @uri file:/rejectedSamples.txt
    # @end LogRejectedSamples


# @end SimulateDataCollection