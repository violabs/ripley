package io.violabs.ripley.pipeline

import io.violabs.ripley.domain.*

class FrameworkModelInferenceEngine(
    private val tensorFlowConfig: ITensorFlowConfig,
    private val pyTorchConfig: IPyTorchConfig,
) {
    inline fun <reified T : IPreTrainedModel> inferFrameworkLoadModel(
        model: Any,
        modelClasses: Map<FrameworkName, () -> T>? = null,
        task: String? = null,
        framework: FrameworkName? = null,
        modelKwargs: ModelKwargs = ModelKwargs()
    ): Pair<FrameworkName, T> {
        return when (model) {
            is String -> inferFrameworkLoadModelByName(model, modelClasses, task, framework, modelKwargs)
            is T -> inferFrameworkWithModel<T>(model, framework)
            else -> throw Exception("Cannot infer framework from $model")
        }
    }

    inline fun <reified T : IPreTrainedModel> inferFrameworkWithModel(
        model: T,
        framework: FrameworkName? = null
    ): Pair<FrameworkName, T> {
        requireActiveFramework()

        val foundFramework = framework ?: inferFramework(model)

        return Pair(foundFramework, model)
    }

    inline fun <reified T : IPreTrainedModel> inferFrameworkLoadModelByName(
        model: String,
        modelClassBuilders: Map<FrameworkName, () -> T>? = null,
        task: String? = null,
        framework: FrameworkName? = null,
        modelKwargs: ModelKwargs = ModelKwargs()
    ): Pair<FrameworkName, T> {
        requireActiveFramework()

        modelKwargs.fromPipeline = task

        val modelClass: T = determineModelClass(model, modelClassBuilders, framework)

        val newKwargs = modelKwargs.copy(
            fromPyTorch = modelClass is IPyTorchModel && model.endsWith(".h5"),
            fromTensorFlow = modelClass is ITensorFlowModel && model.endsWith(".bin")
        )

        val determinedModel: T = try {
            modelClass
                .fromPretrained(model, newKwargs)
                .eval()
                .let {
                    if (it is T) { it }
                    else { throw Exception("Cannot cast $it to ${T::class}") }
                }
        } catch (e: Exception) {
            PipelineBuilder.LOGGER.error(e.message)
            throw Exception("Cannot load model $model")
        }

        val foundFramework = framework ?: inferFramework(determinedModel)

        return Pair(foundFramework, determinedModel)
    }

    fun <T : IPreTrainedModel> determineModelClass(
        model: String,
        modelClassBuilders: Map<FrameworkName, () -> T>? = null,
        framework: FrameworkName? = null
    ): T {
        val modelClassBuilder: (() -> T)? = if (pyTorchConfig.isTorchAvailable and (framework in listOf(FrameworkName.PYTORCH, null))) {
            modelClassBuilders?.get(FrameworkName.PYTORCH)
        } else if (tensorFlowConfig.isTfAvailable and (framework in listOf(FrameworkName.TENSORFLOW, null))) {
            modelClassBuilders?.get(FrameworkName.TENSORFLOW)
        } else {
            null
        }

        return modelClassBuilder?.invoke() ?: throw Exception("Cannot infer suitable model classes from $model")
    }

    fun <T : IPreTrainedModel> inferFramework(modelClass: T) = when (modelClass) {
        is IPyTorchModel -> FrameworkName.PYTORCH
        is ITensorFlowModel -> FrameworkName.TENSORFLOW
        is IFlaxModel -> FrameworkName.FLAX
        else -> throw Exception("Cannot infer framework from $modelClass")
    }

    fun requireActiveFramework() {
        if (!tensorFlowConfig.isTfAvailable && !pyTorchConfig.isTorchAvailable) {
            throw Exception(
                """
                At least one of TensorFlow 2.0 or PyTorch should be installed. 
                To install TensorFlow 2.0, read the instructions at https://www.tensorflow.org/install/.
                To install PyTorch, read the instructions at https://pytorch.org/.
            """.trimIndent()
            )
        }
    }
}