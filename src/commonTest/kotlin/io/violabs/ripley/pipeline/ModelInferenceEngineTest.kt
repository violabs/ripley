package io.violabs.ripley.pipeline

import io.violabs.ripley.domain.FrameworkName
import io.violabs.ripley.domain.IDevice
import io.violabs.ripley.domain.IPreTrainedModel
import io.violabs.ripley.testSuite.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ModelInferenceEngineTest {
    class NullFrameworkTest {
        private val mockPyTorchConfig = FakePyTorchConfig()
        private val mockTensorFlowConfig = FakeTensorFlowConfig()

        private val modelInferenceEngine = ModelInferenceEngine(mockPyTorchConfig, mockTensorFlowConfig)

        @Test
        fun inferModel_pytorch_tensorflow_missing_throws_exception() {
            mockTensorFlowConfig.reset()
            mockPyTorchConfig.reset()

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(FakePyTorchModel::class, "test", modelKwargs = ModelKwargs())
            }
        }

        @Test
        fun inferModel_pytorch_available_missing_model_builder_map_throws_exception() {
            mockPyTorchConfig.modifiedAvailability = true
            mockTensorFlowConfig.reset()

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(FakePyTorchModel::class, "test", modelKwargs = ModelKwargs())
            }
        }

        @Test
        fun inferModel_pytorch_available_missing_build_in_builder_map_throws_exception() {
            mockPyTorchConfig.modifiedAvailability = true
            mockTensorFlowConfig.reset()

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(
                    FakePyTorchModel::class,
                    "test",
                    modelClassBuilders = mapOf(),
                    modelKwargs = ModelKwargs()
                )
            }
        }

        @Test
        fun inferModel_pytorch_available_builder_casts_incorrectly_throws_exception() {
            mockPyTorchConfig.modifiedAvailability = true
            mockTensorFlowConfig.reset()

            val extended = object : FakePyTorchModel() {
                override fun fromPretrained(model: String, modelKwargs: ModelKwargs): IPreTrainedModel {
                    return FakeFlaxModel()
                }

                override fun addDevice(device: IDevice?) {
                    TODO("Not yet implemented")
                }
            }

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(
                    FakePyTorchModel::class,
                    "test",
                    modelClassBuilders = mapOf(FrameworkName.PYTORCH to { extended }),
                    modelKwargs = ModelKwargs()
                )
            }
        }

        @Test
        fun inferModel_pytorch_available_builder_successfully_builds_model() {
            mockPyTorchConfig.modifiedAvailability = true
            mockTensorFlowConfig.reset()

            val model = modelInferenceEngine.inferModel(
                FakePyTorchModel::class,
                "test.h5",
                modelClassBuilders = mapOf(FrameworkName.PYTORCH to { FakePyTorchModel() }),
                modelKwargs = ModelKwargs()
            )

            assertEquals("test.h5", model.model)
            assertEquals(ModelKwargs(fromPyTorch = true), model.modelKwargs)
        }

        @Test
        fun inferModel_tensorflow_available_missing_model_builder_map_throws_exception() {
            mockPyTorchConfig.reset()
            mockTensorFlowConfig.modifiedAvailability = true

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(FakeTensorFlowModel::class, "test", modelKwargs = ModelKwargs())
            }
        }

        @Test
        fun inferModel_tensorflow_available_missing_build_in_builder_map_throws_exception() {
            mockPyTorchConfig.reset()
            mockTensorFlowConfig.modifiedAvailability = true

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(
                    FakeTensorFlowModel::class,
                    "test",
                    modelClassBuilders = mapOf(),
                    modelKwargs = ModelKwargs()
                )
            }
        }

        @Test
        fun inferModel_tensorflow_available_builder_casts_incorrectly_throws_exception() {
            mockPyTorchConfig.reset()
            mockTensorFlowConfig.modifiedAvailability = true

            val extended = object : FakeTensorFlowModel() {
                override fun fromPretrained(model: String, modelKwargs: ModelKwargs): IPreTrainedModel {
                    return FakeFlaxModel()
                }

                override fun addDevice(device: IDevice?) {
                    TODO("Not yet implemented")
                }
            }

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(
                    FakeTensorFlowModel::class,
                    "test",
                    modelClassBuilders = mapOf(FrameworkName.TENSORFLOW to { extended }),
                    modelKwargs = ModelKwargs()
                )
            }
        }

        @Test
        fun inferModel_tensorflow_available_builder_successfully_builds_model() {
            mockPyTorchConfig.reset()
            mockTensorFlowConfig.modifiedAvailability = true

            val model = modelInferenceEngine.inferModel(
                FakeTensorFlowModel::class,
                "test.bin",
                modelClassBuilders = mapOf(FrameworkName.TENSORFLOW to { FakeTensorFlowModel() }),
                modelKwargs = ModelKwargs()
            )

            assertEquals("test.bin", model.model)
            assertEquals(ModelKwargs(fromTensorFlow = true), model.modelKwargs)
        }
    }

    class PyTorchFrameworkTest {
        private val mockPyTorchConfig = FakePyTorchConfig()
        private val mockTensorFlowConfig = FakeTensorFlowConfig()

        private val modelInferenceEngine = ModelInferenceEngine(mockPyTorchConfig, mockTensorFlowConfig)

        @Test
        fun inferModel_pytorch_tensorflow_missing_throws_exception() {
            mockTensorFlowConfig.reset()
            mockPyTorchConfig.reset()

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(
                    FakePyTorchModel::class,
                    "test",
                    framework = FrameworkName.PYTORCH,
                    modelKwargs = ModelKwargs()
                )
            }
        }

        @Test
        fun inferModel_pytorch_available_missing_model_builder_map_throws_exception() {
            mockPyTorchConfig.modifiedAvailability = true
            mockTensorFlowConfig.reset()

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(
                    FakePyTorchModel::class,
                    "test",
                    framework = FrameworkName.PYTORCH,
                    modelKwargs = ModelKwargs()
                )
            }
        }

        @Test
        fun inferModel_pytorch_available_missing_build_in_builder_map_throws_exception() {
            mockPyTorchConfig.modifiedAvailability = true
            mockTensorFlowConfig.reset()

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(
                    FakePyTorchModel::class,
                    "test",
                    modelClassBuilders = mapOf(),
                    framework = FrameworkName.PYTORCH,
                    modelKwargs = ModelKwargs()
                )
            }
        }

        @Test
        fun inferModel_pytorch_available_builder_casts_incorrectly_throws_exception() {
            mockPyTorchConfig.modifiedAvailability = true
            mockTensorFlowConfig.reset()

            val extended = object : FakePyTorchModel() {
                override fun fromPretrained(model: String, modelKwargs: ModelKwargs): IPreTrainedModel {
                    return FakeFlaxModel()
                }

                override fun addDevice(device: IDevice?) {
                    TODO("Not yet implemented")
                }
            }

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(
                    FakePyTorchModel::class,
                    "test",
                    modelClassBuilders = mapOf(FrameworkName.PYTORCH to { extended }),
                    framework = FrameworkName.PYTORCH,
                    modelKwargs = ModelKwargs()
                )
            }
        }

        @Test
        fun inferModel_pytorch_available_builder_successfully_builds_model() {
            mockPyTorchConfig.modifiedAvailability = true
            mockTensorFlowConfig.reset()

            val model = modelInferenceEngine.inferModel(
                FakePyTorchModel::class,
                "test.h5",
                modelClassBuilders = mapOf(FrameworkName.PYTORCH to { FakePyTorchModel() }),
                framework = FrameworkName.PYTORCH,
                modelKwargs = ModelKwargs()
            )

            assertEquals("test.h5", model.model)
            assertEquals(ModelKwargs(fromPyTorch = true), model.modelKwargs)
        }

        @Test
        fun inferModel_tensorflow_available_throw_exception() {
            mockPyTorchConfig.reset()
            mockTensorFlowConfig.modifiedAvailability = true

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(
                    FakePyTorchModel::class,
                    "test",
                    framework = FrameworkName.PYTORCH,
                    modelKwargs = ModelKwargs()
                )
            }
        }
    }

    class TensorFlowFrameworkTest {
        private val mockPyTorchConfig = FakePyTorchConfig()
        private val mockTensorFlowConfig = FakeTensorFlowConfig()

        private val modelInferenceEngine = ModelInferenceEngine(mockPyTorchConfig, mockTensorFlowConfig)

        @Test
        fun inferModel_pytorch_tensorflow_missing_throws_exception() {
            mockTensorFlowConfig.reset()
            mockPyTorchConfig.reset()

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(
                    FakeTensorFlowModel::class,
                    "test",
                    framework = FrameworkName.TENSORFLOW,
                    modelKwargs = ModelKwargs()
                )
            }
        }

        @Test
        fun inferModel_tensorflow_available_missing_model_builder_map_throws_exception() {
            mockPyTorchConfig.reset()
            mockTensorFlowConfig.modifiedAvailability = true

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(
                    FakeTensorFlowModel::class,
                    "test",
                    framework = FrameworkName.TENSORFLOW,
                    modelKwargs = ModelKwargs()
                )
            }
        }

        @Test
        fun inferModel_tensorflow_available_missing_build_in_builder_map_throws_exception() {
            mockPyTorchConfig.reset()
            mockTensorFlowConfig.modifiedAvailability = true

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(
                    FakeTensorFlowModel::class,
                    "test",
                    modelClassBuilders = mapOf(),
                    framework = FrameworkName.TENSORFLOW,
                    modelKwargs = ModelKwargs()
                )
            }
        }

        @Test
        fun inferModel_tensorflow_available_builder_casts_incorrectly_throws_exception() {
            mockPyTorchConfig.reset()
            mockTensorFlowConfig.modifiedAvailability = true

            val extended = object : FakeTensorFlowModel() {
                override fun fromPretrained(model: String, modelKwargs: ModelKwargs): IPreTrainedModel {
                    return FakeFlaxModel()
                }

                override fun addDevice(device: IDevice?) {
                    TODO("Not yet implemented")
                }
            }

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(
                    FakeTensorFlowModel::class,
                    "test",
                    modelClassBuilders = mapOf(FrameworkName.TENSORFLOW to { extended }),
                    framework = FrameworkName.TENSORFLOW,
                    modelKwargs = ModelKwargs()
                )
            }
        }

        @Test
        fun inferModel_tensorflow_available_builder_successfully_builds_model() {
            mockPyTorchConfig.reset()
            mockTensorFlowConfig.modifiedAvailability = true

            val model = modelInferenceEngine.inferModel(
                FakeTensorFlowModel::class,
                "test.bin",
                modelClassBuilders = mapOf(FrameworkName.TENSORFLOW to { FakeTensorFlowModel() }),
                framework = FrameworkName.TENSORFLOW,
                modelKwargs = ModelKwargs()
            )

            assertEquals("test.bin", model.model)
            assertEquals(ModelKwargs(fromTensorFlow = true), model.modelKwargs)
        }

        @Test
        fun inferModel_pytorch_available_throw_exception() {
            mockPyTorchConfig.modifiedAvailability = true
            mockTensorFlowConfig.reset()

            assertFailsWith<Exception> {
                modelInferenceEngine.inferModel(
                    FakeTensorFlowModel::class,
                    "test",
                    framework = FrameworkName.TENSORFLOW,
                    modelKwargs = ModelKwargs()
                )
            }
        }
    }
}