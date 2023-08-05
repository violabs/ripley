package io.violabs.ripley.pipeline

import io.violabs.ripley.common.iff
import io.violabs.ripley.domain.*

class DeviceInferenceService(private val tensorFlowConfig: ITensorFlowConfig) : IDeviceInferenceService {
    override fun inferDeviceBelongsInModel(framework: FrameworkName, inputDevice: IDevice?): IDevice? {
        return iff(framework != FrameworkName.PYTORCH)
            .or { inputDevice == null }
            .or { inputDevice!!.num != null && inputDevice.num!! >= 0 }
            .thenReturn(null, elseReturn = inputDevice)
    }

    override fun <T : AvailableModel> inferDevice(framework: FrameworkName, model: T, inputDevice: IDevice?): IDevice? {
        val foundDevice: IDevice = inputDevice ?: model.hfDeviceMap?.values?.firstOrNull() ?: Device.Default

        return foundDevice.processTensorFlow(framework)
    }

    private fun IDevice.processTensorFlow(framework: FrameworkName): IDevice? {
        if (!tensorFlowConfig.isTfAvailable || framework == FrameworkName.PYTORCH) return null

        return if (this is ITorchDevice) this
        else if (this.name?.isNotBlank() == true) Device.TorchDevice(this)
        else if (this.num != null && this.num!! < 0) Device.TorchDevice("cpu")
        else Device.TorchDevice("cuda:${this.num}")
    }
}