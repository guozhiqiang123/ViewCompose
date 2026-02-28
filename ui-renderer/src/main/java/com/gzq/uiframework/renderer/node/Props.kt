package com.gzq.uiframework.renderer.node

data class Props(
    val values: Map<String, Any?>,
) {
    companion object {
        val Empty: Props = Props(emptyMap())
    }
}
