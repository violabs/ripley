package io.violabs.ripley.testSuite

import io.violabs.ripley.domain.IPyTorchConfig
import io.violabs.ripley.domain.ITensorFlowConfig

data class FakeTensorFlowConfig(var modifiedAvailability: Boolean = false) : ITensorFlowConfig {
    override val isTfAvailable: Boolean
        get() = modifiedAvailability

    fun reset() {
        modifiedAvailability = false
    }
}
data class FakePyTorchConfig(var modifiedAvailability: Boolean = false) : IPyTorchConfig {
    override val isTorchAvailable: Boolean
        get() = modifiedAvailability

    fun reset() {
        modifiedAvailability = false
    }
}