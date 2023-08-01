package io.violabs.ripley.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object SerializationConfig {
    private val module = SerializersModule {
        polymorphic(Any::class) {
            subclass(AnyValue.StringValue::class)
            subclass(AnyValue.IntValue::class)
            subclass(AnyValue.LongValue::class)
            subclass(AnyValue.DoubleValue::class)
            subclass(AnyValue.ListValue::class)
            subclass(AnyValue.MapValue::class)
        }
    }

    val objectMapper = Json { serializersModule = module }
}

@Serializable
sealed class AnyValue {
    @Serializable
    @SerialName("StringValue")
    data class StringValue(val value: String) : AnyValue()

    @Serializable
    @SerialName("IntValue")
    data class IntValue(val value: Int) : AnyValue()

    @Serializable
    @SerialName("LongValue")
    data class LongValue(val value: Long) : AnyValue()

    @Serializable
    @SerialName("DoubleValue")
    data class DoubleValue(val value: Double) : AnyValue()

    @Serializable
    @SerialName("ListValue")
    data class ListValue(val value: List<AnyValue>) : AnyValue()

    @Serializable
    @SerialName("MapValue")
    data class MapValue(val value: Map<AnyValue, AnyValue>) : AnyValue()
}