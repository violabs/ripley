package io.violabs.ripley.pipeline

sealed class PipelineException(message: String) : Exception(message) {
    object MissingTaskOrModelException : PipelineException("task or model must be provided")
    object MissingTokenizerException : PipelineException("""
        Impossible to instantiate a pipeline with tokenizer specified but not the model as the provided tokenizer
        may not be compatible with the default model. Please provide a PreTrainedModel class or a
        path/identifier to a pretrained model when providing tokenizer.
    """.trimIndent())

    object MissingFeatureExtractorException : PipelineException("""
        Impossible to instantiate a pipeline with feature_extractor specified but not the model as the provided
        feature_extractor may not be compatible with the default model. Please provide a PreTrainedModel class
        or a path/identifier to a pretrained model when providing feature_extractor.
    """.trimIndent())

    class AutomaticTaskDetectionException(customTasks: Map<String, *>) : PipelineException("""
        Impossible to infer task from the provided model. Please specify a task name in the pipeline constructor.
        Available tasks are: $customTasks
    """.trimIndent())

    class TakeModelCheckConflictException(model: Any) : PipelineException("""
        Inferring the task automatically requires to check the hub with a model_id defined as a String
        $model is not a valid model_id
    """.trimIndent())

    object OfflineModePipelineException : PipelineException(
        "You cannot infer task automatically within `pipeline` when using offline mode"
    )

    class MissingTaskSetException(ex: Exception) :
        PipelineException("Instantiating a pipeline without a task set raised an error: $ex")

    class IncorrectPipelineTagException(model: Any) :
        PipelineException("The model $model does not seem to have a correct `pipeline_tag` set to infer the task automatically")

    class IncorrectLibraryException(libraryName: Any) :
        PipelineException("This model is meant to be used with $libraryName not with transformers")

    object CustomPipelineMissingTaskException :
        PipelineException("This model introduces a custom pipeline without specifying its implementation")

    object RemoteCodeDisabledException :
        PipelineException("""
            Loading this pipeline requires you to execute the code in the pipeline file in that
            repo on your local machine. Make sure you have read the code there to avoid malicious use, then
            set the option `trust_remote_code=True` to remove this error.
        """.trimIndent())

    class MissingTaskOptionException(taskOptions: Any?) :
        PipelineException("The task does not provide any default models for options $taskOptions")

    object TaskDefaultsException : PipelineException("The task does not provide any default models")

    object DeviceMapModelKwargsDefinedException : PipelineException("""
        You cannot use both `pipeline(... deviceMap=..., modelKwargs={"deviceMap":...})`
         as those arguments might conflict, use only one.)
    """.trimIndent())

    object TorchDataTypeModelKwargsDefinedException : PipelineException("""
        You cannot use both `pipeline(... torchDatatype=..., modelKwargs={"torchDatatype":...})`
         as those arguments might conflict, use only one.)
    """.trimIndent())
}