package io.violabs.ripley.pipeline

import io.violabs.ripley.common.iff
import io.violabs.ripley.domain.*
import mu.two.KotlinLogging


data class ModelKwargs(
    val token: String? = null,
    var fromPipeline: String? = null,
    var fromTensorFlow: Boolean = false,
    var fromPyTorch: Boolean = false,
)

class HubKwargs(
    val revision: String? = null,
    val token: String? = null,
    val trustRemoteCode: Boolean = false,
    val commitHash: String? = null,
)

class Pipeline<T>(
    var model: T,
    var framework: FrameworkName,
    val tokenizer: IPreTrainedTokenizer? = null,
    val featureExtractor: IPreTrainedFeatureExtractor? = null,
    val imageProcessor: IBaseImageProcessor? = null,
    val modelCard: IModelCard? = null,
    val task: String? = "",
    val device: IDevice? = null,
    val torchDataType: ITorchDataType? = null,
    val binaryOutput: Boolean = false,
) : IScikitCompatible where T : AvailableModel, T : IPreTrainedModel {
    var defaultInputNames: String? = null

    init {
        iff(framework != FrameworkName.PYTORCH)
            .or(device == null)
            .or(device!!.num != null && device.num!! >= 0)
            .then { model.to(device) }
    }

    override fun transform(x: Any?) {
        TODO("Not yet implemented")
    }

    override fun predict(x: Any?) {
        TODO("Not yet implemented")
    }

    companion object {
        val LOGGER = KotlinLogging.logger("Pipeline")
    }
}