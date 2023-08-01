package io.violabs.ripley.model.albert

import mu.two.KotlinLogging

object AlbertOriginalTfCheckpointToPyTorchConverter {
    val LOGGER = KotlinLogging.logger("AlbertOriginalTfCheckpointToPyTorchConverter")

    fun convert(tfCheckpointPath: String, albertConfigFile: String?, pytorchDumpPath: String) {
        val config = AlbertConfig.fromJsonFile(albertConfigFile)

        LOGGER.info { "Building PyTorch model from configuration: $config" }

    }
}