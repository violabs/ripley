package io.violabs.ripley.model.albert

import io.violabs.ripley.external.nn.Dropout
import io.violabs.ripley.external.nn.Embedding
import io.violabs.ripley.external.nn.LayerNorm
import io.violabs.ripley.external.nn.Module
import io.violabs.ripley.external.torch.Torch

class AlbertEmbeddings(
    val config: AlbertConfig
) : Module() {
    private val wordEmbeddings = Embedding(
        numberOfEmbeddings = config.vocabSize,
        dimensions = config.embeddingSize,
        paddingIdx = config.padTokenId
    )
    private var positionEmbeddings: Torch.FloatTensor = Embedding(
        numberOfEmbeddings = config.maxPositionEmbeddings,
        dimensions = config.embeddingSize
    )
    private val tokenTypeEmbeddings = Embedding(
        numberOfEmbeddings = config.typeVocabSize,
        dimensions = config.embeddingSize
    )
    private val layerNorm: LayerNorm = LayerNorm(config.embeddingSize, eps = config.layerNormEpsilon)
    private val dropout: Dropout = Dropout(config.hiddenDropoutProbability ?: 0.0)
    private val positionEmbeddingType: Position = config.positionEmbeddingType ?: Position.ABSOLUTE

    init {
        val positionIdsTensor: Torch.LongTensor =
            Torch
                .arange(config.maxPositionEmbeddingsOrDefault())
                .expand(1L to -1L)

        registerBuffer("positionIds", positionIdsTensor, false)

        val tokenTypeIdsTensor: Torch.LongTensor = Torch.zeros(
            size = positionIdsTensor.size(),
            dtype = Torch.DataType.LONG
        )

        registerBuffer("tokenTypeIds", tokenTypeIdsTensor, false)
    }

    fun forward(
        inputIds: Torch.LongTensor? = null,
        tokenTypeIds: Torch.LongTensor? = null,
        positionIds: Torch.LongTensor? = null,
        inputEmbeds: Torch.FloatTensor? = null,
        pastKeyValuesLength: Long = 0
    ): Torch.Tensor<*> {
        val inputShape: List<Long> = parseInputShape(inputIds, inputEmbeds)

        val seqLength: Long =
            inputShape
                .getOrNull(1)
                ?: throw IllegalArgumentException("Input shape must have 2 dimensions, got: $inputShape")

        val checkedPositionIds =
            positionIds ?: super.attr("positionIds")?.slice(
                currentColumnSlice = pastKeyValuesLength,
                endColumnSlice = pastKeyValuesLength + seqLength
            )

        val checkedTokenTypeIds: Torch.LongTensor? = tokenTypeIds ?: checkTokenTypeIds(seqLength, inputShape)

        val checkInputEmbeds: Torch.FloatTensor = inputEmbeds ?: wordEmbeddings[inputIds]

        val tokenTypeEmbeddings: Torch.FloatTensor = this.tokenTypeEmbeddings[checkedTokenTypeIds]

        var embeddings: Torch.FloatTensor = tokenTypeEmbeddings + checkInputEmbeds

        if (this.positionEmbeddingType == Position.ABSOLUTE) {
            positionEmbeddings = this.positionEmbeddings[checkedPositionIds]
            embeddings += positionEmbeddings
        }

        return this.layerNorm[embeddings].let(dropout::get)
    }

    private fun parseInputShape(inputIds: Torch.LongTensor?, inputEmbeds: Torch.FloatTensor?): List<Long> =
        inputIds
            ?.size()
            ?: inputEmbeds
                ?.size()
                ?.dropLast(1)
            ?: emptyList()

    private fun checkTokenTypeIds(seqLength: Long, inputShape: List<Long>): Torch.LongTensor? {
        if (super.hasAttr("tokenTypeIds")) {
            val bufferTokenTypeIds: Torch.LongTensor? = super.attr("tokenTypeIds")?.slice(
                currentColumnSlice = 0,
                endColumnSlice = seqLength
            )

            return bufferTokenTypeIds?.expand(inputShape.first() to seqLength)
        }

        return Torch.zeros(
            size = inputShape,
            dtype = Torch.DataType.LONG,
            device = super.attr("positionIds")?.device
        )
    }
}