package com.viewcompose.host.android

import com.viewcompose.ui.UiContract

/**
 * Android host marker for render session and host bridge APIs.
 */
object UiHostAndroid {
    val dependencyChain: List<String> = UiContract.dependencyChain + "host-android"
}
