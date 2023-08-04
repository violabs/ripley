package io.violabs.ripley.pipeline

import io.violabs.ripley.domain.*

class FrameworkInferenceEngine : IFrameworkInferenceEngine {
    override fun <T : IPreTrainedModel> inferFramework(model: T, framework: FrameworkName?): FrameworkName {
        return framework ?: inferFromModel(model)
    }

    private fun <T : IPreTrainedModel> inferFromModel(modelClass: T): FrameworkName = when (modelClass) {
        is IPyTorchModel -> FrameworkName.PYTORCH
        is ITensorFlowModel -> FrameworkName.TENSORFLOW
        is IFlaxModel -> FrameworkName.FLAX
        else -> throw Exception("Cannot infer framework from $modelClass")
    }
}