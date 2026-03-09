package com.viewcompose.overlay.android.host

import android.view.View
import com.viewcompose.widget.core.OverlayHost
import com.viewcompose.widget.core.OverlayHostFactoryProvider

class AndroidOverlayHostFactoryProvider : OverlayHostFactoryProvider {
    override fun create(rootView: View): OverlayHost {
        return AndroidOverlayHost(rootView)
    }
}

