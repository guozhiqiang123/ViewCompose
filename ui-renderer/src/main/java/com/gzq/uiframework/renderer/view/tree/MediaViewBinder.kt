package com.gzq.uiframework.renderer.view.tree

import android.content.res.ColorStateList
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.RemoteImageLoader
import com.gzq.uiframework.renderer.node.RemoteImageRequest
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.ImageContentScale

internal object MediaViewBinder {
    data class ImageSpec(
        val contentDescription: String?,
        val scaleType: ImageView.ScaleType,
        val tint: Int?,
        val source: ImageSource?,
        val placeholder: ImageSource.Resource?,
        val error: ImageSource.Resource?,
        val fallback: ImageSource.Resource?,
        val remoteImageLoader: RemoteImageLoader?,
    )

    fun bindImage(
        view: ImageView,
        spec: ImageSpec,
    ) {
        view.contentDescription = spec.contentDescription
        view.scaleType = spec.scaleType
        view.imageTintList = spec.tint?.let(ColorStateList::valueOf)

        when (val source = spec.source) {
            is ImageSource.Resource -> {
                view.setImageResource(source.resId)
            }
            is ImageSource.Remote -> {
                val normalizedUrl = source.url?.takeIf { it.isNotBlank() }
                if (normalizedUrl == null) {
                    bindPlaceholder(view, spec.fallback)
                    return
                }
                if (spec.remoteImageLoader == null) {
                    bindPlaceholder(view, spec.error ?: spec.placeholder ?: spec.fallback)
                    return
                }
                bindPlaceholder(view, spec.placeholder)
                spec.remoteImageLoader.load(
                    imageView = view,
                    request = RemoteImageRequest(
                        url = normalizedUrl,
                        placeholderResId = spec.placeholder?.resId,
                        errorResId = spec.error?.resId,
                        fallbackResId = spec.fallback?.resId,
                    ),
                )
            }
            null -> {
                view.setImageDrawable(null)
            }
        }
    }

    fun readImageSpec(node: VNode): ImageSpec {
        val scale = node.props.values[PropKeys.IMAGE_CONTENT_SCALE] as? ImageContentScale ?: ImageContentScale.Fit
        return ImageSpec(
            contentDescription = node.props.values[PropKeys.IMAGE_CONTENT_DESCRIPTION] as? String,
            scaleType = scale.toScaleType(),
            tint = node.props.values[PropKeys.IMAGE_TINT] as? Int,
            source = node.props.values[PropKeys.IMAGE_SOURCE] as? ImageSource,
            placeholder = node.props.values[PropKeys.IMAGE_PLACEHOLDER] as? ImageSource.Resource,
            error = node.props.values[PropKeys.IMAGE_ERROR] as? ImageSource.Resource,
            fallback = node.props.values[PropKeys.IMAGE_FALLBACK] as? ImageSource.Resource,
            remoteImageLoader = node.props.values[PropKeys.IMAGE_REMOTE_LOADER] as? RemoteImageLoader,
        )
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

    private fun ImageContentScale.toScaleType(): ImageView.ScaleType {
        return when (this) {
            ImageContentScale.Fit -> ImageView.ScaleType.FIT_CENTER
            ImageContentScale.Crop -> ImageView.ScaleType.CENTER_CROP
            ImageContentScale.FillBounds -> ImageView.ScaleType.FIT_XY
            ImageContentScale.Inside -> ImageView.ScaleType.CENTER_INSIDE
        }
    }
}
