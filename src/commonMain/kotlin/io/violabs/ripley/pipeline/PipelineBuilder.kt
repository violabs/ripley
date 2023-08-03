package io.violabs.ripley.pipeline

import io.violabs.ripley.domain.*
import mu.two.KotlinLogging

class PipelineBuilder(
    val frameworkModelInferenceEngine: FrameworkModelInferenceEngine
) {

    fun build(): IPipeline {
        return object : IPipeline {}
    }

    inline fun <reified T> buildRefactor(
        inputModel: AvailableModel
    ): Pipeline<T> where T : AvailableModel, T : IPreTrainedModel {
        val (framework, model) = frameworkModelInferenceEngine.inferFrameworkLoadModel<T>(inputModel)

        return Pipeline(
            model = model,
            framework = framework,
            tokenizer = null,
            featureExtractor = null,
            imageProcessor = null,
            modelCard = null,
            task = null,
            device = null,
            torchDataType = null,
            binaryOutput = false
        )
    }



    companion object {
        val LOGGER = KotlinLogging.logger("PipelineBuilder")
    }
}
