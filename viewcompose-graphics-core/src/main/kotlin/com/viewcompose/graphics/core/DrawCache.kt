package com.viewcompose.graphics.core

class DrawCache<T> {
    private var cachedKey: Any? = null
    private var cachedValue: T? = null

    fun clear() {
        cachedKey = null
        cachedValue = null
    }

    fun getOrBuild(
        key: Any?,
        builder: () -> T,
    ): T {
        val cached = cachedValue
        if (cached != null && cachedKey == key) {
            return cached
        }
        val rebuilt = builder()
        cachedKey = key
        cachedValue = rebuilt
        return rebuilt
    }
}
