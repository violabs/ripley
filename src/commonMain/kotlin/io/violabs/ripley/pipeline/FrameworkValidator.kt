package io.violabs.ripley.pipeline

import io.violabs.ripley.domain.IFrameworkValidator
import io.violabs.ripley.domain.IPyTorchConfig
import io.violabs.ripley.domain.ITensorFlowConfig

class FrameworkValidator(
    private val tensorFlowConfig: ITensorFlowConfig,
    private val pyTorchConfig: IPyTorchConfig
) : IFrameworkValidator {
    override fun requireActiveFramework() {
        if (!tensorFlowConfig.isTfAvailable && !pyTorchConfig.isTorchAvailable) {
            throw Exception(
                """
                At least one of TensorFlow 2.0 or PyTorch should be installed. 
                To install TensorFlow 2.0, read the instructions at https://www.tensorflow.org/install/.
                To install PyTorch, read the instructions at https://pytorch.org/.
            """.trimIndent()
            )
        }
    }
}