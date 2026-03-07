package com.gzq.uiframework.widget.core

internal class OverlayRequestStore {
    private val requests = mutableListOf<OverlayRequest>()

    fun beginRender() {
        requests.clear()
    }

    fun register(request: OverlayRequest) {
        requests += request
    }

    fun currentRequests(): List<OverlayRequest> = requests.toList()
}

internal object OverlayRequestContext {
    private val currentStore = ThreadLocal<OverlayRequestStore?>()

    fun <T> withStore(
        store: OverlayRequestStore,
        block: () -> T,
    ): T {
        val previous = currentStore.get()
        store.beginRender()
        currentStore.set(store)
        return try {
            block()
        } finally {
            currentStore.set(previous)
        }
    }

    fun currentStore(): OverlayRequestStore? = currentStore.get()
}

internal fun submitOverlayRequest(request: OverlayRequest) {
    OverlayRequestContext.currentStore()?.register(request)
}
