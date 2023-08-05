package io.violabs.ripley.pipeline

import io.violabs.ripley.domain.IBaseImageProcessor
import io.violabs.ripley.domain.IPreTrainedFeatureExtractor
import io.violabs.ripley.testSuite.testEquals
import org.junit.jupiter.api.Test

class ImageProcessorInferenceServiceTest {
    private val imageProcessorInferenceService = ImageProcessorInferenceService()

    @Test
    fun inferWithFallback_will_return_null_if_both_inputs_are_null() {
        val result = imageProcessorInferenceService.inferWithFallBack(null, null)
        testEquals(null, result)
    }

    @Test
    fun inferWithFallback_will_return_null_if_image_processor_null_and_feature_extractor_not_image_processor() {
        val featureExtractor = object : IPreTrainedFeatureExtractor {}

        val result = imageProcessorInferenceService.inferWithFallBack(null, featureExtractor)
        testEquals(null, result)
    }

    @Test
    fun inferWithFallback_will_return_feature_extracted_image_processor() {
        val featureExtractor = object : IPreTrainedFeatureExtractor, IBaseImageProcessor {}

        val result = imageProcessorInferenceService.inferWithFallBack(null, featureExtractor)
        testEquals(featureExtractor, result)
    }

    @Test
    fun inferWithFallback_will_return_image_processor() {
        val imageProcessor = object : IBaseImageProcessor {}

        val result = imageProcessorInferenceService.inferWithFallBack(imageProcessor, null)
        testEquals(imageProcessor, result)
    }
}