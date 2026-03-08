package com.viewcompose.renderer.modifier

internal class ResolvedModifiers(
    // ViewModifierApplier fields
    @JvmField var alpha: AlphaModifierElement? = null,
    @JvmField var backgroundColor: BackgroundColorModifierElement? = null,
    @JvmField var clickable: ClickableModifierElement? = null,
    @JvmField var contentDescription: ContentDescriptionModifierElement? = null,
    @JvmField var testTag: TestTagModifierElement? = null,
    @JvmField var overlayAnchor: OverlayAnchorModifierElement? = null,
    @JvmField var border: BorderModifierElement? = null,
    @JvmField var cornerRadius: CornerRadiusModifierElement? = null,
    @JvmField var clip: ClipModifierElement? = null,
    @JvmField var elevation: ElevationModifierElement? = null,
    @JvmField var offset: OffsetModifierElement? = null,
    @JvmField var padding: PaddingModifierElement? = null,
    @JvmField var systemBarsInsetsPadding: SystemBarsInsetsPaddingModifierElement? = null,
    @JvmField var imeInsetsPadding: ImeInsetsPaddingModifierElement? = null,
    @JvmField var minHeight: MinHeightModifierElement? = null,
    @JvmField var minWidth: MinWidthModifierElement? = null,
    @JvmField var rippleColor: RippleColorModifierElement? = null,
    @JvmField var visibility: VisibilityModifierElement? = null,
    @JvmField var zIndex: ZIndexModifierElement? = null,
    // ViewLayoutParamsFactory fields
    @JvmField var boxAlign: BoxAlignModifierElement? = null,
    @JvmField var margin: MarginModifierElement? = null,
    @JvmField var size: SizeModifierElement? = null,
    @JvmField var width: WidthModifierElement? = null,
    @JvmField var height: HeightModifierElement? = null,
    @JvmField var weight: WeightModifierElement? = null,
    @JvmField var horizontalAlign: HorizontalAlignModifierElement? = null,
    @JvmField var verticalAlign: VerticalAlignModifierElement? = null,
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
            is RippleColorModifierElement -> result.rippleColor = element
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
