package io.violabs.ripley.external.nn

import io.violabs.ripley.external.torch.Torch

class LayerNorm private constructor(
    var normalizedShapeInt: Long? = null,
    var normalizedShapeList: List<Long>? = null,
    var eps: Double = 1e-5
) : Torch.FloatTensor() {
    constructor(normalizedShape: Long?, eps: Double? = 1e-5) : this(
        normalizedShapeInt = normalizedShape,
        eps = eps ?: 1e-5
    )
}