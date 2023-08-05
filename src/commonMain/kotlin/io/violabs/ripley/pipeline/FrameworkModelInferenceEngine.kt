package io.violabs.ripley.pipeline

import io.violabs.ripley.common.safe
import io.violabs.ripley.domain.*
import kotlin.reflect.KClass
import kotlin.reflect.cast

class FrameworkModelInferenceEngine(
    private val frameworkInferenceEngine: IFrameworkInferenceEngine,
    private val frameworkValidator: IFrameworkValidator,
    private val modelInferenceEngine: IModelInferenceEngine,
) : IFrameworkModelInferenceEngine {
    override fun <T : IPreTrainedModel> inferFrameworkLoadModel(
        clazz: KClass<T>,
        model: Any,
        modelClasses: Map<FrameworkName, () -> T>?,
        task: String?,
        framework: FrameworkName?,
        modelKwargs: ModelKwargs
    ): Pair<FrameworkName, T> {
        return when {
            model is String -> inferFrameworkLoadModelByName(clazz, model, modelClasses, task.safe, framework, modelKwargs)
            clazz.isInstance(model) -> inferFrameworkWithModel(clazz.cast(model), framework)
            else -> throw Exception("Cannot infer framework from $model")
        }
    }

    private fun <T : IPreTrainedModel> inferFrameworkWithModel(
        model: T,
        framework: FrameworkName? = null
    ): Pair<FrameworkName, T> {
        frameworkValidator.requireActiveFramework()

        val foundFramework = frameworkInferenceEngine.inferFramework(model, framework)

        return Pair(foundFramework, model)
    }

    private fun <T : IPreTrainedModel> inferFrameworkLoadModelByName(
        clazz: KClass<T>,
        model: String,
        modelClassBuilders: Map<FrameworkName, () -> T>? = null,
        task: String = "",
        framework: FrameworkName? = null,
        modelKwargs: ModelKwargs = ModelKwargs()
    ): Pair<FrameworkName, T> {
        frameworkValidator.requireActiveFramework()

        modelKwargs.fromPipeline = task

        val evaluatedModel: T = modelInferenceEngine.inferModel(
            clazz,
            model,
            modelClassBuilders,
            framework,
            modelKwargs
        )

        val foundFramework = frameworkInferenceEngine.inferFramework(evaluatedModel, framework)

        return Pair(foundFramework, evaluatedModel)
    }
}