package io.violabs.ripley.testSuite

import io.violabs.ripley.domain.*
import io.violabs.ripley.pipeline.ModelKwargs

class FakeModel : IPreTrainedModel {
    override val config: IPreTrainedConfig?
        get() = TODO("Not yet implemented")
    override val generationConfig: IGenerationConfig?
        get() = TODO("Not yet implemented")

    override fun eval(): IPreTrainedModel {
        TODO("Not yet implemented")
    }

    override fun fromPretrained(model: String, modelKwargs: ModelKwargs): IPreTrainedModel {
        TODO("Not yet implemented")
    }

    override fun canGenerate(): Boolean {
        TODO("Not yet implemented")
    }
}

class FakePyTorchModel : IPyTorchModel {
    override val config: IPreTrainedConfig?
        get() = TODO("Not yet implemented")
    override val generationConfig: IGenerationConfig?
        get() = TODO("Not yet implemented")

    override fun eval(): IPreTrainedModel {
        TODO("Not yet implemented")
    }

    override fun fromPretrained(model: String, modelKwargs: ModelKwargs): IPreTrainedModel {
        TODO("Not yet implemented")
    }

    override fun canGenerate(): Boolean {
        TODO("Not yet implemented")
    }

    override val hfDeviceMap: Map<String, IDevice>?
        get() = TODO("Not yet implemented")

    override fun to(device: IDevice) {
        TODO("Not yet implemented")
    }
}

class FakeTensorFlowModel : ITensorFlowModel {
    override val config: IPreTrainedConfig?
        get() = TODO("Not yet implemented")
    override val generationConfig: IGenerationConfig?
        get() = TODO("Not yet implemented")

    override fun eval(): IPreTrainedModel {
        TODO("Not yet implemented")
    }

    override fun fromPretrained(model: String, modelKwargs: ModelKwargs): IPreTrainedModel {
        TODO("Not yet implemented")
    }

    override fun canGenerate(): Boolean {
        TODO("Not yet implemented")
    }

    override val hfDeviceMap: Map<String, IDevice>?
        get() = TODO("Not yet implemented")

    override fun to(device: IDevice) {
        TODO("Not yet implemented")
    }
}

class FakeFlaxModel : IFlaxModel {
    override val config: IPreTrainedConfig?
        get() = TODO("Not yet implemented")
    override val generationConfig: IGenerationConfig?
        get() = TODO("Not yet implemented")

    override fun eval(): IPreTrainedModel {
        TODO("Not yet implemented")
    }

    override fun fromPretrained(model: String, modelKwargs: ModelKwargs): IPreTrainedModel {
        TODO("Not yet implemented")
    }

    override fun canGenerate(): Boolean {
        TODO("Not yet implemented")
    }
}