package io.violabs.ripley.pipeline

import io.violabs.ripley.common.iff
import io.violabs.ripley.domain.*
import mu.two.KotlinLogging
import kotlin.reflect.KClass

class PipelineBuilder(
    private val deviceInferenceService: DeviceInferenceService,
    private val frameworkModelInferenceEngine: IFrameworkModelInferenceEngine,
    private val tensorFlowConfig: ITensorFlowConfig,
) {

    fun build(): IPipeline {
        return object : IPipeline {}
    }

    fun <T> buildRefactor(
        clazz: KClass<T>,
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
        val (framework, model) = frameworkModelInferenceEngine.inferFrameworkLoadModel(clazz, inputModel)

        val foundDevice: IDevice? = deviceInferenceService.inferDevice(framework, model, inputDevice)

        deviceInferenceService
            .inferDeviceBelongsInModel(framework, inputDevice)
            ?.also { model.addDevice(inputDevice) }

        // todo maybe move this out into model configuration?
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
