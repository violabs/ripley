package io.violabs.ripley.common

val String?.safe: String
    get() = this ?: ""

val Int?.safe: Int
    get() = this ?: 0

inline fun <reified T> Any?.parseAsList(): List<T> {
    if (this !is List<*>) return emptyList()

    return this.filterIsInstance<T>()
}

inline fun <reified K, reified V> Any?.parseAsMap(): Map<K, V> {
    if (this !is Map<*, *>) return emptyMap()

    return this.mapNotNull { (key, value) ->
        if (key !is K) return@mapNotNull null
        if (value !is V) return@mapNotNull null

        key to value
    }.toMap()
}

inline fun <reified K, reified V> Any?.parseAsMultiValueMap(): Map<K, List<V>> {
    if (this !is Map<*, *>) return emptyMap()

    return this.mapNotNull { (key, value) ->
        if (key !is K) return@mapNotNull null
        val valueList = value as? List<*> ?: return@mapNotNull null

        val valueTypeList = valueList.filterIsInstance<V>()

        key to valueTypeList
    }.toMap()
}

typealias Matrix<T> = List<List<T>>

inline fun <reified T> Any?.parseAsMatrix(): Matrix<T> {
    if (this !is List<*>) return emptyList()

    return this.mapNotNull { value ->
        val valueList = value as? List<*> ?: return@mapNotNull null

        val valueTypeList = valueList.filterIsInstance<T>()

        valueTypeList
    }
}

@JvmInline
value class IfElse(private val previousEvalPredicate: () -> Boolean) {

    fun or(predicate: () -> Boolean): IfElse = IfElse {
        previousEvalPredicate() || predicate()
    }

    fun and(predicate: () -> Boolean): IfElse = IfElse {
        previousEvalPredicate() && predicate()
    }

    fun then(block: () -> Unit) {
        if (previousEvalPredicate()) {
            block()
        }
    }

    fun <T> thenReturn(ifReturn: T, elseReturn: T): T =
        ifReturn
            .takeIf { previousEvalPredicate() }
            ?: elseReturn
}

fun iff(condition:  Boolean): IfElse = IfElse { condition }