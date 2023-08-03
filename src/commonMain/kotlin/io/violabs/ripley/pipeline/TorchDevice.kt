package io.violabs.ripley.pipeline

import io.violabs.ripley.domain.IDevice
import io.violabs.ripley.domain.ITorchDevice

class TorchDevice(override val name: String? = null) : ITorchDevice, IDevice {
    override val num: Int?
        get() = throw Exception("Not available")

    constructor(device: IDevice) : this(device.name)
}