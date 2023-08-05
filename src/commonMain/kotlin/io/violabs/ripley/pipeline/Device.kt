package io.violabs.ripley.pipeline

import io.violabs.ripley.domain.IDevice
import io.violabs.ripley.domain.ITorchDevice

sealed class Device(
    override val name: String? = null,
    override val num: Int? = null
) : IDevice {
    object Default : Device(num = -1)
    data class TorchDevice(override val name: String? = null) : ITorchDevice, IDevice, Device(name) {
        override val num: Int
            get() = throw Exception("Not available")

        constructor(device: IDevice) : this(device.name)
    }
    data class GenericDevice(override val name: String? = null, override val num: Int? = null) : IDevice, Device(name, num)

    override fun toString(): String {
        return "Device(name=$name, num=$num)"
    }
}