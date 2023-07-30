package io.violabs.ripley.common

class Kwargs(
    override val entries: MutableSet<MutableMap.MutableEntry<String, Any>>,
    override val keys: MutableSet<String>,
    override val values: MutableCollection<Any>
) : AbstractMutableMap<String, Any>(), MutableMap<String, Any> {
    override fun put(key: String, value: Any): Any? {
        keys.add(key)
        values.add(value)
        entries.add(KwargEntry(key, value))
        return value
    }

    inline fun <reified T> extract(key: String): T? {
        return this.remove(key) as? T
    }

    inline fun <reified T> extractDefault(key: String, default: T): T = extract(key) ?: default

    private class KwargEntry(override val key: String, override var value: Any) : MutableMap.MutableEntry<String, Any> {
        override fun setValue(newValue: Any): Any = newValue.also { value = it }

    }
}

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