package io.violabs.ripley.pipeline

import io.violabs.ripley.domain.FrameworkName
import io.violabs.ripley.testSuite.FakePyTorchModel
import io.violabs.ripley.testSuite.FakeTensorFlowConfig
import io.violabs.ripley.testSuite.testEquals
import org.junit.jupiter.api.Test

class DeviceInferenceServiceTest {
    private val tensorFlowConfig = FakeTensorFlowConfig()

    private val deviceInferenceService = DeviceInferenceService(tensorFlowConfig)

    class InferDeviceBelongsInModelTest {
        private val tensorFlowConfig = FakeTensorFlowConfig()

        private val deviceInferenceService = DeviceInferenceService(tensorFlowConfig)

        @Test
        fun inferDeviceBelongsInModel_will_return_null_if_framework_is_pytorch_and_device_is_null() {
            val actual = deviceInferenceService.inferDeviceBelongsInModel(FrameworkName.PYTORCH, null)

            testEquals(null, actual)
        }

        @Test
        fun inferDeviceBelongsInModel_will_return_null_if_framework_is_tensorflow() {
            val device = Device.Default

            val actual = deviceInferenceService.inferDeviceBelongsInModel(FrameworkName.TENSORFLOW, device)

            testEquals(null, actual)
        }

        @Test
        fun inferDeviceBelongsInModel_will_return_null_if_framework_is_pytorch_and_device_num_is_at_least_0() {
            val device = Device.GenericDevice(num = 0)

            val actual = deviceInferenceService.inferDeviceBelongsInModel(FrameworkName.PYTORCH, device)

            testEquals(null, actual)
        }

        @Test
        fun inferDeviceBelongsInModel_will_return_device_if_framework_is_pytorch_and_device_num_is_less_than_zero() {
            val device = Device.Default

            val actual = deviceInferenceService.inferDeviceBelongsInModel(FrameworkName.PYTORCH, device)

            testEquals(device, actual)
        }
    }

    class InferDeviceTest {
        private val tensorFlowConfig = FakeTensorFlowConfig()

        private val deviceInferenceService = DeviceInferenceService(tensorFlowConfig)

        @Test
        fun inferDevice_will_return_null_if_tensorflow_is_not_available() {
            tensorFlowConfig.reset()

            val device = Device.Default

            val actual = deviceInferenceService.inferDevice(FrameworkName.PYTORCH, FakePyTorchModel(), device)

            testEquals(null, actual)
        }

        @Test
        fun inferDevice_will_return_null_if_framework_is_pytorch() {
            tensorFlowConfig.reset()
            tensorFlowConfig.modifiedAvailability = true

            val device = Device.Default

            val actual = deviceInferenceService.inferDevice(FrameworkName.PYTORCH, FakePyTorchModel(), device)

            testEquals(null, actual)
        }

        @Test
        fun inferDevice_will_returns_device_if_it_is_torch_device() {
            tensorFlowConfig.reset()
            tensorFlowConfig.modifiedAvailability = true

            val device = Device.TorchDevice("test")

            val actual = deviceInferenceService.inferDevice(FrameworkName.TENSORFLOW, FakePyTorchModel(), device)

            testEquals(device, actual)
        }

        @Test
        fun inferDevice_will_returns_torch_device_converted_from_another_device() {
            tensorFlowConfig.reset()
            tensorFlowConfig.modifiedAvailability = true

            val model = FakePyTorchModel().also {
                it.hfDeviceMap = mapOf("test" to Device.GenericDevice("test"))
            }

            val device = Device.GenericDevice("test")

            val expected = Device.TorchDevice("test")

            val actual = deviceInferenceService.inferDevice(FrameworkName.TENSORFLOW, model, device)

            testEquals(expected, actual)
        }

        @Test
        fun inferDevice_will_returns_cpu_torch_device_if_number_below_zero() {
            tensorFlowConfig.reset()
            tensorFlowConfig.modifiedAvailability = true

            val model = FakePyTorchModel()

            val expected = Device.TorchDevice("cpu")

            val actual = deviceInferenceService.inferDevice(FrameworkName.TENSORFLOW, model, null)

            testEquals(expected, actual)
        }

        @Test
        fun inferDevice_will_returns_cuda_torch_device_if_model_map_empty_and_device_num_at_least_0() {
            tensorFlowConfig.reset()
            tensorFlowConfig.modifiedAvailability = true

            val model = FakePyTorchModel().also {
                it.hfDeviceMap = mapOf("test" to Device.GenericDevice("test"))
            }

            val device = Device.GenericDevice(num = 0)

            val expected = Device.TorchDevice("cuda:0")

            val actual = deviceInferenceService.inferDevice(FrameworkName.TENSORFLOW, model, device)

            testEquals(expected, actual)
        }
    }
}