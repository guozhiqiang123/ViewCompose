package com.viewcompose.renderer.modifier

import com.viewcompose.ui.modifier.*

internal class ResolvedModifiers(
    // ViewModifierApplier fields
    var alpha: AlphaModifierElement? = null,
    var backgroundColor: BackgroundColorModifierElement? = null,
    var clickable: ClickableModifierElement? = null,
    var contentDescription: ContentDescriptionModifierElement? = null,
    var testTag: TestTagModifierElement? = null,
    var overlayAnchor: OverlayAnchorModifierElement? = null,
    var border: BorderModifierElement? = null,
    var cornerRadius: CornerRadiusModifierElement? = null,
    var clip: ClipModifierElement? = null,
    var elevation: ElevationModifierElement? = null,
    var offset: OffsetModifierElement? = null,
    var padding: PaddingModifierElement? = null,
    var systemBarsInsetsPadding: SystemBarsInsetsPaddingModifierElement? = null,
    var imeInsetsPadding: ImeInsetsPaddingModifierElement? = null,
    var minHeight: MinHeightModifierElement? = null,
    var minWidth: MinWidthModifierElement? = null,
    var visibility: VisibilityModifierElement? = null,
    var zIndex: ZIndexModifierElement? = null,
    // ViewLayoutParamsFactory fields
    var boxAlign: BoxAlignModifierElement? = null,
    var margin: MarginModifierElement? = null,
    var size: SizeModifierElement? = null,
    var width: WidthModifierElement? = null,
    var height: HeightModifierElement? = null,
    var weight: WeightModifierElement? = null,
    var horizontalAlign: HorizontalAlignModifierElement? = null,
    var verticalAlign: VerticalAlignModifierElement? = null,
)

internal fun Modifier.resolve(): ResolvedModifiers {
    val result = ResolvedModifiers()
    for (element in elements) {
        when (element) {
            is AlphaModifierElement -> result.alpha = element
            is BackgroundColorModifierElement -> result.backgroundColor = element
            is ClickableModifierElement -> result.clickable = element
            is ContentDescriptionModifierElement -> result.contentDescription = element
            is TestTagModifierElement -> result.testTag = element
            is OverlayAnchorModifierElement -> result.overlayAnchor = element
            is BorderModifierElement -> result.border = element
            is CornerRadiusModifierElement -> result.cornerRadius = element
            is ClipModifierElement -> result.clip = element
            is ElevationModifierElement -> result.elevation = element
            is OffsetModifierElement -> result.offset = element
            is PaddingModifierElement -> result.padding = element
            is SystemBarsInsetsPaddingModifierElement -> result.systemBarsInsetsPadding = element
            is ImeInsetsPaddingModifierElement -> result.imeInsetsPadding = element
            is MinHeightModifierElement -> result.minHeight = element
            is MinWidthModifierElement -> result.minWidth = element
            is VisibilityModifierElement -> result.visibility = element
            is ZIndexModifierElement -> result.zIndex = element
            is BoxAlignModifierElement -> result.boxAlign = element
            is MarginModifierElement -> result.margin = element
            is SizeModifierElement -> result.size = element
            is WidthModifierElement -> result.width = element
            is HeightModifierElement -> result.height = element
            is WeightModifierElement -> result.weight = element
            is HorizontalAlignModifierElement -> result.horizontalAlign = element
            is VerticalAlignModifierElement -> result.verticalAlign = element
            is LazyContainerReuseModifierElement -> { /* handled by lazy container binders */ }
            is FocusFollowKeyboardModifierElement -> { /* handled by lazy container binders */ }
            is NativeViewElement -> { /* handled separately in applyNativeViewConfigs */ }
        }
    }
    return result
}

internal fun layoutModifiersChanged(previous: ResolvedModifiers, next: ResolvedModifiers): Boolean {
    return previous.boxAlign != next.boxAlign ||
        previous.margin != next.margin ||
        previous.size != next.size ||
        previous.width != next.width ||
        previous.height != next.height ||
        previous.weight != next.weight ||
        previous.horizontalAlign != next.horizontalAlign ||
        previous.verticalAlign != next.verticalAlign
}
