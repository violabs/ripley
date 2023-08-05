package io.violabs.ripley.pipeline

import io.violabs.ripley.common.iff
import io.violabs.ripley.domain.IBaseImageProcessor
import io.violabs.ripley.domain.IPreTrainedFeatureExtractor

class ImageProcessorInferenceService {
    fun inferWithFallBack(
        imageProcessor: IBaseImageProcessor? = null,
        featureExtractor: IPreTrainedFeatureExtractor? = null
    ): IBaseImageProcessor? {
        var processor = imageProcessor

        iff(imageProcessor == null)
            .and { featureExtractor != null }
            .and { featureExtractor is IBaseImageProcessor }
            .then { processor = featureExtractor as IBaseImageProcessor }

        return processor
    }
}