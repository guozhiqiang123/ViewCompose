package com.gzq.uiframework.renderer.view.tree

import android.content.res.ColorStateList
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.RemoteImageLoader
import com.gzq.uiframework.renderer.node.RemoteImageRequest
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.spec.IconButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.ImageNodeSpec

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
        val spec = node.spec as? ImageNodeSpec
        if (spec != null) {
            return ImageSpec(
                contentDescription = spec.contentDescription,
                scaleType = spec.contentScale.toScaleType(),
                tint = spec.tint,
                source = spec.source,
                placeholder = spec.placeholder,
                error = spec.error,
                fallback = spec.fallback,
                remoteImageLoader = spec.remoteImageLoader,
            )
        }
        val scale = node.props[TypedPropKeys.ImageContentScale] ?: ImageContentScale.Fit
        return ImageSpec(
            contentDescription = node.props[TypedPropKeys.ImageContentDescription],
            scaleType = scale.toScaleType(),
            tint = node.props[TypedPropKeys.ImageTint],
            source = node.props[TypedPropKeys.ImageSource],
            placeholder = node.props[TypedPropKeys.ImagePlaceholder],
            error = node.props[TypedPropKeys.ImageError],
            fallback = node.props[TypedPropKeys.ImageFallback],
            remoteImageLoader = node.props[TypedPropKeys.ImageRemoteLoader],
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

    fun readIconButtonEnabled(node: VNode): Boolean {
        return (node.spec as? IconButtonNodeProps)?.enabled
            ?: (node.props[TypedPropKeys.Enabled] ?: true)
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
