//package io.violabs.ripley.pipeline
//
//import io.violabs.ripley.domain.*
//import io.violabs.ripley.testSuite.FakeModel
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//
//class FrameworkModelInferenceEngineTest {
//    private val pretrained = FakeModel()
//
//    private val tensorFlowConfig = object : ITensorFlowConfig {
//        override val isTfAvailable: Boolean
//            get() = TODO("Not yet implemented")
//    }
//
//    private val pyTorchConfig = object : IPyTorchConfig {
//        override val isTorchAvailable: Boolean
//            get() = TODO("Not yet implemented")
//    }
//
//    private val engine = FrameworkModelInferenceEngine(tensorFlowConfig, pyTorchConfig)
//
//    @Test
//    fun inferFrameworkLoadModel_will_throw_an_error_if_not_a_string_or_matching_class() {
//        assertThrows<Exception> { engine.inferFrameworkLoadModel(FakeModel::class, 1) }
//    }
//}