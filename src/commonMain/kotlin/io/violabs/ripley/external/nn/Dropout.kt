package io.violabs.ripley.external.nn

import io.violabs.ripley.external.torch.Torch

class Dropout(val p: Double = 0.5, inplace: Boolean = false) : Torch.FloatTensor()