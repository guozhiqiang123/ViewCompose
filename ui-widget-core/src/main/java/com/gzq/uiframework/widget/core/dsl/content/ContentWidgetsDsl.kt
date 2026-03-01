package com.gzq.uiframework.widget.core

import android.content.Context
import android.view.View
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.size
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TextAlign
import com.gzq.uiframework.renderer.node.TextOverflow
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.props
import com.gzq.uiframework.renderer.node.spec.AndroidViewNodeProps
import com.gzq.uiframework.renderer.node.spec.ImageNodeProps
import com.gzq.uiframework.renderer.node.spec.TextNodeProps

fun UiTreeBuilder.Text(
    text: String,
    style: UiTextStyle = TextDefaults.bodyStyle(),
    color: Int = TextDefaults.primaryColor(),
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign = TextAlign.Start,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    emit(
        type = NodeType.Text,
        key = key,
        props = props {
            set(TypedPropKeys.Text, text)
            set(TypedPropKeys.TextColor, color)
            set(TypedPropKeys.TextSizeSp, style.fontSizeSp)
            set(TypedPropKeys.TextMaxLines, maxLines)
            set(TypedPropKeys.TextOverflow, overflow)
            set(TypedPropKeys.TextAlign, textAlign)
        },
        spec = TextNodeProps(
            text = text,
            maxLines = maxLines,
            overflow = overflow,
            textAlign = textAlign,
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.Image(
    source: ImageSource,
    contentDescription: String? = null,
    contentScale: ImageContentScale = ImageContentScale.Fit,
    tint: Int? = null,
    placeholder: ImageSource.Resource? = null,
    error: ImageSource.Resource? = placeholder,
    fallback: ImageSource.Resource? = placeholder,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    emit(
        type = NodeType.Image,
        key = key,
        props = props {
            set(TypedPropKeys.ImageSource, source)
            set(TypedPropKeys.ImageContentScale, contentScale)
            set(TypedPropKeys.ImageContentDescription, contentDescription)
            set(TypedPropKeys.ImageRemoteLoader, ImageLoading.current)
            set(TypedPropKeys.ImageTint, tint)
            set(TypedPropKeys.ImagePlaceholder, placeholder)
            set(TypedPropKeys.ImageError, error)
            set(TypedPropKeys.ImageFallback, fallback)
        },
        spec = ImageNodeProps(
            contentDescription = contentDescription,
            contentScale = contentScale,
            tint = tint,
            source = source,
            placeholder = placeholder,
            error = error,
            fallback = fallback,
            remoteImageLoader = ImageLoading.current,
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.Icon(
    source: ImageSource,
    contentDescription: String? = null,
    tint: Int = ContentColor.current,
    size: Int = 24.dp,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    Image(
        source = source,
        contentDescription = contentDescription,
        contentScale = ImageContentScale.Inside,
        tint = tint,
        key = key,
        modifier = Modifier
            .size(width = size, height = size)
            .then(modifier),
    )
}

fun UiTreeBuilder.AndroidView(
    factory: (Context) -> View,
    update: (View) -> Unit = {},
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    emit(
        type = NodeType.AndroidView,
        key = key,
        props = props {
            set(TypedPropKeys.ViewFactory, factory)
            set(TypedPropKeys.ViewUpdate, update)
        },
        spec = AndroidViewNodeProps(
            factory = factory,
            update = update,
        ),
        modifier = modifier,
    )
}
