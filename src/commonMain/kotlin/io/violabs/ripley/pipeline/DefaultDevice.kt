package io.violabs.ripley.pipeline

import io.violabs.ripley.domain.IDevice

sealed class Device(
    override val name: String? = null,
    override val num: Int? = null
) : IDevice {
    object Default : Device(num = -1)
}