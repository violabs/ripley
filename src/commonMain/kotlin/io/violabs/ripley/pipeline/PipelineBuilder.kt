package io.violabs.ripley.pipeline

import io.violabs.ripley.common.iff
import io.violabs.ripley.domain.*
import mu.two.KotlinLogging

class PipelineBuilder(
    val frameworkModelInferenceEngine: FrameworkModelInferenceEngine,
    val tensorFlowConfig: ITensorFlowConfig,
) {

    fun build(): IPipeline {
        return object : IPipeline {}
    }

    inline fun <reified T> buildRefactor(
        inputModel: T,
        inputDevice: IDevice? = null,
        task: String = "",
        featureExtractor: IPreTrainedFeatureExtractor? = null,
        imageProcessor: IBaseImageProcessor? = null,
        tokenizer: IPreTrainedTokenizer? = null,
        modelCard: IModelCard? = null,
        torchDataType: ITorchDataType? = null,
        binaryOutput: Boolean = false,
        modelKwargs: ModelKwargs? = null
    ): Pipeline<T> where T : AvailableModel, T : IPreTrainedModel {
        val (framework, model) = frameworkModelInferenceEngine.inferFrameworkLoadModel<T>(inputModel)

        var foundDevice: IDevice = inputDevice ?: model.hfDeviceMap?.values?.firstOrNull() ?: Device.Default

        iff(framework != FrameworkName.PYTORCH)
            .or { inputDevice == null }
            .or { inputDevice!!.num != null && inputDevice.num!! >= 0 }
            .then { model.to(inputDevice) }

        if (tensorFlowConfig.isTfAvailable && framework == FrameworkName.PYTORCH) {
            foundDevice = if (foundDevice is ITorchDevice) foundDevice
                     else if (foundDevice.name?.isNotBlank() == true) TorchDevice(foundDevice)
                     else if (foundDevice.num != null && foundDevice.num!! < 0) TorchDevice("cpu")
                        else TorchDevice("cuda:${foundDevice.num}")
        }

        model
            .config
            ?.taskSpecificParams
            ?.get(task)
            ?.also {
                model.config?.update(it)
                if (model.canGenerate()) model.generationConfig?.update(it)
            }

        val foundImageProcessor: IBaseImageProcessor? =
            iff(imageProcessor == null)
                .and { featureExtractor != null }
                .and { featureExtractor is IBaseImageProcessor }
                .thenReturn(featureExtractor as IBaseImageProcessor, elseReturn = imageProcessor)

        return Pipeline(
            model = model,
            framework = framework,
            tokenizer = tokenizer,
            featureExtractor = featureExtractor,
            imageProcessor = foundImageProcessor,
            modelCard = modelCard,
            task = task,
            device = foundDevice,
            torchDataType = torchDataType,
            binaryOutput = binaryOutput,
            kwargs = modelKwargs
        )
    }

    companion object {
        val LOGGER = KotlinLogging.logger("PipelineBuilder")
    }
}
