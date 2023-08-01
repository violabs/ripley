package io.violabs.ripley.external.nn

import io.violabs.ripley.external.torch.Torch

class Embedding(
    val numberOfEmbeddings: Long?,
    val dimensions: Long?,
    val paddingIdx: Long? = null,
) : Torch.FloatTensor()