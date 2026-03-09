package com.viewcompose.preview.catalog

import com.viewcompose.preview.catalog.domain.CollectionPreviewSpecs
import com.viewcompose.preview.catalog.domain.ContainerPreviewSpecs
import com.viewcompose.preview.catalog.domain.ContentPreviewSpecs
import com.viewcompose.preview.catalog.domain.AnimationPreviewSpecs
import com.viewcompose.preview.catalog.domain.FeedbackPreviewSpecs
import com.viewcompose.preview.catalog.domain.GesturePreviewSpecs
import com.viewcompose.preview.catalog.domain.InputPreviewSpecs
import com.viewcompose.preview.catalog.domain.ModifierPreviewSpecs
import com.viewcompose.preview.catalog.domain.NavigationPreviewSpecs
import com.viewcompose.preview.catalog.model.PreviewDomain
import com.viewcompose.preview.catalog.model.PreviewSpec

internal object PreviewCatalog {
    val specs: List<PreviewSpec> by lazy {
        buildList {
            addAll(ContentPreviewSpecs.all)
            addAll(InputPreviewSpecs.all)
            addAll(ContainerPreviewSpecs.all)
            addAll(CollectionPreviewSpecs.all)
            addAll(NavigationPreviewSpecs.all)
            addAll(FeedbackPreviewSpecs.all)
            addAll(ModifierPreviewSpecs.all)
            addAll(AnimationPreviewSpecs.all)
            addAll(GesturePreviewSpecs.all)
        }
    }

    private val specsById: Map<String, PreviewSpec> by lazy {
        specs.associateBy(PreviewSpec::id)
    }

    fun byDomain(domain: PreviewDomain): List<PreviewSpec> {
        return specs.filter { it.domain == domain }
    }

    fun require(id: String): PreviewSpec {
        return requireNotNull(specsById[id]) { "Unknown preview spec id: $id" }
    }
}
