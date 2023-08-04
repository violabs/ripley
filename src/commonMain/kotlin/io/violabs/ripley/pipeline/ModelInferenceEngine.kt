package io.violabs.ripley.pipeline

import io.violabs.ripley.domain.*
import kotlin.reflect.KClass
import kotlin.reflect.cast

class ModelInferenceEngine(
    private val pyTorchConfig: IPyTorchConfig,
    private val tensorFlowConfig: ITensorFlowConfig,
) : IModelInferenceEngine {
    override fun <T : IPreTrainedModel> inferModel(
        clazz: KClass<T>,
        model: String,
        modelClassBuilders: Map<FrameworkName, () -> T>?,
        framework: FrameworkName?,
        modelKwargs: ModelKwargs
    ): T {
        val determinedModel: T = determineModelClass(model, modelClassBuilders, framework)

        val newKwargs = modelKwargs.copy(
            fromPyTorch = determinedModel is IPyTorchModel && model.endsWith(".h5"),
            fromTensorFlow = determinedModel is ITensorFlowModel && model.endsWith(".bin")
        )

        return evaluateModel(model, determinedModel, clazz, newKwargs)
    }

    private fun <T : IPreTrainedModel> determineModelClass(
        model: String,
        modelClassBuilders: Map<FrameworkName, () -> T>? = null,
        framework: FrameworkName? = null
    ): T {
        val modelClassBuilder: (() -> T)? =
            if (pyTorchConfig.isTorchAvailable and (framework in listOf(FrameworkName.PYTORCH, null))) {
                modelClassBuilders?.get(FrameworkName.PYTORCH)
            } else if (tensorFlowConfig.isTfAvailable and (framework in listOf(FrameworkName.TENSORFLOW, null))) {
                modelClassBuilders?.get(FrameworkName.TENSORFLOW)
            } else {
                null
            }

        return modelClassBuilder?.invoke() ?: throw Exception("Cannot infer suitable model classes from $model")
    }

    private fun <T : IPreTrainedModel> evaluateModel(
        modelName: String,
        modelClass: T,
        clazz: KClass<T>,
        newModelKwargs: ModelKwargs
    ): T = try {
        modelClass
            .fromPretrained(modelName, newModelKwargs)
            .eval()
            .let {
                if (clazz.isInstance(it)) {
                    clazz.cast(it)
                } else {
                    throw Exception("Cannot cast $it to $clazz")
                }
            }
    } catch (e: Exception) {
        PipelineBuilder.LOGGER.error(e.message)
        throw Exception("Cannot load model $modelName")
    }
}