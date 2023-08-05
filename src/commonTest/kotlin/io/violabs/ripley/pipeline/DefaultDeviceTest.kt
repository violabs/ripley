package io.violabs.ripley.pipeline

import org.junit.jupiter.api.Test

class DefaultDeviceTest {
    @Test
    fun default_has_negative_one() {
        assert(Device.Default.num == -1)
    }
}