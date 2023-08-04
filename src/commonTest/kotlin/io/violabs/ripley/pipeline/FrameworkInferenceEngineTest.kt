package io.violabs.ripley.pipeline

import io.violabs.ripley.domain.FrameworkName
import io.violabs.ripley.testSuite.FakeFlaxModel
import io.violabs.ripley.testSuite.FakeModel
import io.violabs.ripley.testSuite.FakePyTorchModel
import io.violabs.ripley.testSuite.FakeTensorFlowModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FrameworkInferenceEngineTest {
    private val engine = FrameworkInferenceEngine()

    private val flaxModel = FakeFlaxModel()

    @Test
    fun inferFramework_will_return_framework_when_provided() {
        val actual = engine.inferFramework(flaxModel, FrameworkName.PYTORCH)

        assertEquals(FrameworkName.PYTORCH, actual)
    }

    @Test
    fun inferFramework_will_throw_exception_if_not_expected_model() {
        assertFailsWith<Exception> { engine.inferFramework(FakeModel()) }
    }

    @Test
    fun inferFramework_will_return_pytorch_model() {
        val actual = engine.inferFramework(FakePyTorchModel())

        assertEquals(FrameworkName.PYTORCH, actual)
    }

    @Test
    fun inferFramework_will_return_tensorflow_model() {
        val actual = engine.inferFramework(FakeTensorFlowModel())

        assertEquals(FrameworkName.TENSORFLOW, actual)
    }

    @Test
    fun inferFramework_will_return_flax_model() {
        val actual = engine.inferFramework(FakeFlaxModel())

        assertEquals(FrameworkName.FLAX, actual)
    }
}