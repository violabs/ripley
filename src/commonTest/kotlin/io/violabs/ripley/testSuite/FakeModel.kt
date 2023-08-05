package io.violabs.ripley.testSuite

import io.violabs.ripley.domain.*
import io.violabs.ripley.pipeline.ModelKwargs

open class FakeModel : IPreTrainedModel {
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

open class FakePyTorchModel : IPyTorchModel {
    var model: String? = null
    var modelKwargs: ModelKwargs? = null
    var evaluated: Boolean = false
    override val config: IPreTrainedConfig?
        get() = TODO("Not yet implemented")
    override val generationConfig: IGenerationConfig?
        get() = TODO("Not yet implemented")

    override fun eval(): IPreTrainedModel {
        evaluated = true
        return this
    }

    override fun fromPretrained(model: String, modelKwargs: ModelKwargs): IPreTrainedModel {
        this.model = model
        this.modelKwargs = modelKwargs
        return this
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

open class FakeTensorFlowModel : ITensorFlowModel {
    var model: String? = null
    var modelKwargs: ModelKwargs? = null
    var evaluated: Boolean = false
    override val config: IPreTrainedConfig?
        get() = TODO("Not yet implemented")
    override val generationConfig: IGenerationConfig?
        get() = TODO("Not yet implemented")

    override fun eval(): IPreTrainedModel {
        evaluated = true
        return this
    }

    override fun fromPretrained(model: String, modelKwargs: ModelKwargs): IPreTrainedModel {
        this.model = model
        this.modelKwargs = modelKwargs
        return this
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

open class FakeFlaxModel : IFlaxModel {
    override val config: IPreTrainedConfig?
        get() = TODO("Not yet implemented")
    override val generationConfig: IGenerationConfig?
        get() = TODO("Not yet implemented")

    override fun eval(): IPreTrainedModel {
        return this
    }

    override fun fromPretrained(model: String, modelKwargs: ModelKwargs): IPreTrainedModel {
        return this
    }

    override fun canGenerate(): Boolean {
        TODO("Not yet implemented")
    }
}