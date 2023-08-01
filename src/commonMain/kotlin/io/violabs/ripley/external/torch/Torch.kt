package io.violabs.ripley.external.torch

object Torch {
    fun arange(start: Long): LongTensor {
        return LongTensor()
    }

    fun zeros(size: List<Long>, dtype: DataType? = null, device: String? = null): LongTensor {
        return LongTensor()
    }

    abstract class Tensor<T> {

    }

    class LongTensor(val device: String? = null) : Tensor<Long>() {

        fun expand(dimensions: Pair<Long, Long>): LongTensor = apply {

        }

        // this would be the size of the matrix
        fun slice(rowSlice: LongRange = (0L..1L), currentColumnSlice: Long, endColumnSlice: Long): LongTensor {
            return LongTensor()
        }

        fun size(): List<Long> = emptyList()
    }
    open class FloatTensor : Tensor<Float>() {
        fun expand(dimensions: Pair<Float, Float>): FloatTensor = apply {

        }

        operator fun plus(other: FloatTensor): FloatTensor = apply {

        }

        operator fun get(key: LongTensor?): FloatTensor = FloatTensor()
        operator fun get(key: FloatTensor?): FloatTensor = FloatTensor()

        fun size(): List<Long> = emptyList()
    }

    object Jit {
        interface ScriptModule
    }

    enum class DataType(val representation: String) {
        LONG("torch.long")
    }
}