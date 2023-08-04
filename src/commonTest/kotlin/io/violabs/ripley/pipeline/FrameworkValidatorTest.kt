package io.violabs.ripley.pipeline

import io.violabs.ripley.domain.IPyTorchConfig
import io.violabs.ripley.domain.ITensorFlowConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

class FrameworkValidatorTest {
    data class FakeTensorFlowConfig(override val isTfAvailable: Boolean = false) : ITensorFlowConfig
    data class FakePyTorchConfig(override val isTorchAvailable: Boolean = false) : IPyTorchConfig

    @Test
    fun requireActiveFramework_will_throw_exception_if_neither_are_available() {
        val validator = FrameworkValidator(FakeTensorFlowConfig(), FakePyTorchConfig())

        assertThrows<Exception> { validator.requireActiveFramework() }
    }

    @Test
    fun requireActiveFramework_will_pass_if_tensorflow_is_available() {
        val validator = FrameworkValidator(FakeTensorFlowConfig(true), FakePyTorchConfig())

        validator.requireActiveFramework()

        assertTrue(true)
    }

    @Test
    fun requireActiveFramework_will_pass_if_pytorch_is_available() {
        val validator = FrameworkValidator(FakeTensorFlowConfig(), FakePyTorchConfig(true))

        validator.requireActiveFramework()

        assertTrue(true)
    }
}