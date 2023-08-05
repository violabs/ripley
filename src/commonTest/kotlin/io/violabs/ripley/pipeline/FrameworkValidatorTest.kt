package io.violabs.ripley.pipeline

import io.violabs.ripley.testSuite.FakePyTorchConfig
import io.violabs.ripley.testSuite.FakeTensorFlowConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

class FrameworkValidatorTest {


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