package io.violabs.ripley.external.nn

import io.violabs.ripley.external.torch.Torch
import mu.two.KotlinLogging


abstract class Module {
    private val bufferMap: MutableMap<String, Torch.LongTensor?> = mutableMapOf()
    private val persistentNames: MutableList<String> = mutableListOf()
    fun registerBuffer(name: String, buffer: Torch.LongTensor?, persistent: Boolean = true) {
        LOGGER.info { "Registering buffer name=$name, buffer=$buffer, persistent=$persistent" }
        bufferMap[name] = buffer
        if (persistent) {
            persistentNames.add(name)
        }
    }

    fun hasAttr(name: String): Boolean {
        return bufferMap.containsKey(name)
    }

    fun attr(name: String): Torch.LongTensor? {
        return bufferMap[name]
    }

    companion object {
        val LOGGER = KotlinLogging.logger("Module")
    }
}