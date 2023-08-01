package io.violabs.ripley.model.albert

import kotlin.reflect.KClass

class AlbertModel(
    val config: AlbertConfig,
    val addPoolingLayer: Boolean = true
) {
    val configClass: KClass<AlbertConfig> = AlbertConfig::class
    val baseModelPrefix = "albert"
}