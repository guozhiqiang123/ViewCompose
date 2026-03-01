package com.gzq.uiframework.renderer.view.tree

import android.content.res.ColorStateList
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.RemoteImageLoader
import com.gzq.uiframework.renderer.node.RemoteImageRequest

internal object MediaViewBinder {
    fun bindImage(
        view: ImageView,
        contentDescription: String?,
        scaleType: ImageView.ScaleType,
        tint: Int?,
        source: ImageSource?,
        placeholder: ImageSource.Resource?,
        error: ImageSource.Resource?,
        fallback: ImageSource.Resource?,
        remoteImageLoader: RemoteImageLoader?,
    ) {
        view.contentDescription = contentDescription
        view.scaleType = scaleType
        view.imageTintList = tint?.let(ColorStateList::valueOf)

        when (source) {
            is ImageSource.Resource -> {
                view.setImageResource(source.resId)
            }
            is ImageSource.Remote -> {
                val normalizedUrl = source.url?.takeIf { it.isNotBlank() }
                if (normalizedUrl == null) {
                    bindPlaceholder(view, fallback)
                    return
                }
                if (remoteImageLoader == null) {
                    bindPlaceholder(view, error ?: placeholder ?: fallback)
                    return
                }
                bindPlaceholder(view, placeholder)
                remoteImageLoader.load(
                    imageView = view,
                    request = RemoteImageRequest(
                        url = normalizedUrl,
                        placeholderResId = placeholder?.resId,
                        errorResId = error?.resId,
                        fallbackResId = fallback?.resId,
                    ),
                )
            }
            null -> {
                view.setImageDrawable(null)
            }
        }
    }

    fun bindIconButton(
        view: ImageButton,
        enabled: Boolean,
    ) {
        view.isEnabled = enabled
        view.scaleType = ImageView.ScaleType.CENTER_INSIDE
        view.adjustViewBounds = false
    }

    private fun bindPlaceholder(
        view: ImageView,
        source: ImageSource.Resource?,
    ) {
        if (source == null) {
            view.setImageDrawable(null)
            return
        }
        view.setImageDrawable(
            ContextCompat.getDrawable(view.context, source.resId),
        )
    }
}
