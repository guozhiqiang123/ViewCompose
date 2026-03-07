package com.viewcompose.renderer.node

data class Props(
    val values: Map<String, Any?>,
) {
    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(key: PropKey<T>): T? {
        return values[key.name] as? T
    }

    operator fun get(key: String): Any? {
        return values[key]
    }

    companion object {
        val Empty: Props = Props(emptyMap())
    }
}

class PropsBuilder {
    private val values = LinkedHashMap<String, Any?>()

    fun <T> set(
        key: PropKey<T>,
        value: T?,
    ) {
        if (value == null) {
            return
        }
        values[key.name] = value
    }

    fun setRaw(
        key: String,
        value: Any?,
    ) {
        if (value == null) {
            return
        }
        values[key] = value
    }

    fun build(): Props = Props(values.toMap())
}

fun props(
    block: PropsBuilder.() -> Unit,
): Props {
    return PropsBuilder()
        .apply(block)
        .build()
}
