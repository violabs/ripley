package io.violabs.ripley.domain

import io.violabs.ripley.pipeline.HubKwargs
import io.violabs.ripley.pipeline.ModelKwargs
import kotlin.reflect.KClass

interface IPipeline

interface ITask {
    val name: String?
    var pytorch: List<String>?
    var tensorflow: List<String>?
    var default: Map<Any, Map<FrameworkName, IModelBuilderOption>>?
}

interface IPreTrainedConfig {
    val taskSpecificParams: Map<String, ITask>?
    val customPipelines: Map<String, *>?
    fun update(task: ITask)
}

interface IGenerationConfig {
    fun update(task: ITask)
}

interface IModelBuilderOption {
    var defaultRevision: String?
}

interface IPreTrainedModel : IModelBuilderOption {
    val name: String?
    val config: IPreTrainedConfig?
    val generationConfig: IGenerationConfig?
    fun eval(): IPreTrainedModel
    fun fromPretrained(model: String, modelKwargs: ModelKwargs): IPreTrainedModel
    fun canGenerate(): Boolean
}

interface IPreTrainedModelBuilder : IModelBuilderOption {
    val model: Map<FrameworkName, IPreTrainedModel>
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
    var hfDeviceMap: Map<String, IDevice>?
    fun addDevice(device: IDevice?)
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

interface IDeviceInferenceService {
    fun inferDeviceBelongsInModel(framework: FrameworkName, inputDevice: IDevice?): IDevice?
    fun <T : AvailableModel> inferDevice(framework: FrameworkName, model: T, inputDevice: IDevice?): IDevice?
}

interface IImageProcessorInferenceService {
    fun inferImageProcessor(framework: FrameworkName, model: IPreTrainedModel, imageProcessor: IBaseImageProcessor?): IBaseImageProcessor?
}

interface IToken {
    val text: String?
    val use: Boolean?
}

interface IPath

interface IConfigLoader {
    //    if isinstance(config, str):
    //        config = AutoConfig.from_pretrained(config, _from_pipeline=task, **hub_kwargs, **model_kwargs)
    //        hub_kwargs["_commit_hash"] = config._commit_hash
    //    elif config is None and isinstance(model, str):
    //        config = AutoConfig.from_pretrained(model, _from_pipeline=task, **hub_kwargs, **model_kwargs)
    //        hub_kwargs["_commit_hash"] = config._commit_hash
    // line 706 of pipelines/__init__.py
    fun loadConfig(hubKwargs: HubKwargs, config: Any?): IPreTrainedConfig?
    //        if config is None and isinstance(model, str):
    //            config = AutoConfig.from_pretrained(model, _from_pipeline=task, **hub_kwargs, **model_kwargs)
    //            hub_kwargs["_commit_hash"] = config._commit_hash
    // line 763 of pipelines/__init__.py
    fun determineFromModel(hubKwargs: HubKwargs, config: IPreTrainedConfig?, model: Any?): IPreTrainedConfig?
}

interface IEnvConfig {
    fun isOfflineMode(): Boolean
}

interface IModelInfo {
    val pipelineTag: String?
    val libraryName: LibraryName?
}

interface IHuggingFaceModelClient {
    fun modelInfo(
        repoId: String,
        revision: String? = null,
        timeout: Float? = null,
        securityStatus: Boolean? = null,
        filesMetadata: Boolean = false,
        token: IToken? = null,
    ): IModelInfo
}

interface IAcceptedTask : ITask {
    fun impl(): String
}

interface ITransformers {
    fun getClassNameByExternalName(name: String): String
}

interface IDynamicModuleLoader {
    fun loadClassFromDynamicModule(
        classReference: String,
        pretrainedModelNameOrPath: PathLike,
        revision: String? = null,
        token: IToken? = null
    )
}

interface IPipelineRegistry {
    fun checkTask(task: ITask): ITaskGroup
}

typealias PathLike = String

interface ITaskGroup {
    var normalizedTask: ITask?
    var targetedTask: IAcceptedTask?
    var taskOptions: Any?
}