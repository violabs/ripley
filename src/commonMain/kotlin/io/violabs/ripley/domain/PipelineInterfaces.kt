package io.violabs.ripley.domain

import io.violabs.ripley.pipeline.ModelKwargs
import kotlin.reflect.KClass

interface IPipeline

interface ITask

interface IPreTrainedConfig {
    val taskSpecificParams: Map<String, ITask>?
    fun update(task: ITask)
}

interface IGenerationConfig {
    fun update(task: ITask)
}

interface IPreTrainedModel {
    val config: IPreTrainedConfig?
    val generationConfig: IGenerationConfig?
    fun eval(): IPreTrainedModel
    fun fromPretrained(model: String, modelKwargs: ModelKwargs): IPreTrainedModel
    fun canGenerate(): Boolean
}

interface IPreTrainedTokenizer
interface IPreTrainedFeatureExtractor
interface IBaseImageProcessor

interface IModelCard
interface IDevice {
    val num: Int?
    val name: String?
}
interface ITorchDataType

interface IScikitCompatible {
    fun transform(x: Any?)

    fun predict(x: Any?)
}

interface IAutoConfig

interface AvailableModel {
    val hfDeviceMap: Map<String, IDevice>?
    fun to(device: IDevice)
}

interface ITensorFlowConfig {
    val isTfAvailable: Boolean
}

interface ITensorFlowModel : IPreTrainedModel, AvailableModel

interface IPyTorchConfig {
    val isTorchAvailable: Boolean
}

interface IPyTorchModel : IPreTrainedModel, AvailableModel

interface IFlaxModel : IPreTrainedModel

interface ITorchDevice {
    val name: String?
}

interface IFrameworkModelInferenceEngine {
    fun <T : IPreTrainedModel> inferFrameworkLoadModel(
        clazz: KClass<T>,
        model: Any,
        modelClasses: Map<FrameworkName, () -> T>? = null,
        task: String? = null,
        framework: FrameworkName? = null,
        modelKwargs: ModelKwargs = ModelKwargs()
    ): Pair<FrameworkName, T>
}

interface IFrameworkInferenceEngine {
    fun <T : IPreTrainedModel> inferFramework(model: T, framework: FrameworkName? = null): FrameworkName
}

interface IModelInferenceEngine {
    fun <T : IPreTrainedModel> inferModel(
        clazz: KClass<T>,
        model: String,
        modelClassBuilders: Map<FrameworkName, () -> T>? = null,
        framework: FrameworkName? = null,
        modelKwargs: ModelKwargs
    ): T
}

interface IFrameworkValidator {
    fun requireActiveFramework()
}