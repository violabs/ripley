package io.violabs.ripley.domain

import io.violabs.ripley.pipeline.ModelKwargs

interface IPipeline

interface IPreTrainedModel {
    fun eval(): IPreTrainedModel
    fun fromPretrained(model: String, modelKwargs: ModelKwargs): IPreTrainedModel
}

interface IPreTrainedTokenizer
interface IPreTrainedFeatureExtractor
interface IBaseImageProcessor
interface IModelCard
interface IDevice {
    val num: Int?
}
interface ITorchDataType

interface IScikitCompatible {
    fun transform(x: Any?)

    fun predict(x: Any?)
}

interface IAutoConfig

interface AvailableModel {
    val hfDeviceMap: Map<String, IDevice>
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