package com.gzq.uiframework.renderer.view.tree.patch

import android.content.res.ColorStateList
import android.widget.ImageButton
import android.widget.ImageView
import com.gzq.uiframework.renderer.view.tree.IconButtonNodePatch
import com.gzq.uiframework.renderer.view.tree.ImageNodePatch
import com.gzq.uiframework.renderer.view.tree.MediaViewBinder

internal object MediaNodePatchApplier {
    fun applyImagePatch(
        view: ImageView,
        patch: ImageNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        if (previous.contentDescription != next.contentDescription) {
            view.contentDescription = next.contentDescription
        }
        if (previous.contentScale != next.contentScale) {
            with(MediaViewBinder) {
                view.scaleType = next.contentScale.toScaleType()
            }
        }
        if (previous.tint != next.tint) {
            view.imageTintList = next.tint?.let(ColorStateList::valueOf)
        }
        val sourceChanged = previous.source != next.source ||
            previous.placeholder != next.placeholder ||
            previous.error != next.error ||
            previous.fallback != next.fallback ||
            previous.remoteImageLoader != next.remoteImageLoader
        if (sourceChanged) {
            MediaViewBinder.bindImage(
                view = view,
                spec = MediaViewBinder.ImageSpec(
                    contentDescription = next.contentDescription,
                    scaleType = with(MediaViewBinder) { next.contentScale.toScaleType() },
                    tint = next.tint,
                    source = next.source,
                    placeholder = next.placeholder,
                    error = next.error,
                    fallback = next.fallback,
                    remoteImageLoader = next.remoteImageLoader,
                ),
            )
        }
    }

    fun applyIconButtonPatch(
        view: ImageButton,
        patch: IconButtonNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        if (previous.contentDescription != next.contentDescription) {
            view.contentDescription = next.contentDescription
        }
        if (previous.contentScale != next.contentScale) {
            with(MediaViewBinder) {
                view.scaleType = next.contentScale.toScaleType()
            }
        }
        if (previous.tint != next.tint) {
            view.imageTintList = next.tint?.let(ColorStateList::valueOf)
        }
        val sourceChanged = previous.source != next.source ||
            previous.placeholder != next.placeholder ||
            previous.error != next.error ||
            previous.fallback != next.fallback ||
            previous.remoteImageLoader != next.remoteImageLoader
        if (sourceChanged) {
            MediaViewBinder.bindImage(
                view = view,
                spec = MediaViewBinder.ImageSpec(
                    contentDescription = next.contentDescription,
                    scaleType = with(MediaViewBinder) { next.contentScale.toScaleType() },
                    tint = next.tint,
                    source = next.source,
                    placeholder = next.placeholder,
                    error = next.error,
                    fallback = next.fallback,
                    remoteImageLoader = next.remoteImageLoader,
                ),
            )
        }
        if (previous.enabled != next.enabled) {
            view.isEnabled = next.enabled
        }
    }
}
