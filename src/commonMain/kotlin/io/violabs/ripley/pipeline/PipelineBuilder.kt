package io.violabs.ripley.pipeline

import io.violabs.ripley.common.parseAsMap
import io.violabs.ripley.domain.*
import mu.two.KotlinLogging
import kotlin.reflect.KClass

class PipelineBuilder(
    private val configLoader: IConfigLoader,
    private val deviceInferenceService: IDeviceInferenceService,
    private val frameworkModelInferenceEngine: IFrameworkModelInferenceEngine,
    private val imageProcessorInferenceService: IImageProcessorInferenceService,
    private val envConfig: IEnvConfig,
    private val huggingFaceModelClient: IHuggingFaceModelClient,
    private val transformers: ITransformers,
    private val dynamicModuleLoader: IDynamicModuleLoader,
    private val pipelineRegistry: IPipelineRegistry,
    private val pyTorchConfig: IPyTorchConfig,
    private val tensorflowConfig: ITensorFlowConfig,
) {

    fun <T> build(
        task: String? = null,
        clazz: KClass<T>,
        inputModel: Any? = null,
        config: Any? = null,
        inputDevice: IDevice? = null,
        featureExtractor: IPreTrainedFeatureExtractor? = null,
        imageProcessor: IBaseImageProcessor? = null,
        framework: FrameworkName? = null,
        tokenizer: IPreTrainedTokenizer? = null,
        revision: String? = null,
        token: IToken? = null,
        trustRemoteCode: Boolean? = null,
        modelCard: IModelCard? = null,
        torchDataType: ITorchDataType? = null,
        binaryOutput: Boolean = false,
        pipelineClass: Any? = null,
        deviceMap: Any? = null,
        modelKwargs: ModelKwargs = ModelKwargs()
    ): IPipeline where T : AvailableModel, T : IPreTrainedModel {
        val hubKwargs = HubKwargs(
            revision = revision,
            token = token,
            trustRemoteCode = trustRemoteCode
        )

        if (task == null && inputModel == null) throw PipelineException.MissingTaskOrModelException
        if (inputModel == null && tokenizer == null) throw PipelineException.MissingTokenizerException
        if (inputModel == null && featureExtractor == null) throw PipelineException.MissingFeatureExtractorException

        val model: Any? = if (inputModel is PathLike) inputModel.toString() else inputModel

        // todo - implement (first part of pipleine and line 763 of pipelines/__init__.py)
        var foundConfig: IPreTrainedConfig? =
            configLoader
                .loadConfig(hubKwargs, config)
                ?: config.takeIf { it is IPreTrainedConfig } as IPreTrainedConfig?

        val (taskName: String?, customTasks) = determineTaskNameAndCustomTasks(foundConfig, task, trustRemoteCode)

        val foundTask = getTask(taskName, model, token)

        val (taskGroup, foundPipelineClass) = processCustomTask(
            foundTask,
            model?.toString() ?: "",
            customTasks,
            pipelineClass,
            trustRemoteCode ?: false
        ) ?: run {
            val group = pipelineRegistry.checkTask(foundTask)
            val foundPipelineClass = pipelineClass ?: group.targetedTask?.impl()

            group to foundPipelineClass
        }

        val foundModel: IPreTrainedModel? = processModel(model, taskGroup, framework, revision)
        val foundRevision: String? = foundModel?.defaultRevision

        foundConfig = configLoader.determineFromModel(hubKwargs, foundConfig, foundModel)

        processDeviceMap(deviceMap, modelKwargs, inputDevice)

        torchDataType?.let {
            modelKwargs.torchDatatype?.let {
                throw PipelineException.TorchDataTypeModelKwargsDefinedException
            }

            modelKwargs.torchDatatype = it
        }

        val modelName = if (model is String?) model else null

        return object : IPipeline {}
    }

    private fun processDeviceMap(deviceMap: Any?, modelKwargs: ModelKwargs, inputDevice: IDevice?) {
        if (deviceMap == null) return

        if (modelKwargs.deviceMap != null) throw PipelineException.DeviceMapModelKwargsDefinedException

        if (inputDevice != null) LOGGER.warn("""
            Both `device` and `device_map` are specified. `device` will override `device_map`. You
            will most likely encounter unexpected behavior. Please remove `device` and keep `device_map`.
        """.trimIndent())

        modelKwargs.deviceMap = deviceMap
    }

    private fun processModel(
        model: Any?,
        taskGroup: ITaskGroup,
        framework: FrameworkName? = null,
        revision: String? = null,
        config: Any? = null
    ): IPreTrainedModel? {
        if (model != null) return null

        val revisedModel: IPreTrainedModel? = getDefaultModelAndRevision(taskGroup, framework)

        revisedModel?.defaultRevision = revision ?: revisedModel?.defaultRevision

        LOGGER.warn {
            """
                No model was passed, using ${revisedModel?.name} as the default model for this task.
                Please specify a model explicitly or use the pipeline constructor to override the default model.
            """.trimIndent()
        }

        return revisedModel
    }

    private fun getDefaultModelAndRevision(
        taskGroup: ITaskGroup,
        framework: FrameworkName? = null
    ): IPreTrainedModel? {
        val targetedTask = taskGroup.targetedTask

        val defaults = targetedTask?.default

        val taskOptions: Any? = taskGroup.taskOptions

        val defaultModels: Map<FrameworkName, IPreTrainedModel>? = if (taskOptions != null) {
            if (defaults?.containsKey(taskOptions) == false) {
                throw PipelineException.MissingTaskOptionException(taskOptions)
            }

            val modelBuilder = defaults?.get(taskOptions) as? IPreTrainedModelBuilder

            modelBuilder?.model
        } else if (defaults?.containsKey("model") == true) {
            defaults["model"].parseAsMap()
        } else {
            throw PipelineException.TaskDefaultsException
        }

        val foundFramework: FrameworkName = FrameworkName.PYTORCH.takeIf {
            tensorflowConfig.isTfAvailable && !pyTorchConfig.isTorchAvailable
        } ?: FrameworkName.TENSORFLOW.takeIf {
            pyTorchConfig.isTorchAvailable && !tensorflowConfig.isTfAvailable
        } ?: framework ?: FrameworkName.PYTORCH

        return defaultModels?.get(foundFramework)
    }

    private fun processCustomTask(
        task: ITask?,
        model: PathLike,
        customTasks: Map<String, *>,
        pipelineClass: Any?,
        trustRemoteCode: Boolean,
        revision: String? = null,
        token: IToken? = null
    ): Pair<ITaskGroup, Any>? {
        val taskName: String = task?.name ?: return null

        if (taskName !in customTasks.keys || pipelineClass != null) return null

        if (!trustRemoteCode) throw PipelineException.RemoteCodeDisabledException

        val targetedTask = cleanCustomTask(customTasks[taskName] as ITask)

        val classRef = targetedTask.impl()

        val taskGroup = DefaultTaskGroup(
            normalizedTask = task,
            targetedTask = targetedTask
        )

        val clazz =
            dynamicModuleLoader
                .loadClassFromDynamicModule(classRef, model, revision = revision, token = token)

        return taskGroup to clazz
    }

    private fun cleanCustomTask(taskInfo: ITask): IAcceptedTask {
        val acceptedTask = taskInfo as? IAcceptedTask ?: throw PipelineException.CustomPipelineMissingTaskException
        val pytorchClassNames = acceptedTask.pytorch?.map(transformers::getClassNameByExternalName)
        val tensorflowClassNames = acceptedTask.tensorflow?.map(transformers::getClassNameByExternalName)
        taskInfo.pytorch = pytorchClassNames
        taskInfo.tensorflow = tensorflowClassNames
        return taskInfo
    }

    private fun getTask(taskName: String?, model: Any?, token: IToken? = null): ITask {
        if (taskName != null || model == null) return DefaultTask(taskName)

        if (model !is String) throw PipelineException.TakeModelCheckConflictException(model)

        if (envConfig.isOfflineMode()) throw PipelineException.OfflineModePipelineException

        val info: IModelInfo? = try {
            huggingFaceModelClient.modelInfo(model, token = token)
        } catch (e: Exception) {
            LOGGER.warn { "Unable to retrieve model info from the hub" }
            throw PipelineException.MissingTaskSetException(e)
        }

        val foundPipelineTag = info?.pipelineTag ?: throw PipelineException.IncorrectPipelineTagException(model)

        if ((info.libraryName ?: LibraryName.TRANSFORMERS) != LibraryName.TRANSFORMERS) {
            throw PipelineException.IncorrectLibraryException(info.libraryName ?: LibraryName.UNKNOWN)
        }

        return DefaultTask(foundPipelineTag)
    }

    private fun determineTaskNameAndCustomTasks(
        config: IPreTrainedConfig?,
        task: String?,
        trustRemoteCode: Boolean?
    ): Pair<String?, Map<String, *>> {
        if (config == null || (config.customPipelines != null && config.customPipelines!!.isEmpty()))
            return task to mapOf<String, Any>()

        val customTasks = config.customPipelines!!

        if (task != null || trustRemoteCode == false) return (null to mapOf<String, Any>())

        val taskName = customTasks.keys.firstOrNull()
            ?: throw PipelineException.AutomaticTaskDetectionException(customTasks)

        return taskName to customTasks
    }

    fun <T> buildRefactor(
        clazz: KClass<T>,
        inputModel: Any,
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
            imageProcessorInferenceService.inferImageProcessor(framework, model, imageProcessor)

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