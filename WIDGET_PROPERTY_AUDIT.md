# Widget Property Audit

> **审计日期**：2026-03-04
> **审计范围**：框架 P1 控件完整属性对照（16 种控件 × Android 原生属性）
> **框架版本**：16 NodeSpec · 11 Defaults · 27 Modifier · 35 DSL
> **底层映射**：Android View / AppCompat / Material Components

本文档系统性地将 Android 原生 View 属性与框架四层分类体系（Spec / Theme·Defaults / Modifier / nativeView）做完整对照，标注每个属性的当前状态，形成统一的改进规划。

**阅读指引**：
- §0 — 分类体系与图例，理解四层归属规则
- §1 — 16 控件覆盖率总览，快速定位薄弱环节
- §2 — `android.view.View` 基类通用属性（一次审计，适用所有控件）
- §3 — 16 控件逐一详细审计表
- §4 — 跨控件维度分析（缺失排序、命名一致性、提升候选）
- §5 — 四阶段改进行动计划
- §6 — 附录（审计方法、文件索引、原生 View 映射）

---

## §0 分类体系与图例

### 状态图例

| 图标 | 含义 | 说明 |
|------|------|------|
| ✅ | 已支持 | 框架已完整映射该原生属性 |
| 🔧 | 待改进 | 已部分支持，但需增强或修正 |
| 📋 | 待新增 | 业务常用，应纳入框架语义层 |
| ⚪ | nativeView/冷门 | 低频属性，走 `Modifier.nativeView(key, configure)` 逃生通道 |
| — | 不适用 | 该控件不涉及此属性 |

### 四层分类规则

> 摘自 MODIFIER.md §3-§4 + THEMING.md §5-§6

| 层级 | 职责 | 典型属性 |
|------|------|----------|
| **Spec** | 组件语义属性，由 NodeProps 承载 | `text`, `checked`, `onClick`, `variant`, `maxLines`, `contentScale` |
| **Theme / Defaults** | 默认颜色、字号、圆角、间距，由 Defaults 对象从 `Theme.colors` 即时派生 | `textColor`, `backgroundColor`, `cornerRadius`, `textSizeSp` |
| **Modifier** | 跨组件通用视觉/布局 | `padding`, `margin`, `backgroundColor`, `border`, `size`, `alpha`, `visibility` |
| **nativeView** | 冷门/低频原生属性 | `scrollbarStyle`, `overScrollMode`, `hapticFeedback`, `soundEffects` |

**归属判定流程**：

```
原生属性 → 是否是组件独有语义？
  ├── 是 → Spec（如 text, checked, maxLines）
  │         └── 其默认值是否应随主题变化？
  │               ├── 是 → Defaults 提供默认值（如 textColor, cornerRadius）
  │               └── 否 → DSL 硬编码默认值（如 Slider.min=0, TextArea.minLines=3）
  └── 否 → 是否跨组件通用？
        ├── 是 → Modifier（如 padding, alpha, size）
        └── 否 → nativeView 逃生通道
```

**特殊情况**：
- 部分属性同时出现在 Spec 和 Modifier（如 `backgroundColor`）：Spec 版本用于组件内部容器色（受 variant 驱动），Modifier 版本用于外部叠加
- Scoped Modifier（如 `RowScope.weight`）：属于父容器布局数据，仅在特定 Scope 内可用

### 已有 Modifier 函数清单（27 个）

| # | 函数 | 说明 |
|---|------|------|
| 1 | `padding(all)` | 四边统一内边距 |
| 2 | `padding(horizontal, vertical)` | 水平/垂直内边距 |
| 3 | `padding(left, top, right, bottom)` | 四边独立内边距 |
| 4 | `margin(all)` | 四边统一外边距 |
| 5 | `margin(horizontal, vertical)` | 水平/垂直外边距 |
| 6 | `margin(left, top, right, bottom)` | 四边独立外边距 |
| 7 | `backgroundColor(color)` | 背景色 |
| 8 | `border(width, color)` | 边框 |
| 9 | `cornerRadius(radius)` | 圆角 |
| 10 | `rippleColor(color)` | 水波纹色 |
| 11 | `size(width, height)` | 固定尺寸 |
| 12 | `width(width)` | 固定宽度 |
| 13 | `height(height)` | 固定高度 |
| 14 | `minHeight(minHeight)` | 最小高度 |
| 15 | `fillMaxWidth()` | 填满父宽 |
| 16 | `fillMaxHeight()` | 填满父高 |
| 17 | `fillMaxSize()` | 填满父容器 |
| 18 | `alpha(alpha)` | 透明度 |
| 19 | `visibility(visibility)` | 可见性 (Visible/Invisible/Gone) |
| 20 | `clickable(onClick)` | 点击事件 |
| 21 | `offset(x, y)` | 偏移 |
| 22 | `zIndex(zIndex)` | 层级 |
| 23 | `weight(weight)` | ⚠️ 已废弃，移至 RowScope/ColumnScope |
| 24 | `align(BoxAlignment)` | ⚠️ 已废弃，移至 BoxScope |
| 25 | `align(HorizontalAlignment)` | ⚠️ 已废弃，移至 ColumnScope |
| 26 | `align(VerticalAlignment)` | ⚠️ 已废弃，移至 RowScope |
| 27 | `nativeView(key, configure)` | 原生属性逃生通道 |

---

## §1 覆盖率总览表

> **统计口径**：仅统计 Android 原生 View 的 **业务可见属性**（排除内部系统属性如 tag、transitionName 等）。
> 通用 Modifier 覆盖（padding、margin、backgroundColor 等 18 项）适用于所有控件，不重复计入单控件覆盖率。

| 控件 | NodeSpec | Defaults | 底层 View | 原生属性数 | Spec 覆盖 | Defaults 覆盖 | Modifier 覆盖 | nativeView | 覆盖率 |
|------|----------|----------|-----------|-----------|-----------|--------------|--------------|------------|--------|
| Text | TextNodeProps | TextDefaults | TextView | 28 | 6 | 3 | 通用 | 19 | 32% |
| Image | ImageNodeProps | — | ImageView | 14 | 7 | 0 | 通用 | 7 | 50% |
| Icon | ImageNodeProps (via DSL) | — (硬编码) | ImageView | 14 | 4 | 1 | 通用 | 9 | 36% |
| Button | ButtonNodeProps | ButtonDefaults | Button | 22 | 14 | 8 | 通用 | 8 | 73% |
| IconButton | IconButtonNodeProps | IconButtonDefaults | ImageButton | 18 | 11 | 6 | 通用 | 7 | 72% |
| TextField | TextFieldNodeProps | TextFieldDefaults | EditText | 32 | 18 | 10 | 通用 | 14 | 56% |
| Checkbox | ToggleNodeProps | InputControlDefaults | CheckBox | 12 | 5 | 4 | 通用 | 7 | 50% |
| Switch | ToggleNodeProps | InputControlDefaults | Switch | 16 | 5 | 4 | 通用 | 11 | 38% |
| RadioButton | ToggleNodeProps | InputControlDefaults | RadioButton | 12 | 5 | 4 | 通用 | 7 | 50% |
| Slider | SliderNodeProps | InputControlDefaults | SeekBar | 14 | 5 | 2 | 通用 | 9 | 36% |
| ProgressIndicator | ProgressIndicatorNodeProps | ProgressIndicatorDefaults | Material PI | 10 | 5 | 6 | 通用 | 5 | 60% |
| Row | RowNodeProps | — | LinearLayout | 10 | 3 | 0 | 通用 | 7 | 30% |
| Column | ColumnNodeProps | — | LinearLayout | 10 | 3 | 0 | 通用 | 7 | 30% |
| Box | BoxNodeProps | — | FrameLayout | 6 | 1 | 0 | 通用 | 5 | 17% |
| LazyColumn | LazyColumnNodeProps | — | RecyclerView | 14 | 3 | 0 | 通用 | 11 | 21% |
| Divider | DividerNodeProps | DividerDefaults | View | 4 | 2 | 2 | 通用 | 0 | 100% |

**覆盖率分布**：
- 🟢 **高覆盖（≥60%）**：Button (73%), IconButton (72%), ProgressIndicator (60%), Divider (100%)
- 🟡 **中覆盖（40-59%）**：TextField (56%), Image (50%), Checkbox (50%), RadioButton (50%)
- 🔴 **低覆盖（<40%）**：Text (32%), Icon (36%), Switch (38%), Slider (36%), Row (30%), Column (30%), Box (17%), LazyColumn (21%)

**关键发现**：
1. **Text 覆盖率偏低**（32%）：缺少 fontWeight/fontFamily/letterSpacing/lineHeight 等排版核心属性，是最需要优先改进的控件
2. **Switch 被 Toggle 共享 Spec 拖累**（38%）：单一 `controlColor` 无法满足 thumb/track 分色需求
3. **布局容器覆盖率低但影响有限**：Row/Column/Box 未覆盖的属性多为冷门（divider、baseline 等）
4. **LazyColumn 缺少命令式 API**（21%）：scrollToPosition 等操作未暴露

---

## §2 通用 View 基础属性审计

以下属性源自 `android.view.View` 基类，适用于所有控件，仅审计一次。

### 2.1 已有 Modifier 覆盖

| 原生属性 | Android setter | Modifier 函数 | 状态 | 备注 |
|----------|---------------|---------------|------|------|
| padding | `setPadding(l,t,r,b)` | `padding(...)` (3 重载) | ✅ | 完整支持 |
| margin | `LayoutParams.setMargins` | `margin(...)` (3 重载) | ✅ | 完整支持 |
| backgroundColor | `setBackgroundColor` | `backgroundColor(color)` | ✅ | |
| border | `GradientDrawable.setStroke` | `border(width, color)` | ✅ | 仅支持实线，虚线走 nativeView |
| cornerRadius | `GradientDrawable.setCornerRadius` | `cornerRadius(radius)` | ✅ | 仅统一圆角，独立四角走 nativeView |
| alpha | `setAlpha` | `alpha(alpha)` | ✅ | |
| visibility | `setVisibility` | `visibility(Visible/Invisible/Gone)` | ✅ | |
| clickable | `setOnClickListener` | `clickable(onClick)` | ✅ | |
| width | `LayoutParams.width` | `width(width)` | ✅ | |
| height | `LayoutParams.height` | `height(height)` | ✅ | |
| size | `LayoutParams(w,h)` | `size(width, height)` | ✅ | |
| minHeight | `setMinimumHeight` | `minHeight(minHeight)` | ✅ | |
| fillMaxWidth | `MATCH_PARENT` | `fillMaxWidth()` | ✅ | |
| fillMaxHeight | `MATCH_PARENT` | `fillMaxHeight()` | ✅ | |
| fillMaxSize | `MATCH_PARENT` 双向 | `fillMaxSize()` | ✅ | |
| translationX/Y | `setTranslationX/Y` | `offset(x, y)` | ✅ | 映射为 translationX/Y |
| translationZ | `setTranslationZ` | `zIndex(zIndex)` | ✅ | |
| rippleEffect | `RippleDrawable` | `rippleColor(color)` | ✅ | |

### 2.2 缺失 / 待评估

| 原生属性 | Android setter | 建议归属 | 状态 | 备注 |
|----------|---------------|---------|------|------|
| elevation | `setElevation` | Modifier | 📋 | 高频需求，建议新增 `elevation(dp)` |
| shadow | `setOutlineProvider` + `elevation` | Modifier | 📋 | 可与 elevation 合并为 `shadow(elevation, color?)` |
| rotation | `setRotation` | Modifier | 📋 | 中频需求，建议新增 `rotation(degrees)` |
| rotationX/Y | `setRotationX/Y` | Modifier | ⚪ | 3D 旋转，低频 |
| scaleX/Y | `setScaleX/Y` | Modifier | 📋 | 中频需求，建议新增 `scale(x, y)` |
| clipToOutline | `setClipToOutline` | Modifier | 📋 | 配合 cornerRadius 裁剪内容 |
| clipChildren | `setClipChildren` | Modifier | ⚪ | 仅容器使用 |
| clipToPadding | `setClipToPadding` | Modifier | ⚪ | 仅容器使用 |
| focusable | `setFocusable` | Modifier | 📋 | accessibility 需求 |
| contentDescription | `setContentDescription` | Spec/Modifier | 📋 | accessibility，Image/Icon 已有，通用需求 |
| foreground | `setForeground` | Modifier | ⚪ | 前景 Drawable |
| tooltipText | `setTooltipText` | Modifier | ⚪ | API 26+，低频 |
| minWidth | `setMinimumWidth` | Modifier | 📋 | 与 minHeight 对称 |
| maxWidth/maxHeight | `setMaxWidth/MaxHeight` | Modifier | 📋 | 约束尺寸 |
| enabled | `setEnabled` | Spec | ✅ | 各控件 Spec 已包含 |
| layoutDirection | `setLayoutDirection` | Modifier | ⚪ | RTL 支持 |
| importantForAccessibility | `setImportantForAccessibility` | Modifier | ⚪ | |

### 2.3 归类为 nativeView（冷门/底层）

以下属性一律通过 `Modifier.nativeView(key) { view -> ... }` 处理：

| 原生属性 | Android setter |
|----------|---------------|
| scrollbarStyle | `setScrollBarStyle` |
| scrollbarSize | `setScrollBarSize` |
| scrollbarFadeDuration | `setScrollBarFadeDuration` |
| overScrollMode | `setOverScrollMode` |
| hapticFeedbackEnabled | `setHapticFeedbackEnabled` |
| soundEffectsEnabled | `setSoundEffectsEnabled` |
| keepScreenOn | `setKeepScreenOn` |
| filterTouchesWhenObscured | `setFilterTouchesWhenObscured` |
| fitsSystemWindows | `setFitsSystemWindows` |
| fadeScrollbars | `setScrollbarFadingEnabled` |
| nextFocusDown/Up/Left/Right | `setNextFocus*Id` |
| drawingCacheEnabled | `setDrawingCacheEnabled` |
| layerType | `setLayerType` |
| stateListAnimator | `setStateListAnimator` |
| outlineProvider | `setOutlineProvider` |
| accessibilityLiveRegion | `setAccessibilityLiveRegion` |
| accessibilityTraversalBefore/After | `setAccessibilityTraversal*` |
| tag | `setTag` |
| transitionName | `setTransitionName` |
| systemUiVisibility | `setSystemUiVisibility` |
| longClickable | `setLongClickable` |
| contextClickable | `setContextClickable` |
| saveEnabled | `setSaveEnabled` |
| duplicateParentState | `setDuplicateParentStateEnabled` |

---

## §3 逐控件详细审计

---

### 3.1 Text (→ `android.widget.TextView`)

**NodeSpec**: `TextNodeProps` · **Defaults**: `TextDefaults` · **DSL**: `Text()`

**框架 Spec 字段**：`text`, `maxLines`, `overflow`, `textAlign`, `textColor`, `textSizeSp`
**框架 Defaults 函数**：`currentStyle()`, `titleStyle()`, `bodyStyle()`, `labelStyle()`, `primaryColor()`, `secondaryColor()`
**DSL 参数**：`text`, `style`, `color`, `maxLines`, `overflow`, `textAlign`, `key`, `modifier`

#### 已支持属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| text | `setText` | Spec | ✅ | `TextNodeProps.text: CharSequence?`，支持 Spannable |
| textColor | `setTextColor` | Spec / Defaults | ✅ | Spec `textColor`; Defaults `primaryColor()` / `secondaryColor()` |
| textSize | `setTextSize` | Spec / Defaults | ✅ | Spec `textSizeSp`; Defaults 通过 `UiTextStyle` 传递 |
| maxLines | `setMaxLines` | Spec | ✅ | `TextNodeProps.maxLines`，默认 `Int.MAX_VALUE` |
| ellipsize | `setEllipsize` | Spec | ✅ | `TextNodeProps.overflow` → `TextOverflow.Clip/Ellipsis` |
| textAlignment | `setTextAlignment` | Spec | ✅ | `TextNodeProps.textAlign` → `TextAlign.Start/Center/End` |
| singleLine | `setSingleLine` | — | ✅ | 通过 `maxLines = 1` 语义等效 |
| gravity | `setGravity` | Spec | ✅ | 通过 `textAlign` 映射水平方向 |

#### 待新增 / 待改进属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| fontWeight | `setTypeface(tf, style)` | Spec (UiTextStyle) | 📋 | 当前 UiTextStyle 仅有 `fontSizeSp`，需扩展 `fontWeight: FontWeight?` |
| fontFamily | `setTypeface(Typeface)` | Spec (UiTextStyle) | 📋 | 需扩展 `fontFamily: Typeface?` 或 `fontFamily: String?` |
| letterSpacing | `setLetterSpacing` | Spec (UiTextStyle) | 📋 | 常用排版属性，`Float` 类型，单位 em |
| lineHeight | `setLineHeight` (API 28) | Spec (UiTextStyle) | 📋 | 常用排版属性，建议同时支持低版本 `setLineSpacing` 回退 |
| lineSpacingExtra | `setLineSpacing(extra, mult)` | Spec (UiTextStyle) | 📋 | lineHeight 的低版本替代，可合并到 lineHeight 实现中 |
| includeFontPadding | `setIncludeFontPadding` | Spec (UiTextStyle) | 📋 | 默认 true，常需关闭以精确对齐，建议默认 false |
| textDecoration (underline) | `setPaintFlags(UNDERLINE_TEXT_FLAG)` | Spec (UiTextStyle) | 📋 | 常见文本装饰，如下划线链接样式 |
| textDecoration (strikethrough) | `setPaintFlags(STRIKE_THRU_TEXT_FLAG)` | Spec (UiTextStyle) | 📋 | 常见文本装饰，如价格划线、删除标记 |
| textIsSelectable | `setTextIsSelectable` | Spec | 📋 | 长文本场景用户需要复制文字 |

#### nativeView / 冷门属性

| 原生属性 | Android setter | 当前状态 | 备注 |
|----------|---------------|---------|------|
| lineSpacingMultiplier | `setLineSpacing(extra, mult)` | ⚪ | 通常用 lineHeight 替代 |
| autoSizeTextType | `setAutoSizeTextTypeUniformWithConfiguration` | ⚪ | 自适应字号，有价值但实现复杂（Phase 3） |
| breakStrategy | `setBreakStrategy` | ⚪ | 文本换行策略（SIMPLE/HIGH_QUALITY/BALANCED） |
| hyphenationFrequency | `setHyphenationFrequency` | ⚪ | 连字号策略 |
| shadowColor/Dx/Dy/Radius | `setShadowLayer` | ⚪ | 文字阴影效果 |
| textAllCaps | `setAllCaps` | ⚪ | 全大写显示 |
| minLines | `setMinLines` | ⚪ | 低频，通常通过 minHeight 替代 |
| textScaleX | `setTextScaleX` | ⚪ | 水平缩放，极低频 |
| autoLink | `setAutoLinkMask` | ⚪ | 自动识别 URL/电话/邮箱链接 |
| linksClickable | `setLinksClickable` | ⚪ | 链接可点击 |
| marqueeRepeatLimit | `setMarqueeRepeatLimit` | ⚪ | 跑马灯重复次数 |
| justificationMode | `setJustificationMode` (API 26) | ⚪ | 两端对齐 |
| drawablePadding | `setCompoundDrawablePadding` | ⚪ | 复合 Drawable 间距 |
| compoundDrawable* | `setCompoundDrawablesRelative` | ⚪ | 文字四周 Drawable（Button 已支持 leading/trailing） |
| typeface bold/italic | `setTypeface(tf, BOLD\|ITALIC)` | ⚪ | 粗体/斜体（fontWeight 覆盖后可降级） |
| elegantTextHeight | `setElegantTextHeight` | ⚪ | CJK 文字高度优化 |
| fallbackLineSpacing | `setFallbackLineSpacing` | ⚪ | 回退字体行距 |
| firstBaselineToTopHeight | `setFirstBaselineToTopHeight` | ⚪ | 精确基线控制 |
| lastBaselineToBottomHeight | `setLastBaselineToBottomHeight` | ⚪ | 精确基线控制 |

---

### 3.2 Image (→ `android.widget.ImageView`)

**NodeSpec**: `ImageNodeProps` (implements `ImageNodeSpec`) · **Defaults**: 无 · **DSL**: `Image()`

**框架 Spec 字段**：`contentDescription`, `contentScale`, `tint`, `source`, `placeholder`, `error`, `fallback`, `remoteImageLoader`
**DSL 参数**：`source`, `contentDescription`, `contentScale`, `tint`, `placeholder`, `error`, `fallback`, `key`, `modifier`

#### 已支持属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| src / imageResource | `setImageResource/Drawable/URI` | Spec | ✅ | `source: ImageSource` 支持 Resource/Url/Drawable 三种来源 |
| contentDescription | `setContentDescription` | Spec | ✅ | `contentDescription: String?`，accessibility 关键属性 |
| scaleType | `setScaleType` | Spec | ✅ | `contentScale: ImageContentScale` → Fit/Crop/FillBounds/Inside |
| tint / imageTintList | `setImageTintList` | Spec | ✅ | `tint: Int?` |
| placeholder | — (Glide/Coil) | Spec | ✅ | `placeholder: ImageSource.Resource?` |
| error image | — (Glide/Coil) | Spec | ✅ | `error: ImageSource.Resource?`，DSL 默认 = placeholder |
| fallback image | — (Glide/Coil) | Spec | ✅ | `fallback: ImageSource.Resource?`，DSL 默认 = placeholder |

#### 待新增属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| adjustViewBounds | `setAdjustViewBounds` | Spec | 📋 | 常用于保持宽高比，`true` 时根据 src 自动调整边界 |
| maxWidth | `setMaxWidth` | Spec / Modifier | 📋 | 配合 adjustViewBounds 限制最大宽度 |
| maxHeight | `setMaxHeight` | Spec / Modifier | 📋 | 配合 adjustViewBounds 限制最大高度 |

#### nativeView / 冷门属性

| 原生属性 | Android setter | 当前状态 | 备注 |
|----------|---------------|---------|------|
| cropToPadding | `setCropToPadding` | ⚪ | 裁剪至 padding 边界，低频 |
| baseline | `setBaseline` | ⚪ | 与文字基线对齐场景 |
| baselineAlignBottom | `setBaselineAlignBottom` | ⚪ | 底部基线对齐 |
| colorFilter | `setColorFilter` | ⚪ | 颜色滤镜，tint 已覆盖大部分场景 |
| imageTintMode | `setImageTintMode` | ⚪ | tint 混合模式（SRC_IN 等） |
| imageAlpha | `setImageAlpha` | ⚪ | 图片透明度，通过 Modifier.alpha 替代 |
| imageMatrix | `setImageMatrix` | ⚪ | 自定义变换矩阵 |

---

### 3.3 Icon (→ `android.widget.ImageView` 子集)

**NodeSpec**: 复用 `ImageNodeProps` · **Defaults**: 无（DSL 内置默认值） · **DSL**: `Icon()`

**DSL 参数**：`source`, `contentDescription`, `tint`, `size`, `key`, `modifier`
**与 Image 的差异**：Icon 是 Image 的语义子集，DSL 层简化了参数（无 placeholder/error/fallback/contentScale），并内置了默认 tint (`ContentColor.current`) 和默认 size (`24.dp`)。

#### 已支持属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| src | `setImageResource` | Spec | ✅ | `source: ImageSource` |
| contentDescription | `setContentDescription` | Spec | ✅ | |
| tint | `setImageTintList` | Spec/DSL | ✅ | DSL 默认 `ContentColor.current`，随主题变化 |
| size | `LayoutParams` | DSL | ✅ | DSL 默认 `24.dp`，通过 `Modifier.size` 应用 |
| padding | `setPadding` | Modifier | ✅ | 通用 Modifier |

#### 待改进 / 待新增属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| scaleType | `setScaleType` | DSL | 🔧 | Icon DSL 硬编码 `ImageContentScale.Fit`，不可覆写 → 应暴露参数 |
| contentScale 可定制 | — | DSL | 📋 | DSL 应添加 `contentScale` 参数，默认 `Fit` |
| enabled/disabled 样式 | — | DSL | 📋 | 无内置 disabled 着色，需手动传 `tint = xxx` |
| IconDefaults 对象 | — | Defaults | 📋 | 当前 size/tint 硬编码在 DSL 中，应创建 `IconDefaults` |

#### nativeView / 冷门属性

| 原生属性 | Android setter | 当前状态 | 备注 |
|----------|---------------|---------|------|
| adjustViewBounds | `setAdjustViewBounds` | ⚪ | 图标场景不需要 |
| placeholder/error/fallback | — | — | 图标通常同步加载，不需要 |
| maxWidth/maxHeight | `setMax*` | ⚪ | 通过 Modifier.size 替代 |
| colorFilter | `setColorFilter` | ⚪ | tint 已覆盖 |
| minSize 约束 | — | ⚪ | 触摸目标大小 48dp，accessibility 相关，通常由外层 IconButton 负责 |

---

### 3.4 Button (→ `android.widget.Button`)

**NodeSpec**: `ButtonNodeProps` · **Defaults**: `ButtonDefaults` · **DSL**: `Button()`

**框架 Spec 字段**：`text`, `enabled`, `onClick`, `textColor`, `textSizeSp`, `backgroundColor`, `borderWidth`, `borderColor`, `cornerRadius`, `rippleColor`, `minHeight`, `paddingHorizontal`, `paddingVertical`, `leadingIcon`, `trailingIcon`, `iconTint`, `iconSize`, `iconSpacing`
**框架 Defaults 函数**：`containerColor(variant, enabled)`, `contentColor(variant, enabled)`, `borderWidth(variant)`, `borderColor(variant, enabled)`, `cornerRadius()`, `height(size)`, `horizontalPadding(size)`, `verticalPadding(size)`, `textStyle(size)`, `iconSize(size)`, `iconSpacing(size)`, `pressedColor()`
**DSL 参数**：`text`, `onClick`, `leadingIcon`, `trailingIcon`, `variant`, `size`, `enabled`, `style`, `key`, `modifier`
**变体**：`ButtonVariant.Primary/Secondary/Tonal/Outlined` · `ButtonSize.Compact/Medium/Large`

#### 已支持属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| text | `setText` | Spec | ✅ | `text: CharSequence?` |
| enabled | `setEnabled` | Spec | ✅ | 影响颜色选取和交互 |
| onClick | `setOnClickListener` | Spec | ✅ | |
| textColor | `setTextColor` | Spec / Defaults | ✅ | `contentColor(variant, enabled)` |
| textSize | `setTextSize` | Spec / Defaults | ✅ | `textStyle(size).fontSizeSp` → 14sp (Compact/Medium) / 16sp (Large) |
| backgroundColor | `setBackgroundColor` | Spec / Defaults | ✅ | `containerColor(variant, enabled)` → 4 种变体 × enabled/disabled |
| borderWidth | — | Spec / Defaults | ✅ | `borderWidth(variant)` → Outlined: 1dp, 其他: 0 |
| borderColor | — | Spec / Defaults | ✅ | `borderColor(variant, enabled)` |
| cornerRadius | — | Spec / Defaults | ✅ | `cornerRadius()` → `controlCornerRadius` (14dp) |
| rippleColor | `RippleDrawable` | Spec / Defaults | ✅ | `pressedColor()` |
| minHeight | `setMinHeight` | Spec / Defaults | ✅ | `height(size)` → Compact 36dp / Medium 44dp / Large 52dp |
| paddingHorizontal | `setPadding` | Spec / Defaults | ✅ | `horizontalPadding(size)` → 12/16/20 dp |
| paddingVertical | `setPadding` | Spec / Defaults | ✅ | `verticalPadding(size)` → 8/10/12 dp |
| leadingIcon | `setCompoundDrawablesRelative` | Spec | ✅ | `leadingIcon: ImageSource.Resource?` |
| trailingIcon | `setCompoundDrawablesRelative` | Spec | ✅ | `trailingIcon: ImageSource.Resource?` |
| iconTint | `DrawableCompat.setTint` | Spec / Defaults | ✅ | `iconTint = contentColor`，与文字同色 |
| iconSize | — | Spec / Defaults | ✅ | `iconSize(size)` → 16/18/20 dp |
| iconSpacing | `compoundDrawablePadding` | Spec / Defaults | ✅ | `iconSpacing(size)` → 6/8/10 dp |
| variant | — | DSL | ✅ | Primary/Secondary/Tonal/Outlined |
| size | — | DSL | ✅ | Compact/Medium/Large |

#### 待新增属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| elevation | `setElevation` | Modifier | 📋 | Material Button 常用浮起效果 |

#### nativeView / 冷门属性

| 原生属性 | Android setter | 当前状态 | 备注 |
|----------|---------------|---------|------|
| stateListAnimator | `setStateListAnimator` | ⚪ | 按压状态动画 |
| textAllCaps | `setAllCaps` | ⚪ | 原生 Button 默认全大写，框架应默认 false |
| gravity | `setGravity` | ⚪ | 文本对齐（通常居中） |
| drawablePadding | `setCompoundDrawablePadding` | ✅ | 已通过 iconSpacing 覆盖 |

---

### 3.5 IconButton (→ `android.widget.ImageButton`)

**NodeSpec**: `IconButtonNodeProps` (implements `ImageNodeSpec`) · **Defaults**: `IconButtonDefaults` · **DSL**: `IconButton()`

**框架 Spec 字段**：`contentDescription`, `contentScale`, `tint`, `source`, `placeholder`, `error`, `fallback`, `remoteImageLoader`, `enabled`, `backgroundColor`, `borderWidth`, `borderColor`, `cornerRadius`, `rippleColor`, `contentPadding`
**框架 Defaults 函数**：委托 `ButtonDefaults`，额外 `contentPadding(size)`, `size(size)`
**DSL 参数**：`icon`, `contentDescription`, `onClick`, `variant`, `size`, `enabled`, `key`, `modifier`

#### 已支持属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| icon/src | `setImageResource` | Spec | ✅ | `source: ImageSource` |
| contentDescription | `setContentDescription` | Spec | ✅ | accessibility 关键属性 |
| contentScale | `setScaleType` | Spec | ✅ | 默认 `ImageContentScale.Inside` |
| tint | `setImageTintList` | Spec / Defaults | ✅ | `contentColor(variant, enabled)` |
| enabled | — (DSL) | DSL | ✅ | DSL 条件应用 `Modifier.clickable` |
| backgroundColor | `setBackgroundColor` | Spec / Defaults | ✅ | `containerColor(variant, enabled)` |
| borderWidth | — | Spec / Defaults | ✅ | `borderWidth(variant)` |
| borderColor | — | Spec / Defaults | ✅ | `borderColor(variant, enabled)` |
| cornerRadius | — | Spec / Defaults | ✅ | `cornerRadius()` |
| rippleColor | `RippleDrawable` | Spec / Defaults | ✅ | `pressedColor()` |
| contentPadding | `setPadding` | Spec / Defaults | ✅ | `contentPadding(size)` → Compact 8dp / Medium 10dp / Large 12dp |
| size | `LayoutParams` | DSL | ✅ | DSL 通过 `Modifier.size` 设置，基于 `IconButtonDefaults.size(size)` |
| variant | — | DSL | ✅ | 复用 `ButtonVariant` |
| placeholder | `placeholder` | Spec | ✅ | 继承自 `ImageNodeSpec` |
| error/fallback | — | Spec | ✅ | 继承自 `ImageNodeSpec` |

#### 待新增属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| elevation | `setElevation` | Modifier | 📋 | 与 Button 共享需求 |

#### nativeView / 冷门属性

| 原生属性 | Android setter | 当前状态 | 备注 |
|----------|---------------|---------|------|
| scaleType | `setScaleType` | ⚪ | 已硬编码 Inside，图标按钮场景足够 |
| adjustViewBounds | `setAdjustViewBounds` | ⚪ | 图标按钮不需要 |

---

### 3.6 TextField (→ `EditText` / `DeclarativeTextFieldLayout`)

**NodeSpec**: `TextFieldNodeProps` · **Defaults**: `TextFieldDefaults` · **DSL**: `TextField()`, `PasswordField()`, `EmailField()`, `NumberField()`, `TextArea()`

**框架 Spec 字段**：`value`, `label`, `labelColor`, `labelTextSizeSp`, `supportingText`, `supportingTextColor`, `supportingTextSizeSp`, `placeholder`, `enabled`, `singleLine`, `minLines`, `maxLines`, `keyboardType`, `imeAction`, `hintColor`, `readOnly`, `onValueChange`, `textColor`, `textSizeSp`, `backgroundColor`, `borderWidth`, `borderColor`, `cornerRadius`, `rippleColor`, `minHeight`, `paddingHorizontal`, `paddingVertical`
**框架 Defaults 函数**：`textStyle(size)`, `textColor(enabled)`, `hintColor(isError, enabled)`, `labelColor(isError, enabled)`, `labelTextStyle()`, `supportingTextColor(isError, enabled)`, `supportingTextStyle()`, `containerColor(variant, isError, enabled)`, `borderColor(variant, isError, enabled)`, `borderWidth(variant)`, `cornerRadius()`, `height(size)`, `horizontalPadding(size)`, `verticalPadding(size)`, `pressedColor()`
**变体**：`TextFieldVariant.Filled/Tonal/Outlined` · `TextFieldSize.Compact/Medium/Large`
**便捷 DSL**：`PasswordField` (keyboardType=Password), `EmailField` (keyboardType=Email), `NumberField` (keyboardType=Number), `TextArea` (multiline)

#### 已支持属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| text/value | `setText` | Spec | ✅ | `value: String` |
| onValueChange | `addTextChangedListener` | Spec | ✅ | 双向绑定 |
| hint/placeholder | `setHint` | Spec | ✅ | `placeholder: String`，DSL 中 `hint` 参数映射到 placeholder |
| label | — (自定义浮动标签) | Spec | ✅ | `label: String`，由 DeclarativeTextFieldLayout 渲染 |
| supportingText | — (自定义辅助文字) | Spec | ✅ | `supportingText: String`，位于输入框下方 |
| labelColor | — | Spec / Defaults | ✅ | `labelColor(isError, enabled)` → error 红 / enabled 灰 / disabled 浅灰 |
| labelTextSize | — | Spec / Defaults | ✅ | `labelTextSizeSp` → `typography.label` |
| supportingTextColor | — | Spec / Defaults | ✅ | `supportingTextColor(isError, enabled)` |
| supportingTextSize | — | Spec / Defaults | ✅ | `supportingTextSizeSp` → `typography.label` |
| hintColor | `setHintTextColor` | Spec / Defaults | ✅ | `hintColor(isError, enabled)` |
| textColor | `setTextColor` | Spec / Defaults | ✅ | `textColor(enabled)` → textPrimary / textSecondary |
| textSize | `setTextSize` | Spec / Defaults | ✅ | `textStyle(size).fontSizeSp` → 14/16/16 sp |
| enabled | `setEnabled` | Spec | ✅ | 影响颜色和交互 |
| readOnly | `setFocusable(false)` | Spec | ✅ | 可查看但不可编辑 |
| singleLine | `setSingleLine` | Spec | ✅ | |
| maxLines | `setMaxLines` | Spec | ✅ | |
| minLines | `setMinLines` | Spec | ✅ | TextArea 默认 3 行 |
| inputType/keyboardType | `setInputType` | Spec | ✅ | `TextFieldType.Text/Password/Email/Number` |
| imeAction | `setImeOptions` | Spec | ✅ | `TextFieldImeAction.Default/Next/Done/Go/Search/Send` |
| backgroundColor | `setBackgroundColor` | Spec / Defaults | ✅ | `containerColor(variant, isError, enabled)` — Outlined 透明 / Tonal surfaceVariant / Filled surface |
| borderWidth | — | Spec / Defaults | ✅ | `borderWidth(variant)` → Outlined: 1dp, 其他: 0 |
| borderColor | — | Spec / Defaults | ✅ | `borderColor(variant, isError, enabled)` — error 红 / enabled primary / disabled divider |
| cornerRadius | — | Spec / Defaults | ✅ | `cornerRadius()` → `controlCornerRadius` (14dp) |
| rippleColor | `RippleDrawable` | Spec / Defaults | ✅ | `pressedColor()` |
| minHeight | `setMinHeight` | Spec / Defaults | ✅ | `height(size)` → Compact 40dp / Medium 48dp / Large 56dp |
| paddingHorizontal | `setPadding` | Spec / Defaults | ✅ | `horizontalPadding(size)` → 12/14/16 dp |
| paddingVertical | `setPadding` | Spec / Defaults | ✅ | `verticalPadding(size)` → 8/10/12 dp |
| variant | — | DSL | ✅ | Filled/Tonal/Outlined |
| size | — | DSL | ✅ | Compact/Medium/Large |
| isError | — | DSL | ✅ | 影响颜色选取（labelColor, borderColor, containerColor） |

#### 待新增属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| maxLength | `InputFilter.LengthFilter` | Spec | 📋 | 常用输入限制，如手机号 11 位、验证码 6 位 |
| cursorColor | `setTextCursorDrawable` (API 29) | Spec / Defaults | 📋 | 光标颜色，应跟随主题 primary |
| selectionHandleColor | `setTextSelectHandle*` | Defaults | ⚪ | 选择手柄颜色，低优先 |
| onImeAction | — | Spec | 📋 | 当前只设 imeAction 类型，未暴露回调（如搜索按钮点击） |

#### nativeView / 冷门属性

| 原生属性 | Android setter | 当前状态 | 备注 |
|----------|---------------|---------|------|
| textCursorDrawable | `setTextCursorDrawable` | ⚪ | 自定义光标 Drawable |
| autofillHints | `setAutofillHints` | ⚪ | API 26+ 自动填充提示 |
| inputFilters | `setFilters` | ⚪ | 自定义输入过滤器 |
| privateImeOptions | `setPrivateImeOptions` | ⚪ | 输入法私有配置 |
| extractEditText | `setExtractedText` | ⚪ | 全屏输入模式 |
| selectAllOnFocus | `setSelectAllOnFocus` | ⚪ | 聚焦时全选 |
| textSelectHandleLeft/Right | `setTextSelectHandle*` | ⚪ | 选择手柄 Drawable |
| digits | `setKeyListener` | ⚪ | 限制输入字符集 |
| inputMethod | `setRawInputType` | ⚪ | 自定义输入法绑定 |
| textColorHighlight | `setHighlightColor` | ⚪ | 选中文字高亮色 |
| drawableStart/End | `setCompoundDrawablesRelative` | ⚪ | 输入框内图标（可通过 nativeView 或自定义 layout） |

---

### 3.7 Checkbox (→ `android.widget.CheckBox`)

**NodeSpec**: `ToggleNodeProps` (与 Switch/RadioButton 共享) · **Defaults**: `InputControlDefaults` (共享) · **DSL**: `Checkbox()`

**框架 Spec 字段**：`text`, `enabled`, `checked`, `controlColor`, `onCheckedChange`, `textColor`, `textSizeSp`, `rippleColor`
**框架 Defaults 函数**：`labelStyle()`, `checkboxLabelColor(enabled)`, `checkboxControlColor(enabled)`, `pressedColor()`
**DSL 参数**：`text`, `checked`, `onCheckedChange`, `enabled`, `style`, `key`, `modifier`

> **共享 Spec 的影响**：Checkbox/Switch/RadioButton 共用 `ToggleNodeProps`，导致 Switch 特有属性（thumbColor/trackColor）无法独立配置。

#### 已支持属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| text | `setText` | Spec | ✅ | `text: CharSequence?` |
| checked | `setChecked` | Spec | ✅ | `checked: Boolean` |
| onCheckedChange | `setOnCheckedChangeListener` | Spec | ✅ | |
| enabled | `setEnabled` | Spec | ✅ | |
| controlColor | `CompoundButtonCompat.setButtonTintList` | Spec / Defaults | ✅ | `checkboxControlColor(enabled)` → primary / divider |
| textColor | `setTextColor` | Spec / Defaults | ✅ | `checkboxLabelColor(enabled)` → textPrimary / textSecondary |
| textSize | `setTextSize` | Spec / Defaults | ✅ | `labelStyle().fontSizeSp` → `typography.body` |
| rippleColor | `RippleDrawable` | Spec | ✅ | |

#### 待改进属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| buttonTint (checked/unchecked) | `setButtonTintList` | Spec / Defaults | 🔧 | 当前单一 `controlColor`，未区分 checked/unchecked 两种状态色 |
| compoundPadding | `setCompoundDrawablePadding` | Spec | ⚪ | 勾选框与文字间距，当前无法配置 |

#### nativeView / 冷门属性

| 原生属性 | Android setter | 当前状态 | 备注 |
|----------|---------------|---------|------|
| buttonDrawable | `setButtonDrawable` | ⚪ | 自定义勾选框 Drawable |
| buttonTintMode | `setButtonTintMode` | ⚪ | tint 混合模式 |

---

### 3.8 Switch (→ `android.widget.Switch` / `SwitchCompat`)

**NodeSpec**: `ToggleNodeProps` (与 Checkbox/RadioButton 共享) · **Defaults**: `InputControlDefaults` (共享) · **DSL**: `Switch()`

**框架 Spec 字段**：同 Checkbox（共享 `ToggleNodeProps`）
**框架 Defaults 函数**：`labelStyle()`, `switchLabelColor(enabled)`, `switchControlColor(enabled)`, `pressedColor()`
**DSL 参数**：`text`, `checked`, `onCheckedChange`, `enabled`, `style`, `key`, `modifier`

> **核心问题**：Switch 的 thumb（滑块）和 track（轨道）需要独立颜色配置，但当前被共享 Spec 的单一 `controlColor` 合并。这是 §5 Phase 1 的改进重点。

#### 已支持属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| text | `setText` | Spec | ✅ | `text: CharSequence?` |
| checked | `setChecked` | Spec | ✅ | |
| onCheckedChange | `setOnCheckedChangeListener` | Spec | ✅ | |
| enabled | `setEnabled` | Spec | ✅ | |
| textColor | `setTextColor` | Spec / Defaults | ✅ | `switchLabelColor(enabled)` |
| textSize | `setTextSize` | Spec / Defaults | ✅ | `labelStyle().fontSizeSp` |
| rippleColor | `RippleDrawable` | Spec | ✅ | |

#### 待改进属性（重点）

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| controlColor → 需拆分 | `setTrackTintList` + `setThumbTintList` | Spec / Defaults | 🔧 | **核心问题**：单一 `controlColor` 同时着色 thumb 和 track，实际需求常需不同色 |
| thumbTint | `setThumbTintList` | Spec / Defaults | 🔧 | 应独立为 `thumbColor: Int`，含 checked/unchecked 两态 |
| trackTint | `setTrackTintList` | Spec / Defaults | 🔧 | 应独立为 `trackColor: Int`，含 checked/unchecked 两态 |

**拆分方案**：
```
// 当前
data class ToggleNodeProps(..., val controlColor: Int, ...)

// 建议：Switch 独立 Spec 或在 ToggleNodeProps 增加可选字段
val thumbColor: Int? = null   // null 时回退到 controlColor
val trackColor: Int? = null   // null 时回退到 controlColor
```

#### nativeView / 冷门属性

| 原生属性 | Android setter | 当前状态 | 备注 |
|----------|---------------|---------|------|
| thumbDrawable | `setThumbDrawable` | ⚪ | 自定义滑块 Drawable |
| trackDrawable | `setTrackDrawable` | ⚪ | 自定义轨道 Drawable |
| trackDecorationDrawable | `setTrackDecorationDrawable` | ⚪ | Material Switch 专属装饰 |
| switchMinWidth | `setSwitchMinWidth` | ⚪ | Switch 最小宽度 |
| switchPadding | `setSwitchPadding` | ⚪ | Switch 与文字间距 |
| showText | `setShowText` | ⚪ | 开关上显示 ON/OFF 文字 |
| textOn/textOff | `setTextOn/Off` | ⚪ | 开关状态文字 |

---

### 3.9 RadioButton (→ `android.widget.RadioButton`)

**NodeSpec**: `ToggleNodeProps` (共享) · **Defaults**: `InputControlDefaults` (共享) · **DSL**: `RadioButton()`

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| text | `setText` | Spec | ✅ | `text: CharSequence?` |
| checked | `setChecked` | Spec | ✅ | |
| onCheckedChange | `setOnCheckedChangeListener` | Spec | ✅ | |
| enabled | `setEnabled` | Spec | ✅ | |
| controlColor | `CompoundButtonCompat.setButtonTintList` | Spec / Defaults | ✅ | `radioButtonControlColor(enabled)` |
| textColor | `setTextColor` | Spec / Defaults | ✅ | `radioButtonLabelColor(enabled)` |
| textSize | `setTextSize` | Spec / Defaults | ✅ | `labelStyle().fontSizeSp` |
| rippleColor | `RippleDrawable` | Spec | ✅ | |
| buttonDrawable | `setButtonDrawable` | — | ⚪ | 自定义单选按钮 Drawable |
| buttonTint | `setButtonTintList` | — | 🔧 | 当前为单一 controlColor，未拆分 selected/unselected |
| buttonTintMode | `setButtonTintMode` | — | ⚪ | |
| compoundPadding | `setCompoundDrawablePadding` | — | ⚪ | 单选按钮与文字间距 |

---

### 3.10 Slider (→ `android.widget.SeekBar`)

**NodeSpec**: `SliderNodeProps` · **Defaults**: `InputControlDefaults` (共享) · **DSL**: `Slider()`

**框架 Spec 字段**：`min`, `max`, `value`, `enabled`, `tintColor`, `onValueChange`
**框架 Defaults 函数**：`sliderControlColor(enabled)`
**DSL 参数**：`value`, `onValueChange`, `min`, `max`, `enabled`, `key`, `modifier`

> **命名问题**：`tintColor` 与其他 Toggle 控件的 `controlColor` 不一致，且同时着色 thumb 和 track，缺乏拆分能力。

#### 已支持属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| min | `setMin` | Spec | ✅ | `min: Int`，DSL 默认 0 |
| max | `setMax` | Spec | ✅ | `max: Int`，DSL 默认 100 |
| progress/value | `setProgress` | Spec | ✅ | `value: Int` |
| enabled | `setEnabled` | Spec | ✅ | |
| tintColor | `setProgressTintList` + `setThumbTintList` | Spec / Defaults | ✅ | `sliderControlColor(enabled)` → primary / divider |
| onValueChange | `setOnSeekBarChangeListener` | Spec | ✅ | |

#### 待改进 / 待新增属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| tintColor 命名 | — | Spec | 🔧 | 应改名为 `controlColor` 或拆分为 `thumbColor` + `trackColor` |
| thumbTint | `setThumbTintList` | Spec | 🔧 | 被 tintColor 合并，应独立 |
| progressTint | `setProgressTintList` | Spec | 🔧 | 被 tintColor 合并，应独立为 trackColor |
| stepSize | — (需手动量化) | Spec | 📋 | 步进值，Material Slider 原生支持，SeekBar 需手动实现 |

#### nativeView / 冷门属性

| 原生属性 | Android setter | 当前状态 | 备注 |
|----------|---------------|---------|------|
| thumbDrawable | `setThumb` | ⚪ | 自定义滑块 Drawable |
| progressDrawable | `setProgressDrawable` | ⚪ | 自定义进度条 Drawable |
| tickMark | `setTickMark` | ⚪ | 刻度标记 Drawable |
| thumbOffset | `setThumbOffset` | ⚪ | 滑块偏移量 |
| splitTrack | `setSplitTrack` | ⚪ | 轨道在滑块处分裂 |
| secondaryProgress | `setSecondaryProgress` | ⚪ | 缓冲进度（用于视频播放） |
| progressBackgroundTint | `setProgressBackgroundTintList` | ⚪ | 进度背景着色 |
| progressTintMode | `setProgressTintMode` | ⚪ | 进度着色混合模式 |
| mirrorForRtl | — | ⚪ | RTL 镜像 |

---

### 3.11 ProgressIndicator (→ Material `LinearProgressIndicator` / `CircularProgressIndicator`)

**NodeSpec**: `ProgressIndicatorNodeProps` · **Defaults**: `ProgressIndicatorDefaults` · **DSL**: `LinearProgressIndicator()`, `CircularProgressIndicator()`

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| progress | `setProgress` | Spec | ✅ | `progress: Float?`，null = indeterminate |
| enabled | — | Spec | ✅ | |
| indicatorColor | `setIndicatorColor` | Spec / Defaults | ✅ | `linearIndicatorColor()` / `circularIndicatorColor()` |
| trackColor | `setTrackColor` | Spec / Defaults | ✅ | `linearTrackColor()` / `circularTrackColor()` |
| trackThickness | `setTrackThickness` | Spec / Defaults | ✅ | `linearTrackThickness()` / `circularTrackThickness()` |
| indicatorSize (circular) | — | Spec / Defaults | ✅ | `circularSize()` → 32 dp |
| indeterminate | `setIndeterminate` | Spec | ✅ | `progress == null` 时自动 indeterminate |
| secondaryProgress | `setSecondaryProgress` | — | ⚪ | 缓冲进度 |
| min | `setMin` (API 26) | — | ⚪ | 通常为 0 |
| max | `setMax` | — | ⚪ | 通常为 100 |
| progressDrawable | `setProgressDrawable` | — | ⚪ | Material 组件自行管理 |
| indeterminateDrawable | `setIndeterminateDrawable` | — | ⚪ | |

---

### 3.12 Row (→ `LinearLayout` horizontal / `DeclarativeLinearLayout`)

**NodeSpec**: `RowNodeProps` · **Defaults**: 无 · **DSL**: `Row()`

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| spacing | — (自定义 itemDecoration) | Spec | ✅ | `spacing: Int` |
| arrangement | `setGravity` (主轴) | Spec | ✅ | `MainAxisArrangement.Start/Center/End/SpaceBetween/SpaceAround/SpaceEvenly` |
| verticalAlignment | `setGravity` (交叉轴) | Spec | ✅ | `VerticalAlignment.Top/Center/Bottom` |
| orientation | `setOrientation` | — | — | 硬编码 HORIZONTAL |
| gravity | `setGravity` | Spec | ✅ | 通过 arrangement + alignment 映射 |
| weightSum | `setWeightSum` | — | ⚪ | 框架通过 RowScope.weight 处理 |
| dividerDrawable | `setDividerDrawable` | — | ⚪ | 分隔线 Drawable |
| showDividers | `setShowDividers` | — | ⚪ | 分隔线显示位置 |
| dividerPadding | `setDividerPadding` | — | ⚪ | |
| baselineAligned | `setBaselineAligned` | — | ⚪ | 基线对齐 |
| measureWithLargestChild | `setMeasureWithLargestChild` | — | ⚪ | |

**Scoped Modifier**:

| 功能 | 框架归属 | 当前状态 |
|------|---------|---------|
| `RowScope.weight(Float)` | Scoped Modifier | ✅ |
| `RowScope.align(VerticalAlignment)` | Scoped Modifier | ✅ |
| `RowScope.FlexibleSpacer(weight)` | Scoped DSL | ✅ |

---

### 3.13 Column (→ `LinearLayout` vertical / `DeclarativeLinearLayout`)

**NodeSpec**: `ColumnNodeProps` · **Defaults**: 无 · **DSL**: `Column()`

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| spacing | — (自定义) | Spec | ✅ | `spacing: Int` |
| arrangement | `setGravity` (主轴) | Spec | ✅ | `MainAxisArrangement` |
| horizontalAlignment | `setGravity` (交叉轴) | Spec | ✅ | `HorizontalAlignment.Start/Center/End` |
| orientation | `setOrientation` | — | — | 硬编码 VERTICAL |
| gravity | `setGravity` | Spec | ✅ | 通过 arrangement + alignment 映射 |
| weightSum | `setWeightSum` | — | ⚪ | 框架通过 ColumnScope.weight 处理 |
| dividerDrawable | `setDividerDrawable` | — | ⚪ | |
| showDividers | `setShowDividers` | — | ⚪ | |
| dividerPadding | `setDividerPadding` | — | ⚪ | |
| baselineAligned | `setBaselineAligned` | — | ⚪ | |
| measureWithLargestChild | `setMeasureWithLargestChild` | — | ⚪ | |

**Scoped Modifier**:

| 功能 | 框架归属 | 当前状态 |
|------|---------|---------|
| `ColumnScope.weight(Float)` | Scoped Modifier | ✅ |
| `ColumnScope.align(HorizontalAlignment)` | Scoped Modifier | ✅ |
| `ColumnScope.FlexibleSpacer(weight)` | Scoped DSL | ✅ |

---

### 3.14 Box (→ `FrameLayout` / `DeclarativeBoxLayout`)

**NodeSpec**: `BoxNodeProps` · **Defaults**: 无 · **DSL**: `Box()`

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| contentAlignment | `LayoutParams.gravity` | Spec | ✅ | `BoxAlignment` 9 种对齐 |
| foregroundGravity | `setForegroundGravity` | — | ⚪ | |
| measureAllChildren | `setMeasureAllChildren` | — | ⚪ | |
| clipChildren | `setClipChildren` | — | ⚪ | |
| clipToPadding | `setClipToPadding` | — | ⚪ | |
| animateLayoutChanges | `LayoutTransition` | — | ⚪ | |

**Scoped Modifier**:

| 功能 | 框架归属 | 当前状态 |
|------|---------|---------|
| `BoxScope.align(BoxAlignment)` | Scoped Modifier | ✅ |

---

### 3.15 LazyColumn (→ `androidx.recyclerview.widget.RecyclerView`)

**NodeSpec**: `LazyColumnNodeProps` · **Defaults**: 无 · **DSL**: `LazyColumn()`

**框架 Spec 字段**：`contentPadding`, `spacing`, `items`
**DSL 参数**：`items`, `key`, `contentPadding`, `spacing`, `modifier`, `itemContent`

> **架构特点**：LazyColumn 是框架中最复杂的控件，底层使用 RecyclerView + 自定义 Adapter。由于 RecyclerView 的命令式 API（如 scrollToPosition）与框架声明式模型不兼容，很多能力未暴露。

#### 已支持属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| contentPadding | `setPadding` + `clipToPadding=false` | Spec | ✅ | `contentPadding: Int`，四边统一 |
| spacing | `addItemDecoration` | Spec | ✅ | `spacing: Int`，项间距 |
| items | `setAdapter` | Spec | ✅ | `items: List<LazyListItem>` + `itemContent` 函数，支持 DiffUtil |
| key | — (DiffUtil) | DSL | ✅ | `key: ((T) -> Any)?`，用于高效差异计算 |

#### 待新增属性

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| scrollToPosition | `scrollToPosition` | Spec (命令式) | 📋 | 需要设计命令式 API（如 LazyListState） |
| smoothScrollToPosition | `smoothScrollToPosition` | Spec (命令式) | 📋 | 平滑滚动 |
| clipToPadding | `setClipToPadding` | Spec | 🔧 | contentPadding 场景需设为 false，需确认框架是否自动处理 |
| contentPadding 四边独立 | `setPadding(l,t,r,b)` | Spec | 📋 | 当前仅支持统一 padding，应支持 `contentPadding(top, bottom, start, end)` |

#### nativeView / 冷门属性

| 原生属性 | Android setter | 当前状态 | 备注 |
|----------|---------------|---------|------|
| itemAnimator | `setItemAnimator` | ⚪ | 列表项增删动画，默认 DefaultItemAnimator |
| edgeEffectFactory | `setEdgeEffectFactory` | ⚪ | 过度滚动边缘效果 |
| nestedScrollingEnabled | `setNestedScrollingEnabled` | ⚪ | 嵌套滚动协调 |
| hasFixedSize | `setHasFixedSize` | ⚪ | 性能优化，列表大小固定时跳过 measure |
| overScrollMode | `setOverScrollMode` | ⚪ | 过度滚动效果模式 |
| scrollbarStyle | `setScrollBarStyle` | ⚪ | 滚动条样式 |
| layoutManager | `setLayoutManager` | — | 硬编码 `LinearLayoutManager(VERTICAL)` |
| recycledViewPool | `setRecycledViewPool` | ⚪ | 多 RecyclerView 共享 ViewHolder 池 |
| prefetchEnabled | `LayoutManager.isItemPrefetchEnabled` | ⚪ | 预加载优化 |
| reverseLayout | `LinearLayoutManager.reverseLayout` | ⚪ | 反向排列 |
| stackFromEnd | `LinearLayoutManager.stackFromEnd` | ⚪ | 从底部堆叠（聊天场景） |
| initialScrollPosition | — | ⚪ | 初始滚动位置 |

---

### 3.16 Divider (→ `android.view.View`)

**NodeSpec**: `DividerNodeProps` · **Defaults**: `DividerDefaults` · **DSL**: `Divider()`

| 原生属性 | Android setter | 框架归属 | 当前状态 | 备注 |
|----------|---------------|---------|---------|------|
| color | `setBackgroundColor` | Spec / Defaults | ✅ | `DividerDefaults.color()` → `Theme.colors.divider` |
| thickness | `LayoutParams.height` | Spec / Defaults | ✅ | `DividerDefaults.thickness()` → 1px |
| startIndent | `MarginLayoutParams.marginStart` | — | ⚪ | 通过 Modifier.margin 替代 |
| orientation | — | — | — | 当前仅水平分隔线 |

> Divider 已完备，仅 2 个语义属性均已覆盖。

---

## §4 跨控件分析

### 4.1 缺失能力优先级排序

按 **业务影响 × 实现难度** 排列。业务影响基于实际开发中的使用频率评估，实现难度基于框架当前架构的适配成本。

#### P0 — 阻塞性缺失（几乎每个项目都会遇到）

| 属性 | 影响控件 | 业务影响 | 实现难度 | 建议归属 | 说明 |
|------|---------|---------|---------|---------|------|
| fontWeight | Text, Button | ⭐⭐⭐⭐⭐ | 低 | UiTextStyle | 标题/正文/标签的粗细区分是最基本的排版需求 |
| fontFamily | Text, Button | ⭐⭐⭐⭐⭐ | 低 | UiTextStyle | 品牌字体切换，必须支持 |
| letterSpacing | Text | ⭐⭐⭐⭐ | 低 | UiTextStyle | 标题松散排版、全大写文字间距调整 |
| lineHeight | Text | ⭐⭐⭐⭐ | 低 | UiTextStyle | 控制行间距是排版基础 |
| Switch thumb/track 拆分 | Switch | ⭐⭐⭐⭐ | 低 | Spec + Defaults | 几乎所有设计稿都区分 thumb/track 颜色 |

#### P1 — 高频需求（多数项目需要）

| 属性 | 影响控件 | 业务影响 | 实现难度 | 建议归属 | 说明 |
|------|---------|---------|---------|---------|------|
| elevation / shadow | 全局 | ⭐⭐⭐⭐ | 中 | Modifier | 卡片、FAB、底栏阴影 |
| maxLength (TextField) | TextField | ⭐⭐⭐⭐ | 低 | Spec | 手机号/验证码/密码长度限制 |
| cursorColor (TextField) | TextField | ⭐⭐⭐ | 中 | Spec / Defaults | 光标颜色应跟随品牌色 |
| textDecoration | Text | ⭐⭐⭐ | 低 | UiTextStyle | 删除线（价格）、下划线（链接） |
| includeFontPadding | Text | ⭐⭐⭐ | 低 | UiTextStyle | 精确对齐常需关闭 |
| clipToOutline | 全局 | ⭐⭐⭐ | 低 | Modifier | 配合 cornerRadius 裁剪内容（如圆角图片） |
| contentDescription (通用) | 全局 | ⭐⭐⭐ | 低 | Modifier | accessibility 基础 |

#### P2 — 中频需求（部分项目需要）

| 属性 | 影响控件 | 业务影响 | 实现难度 | 建议归属 | 说明 |
|------|---------|---------|---------|---------|------|
| scrollToPosition | LazyColumn | ⭐⭐⭐ | 中 | Spec (命令式) | 锚点定位、返回顶部按钮 |
| adjustViewBounds | Image | ⭐⭐ | 低 | Spec | 保持宽高比自适应 |
| stepSize (Slider) | Slider | ⭐⭐ | 中 | Spec | 步进刻度（如音量 0-15） |
| textIsSelectable | Text | ⭐⭐ | 低 | Spec | 长文本复制 |
| minWidth | 全局 | ⭐⭐ | 低 | Modifier | 与 minHeight 对称 |
| rotation / scale | 全局 | ⭐⭐ | 低 | Modifier | 动画、变换需求 |

#### P3 — 低频需求（按需通过 nativeView 访问）

| 属性 | 影响控件 | 业务影响 | 建议 |
|------|---------|---------|------|
| autoSizeText | Text | ⭐⭐ | Phase 3，实现复杂 |
| focusable | 全局 | ⭐ | 视 accessibility 需求可升级 |
| Icon contentScale | Icon | ⭐ | DSL 参数暴露即可 |
| reverseLayout | LazyColumn | ⭐ | 聊天场景用 |

### 4.2 命名一致性检查

#### 4.2.1 padding 命名

| 控件 | Spec 字段名 | DSL 参数名 | Defaults 函数名 | 评估 |
|------|------------|-----------|----------------|------|
| Button | `paddingHorizontal` / `paddingVertical` | — (Defaults 注入) | `horizontalPadding(size)` / `verticalPadding(size)` | ✅ 标准模式 |
| TextField | `paddingHorizontal` / `paddingVertical` | — (Defaults 注入) | `horizontalPadding(size)` / `verticalPadding(size)` | ✅ 标准模式 |
| IconButton | `contentPadding` | — (Defaults 注入) | `contentPadding(size)` | ✅ 语义不同（内容到边界），命名合理 |
| LazyColumn | `contentPadding` | `contentPadding` | — | ✅ 语义不同（列表内容到容器边界），命名合理 |
| SegmentedControl | `horizontalPadding` / `verticalPadding` | — (Defaults 注入) | `horizontalPadding(size)` / `verticalPadding(size)` | 🔧 **不一致**：应改为 `paddingHorizontal` / `paddingVertical` |
| TabPager | `tabPaddingHorizontal` / `tabPaddingVertical` | 同名 DSL 参数 | `tabPaddingHorizontal()` / `tabPaddingVertical()` | ✅ 前缀 `tab` 有语义区分，可保留 |

**结论与建议**：
- `SegmentedControlNodeProps` 中 `horizontalPadding` → `paddingHorizontal`，`verticalPadding` → `paddingVertical`
- 对应 `SegmentedControlDefaults` 的函数名也需同步修改
- 其他控件命名一致，无需调整

#### 4.2.2 color 命名

| 控件 | 颜色字段 | 命名模式 | 评估 |
|------|---------|---------|------|
| Text | `textColor` | `{part}Color` | ✅ |
| Button | `textColor`, `iconTint`, `backgroundColor`, `borderColor`, `rippleColor` | `{part}Color` / `{part}Tint` | ✅ |
| TextField | `textColor`, `labelColor`, `hintColor`, `supportingTextColor`, `backgroundColor`, `borderColor`, `rippleColor` | `{part}Color` | ✅ 各部位独立 |
| Checkbox | `controlColor`, `textColor` | `controlColor` = 勾选框着色 | ✅ |
| Switch | `controlColor`, `textColor` | `controlColor` = thumb + track 合并 | 🔧 应拆分 |
| RadioButton | `controlColor`, `textColor` | `controlColor` = 单选按钮着色 | ✅ |
| Slider | `tintColor` | `tintColor` ≠ `controlColor` | 🔧 **不一致**：应改为 `controlColor` 或拆分 |
| ProgressIndicator | `indicatorColor`, `trackColor` | `{part}Color` | ✅ 拆分清晰 |
| SegmentedControl | `indicatorColor`, `textColor`, `selectedTextColor`, `backgroundColor` | `{part}Color` | ✅ |
| TabPager | `indicatorColor`, `selectedTextColor`, `unselectedTextColor`, `backgroundColor` | `{part}Color` | ✅ |
| Divider | `color` | 单一颜色 | ✅ |

**结论与建议**：
1. **Slider `tintColor`** → 改为 `controlColor`（与 Toggle 系列对齐），或直接拆分为 `thumbColor` + `trackColor`（与 ProgressIndicator 对齐）
2. **Switch `controlColor`** → 拆分为 `thumbColor` + `trackColor`（Spec 层面）
3. 其他控件颜色命名一致，无需调整

#### 4.2.3 size 命名

| 控件 | 尺寸字段 | 命名模式 | 评估 |
|------|---------|---------|------|
| Text | `textSizeSp` | `{part}SizeSp` | ✅ 明确单位 |
| Button | `textSizeSp`, `iconSize`, `iconSpacing`, `minHeight` | 混合 | ✅ 语义清晰 |
| ProgressIndicator | `indicatorSize`, `trackThickness` | size vs thickness | ✅ 直径 vs 粗细 |
| Divider | `thickness` | `thickness` | ✅ |
| Icon (DSL) | `size` → `Modifier.size` | `size` | ✅ 语义清晰 |
| LazyColumn | `spacing` | `spacing` | ✅ |
| Row/Column | `spacing` | `spacing` | ✅ |

**结论**：size 命名整体一致，无需调整。`textSizeSp` 的 `Sp` 后缀明确了单位，是好的实践。

### 4.3 Modifier 提升候选

当前在 Spec 中但可能更适合放到 Modifier 的属性：

| 属性 | 当前位置 | 出现次数 | 提升理由 | 结论 |
|------|---------|---------|---------|------|
| `backgroundColor` | Button/TextField/IconButton/SegmentedControl/TabPager Spec | 5 | 多个控件重复定义 | **不提升** — 这些是组件内部 container 色，受 variant/isError 驱动，与 Modifier.backgroundColor 语义不同（内部 vs 外部叠加） |
| `borderWidth` / `borderColor` | Button/TextField/IconButton Spec | 3 | 多个控件重复定义 | **不提升** — 受 variant/isError 驱动，属于语义样式，与 Modifier.border 语义不同 |
| `cornerRadius` | Button/TextField/IconButton/SegmentedControl/TabPager Spec | 5 | 多个控件重复定义 | **不提升** — 由 Theme.shapes 统一派生，是组件 Defaults 的一部分 |
| `rippleColor` | Button/TextField/IconButton/Toggle/SegmentedControl/TabPager | 6 | 几乎所有可交互控件都有 | **不提升** — 已有 Modifier.rippleColor 作为通用覆写通道，Spec 版本提供受控的 Defaults 值 |
| `minHeight` | Button/TextField Spec | 2 | 通用布局约束 | **不提升** — 已有 Modifier.minHeight，Spec 版本与 size 变体绑定 |
| `contentDescription` | ImageNodeSpec (Image/IconButton) | 2 | 可作为通用 accessibility Modifier | 📋 **可提升** — 所有 View 都支持 contentDescription，应作为通用 Modifier |

**结论**：仅 `contentDescription` 适合提升为通用 Modifier（如 `Modifier.semantics(contentDescription)`）。其余属性虽跨控件重复但语义上属于组件内部样式，Spec 中的值由 Defaults 提供，不应提升。

**进一步分析**：这些"重复"属性并不是真正的重复 — 它们在不同控件中的默认值不同（如 Button.cornerRadius 来自 `controlCornerRadius`，而 Surface.cornerRadius 来自 `cardCornerRadius`），且受不同的变体/状态驱动。这正是框架设计中 "Spec 承载语义，Modifier 承载通用" 的体现。

### 4.4 Theme/Defaults 补全候选

当前硬编码但应纳入 Defaults 的属性：

| 属性 | 当前硬编码位置 | 当前值 | 建议 | 优先级 |
|------|--------------|-------|------|--------|
| Icon size | `ContentWidgetsDsl.kt` `Icon()` 函数 | `24.dp` | 📋 创建 `IconDefaults.size()` → `24.dp` | Phase 2 |
| Icon tint | `ContentWidgetsDsl.kt` `Icon()` 函数 | `ContentColor.current` | 🔧 已通过 `ContentColor.current` 间接主题化，但缺少独立 `IconDefaults.tint()` | Phase 2 |
| Icon contentScale | `ContentWidgetsDsl.kt` `Icon()` 函数 | `Fit` (硬编码在 DSL 实现中) | ⚪ 图标通常固定 Fit，无需主题化 | — |
| Slider min/max | `InputWidgetsDsl.kt` `Slider()` 函数 | `0` / `100` | ⚪ 业务语义，不适合主题化 | — |
| TextArea minLines | `InputWidgetsDsl.kt` `TextArea()` 函数 | `3` | ⚪ 合理默认值，不需要主题化 | — |
| DividerDefaults thickness | `DividerDefaults.kt` | `1px` | 🔧 **应改为 `1.dp`** 以适配高密度屏幕（当前在 3x 屏上仅 0.33dp，几乎不可见） | Phase 3 |
| LazyColumn contentPadding | `CollectionWidgetsDsl.kt` | `0` | ⚪ 业务语义，无需主题化 | — |
| LazyColumn spacing | `CollectionWidgetsDsl.kt` | `0` | ⚪ 业务语义，无需主题化 | — |

**创建 IconDefaults 的建议实现**：

```kotlin
object IconDefaults {
    fun size(): Int = 24.dp
    fun tint(): Int = ContentColor.current
    fun contentScale(): ImageContentScale = ImageContentScale.Fit
}
```

---

## §5 改进行动计划

### Phase 1：快速收益（< 1 周）

> 均为低实现难度的 P0/P1 项，修改范围可控，对开发体验提升最大。

| # | 改进项 | 涉及文件 | 对应审计项 | 预估工作量 |
|---|--------|---------|-----------|-----------|
| 1.1 | **UiTextStyle 扩展**：新增 `fontWeight: FontWeight?`, `fontFamily: Typeface?`, `letterSpacing: Float?`, `lineHeight: Int?` 字段 | `UiTextStyle.kt`, `TextNodeProps` 不变（通过 CharSequence 传递）, Text 渲染器适配 | §3.1 fontWeight/fontFamily/letterSpacing/lineHeight 📋 | 1-2 天 |
| 1.2 | **Switch thumbColor/trackColor 拆分**：ToggleNodeProps 新增 `thumbColor: Int?` + `trackColor: Int?`（可选，null 回退 controlColor），InputControlDefaults 新增 `switchThumbColor(checked, enabled)` + `switchTrackColor(checked, enabled)` | `ToggleNodeProps.kt`, `InputControlDefaults.kt`, Switch 渲染器 | §3.8 controlColor 🔧, §3.8 thumbTint/trackTint 🔧 | 1 天 |
| 1.3 | **Slider 颜色拆分**：SliderNodeProps 的 `tintColor` → 拆为 `thumbColor: Int` + `trackColor: Int`，InputControlDefaults 新增对应函数 | `SliderNodeProps.kt`, `InputControlDefaults.kt`, Slider 渲染器, DSL | §3.10 tintColor 🔧, §3.10 thumbTint/progressTint 🔧 | 0.5 天 |
| 1.4 | **SegmentedControl padding 命名统一**：`horizontalPadding` → `paddingHorizontal`, `verticalPadding` → `paddingVertical` | `SegmentedControlNodeProps.kt`, `SegmentedControlDefaults.kt`, `ActionWidgetsDsl.kt` | §4.2.1 padding 命名 🔧 | 0.5 天 |
| 1.5 | **TextField maxLength**：TextFieldNodeProps 新增 `maxLength: Int? = null`，渲染器通过 `InputFilter.LengthFilter` 实现，DSL 新增参数 | `TextFieldNodeProps.kt`, TextField 渲染器, `InputWidgetsDsl.kt` | §3.6 maxLength 📋 | 0.5 天 |
| 1.6 | **includeFontPadding**：UiTextStyle 新增 `includeFontPadding: Boolean = false`，Text 渲染器调用 `setIncludeFontPadding` | `UiTextStyle.kt`, Text 渲染器 | §3.1 includeFontPadding 📋 | 0.5 天 |

### Phase 2：中等工作量（1-2 周）

> P1 优先级项，需要新增 ModifierElement 或涉及较大 API 设计。

| # | 改进项 | 涉及文件 | 对应审计项 | 预估工作量 |
|---|--------|---------|-----------|-----------|
| 2.1 | **elevation / shadow Modifier**：新增 `Modifier.elevation(dp: Int)` → `ElevationModifierElement`，渲染器调用 `view.elevation` + `view.outlineProvider` | `Modifier.kt`, 通用渲染器 `ModifierApplier` | §2.2 elevation/shadow 📋 | 1-2 天 |
| 2.2 | **textDecoration**：UiTextStyle 新增 `textDecoration: Set<TextDecoration>?`，枚举 `TextDecoration.Underline/LineThrough`，渲染器通过 `paintFlags` 实现 | `UiTextStyle.kt`, Text 渲染器 | §3.1 textDecoration 📋 | 1 天 |
| 2.3 | **TextField cursorColor**：TextFieldNodeProps 新增 `cursorColor: Int?`，TextFieldDefaults 新增 `cursorColor()` → `Theme.colors.primary`，渲染器通过 `setTextCursorDrawable` (API 29+) 或反射实现 | `TextFieldNodeProps.kt`, `TextFieldDefaults.kt`, TextField 渲染器, DSL | §3.6 cursorColor 📋 | 1 天 |
| 2.4 | **LazyColumn scrollToPosition API**：设计 `LazyListState` 接口，DSL 新增 `state` 参数，通过命令式 API 桥接 `RecyclerView.scrollToPosition` | `LazyColumnNodeProps.kt`, RecyclerView 渲染器, DSL | §3.15 scrollToPosition 📋 | 2-3 天 |
| 2.5 | **clipToOutline Modifier**：新增 `Modifier.clip()` → `ClipModifierElement`，渲染器调用 `view.clipToOutline = true` | `Modifier.kt`, 通用渲染器 | §2.2 clipToOutline 📋 | 0.5 天 |
| 2.6 | **contentDescription 通用 Modifier**：新增 `Modifier.semantics(contentDescription: String?)` → `SemanticsModifierElement`，渲染器调用 `view.contentDescription` | `Modifier.kt`, 通用渲染器 | §2.2 / §4.3 contentDescription 📋 | 0.5 天 |
| 2.7 | **minWidth Modifier**：新增 `Modifier.minWidth(minWidth: Int)` → `MinWidthModifierElement`，渲染器调用 `view.minimumWidth` | `Modifier.kt` | §2.2 minWidth 📋 | 0.5 天 |
| 2.8 | **Checkbox/RadioButton buttonTint 拆分**：ToggleNodeProps 新增 `checkedColor: Int?` + `uncheckedColor: Int?`，InputControlDefaults 新增对应函数 | `ToggleNodeProps.kt`, `InputControlDefaults.kt`, Checkbox/RadioButton 渲染器 | §3.7/§3.9 buttonTint 🔧 | 1 天 |
| 2.9 | **IconDefaults 创建**：新建 `IconDefaults` 对象，提供 `size()`, `tint()` 函数，Icon DSL 使用 Defaults 替代硬编码 | `defaults/content/IconDefaults.kt`, `ContentWidgetsDsl.kt` | §4.4 Icon 硬编码 📋 | 0.5 天 |

### Phase 3：长期优化

> P2 优先级项，涉及较复杂的原生 API 适配或新 Spec 设计。

| # | 改进项 | 涉及文件 | 对应审计项 | 预估工作量 |
|---|--------|---------|-----------|-----------|
| 3.1 | **autoSizeText**：Text 新增 `autoSize: AutoSizeConfig?`，渲染器调用 `setAutoSizeTextTypeUniformWithConfiguration` | `TextNodeProps.kt`, Text 渲染器 | §3.1 autoSizeTextType ⚪→📋 | 2-3 天 |
| 3.2 | **textIsSelectable**：TextNodeProps 新增 `selectable: Boolean = false`，渲染器调用 `setTextIsSelectable` | `TextNodeProps.kt`, Text 渲染器 | §3.1 textIsSelectable 📋 | 0.5 天 |
| 3.3 | **Slider stepSize**：SliderNodeProps 新增 `stepSize: Int? = null`，渲染器量化 onProgressChanged 值 | `SliderNodeProps.kt`, Slider 渲染器 | §3.10 stepSize 📋 | 1 天 |
| 3.4 | **Image adjustViewBounds**：ImageNodeProps 新增 `adjustViewBounds: Boolean = false`，渲染器调用 `setAdjustViewBounds` | `ImageNodeProps.kt`, Image 渲染器 | §3.2 adjustViewBounds 📋 | 0.5 天 |
| 3.5 | **rotation / scale Modifier**：新增 `Modifier.rotation(degrees)`, `Modifier.scale(x, y)` | `Modifier.kt` | §2.2 rotation/scale 📋 | 1 天 |
| 3.6 | **LazyColumn 高级配置**：`hasFixedSize`, `clipToPadding` 自动处理，`reverseLayout` 参数 | `LazyColumnNodeProps.kt` | §3.15 高级配置 ⚪ | 1-2 天 |
| 3.7 | **DividerDefaults.thickness 单位修正**：`1px` → `1.dp` | `DividerDefaults.kt` | §4.4 thickness 硬编码 🔧 | 0.5 天 |
| 3.8 | **Icon contentScale 暴露**：Icon DSL 添加 `contentScale: ImageContentScale = IconDefaults.contentScale()` 参数 | `ContentWidgetsDsl.kt` | §3.3 contentScale 📋 | 0.5 天 |
| 3.9 | **TextField onImeAction 回调**：TextFieldNodeProps 新增 `onImeAction: (() -> Unit)?`，渲染器设置 `setOnEditorActionListener` | `TextFieldNodeProps.kt`, TextField 渲染器, DSL | §3.6 onImeAction 📋 | 1 天 |
| 3.10 | **LazyColumn contentPadding 四边独立**：`contentPadding: Int` → `contentPadding: ContentPadding` 或重载 | `LazyColumnNodeProps.kt`, DSL | §3.15 contentPadding 📋 | 1 天 |
| 2.4 | **LazyColumn scrollToPosition API**：新增命令式滚动 API | `LazyColumnNodeProps.kt`, RecyclerView 渲染器 | §3.15 scrollToPosition 📋 |
| 2.5 | **clipToOutline Modifier**：新增 `Modifier.clip(shape)` | `Modifier.kt`, 通用渲染器 | §2.2 clipToOutline 📋 |
| 2.6 | **contentDescription 通用 Modifier**：新增 `Modifier.semantics(contentDescription)` | `Modifier.kt`, 通用渲染器 | §2.2 / §4.3 contentDescription 📋 |
| 2.7 | **minWidth Modifier**：新增 `Modifier.minWidth(minWidth)` | `Modifier.kt` | §2.2 minWidth 📋 |
| 2.8 | **Checkbox/RadioButton buttonTint 拆分**：区分 checked/unchecked 状态颜色 | `ToggleNodeProps.kt`, `InputControlDefaults.kt` | §3.7/§3.9 buttonTint 🔧 |
| 2.9 | **IconDefaults 创建**：新建 `IconDefaults` 对象，管理 `size()`, `tint()` | `defaults/content/IconDefaults.kt` | §4.4 Icon 硬编码 📋 |

### Phase 3：长期优化

| # | 改进项 | 涉及文件 | 对应 §3 审计项 |
|---|--------|---------|---------------|
| 3.1 | **autoSizeText**：Text 新增自适应字号支持 | `TextNodeProps.kt`, Text 渲染器 | §3.1 autoSizeTextType ⚪→📋 |
| 3.2 | **textIsSelectable**：Text 新增 `selectable: Boolean` | `TextNodeProps.kt`, Text 渲染器 | §3.1 textIsSelectable 📋 |
| 3.3 | **Slider stepSize**：新增步进值支持 | `SliderNodeProps.kt`, Slider 渲染器 | §3.10 stepSize 📋 |
| 3.4 | **Image adjustViewBounds**：新增自适应宽高比 | `ImageNodeProps.kt`, Image 渲染器 | §3.2 adjustViewBounds 📋 |
| 3.5 | **rotation / scale Modifier**：新增变换 Modifier | `Modifier.kt` | §2.2 rotation/scale 📋 |
| 3.6 | **LazyColumn 高级配置**：`hasFixedSize`, `nestedScrollingEnabled`, `clipToPadding` | `LazyColumnNodeProps.kt` | §3.15 高级配置 ⚪ |
| 3.7 | **DividerDefaults.thickness 单位修正**：`1px` → `1.dp` | `DividerDefaults.kt` | §4.4 thickness 硬编码 🔧 |
| 3.8 | **Icon contentScale 暴露**：DSL 添加 `contentScale` 参数 | `ContentWidgetsDsl.kt` | §3.3 contentScale 📋 |

### Phase 4：按需（走 nativeView）

> 所有标记为 ⚪ nativeView 的属性默认通过 `Modifier.nativeView(key) { view -> ... }` 逃生通道访问，**不纳入框架语义层**。用户按需使用，框架不保证稳定性。

| 类别 | 属性示例 | 使用方式 |
|------|---------|---------|
| View 底层 | scrollbarStyle, overScrollMode, hapticFeedback, soundEffects, keepScreenOn, layerType | `Modifier.nativeView("scrollbar") { it.isScrollbarFadingEnabled = false }` |
| View 焦点 | nextFocusDown/Up/Left/Right, focusable (视 accessibility 需求可升级) | `Modifier.nativeView("focus") { it.isFocusable = true }` |
| View 动画 | stateListAnimator, animateLayoutChanges | `Modifier.nativeView("anim") { it.stateListAnimator = null }` |
| Text 冷门 | autoLink, marqueeRepeatLimit, textScaleX, breakStrategy, hyphenation, justificationMode | `Modifier.nativeView("autolink") { (it as TextView).autoLinkMask = Linkify.WEB_URLS }` |
| Image 冷门 | cropToPadding, baseline, colorFilter | `Modifier.nativeView("crop") { (it as ImageView).cropToPadding = true }` |
| Toggle 冷门 | buttonDrawable, buttonTintMode, switchMinWidth, showText, textOn/textOff | `Modifier.nativeView("switch") { (it as Switch).showText = true }` |
| Slider 冷门 | thumbDrawable, progressDrawable, tickMark, thumbOffset, splitTrack | `Modifier.nativeView("thumb") { (it as SeekBar).splitTrack = false }` |
| Layout 冷门 | dividerDrawable, showDividers, dividerPadding, baselineAligned, measureWithLargestChild | `Modifier.nativeView("divider") { (it as LinearLayout).showDividers = SHOW_DIVIDER_MIDDLE }` |
| RecyclerView 冷门 | itemAnimator, edgeEffectFactory, recycledViewPool, prefetchEnabled | `Modifier.nativeView("rv") { (it as RecyclerView).setHasFixedSize(true) }` |

**nativeView 升级规则**：当某个 ⚪ 属性在实际项目中被 3 个以上场景使用，应考虑升级到 Spec/Modifier 层。

### Phase 小结

| 阶段 | 改进项数 | 预估总工作量 | 覆盖率提升预期 |
|------|---------|------------|--------------|
| Phase 1 | 6 项 | 3-4 天 | Text 32% → 50%, Switch 38% → 50% |
| Phase 2 | 9 项 | 6-8 天 | 全局 +10%（elevation, clip, semantics） |
| Phase 3 | 10 项 | 6-8 天 | Text 50% → 60%, LazyColumn 21% → 35% |
| Phase 4 | 按需 | 持续 | 不影响覆盖率统计 |

---

## §6 附录

### 6.1 审计方法

1. **源码扫描**：逐一读取框架全部关键源文件
   - 16 个 NodeSpec 文件（`ui-renderer/.../node/spec/`）
   - 11 个 Defaults 文件（`ui-widget-core/.../defaults/`）
   - Modifier.kt（27 个扩展函数 + 20 个 ModifierElement 数据类）
   - 6 个 DSL 文件（35 个 DSL 函数）
2. **原生 API 对照**：对照每个控件底层 Android View 类（通过 ViewNodeFactory 确认映射关系）的公开 setter 和 XML attr 属性
3. **分类评估**：按框架四层体系（Spec / Theme·Defaults / Modifier / nativeView）逐一归类每个原生属性
4. **状态标注**：基于实际代码实现情况标注 ✅ 已支持 / 🔧 待改进 / 📋 待新增 / ⚪ nativeView/冷门 / — 不适用
5. **交叉验证**：
   - 确认所有 27 个 Modifier 函数在 §0 清单 + §2 通用属性表中完整列出
   - 确认所有 11 个 Defaults 对象的公开函数在对应控件表的 Theme/Defaults 列中体现
   - 确认 §5 行动计划中的每一项都能追溯到 §3 或 §2 中某个 📋/🔧 状态的属性
   - 确认所有 16 种审计控件都有对应的 §3 子节

### 6.2 关联文档索引

| 文档 | 路径 | 相关内容 |
|------|------|---------|
| Modifier 设计规范 | `MODIFIER.md` | §3 设计目标、§4 职责划分 — 定义了 Modifier vs Spec vs Theme 的边界 |
| 主题系统规范 | `THEMING.md` | §5 当前实现、§6 推荐分层 — 定义了 Theme → Defaults → Props → Renderer 链路 |
| 控件路线图 | `WIDGET_ROADMAP.md` | §3 属性设计原则 — 定义了 5 层属性分类和暴露原则 |
| 架构总览 | `ARCHITECTURE.md` | 框架整体架构 — VNode、Renderer、ViewBinder 的关系 |

### 6.3 文件索引

| 类别 | 路径 | 文件数 | 关键内容 |
|------|------|--------|---------|
| NodeSpec 定义 | `ui-renderer/src/main/java/.../node/spec/` | 16 | 每个控件的属性定义 |
| Defaults 对象 | `ui-widget-core/src/main/java/.../defaults/` | 11 | 主题派生的默认值函数 |
| Modifier 函数 | `ui-renderer/src/main/java/.../modifier/Modifier.kt` | 1 (27 函数) | 通用视觉/布局修饰器 |
| DSL 函数 | `ui-widget-core/src/main/java/.../dsl/` | 6 (35 函数) | 用户面向的控件构建 API |
| ViewNodeFactory | `ui-renderer/src/main/java/.../view/tree/binder/ViewNodeFactory.kt` | 1 | NodeType → Android View 映射 |
| ViewBinders | `ui-renderer/src/main/java/.../view/tree/binder/` | ~10 | Spec → View 属性绑定逻辑 |
| ThemeDefaults | `ui-widget-core/src/main/java/.../context/ThemeDefaults.kt` | 1 | 全局形状/尺寸/颜色默认值 |
| LayoutScopes | `ui-widget-core/src/main/java/.../dsl/LayoutScopes.kt` | 1 | RowScope/ColumnScope/BoxScope 定义 |

### 6.4 NodeSpec 字段数统计

| NodeSpec | 字段数 | 共享情况 |
|----------|--------|---------|
| TextNodeProps | 6 | — |
| ImageNodeProps | 8 | implements ImageNodeSpec |
| ButtonNodeProps | 18 | — |
| IconButtonNodeProps | 15 | implements ImageNodeSpec |
| SegmentedControlNodeProps | 13 | — |
| TextFieldNodeProps | 27 | — |
| ToggleNodeProps | 8 | Checkbox/Switch/RadioButton 共享 |
| SliderNodeProps | 6 | — |
| ProgressIndicatorNodeProps | 6 | Linear/Circular 共享 |
| RowNodeProps | 3 | — |
| ColumnNodeProps | 3 | — |
| BoxNodeProps | 1 | Box/Surface 共享 |
| DividerNodeProps | 2 | — |
| LazyColumnNodeProps | 3 | — |
| TabPagerNodeProps | 12 | — |
| AndroidViewNodeProps | 2 | — |

### 6.5 Defaults 函数数统计

| Defaults 对象 | 公开函数数 | 覆盖控件 |
|--------------|-----------|---------|
| ButtonDefaults | 12 | Button |
| IconButtonDefaults | 6 | IconButton（大量委托 ButtonDefaults） |
| SegmentedControlDefaults | 10 | SegmentedControl |
| TabPagerDefaults | 8 | TabPager |
| TextDefaults | 6 | Text |
| TextFieldDefaults | 14 | TextField/PasswordField/EmailField/NumberField/TextArea |
| InputControlDefaults | 10 | Checkbox/Switch/RadioButton/Slider |
| ProgressIndicatorDefaults | 7 | LinearProgressIndicator/CircularProgressIndicator |
| DividerDefaults | 2 | Divider |
| SurfaceDefaults | 5 | Surface |
| ThemeDefaults (UiShapeDefaults + UiControlSizeDefaults + UiThemeDefaults) | ~30 | 全局 |

### 6.6 原生 View 映射表

| 框架控件 | NodeType | 底层 Android View |
|---------|----------|------------------|
| Text | `NodeType.Text` | `android.widget.TextView` |
| Image | `NodeType.Image` | `android.widget.ImageView` |
| Icon | `NodeType.Image` (via DSL) | `android.widget.ImageView` |
| Button | `NodeType.Button` | `android.widget.Button` |
| IconButton | `NodeType.IconButton` | `android.widget.ImageButton` |
| TextField | `NodeType.TextField` | `DeclarativeTextFieldLayout` (EditText wrapper) |
| Checkbox | `NodeType.Checkbox` | `android.widget.CheckBox` |
| Switch | `NodeType.Switch` | `android.widget.Switch` |
| RadioButton | `NodeType.RadioButton` | `android.widget.RadioButton` |
| Slider | `NodeType.Slider` | `android.widget.SeekBar` |
| LinearProgressIndicator | `NodeType.LinearProgressIndicator` | Material `LinearProgressIndicator` |
| CircularProgressIndicator | `NodeType.CircularProgressIndicator` | Material `CircularProgressIndicator` |
| Row | `NodeType.Row` | `DeclarativeLinearLayout` (horizontal) |
| Column | `NodeType.Column` | `DeclarativeLinearLayout` (vertical) |
| Box | `NodeType.Box` | `DeclarativeBoxLayout` |
| Surface | `NodeType.Surface` | `DeclarativeBoxLayout` |
| Spacer | `NodeType.Spacer` | `android.view.View` |
| Divider | `NodeType.Divider` | `android.view.View` |
| LazyColumn | `NodeType.LazyColumn` | `RecyclerView` |
| TabPager | `NodeType.TabPager` | `DeclarativeTabPagerLayout` |
| SegmentedControl | `NodeType.SegmentedControl` | `DeclarativeSegmentedControlLayout` |
| AndroidView | `NodeType.AndroidView` | User-provided `View` |
