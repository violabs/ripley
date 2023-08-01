package io.violabs.ripley.model.albert

class AlbertForPreTraining(
    val config: AlbertConfig
) {
    private val tiedWeightsKeys = listOf(
        "predictions.decoder.bias",
        "predictions.decoder.weight"
    )

//    private val albert = AlbertModel(config)

}