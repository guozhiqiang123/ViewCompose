# Compose 组件对照与缺口分析

> **日期**：2026-03-04
> **对照基准**：Jetpack Compose Material 3 1.4.x (stable) + Foundation 1.9.x
> **框架当前状态**：18 NodeSpec · 11 Defaults · 27 Modifier · 50 DSL · 24 NodeType

---

## §0 背景与方法

### 当前框架已有组件

| 类别 | 已有组件 |
|------|---------|
| 布局容器 | Box, Row, Column, Surface, Spacer, Divider, Card (3 variant), ListItem, ScrollableColumn, ScrollableRow |
| 内容展示 | Text, Image, Icon, Badge, BadgedBox |
| 按钮动作 | Button (5 variant incl. Text), TextButton, IconButton (4 variant), SegmentedControl, FloatingActionButton, ExtendedFloatingActionButton |
| 文本输入 | TextField, PasswordField, EmailField, NumberField, TextArea |
| 选择输入 | Checkbox, Switch, RadioButton, Slider |
| 反馈提示 | LinearProgressIndicator, CircularProgressIndicator, Snackbar, Toast, Dialog, Popup, AlertDialog, PlainTooltip |
| 集合列表 | LazyColumn, LazyRow, TabPager |
| 导航 | TopAppBar, BottomAppBar |
| 逃生通道 | AndroidView |

### Compose Material 3 组件总量

| 包 | 组件数 | 说明 |
|---|--------|------|
| `material3` 核心 | ~143 | 含 Expressive 新增约 28 个 |
| `foundation.layout` | ~9 | Box, Row, Column, FlowRow, FlowColumn 等 |
| `foundation.lazy` | ~6 | LazyColumn/Row, LazyGrid, StaggeredGrid |
| `foundation.pager` | 2 | HorizontalPager, VerticalPager |
| `material3-adaptive` | ~5 | ListDetailPaneScaffold 等 |

### 分析维度

本文档将 Compose 中我们尚未覆盖的组件按四个维度分类：

1. **T1 核心缺口** — 几乎每个 App 都会用到，缺少会阻塞常规开发
2. **T2 中等组件** — 多数 App 会用到其中一部分，可按需引入
3. **T3 AndroidView** — 需要原生 View 能力支撑，不适合虚拟化
4. **T4 组合封装** — 可由现有基础组件组合实现，无需新增 NodeType

---

## §1 核心缺口（T1）

> 这些组件在 Compose 中使用频率极高，我们框架缺少会直接影响日常开发效率。

### 1.1 总览

| # | 组件 | Compose 对应 | 使用频率 | 建议实现方式 | 优先级 |
|---|------|-------------|---------|------------|--------|
| 1 | Card | `Card`, `ElevatedCard`, `OutlinedCard` | ⭐⭐⭐⭐⭐ | 组合封装（Surface 变体） | P2-A |
| 2 | FloatingActionButton | `FloatingActionButton`, `Extended~`, `Small~`, `Large~` | ⭐⭐⭐⭐⭐ | 组合封装（IconButton / Button 变体） | P2-A |
| 3 | Scaffold | `Scaffold` | ⭐⭐⭐⭐⭐ | 新增虚拟组件 | P2-A |
| 4 | TopAppBar | `TopAppBar`, `CenterAligned~`, `Medium~`, `Large~` | ⭐⭐⭐⭐⭐ | 组合封装（Row + IconButton + Text） | P2-A |
| 5 | BottomNavigationBar | `NavigationBar`, `NavigationBarItem` | ⭐⭐⭐⭐⭐ | 新增虚拟组件 | P2-A |
| 6 | AlertDialog | `AlertDialog` | ⭐⭐⭐⭐ | 组合封装（Dialog + 标准布局） | P2-A |
| 7 | DropdownMenu | `DropdownMenu`, `DropdownMenuItem` | ⭐⭐⭐⭐ | 新增虚拟组件 | P2-B |
| 8 | ~~LazyRow~~ | `LazyRow` | ⭐⭐⭐⭐ | ~~新增虚拟组件（复用 LazyColumn 架构）~~ | ✅ Phase B |
| 9 | ListItem | `ListItem` | ⭐⭐⭐⭐ | 组合封装（Row + Column + Icon/Image） | P2-A |
| 10 | Badge | `Badge`, `BadgedBox` | ⭐⭐⭐ | 组合封装（Box + Text） | P2-B |

### 1.2 详细分析

#### 1.2.1 Card

**Compose 提供**：`Card`, `ElevatedCard`, `OutlinedCard`

**为什么是核心**：信息卡片是最常见的 UI 模式之一 — 商品卡、订单卡、设置项、内容流卡片。

**当前替代**：`Surface(variant = ...)` + padding + content

**建议方案**：**组合封装 DSL**，不需要新 NodeType

```kotlin
fun UiTreeBuilder.Card(
    variant: CardVariant = CardVariant.Filled,  // Filled, Elevated, Outlined
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: BoxScope.() -> Unit,
)
```

**实现路径**：内部调用 `Surface()` + 适当的 Defaults（CardDefaults 提供 elevation、border、containerColor）。需要先完成 §WIDGET_PROPERTY_AUDIT Phase 2.1 的 elevation Modifier。

**涉及新文件**：`CardDefaults.kt`, DSL 中新增 `Card()` 函数

---

#### 1.2.2 FloatingActionButton

**Compose 提供**：`FloatingActionButton`, `SmallFloatingActionButton`, `LargeFloatingActionButton`, `ExtendedFloatingActionButton`

**为什么是核心**：Material Design 主要动作入口，几乎每个有"创建/新增"操作的页面都需要。

**当前替代**：`IconButton(variant = Primary)` + `Modifier.size()` 手动调整

**建议方案**：**组合封装 DSL**，不需要新 NodeType

```kotlin
fun UiTreeBuilder.FloatingActionButton(
    onClick: () -> Unit,
    size: FabSize = FabSize.Medium,  // Small, Medium, Large
    containerColor: Int = FabDefaults.containerColor(),
    contentColor: Int = FabDefaults.contentColor(),
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: BoxScope.() -> Unit,
)

fun UiTreeBuilder.ExtendedFloatingActionButton(
    text: String,
    icon: ImageSource? = null,
    onClick: () -> Unit,
    ...
)
```

**实现路径**：内部调用 `Surface()` + `Box()` / `Row()`，需要 elevation Modifier 支持。

**涉及新文件**：`FabDefaults.kt`, DSL 中新增 FAB 函数

---

#### 1.2.3 Scaffold

**Compose 提供**：`Scaffold` — 提供 topBar / bottomBar / floatingActionButton / snackbarHost / content 的标准页面骨架

**为什么是核心**：几乎每个页面都是 Scaffold 结构（顶栏 + 内容区 + 可选底栏/FAB）。

**当前替代**：`Column { topBar(); Box(modifier = fillMaxSize) { content() }; bottomBar() }`

**建议方案**：**新增虚拟组件**（需要新 NodeType，因为涉及多 slot 布局和 insets 处理）

```kotlin
fun UiTreeBuilder.Scaffold(
    topBar: (UiTreeBuilder.() -> Unit)? = null,
    bottomBar: (UiTreeBuilder.() -> Unit)? = null,
    floatingActionButton: (UiTreeBuilder.() -> Unit)? = null,
    snackbarHost: (UiTreeBuilder.() -> Unit)? = null,
    containerColor: Int = ScaffoldDefaults.containerColor(),
    contentColor: Int = ScaffoldDefaults.contentColor(),
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: UiTreeBuilder.() -> Unit,
)
```

**复杂度评估**：中等。核心是多 slot 布局管理 + 内容区域 padding 自动计算。可基于 `DeclarativeBoxLayout` 扩展实现。

**涉及新文件**：`ScaffoldNodeProps.kt`, `ScaffoldDefaults.kt`, `DeclarativeScaffoldLayout.kt`, DSL 函数

---

#### 1.2.4 TopAppBar

**Compose 提供**：`TopAppBar`, `CenterAlignedTopAppBar`, `MediumTopAppBar`, `LargeTopAppBar`

**为什么是核心**：绝大多数页面都有顶部导航栏。

**当前替代**：`Row { IconButton(...); Text(...); Spacer(...); IconButton(...) }` + 手动样式

**建议方案**：**组合封装 DSL**，不需要新 NodeType

```kotlin
fun UiTreeBuilder.TopAppBar(
    title: String,
    navigationIcon: (() -> Unit)? = null,  // 返回按钮 slot
    actions: (RowScope.() -> Unit)? = null,  // 右侧操作按钮
    variant: TopAppBarVariant = TopAppBarVariant.Small,
    colors: TopAppBarColors = TopAppBarDefaults.colors(),
    key: Any? = null,
    modifier: Modifier = Modifier,
)
```

**实现路径**：内部使用 `Row()` + `Text()` + `IconButton()` + `Spacer()`。

**涉及新文件**：`TopAppBarDefaults.kt`, DSL 中新增 `TopAppBar()` 函数

---

#### 1.2.5 BottomNavigationBar

**Compose 提供**：`NavigationBar`, `NavigationBarItem`

**为什么是核心**：多 Tab 应用的标准导航模式，微信/淘宝/抖音等几乎所有主流 App 都使用。

**当前替代**：`Row { Column { Icon(); Text() } ... }` + 手动选中状态管理

**建议方案**：**新增虚拟组件**（选中状态、指示器动画、badge 集成需要原生渲染支持）

```kotlin
fun UiTreeBuilder.NavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    key: Any? = null,
    modifier: Modifier = Modifier,
    items: NavigationBarScope.() -> Unit,
)

class NavigationBarScope {
    fun Item(
        icon: ImageSource,
        label: String,
        selectedIcon: ImageSource? = null,
        badge: String? = null,
    )
}
```

**复杂度评估**：中等。选中态指示器动画是核心难点。

**涉及新文件**：`NavigationBarNodeProps.kt`, `NavigationBarDefaults.kt`, `DeclarativeNavigationBarLayout.kt`, DSL

---

#### 1.2.6 AlertDialog

**Compose 提供**：`AlertDialog` — 标准化的标题 + 内容 + 确认/取消按钮对话框

**为什么是核心**：确认操作、错误提示、权限请求等场景极高频。

**当前替代**：`Dialog(visible) { Column { Text(title); Text(message); Row { Button(...); Button(...) } } }`

**建议方案**：**组合封装 DSL**，基于现有 Dialog

```kotlin
fun UiTreeBuilder.AlertDialog(
    visible: Boolean,
    title: String,
    text: String,
    confirmButton: String,
    onConfirm: () -> Unit,
    dismissButton: String? = null,
    onDismiss: (() -> Unit)? = null,
    icon: ImageSource? = null,
    requestKey: String = "alert",
)
```

**实现路径**：内部调用 `Dialog()` + `Column()` + `Text()` + `Row()` + `Button()`。

**涉及新文件**：DSL 中新增 `AlertDialog()` 函数，可选 `AlertDialogDefaults.kt`

---

#### 1.2.7 DropdownMenu

**Compose 提供**：`DropdownMenu`, `DropdownMenuItem`, `ExposedDropdownMenuBox`

**为什么是核心**：下拉选择器在表单场景中极为常见（省市选择、类型选择等）。

**当前替代**：`Popup(visible, anchorId)` + `Column { items }` — 需要大量手动布局

**建议方案**：**新增虚拟组件**（菜单弹出定位、焦点管理、dismiss 行为需要原生 PopupWindow 支持）

```kotlin
fun UiTreeBuilder.DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    anchorId: String,
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: DropdownMenuScope.() -> Unit,
)

class DropdownMenuScope {
    fun Item(
        text: String,
        onClick: () -> Unit,
        leadingIcon: ImageSource? = null,
        trailingIcon: ImageSource? = null,
        enabled: Boolean = true,
    )
}
```

**实现路径**：基于现有 `Popup` 组件扩展，增加菜单项的标准化布局和交互。

**涉及新文件**：`DropdownMenuNodeProps.kt`, `DropdownMenuDefaults.kt`, DSL

---

#### 1.2.8 LazyRow

**Compose 提供**：`LazyRow` — 水平方向的懒加载列表

**为什么是核心**：横向滑动列表极为常见 — 推荐列表、标签列表、图片画廊等。

**当前替代**：无直接替代。需要 `AndroidView { RecyclerView(HORIZONTAL) }`。

**建议方案**：**新增虚拟组件**（可完全复用 LazyColumn 的 RecyclerView 架构，仅改方向）

```kotlin
fun <T> UiTreeBuilder.LazyRow(
    items: List<T>,
    key: ((T) -> Any)? = null,
    contentPadding: Int = 0,
    spacing: Int = 0,
    modifier: Modifier = Modifier,
    itemContent: UiTreeBuilder.(T) -> Unit,
)
```

**实现路径**：复用 `LazyColumnNodeProps` 架构，新增 `LazyRowNodeProps`（增加 `orientation` 或独立类型），ViewNodeFactory 创建 `RecyclerView` + `LinearLayoutManager(HORIZONTAL)`。

**复杂度评估**：低。核心架构完全复用。

**涉及新文件**：`LazyRowNodeProps.kt`, DSL 中新增 `LazyRow()`, ViewNodeFactory 注册

---

#### 1.2.9 ListItem

**Compose 提供**：`ListItem` — 标准化的列表项（leading + headline + supporting + trailing）

**为什么是核心**：设置页、联系人列表、消息列表等场景极高频。

**当前替代**：`Row { Icon(...); Column { Text(title); Text(subtitle) }; Spacer(); Icon(...) }` — 每次手写布局

**建议方案**：**组合封装 DSL**

```kotlin
fun UiTreeBuilder.ListItem(
    headlineText: String,
    supportingText: String? = null,
    overlineText: String? = null,
    leadingContent: (UiTreeBuilder.() -> Unit)? = null,
    trailingContent: (UiTreeBuilder.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    key: Any? = null,
    modifier: Modifier = Modifier,
)
```

**实现路径**：内部使用 `Row()` + `Column()` + `Text()` + 可选 slot。

**涉及新文件**：`ListItemDefaults.kt`, DSL 中新增 `ListItem()` 函数

---

#### 1.2.10 Badge

**Compose 提供**：`Badge`, `BadgedBox`

**为什么是核心**：消息未读数、通知角标、NavigationBar 上的 badge。

**当前替代**：`Box { content(); Box(modifier = align(TopEnd).size(16.dp)) { Text("3") } }` — 手动定位

**建议方案**：**组合封装 DSL**

```kotlin
fun UiTreeBuilder.BadgedBox(
    badge: (UiTreeBuilder.() -> Unit)?,
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: BoxScope.() -> Unit,
)

fun UiTreeBuilder.Badge(
    count: Int? = null,  // null = 小圆点, 0 = 不显示, >0 = 数字
    key: Any? = null,
    modifier: Modifier = Modifier,
)
```

**实现路径**：`BadgedBox` = `Box()` + 绝对定位的 `Badge`。`Badge` = 小圆形 `Surface` + `Text`。

**涉及新文件**：`BadgeDefaults.kt`, DSL 中新增 `BadgedBox()` / `Badge()` 函数

---

## §2 中等组件（T2）

> 多数 App 会用到其中一部分，但不是每个页面都需要。可按业务需求分批引入。

### 2.1 总览

| # | 组件 | Compose 对应 | 使用频率 | 建议实现方式 | 优先级 |
|---|------|-------------|---------|------------|--------|
| 1 | Chip | `AssistChip`, `FilterChip`, `InputChip`, `SuggestionChip` | ⭐⭐⭐⭐ | 组合封装（Button 小变体） | P2-B |
| 2 | ModalBottomSheet | `ModalBottomSheet` | ⭐⭐⭐⭐ | 新增虚拟组件 | P2-B |
| 3 | NavigationDrawer | `ModalNavigationDrawer`, `PermanentNavigationDrawer` | ⭐⭐⭐ | 新增虚拟组件 | P2-C |
| 4 | NavigationRail | `NavigationRail`, `NavigationRailItem` | ⭐⭐ | 新增虚拟组件 | P2-C |
| 5 | SearchBar | `SearchBar`, `DockedSearchBar` | ⭐⭐⭐ | 组合封装（TextField 变体） | P2-B |
| 6 | Tooltip | `PlainTooltip`, `RichTooltip`, `TooltipBox` | ⭐⭐⭐ | 组合封装（Popup 变体） | P2-C |
| 7 | SwipeToDismiss | `SwipeToDismissBox` | ⭐⭐⭐ | 新增虚拟组件（手势） | P2-C |
| 8 | PullToRefresh | `PullToRefreshBox` | ⭐⭐⭐⭐ | 新增虚拟组件（手势） | P2-B |
| 9 | DatePicker | `DatePicker`, `DateRangePicker`, `DatePickerDialog` | ⭐⭐⭐ | AndroidView 或新增虚拟组件 | P2-C |
| 10 | TimePicker | `TimePicker`, `TimeInput` | ⭐⭐ | AndroidView | P3 |
| 11 | RangeSlider | `RangeSlider` | ⭐⭐ | 新增虚拟组件 | P2-C |
| 12 | LazyGrid | `LazyVerticalGrid`, `LazyHorizontalGrid` | ⭐⭐⭐ | 新增虚拟组件 | P2-B |
| 13 | StaggeredGrid | `LazyVerticalStaggeredGrid` | ⭐⭐ | 新增虚拟组件 | P2-C |
| 14 | HorizontalPager | `HorizontalPager` | ⭐⭐⭐ | 新增虚拟组件（复用 ViewPager2） | P2-B |
| 15 | FlowRow / FlowColumn | `FlowRow`, `FlowColumn` | ⭐⭐⭐ | 新增虚拟组件 | P2-B |
| 16 | BottomAppBar | `BottomAppBar` | ⭐⭐⭐ | 组合封装（Row 变体） | P2-B |
| 17 | ~~ScrollableContainer~~ | `verticalScroll`, `horizontalScroll` | ⭐⭐⭐ | ~~新增虚拟组件~~ | ✅ Phase B |
| 18 | TriStateCheckbox | `TriStateCheckbox` | ⭐⭐ | 组合封装（Checkbox 扩展） | P2-C |
| 19 | Tab / TabRow | `Tab`, `TabRow`, `ScrollableTabRow` | ⭐⭐⭐ | 已有 TabPager，可补充独立 TabRow | P2-C |
| 20 | Carousel | `HorizontalMultiBrowseCarousel` | ⭐⭐ | 组合封装（HorizontalPager 变体） | P2-C |

### 2.2 重点组件详述

#### 2.2.1 Chip / ChipGroup

**Compose 提供**：`AssistChip`, `FilterChip`, `InputChip`, `SuggestionChip` (各有 Elevated 变体)

**使用场景**：标签筛选（FilterChip）、搜索历史（SuggestionChip）、已选实体（InputChip with dismiss）

**建议方案**：组合封装

```kotlin
fun UiTreeBuilder.Chip(
    label: String,
    onClick: () -> Unit,
    variant: ChipVariant = ChipVariant.Assist,  // Assist, Filter, Input, Suggestion
    selected: Boolean = false,  // FilterChip 用
    leadingIcon: ImageSource? = null,
    trailingIcon: ImageSource? = null,  // InputChip 的删除按钮
    enabled: Boolean = true,
    size: ChipSize = ChipSize.Medium,
    key: Any? = null,
    modifier: Modifier = Modifier,
)
```

**实现路径**：本质是小号 Button 变体 — `Surface()` + `Row()` + `Icon()` + `Text()`。需要新建 `ChipDefaults.kt` 管理圆角（通常全圆）、高度（32dp）、padding 等。

---

#### 2.2.2 ModalBottomSheet

**Compose 提供**：`ModalBottomSheet` — 从底部滑出的模态面板

**使用场景**：分享面板、操作菜单、详情半屏、表单输入

**建议方案**：新增虚拟组件

```kotlin
fun UiTreeBuilder.ModalBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    requestKey: String = "bottom_sheet",
    dragHandle: Boolean = true,
    scrimColor: Int = BottomSheetDefaults.scrimColor(),
    containerColor: Int = BottomSheetDefaults.containerColor(),
    key: Any? = null,
    content: UiTreeBuilder.() -> Unit,
)
```

**复杂度评估**：高。涉及手势驱动的滑动交互、部分展开/完全展开状态、scrim 层管理。底层可基于 `BottomSheetBehavior` 或自定义 `CoordinatorLayout` 实现。

---

#### 2.2.3 PullToRefresh

**Compose 提供**：`PullToRefreshBox`

**使用场景**：列表下拉刷新，几乎所有有列表的页面都可能需要

**建议方案**：新增虚拟组件

```kotlin
fun UiTreeBuilder.PullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    indicatorColor: Int = PullToRefreshDefaults.indicatorColor(),
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: UiTreeBuilder.() -> Unit,
)
```

**实现路径**：底层可使用 `SwipeRefreshLayout` 或 Material 的 `CircularProgressIndicator` + 手势检测。

---

#### 2.2.4 LazyGrid

**Compose 提供**：`LazyVerticalGrid`, `LazyHorizontalGrid`

**使用场景**：商品网格、图片库、设置项网格

**建议方案**：新增虚拟组件

```kotlin
fun <T> UiTreeBuilder.LazyVerticalGrid(
    items: List<T>,
    columns: Int = 2,  // 或 GridCells.Fixed(n) / GridCells.Adaptive(minSize)
    key: ((T) -> Any)? = null,
    contentPadding: Int = 0,
    horizontalSpacing: Int = 0,
    verticalSpacing: Int = 0,
    modifier: Modifier = Modifier,
    itemContent: UiTreeBuilder.(T) -> Unit,
)
```

**实现路径**：基于 RecyclerView + `GridLayoutManager`，复用 LazyColumn 的 Adapter 架构。

---

#### 2.2.5 FlowRow / FlowColumn

**Compose 提供**：`FlowRow`, `FlowColumn` — 自动换行的流式布局

**使用场景**：标签云、Chip 组、动态宽度元素排列

**建议方案**：新增虚拟组件

```kotlin
fun UiTreeBuilder.FlowRow(
    horizontalSpacing: Int = 0,
    verticalSpacing: Int = 0,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: LayoutScope.() -> Unit,
)
```

**实现路径**：底层可基于 `FlexboxLayout`（Google 官方库）或自定义 `ViewGroup` 实现 measure/layout 逻辑。

---

#### 2.2.6 HorizontalPager

**Compose 提供**：`HorizontalPager`, `VerticalPager` — 独立的翻页容器（与 Tab 解耦）

**使用场景**：引导页、图片浏览、独立轮播

**当前状态**：已有 `TabPager`（Tab + Pager 绑定），但缺少独立 Pager

**建议方案**：新增虚拟组件

```kotlin
fun UiTreeBuilder.HorizontalPager(
    pageCount: Int,
    currentPage: Int,
    onPageChanged: (Int) -> Unit,
    key: Any? = null,
    modifier: Modifier = Modifier,
    pageContent: UiTreeBuilder.(pageIndex: Int) -> Unit,
)
```

**实现路径**：底层使用 `ViewPager2`，与 TabPager 共享部分架构。

---

#### 2.2.7 ScrollableContainer

**Compose 提供**：`Modifier.verticalScroll()`, `Modifier.horizontalScroll()` — 为固定内容添加滚动能力

**使用场景**：表单页面、长内容页面（非 lazy 列表场景）

**当前替代**：无。当前框架没有非 lazy 的滚动容器。

**建议方案**：新增虚拟组件或 Modifier

```kotlin
// 方案 A：容器组件
fun UiTreeBuilder.ScrollableColumn(
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: ColumnScope.() -> Unit,
)

// 方案 B：Modifier（更灵活，更贴近 Compose）
fun Modifier.verticalScroll(): Modifier
```

**实现路径**：方案 A 底层使用 `ScrollView`；方案 B 需要在 `ModifierApplier` 中将容器包裹在 `ScrollView` 中。

---

#### 2.2.8 SearchBar

**Compose 提供**：`SearchBar`, `DockedSearchBar`

**使用场景**：搜索页面的搜索输入框，通常带有展开/收缩动画

**建议方案**：组合封装

```kotlin
fun UiTreeBuilder.SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    placeholder: String = "",
    leadingIcon: ImageSource? = null,
    trailingIcon: ImageSource? = null,
    key: Any? = null,
    modifier: Modifier = Modifier,
)
```

**实现路径**：本质是带搜索图标的 `TextField` 变体 + 展开状态管理。

---

## §3 AndroidView 组件（T3）

> 这些组件由于生命周期复杂、平台耦合度高或 SDK 绑定强，不适合虚拟化，应继续使用 `AndroidView` 逃生通道。

| # | 组件 | 原生承载 | 不虚拟化原因 | 使用方式 |
|---|------|---------|------------|---------|
| 1 | WebView | `android.webkit.WebView` | 生命周期极复杂、安全边界、JS 交互、cookie 管理 | `AndroidView { WebView(it) }` |
| 2 | MapView | Google Maps SDK / 高德/百度 SDK | 强 SDK 绑定、生命周期与 Fragment 耦合 | `AndroidView { MapView(it) }` |
| 3 | VideoPlayer | `ExoPlayer` + `PlayerView` / `MediaController` | 媒体会话、全屏切换、DRM、编解码器管理 | `AndroidView { PlayerView(it) }` |
| 4 | CameraPreview | `CameraX PreviewView` | 相机生命周期、权限、帧回调 | `AndroidView { PreviewView(it) }` |
| 5 | SurfaceView | `SurfaceView` / `TextureView` / `GLSurfaceView` | 独立渲染线程、OpenGL 上下文 | `AndroidView { GLSurfaceView(it) }` |
| 6 | CalendarView | `CalendarView` / 三方日历库 | 状态表面积大、手势复杂、国际化差异大 | `AndroidView { CalendarView(it) }` |
| 7 | RichTextEditor | 基于 `EditText` + Span 的富文本编辑 | 光标管理、Span 状态、工具栏交互极复杂 | `AndroidView { RichEditor(it) }` |
| 8 | 签名板 | 自定义 `View` + `Canvas` + `Path` | 手势+路径绘制，纯绘图逻辑 | `AndroidView { SignatureView(it) }` |
| 9 | 图表 | MPAndroidChart / ECharts WebView | 交互复杂、动画多、数据绑定深 | `AndroidView { BarChart(it) }` |
| 10 | 二维码/条码扫描 | `CameraX` + `MLKit` / ZXing | 相机 + 实时识别 | `AndroidView { PreviewView(it) }` |
| 11 | PDF 查看器 | `PdfRenderer` / 三方库 | 分页渲染、缩放手势、内存管理 | `AndroidView { PdfView(it) }` |
| 12 | FragmentContainerView | `FragmentContainerView` | 导航/宿主层架构，不属于控件体系 | 宿主层处理 |
| 13 | NumberPicker | `NumberPicker` | 使用频率低、平台差异大 | `AndroidView { NumberPicker(it) }` |
| 14 | TimePicker | Material `TimePicker` | 时钟面盘交互极复杂 | `AndroidView { TimePicker(it) }` |

**AndroidView 最佳实践**：

```kotlin
// 已有的 AndroidView DSL
AndroidView(
    factory = { context -> WebView(context).apply { settings.javaScriptEnabled = true } },
    update = { webView -> webView.loadUrl(url) },
    modifier = Modifier.fillMaxSize(),
)

// 配合 nativeView 逃生通道做微调
AndroidView(
    factory = { context -> PlayerView(context) },
    update = { view -> view.player = exoPlayer },
    modifier = Modifier
        .fillMaxSize()
        .nativeView("player") { (it as PlayerView).useController = true },
)
```

---

## §4 组合封装组件（T4）

> 这些组件不需要新增 NodeType，可完全由现有基础组件（Box/Row/Column/Surface/Text/Icon/Button 等）组合实现。核心工作是定义 DSL 函数 + Defaults 对象。

### 4.1 总览

| # | 组件 | 组合方式 | 新增文件 | 工作量 |
|---|------|---------|---------|--------|
| 1 | Card | `Surface` + `Box` | `CardDefaults.kt`, DSL | 0.5 天 |
| 2 | AlertDialog | `Dialog` + `Column` + `Text` + `Row` + `Button` | DSL 函数 | 0.5 天 |
| 3 | TopAppBar | `Row` + `IconButton` + `Text` + `Spacer` | `TopAppBarDefaults.kt`, DSL | 1 天 |
| 4 | BottomAppBar | `Surface` + `Row` + `IconButton` | `BottomAppBarDefaults.kt`, DSL | 0.5 天 |
| 5 | FloatingActionButton | `Surface` + `Box`/`Row` + `Icon`/`Text` | `FabDefaults.kt`, DSL | 0.5 天 |
| 6 | ListItem | `Row` + `Column` + `Text` + 可选 slot | `ListItemDefaults.kt`, DSL | 0.5 天 |
| 7 | Badge / BadgedBox | `Box` + `Text` with positioning | `BadgeDefaults.kt`, DSL | 0.5 天 |
| 8 | Chip | `Surface` + `Row` + `Icon` + `Text` | `ChipDefaults.kt`, DSL | 1 天 |
| 9 | SearchBar | `TextField` variant + `Row` + `Icon` | `SearchBarDefaults.kt`, DSL | 1 天 |
| 10 | Tooltip | `Popup` + `Surface` + `Text` | `TooltipDefaults.kt`, DSL | 0.5 天 |
| 11 | TriStateCheckbox | `Checkbox` 扩展 | DSL 函数 | 0.5 天 |
| 12 | Carousel | `LazyRow` (待实现) + 指示器 | `CarouselDefaults.kt`, DSL | 1 天 |
| 13 | Avatar | `Image` + `Modifier.cornerRadius` (clip) | DSL 函数 | 0.5 天 |
| 14 | FormField | `Column` + `Text` + `TextField` + `Text` | DSL 函数 | 0.5 天 |

### 4.2 组合封装设计原则

1. **不引入新 NodeType**：组合组件内部复用现有的 VNode 类型
2. **创建独立 Defaults**：每个组合组件应有自己的 Defaults 对象，从 Theme 派生默认值
3. **DSL 签名与 Compose 对齐**：参数命名和语义尽量与 Compose Material 3 一致，降低迁移成本
4. **遵循现有四层体系**：语义属性 → Spec/DSL 参数，默认样式 → Defaults，通用视觉 → Modifier

### 4.3 示例实现：Card

```kotlin
// CardDefaults.kt
object CardDefaults {
    fun containerColor(variant: CardVariant): Int = when (variant) {
        CardVariant.Filled -> Theme.colors.surfaceVariant
        CardVariant.Elevated -> Theme.colors.surface
        CardVariant.Outlined -> Theme.colors.surface
    }
    fun cornerRadius(): Int = Theme.shapes.cardCornerRadius
    fun borderWidth(variant: CardVariant): Int = when (variant) {
        CardVariant.Outlined -> 1.dp
        else -> 0
    }
    fun borderColor(variant: CardVariant): Int = when (variant) {
        CardVariant.Outlined -> Theme.colors.divider
        else -> 0
    }
    // Elevated variant 需要 elevation Modifier（Phase 2.1）
}

enum class CardVariant { Filled, Elevated, Outlined }

// DSL
fun UiTreeBuilder.Card(
    variant: CardVariant = CardVariant.Filled,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    key: Any? = null,
    modifier: Modifier = Modifier,
    content: BoxScope.() -> Unit,
) {
    Surface(
        variant = SurfaceVariant.Default,
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier
            .backgroundColor(CardDefaults.containerColor(variant))
            .cornerRadius(CardDefaults.cornerRadius())
            .border(CardDefaults.borderWidth(variant), CardDefaults.borderColor(variant))
            .then(modifier),
        key = key,
        content = content,
    )
}
```

### 4.4 示例实现：ListItem

```kotlin
// ListItemDefaults.kt
object ListItemDefaults {
    fun headlineStyle(): UiTextStyle = TextDefaults.bodyStyle()
    fun supportingStyle(): UiTextStyle = TextDefaults.labelStyle()
    fun overlineStyle(): UiTextStyle = TextDefaults.labelStyle()
    fun headlineColor(): Int = TextDefaults.primaryColor()
    fun supportingColor(): Int = TextDefaults.secondaryColor()
    fun containerColor(): Int = Theme.colors.surface
    fun verticalPadding(): Int = 8.dp
    fun horizontalPadding(): Int = 16.dp
    fun leadingContentSpacing(): Int = 16.dp
    fun trailingContentSpacing(): Int = 16.dp
}

// DSL
fun UiTreeBuilder.ListItem(
    headlineText: String,
    supportingText: String? = null,
    overlineText: String? = null,
    leadingContent: (UiTreeBuilder.() -> Unit)? = null,
    trailingContent: (UiTreeBuilder.() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val baseModifier = Modifier
        .fillMaxWidth()
        .padding(
            horizontal = ListItemDefaults.horizontalPadding(),
            vertical = ListItemDefaults.verticalPadding(),
        )
        .then(if (onClick != null) Modifier.clickable(onClick) else Modifier)
        .then(modifier)

    Row(
        key = key,
        verticalAlignment = VerticalAlignment.Center,
        modifier = baseModifier,
    ) {
        if (leadingContent != null) {
            leadingContent()
            Spacer(modifier = Modifier.width(ListItemDefaults.leadingContentSpacing()))
        }
        Column(modifier = Modifier.weight(1f)) {
            if (overlineText != null) {
                Text(overlineText, style = ListItemDefaults.overlineStyle(), color = ListItemDefaults.supportingColor())
            }
            Text(headlineText, style = ListItemDefaults.headlineStyle(), color = ListItemDefaults.headlineColor())
            if (supportingText != null) {
                Text(supportingText, style = ListItemDefaults.supportingStyle(), color = ListItemDefaults.supportingColor())
            }
        }
        if (trailingContent != null) {
            Spacer(modifier = Modifier.width(ListItemDefaults.trailingContentSpacing()))
            trailingContent()
        }
    }
}
```

---

## §5 Compose 对照矩阵

> 完整对照表，一目了然查看每个 Compose 组件在我们框架中的对应状态。

### 5.1 布局与容器

| Compose 组件 | 框架状态 | 框架对应 | 归类 |
|-------------|---------|---------|------|
| `Box` | ✅ 已有 | `Box()` | — |
| `BoxWithConstraints` | ⚪ 缺失 | — | T2（低频） |
| `Row` | ✅ 已有 | `Row()` | — |
| `Column` | ✅ 已有 | `Column()` | — |
| `Surface` | ✅ 已有 | `Surface()` | — |
| `Spacer` | ✅ 已有 | `Spacer()` | — |
| `FlowRow` | ❌ 缺失 | — | T2 新增虚拟 |
| `FlowColumn` | ❌ 缺失 | — | T2 新增虚拟 |
| `Modifier.verticalScroll` | ✅ 已有 | `ScrollableColumn()` | Phase B |
| `Modifier.horizontalScroll` | ✅ 已有 | `ScrollableRow()` | Phase B |
| `Scaffold` | ❌ 缺失 | — | T1 新增虚拟 |
| `BottomSheetScaffold` | ❌ 缺失 | — | T2 新增虚拟 |

### 5.2 内容展示

| Compose 组件 | 框架状态 | 框架对应 | 归类 |
|-------------|---------|---------|------|
| `Text` | ✅ 已有 | `Text()` | — |
| `Icon` | ✅ 已有 | `Icon()` | — |
| `Image` | ✅ 已有 | `Image()` | — |
| `HorizontalDivider` | ✅ 已有 | `Divider()` | — |
| `VerticalDivider` | ❌ 缺失 | — | T4 组合（Divider 旋转） |
| `Badge` / `BadgedBox` | ✅ 已有 | `Badge()`, `BadgedBox()` | Phase A |
| `ListItem` | ✅ 已有 | `ListItem()` | Phase A |
| `Card` / `ElevatedCard` / `OutlinedCard` | ✅ 已有 | `Card(variant = Filled/Elevated/Outlined)` | Phase A |

### 5.3 按钮与动作

| Compose 组件 | 框架状态 | 框架对应 | 归类 |
|-------------|---------|---------|------|
| `Button` (Filled) | ✅ 已有 | `Button(variant = Primary)` | — |
| `ElevatedButton` | 🔧 部分 | `Button(variant = Secondary)` | 缺 elevation |
| `FilledTonalButton` | ✅ 已有 | `Button(variant = Tonal)` | — |
| `OutlinedButton` | ✅ 已有 | `Button(variant = Outlined)` | — |
| `TextButton` | ✅ 已有 | `TextButton()` / `Button(variant = Text)` | Phase A |
| `IconButton` | ✅ 已有 | `IconButton()` | — |
| `IconToggleButton` | ❌ 缺失 | — | T4 组合（IconButton + state） |
| `FloatingActionButton` | ✅ 已有 | `FloatingActionButton()` | Phase A |
| `ExtendedFloatingActionButton` | ✅ 已有 | `ExtendedFloatingActionButton()` | Phase A |

### 5.4 Chip

| Compose 组件 | 框架状态 | 框架对应 | 归类 |
|-------------|---------|---------|------|
| `AssistChip` | ❌ 缺失 | — | T4 组合封装 |
| `FilterChip` | ❌ 缺失 | — | T4 组合封装 |
| `InputChip` | ❌ 缺失 | — | T4 组合封装 |
| `SuggestionChip` | ❌ 缺失 | — | T4 组合封装 |

### 5.5 文本输入

| Compose 组件 | 框架状态 | 框架对应 | 归类 |
|-------------|---------|---------|------|
| `TextField` (Filled) | ✅ 已有 | `TextField(variant = Filled)` | — |
| `OutlinedTextField` | ✅ 已有 | `TextField(variant = Outlined)` | — |
| `SecureTextField` | ✅ 已有 | `PasswordField()` | — |
| `SearchBar` | ❌ 缺失 | — | T4 组合封装 |

### 5.6 选择控件

| Compose 组件 | 框架状态 | 框架对应 | 归类 |
|-------------|---------|---------|------|
| `Checkbox` | ✅ 已有 | `Checkbox()` | — |
| `TriStateCheckbox` | ❌ 缺失 | — | T4 组合 |
| `Switch` | ✅ 已有 | `Switch()` | — |
| `RadioButton` | ✅ 已有 | `RadioButton()` | — |
| `Slider` | ✅ 已有 | `Slider()` | — |
| `RangeSlider` | ❌ 缺失 | — | T2 新增虚拟 |
| `SegmentedButton` | ✅ 已有 | `SegmentedControl()` | — |
| `DropdownMenu` | ❌ 缺失 | — | T1 新增虚拟 |
| `ExposedDropdownMenuBox` | ❌ 缺失 | — | T2 新增虚拟 |

### 5.7 导航

| Compose 组件 | 框架状态 | 框架对应 | 归类 |
|-------------|---------|---------|------|
| `TopAppBar` | ✅ 已有 | `TopAppBar()` | Phase A |
| `CenterAlignedTopAppBar` | ❌ 缺失 | — | T4 组合封装 |
| `BottomAppBar` | ✅ 已有 | `BottomAppBar()` | Phase A |
| `NavigationBar` | ❌ 缺失 | — | T1 新增虚拟 |
| `NavigationRail` | ❌ 缺失 | — | T2 新增虚拟 |
| `ModalNavigationDrawer` | ❌ 缺失 | — | T2 新增虚拟 |
| `Tab` / `TabRow` | ✅ 已有 | `TabPager()` | 已绑定 Pager |
| `ScrollableTabRow` | 🔧 部分 | `TabPager()` | 缺独立 TabRow |

### 5.8 反馈与弹层

| Compose 组件 | 框架状态 | 框架对应 | 归类 |
|-------------|---------|---------|------|
| `Snackbar` | ✅ 已有 | `Snackbar()` | — |
| `AlertDialog` | ✅ 已有 | `AlertDialog()` | Phase A |
| `CircularProgressIndicator` | ✅ 已有 | `CircularProgressIndicator()` | — |
| `LinearProgressIndicator` | ✅ 已有 | `LinearProgressIndicator()` | — |
| `ModalBottomSheet` | ❌ 缺失 | — | T2 新增虚拟 |
| `PullToRefreshBox` | ❌ 缺失 | — | T2 新增虚拟 |
| `SwipeToDismissBox` | ❌ 缺失 | — | T2 新增虚拟 |
| `PlainTooltip` / `RichTooltip` | 🔧 部分 | `PlainTooltip()` (RichTooltip 待实现) | Phase A |

### 5.9 集合与列表

| Compose 组件 | 框架状态 | 框架对应 | 归类 |
|-------------|---------|---------|------|
| `LazyColumn` | ✅ 已有 | `LazyColumn()` | — |
| `LazyRow` | ✅ 已有 | `LazyRow()` | Phase B |
| `LazyVerticalGrid` | ❌ 缺失 | — | T2 新增虚拟 |
| `LazyHorizontalGrid` | ❌ 缺失 | — | T2 新增虚拟 |
| `LazyVerticalStaggeredGrid` | ❌ 缺失 | — | T2 新增虚拟 |
| `HorizontalPager` | ❌ 缺失 | — | T2 新增虚拟 |
| `VerticalPager` | ❌ 缺失 | — | T2 新增虚拟 |

### 5.10 日期与时间

| Compose 组件 | 框架状态 | 框架对应 | 归类 |
|-------------|---------|---------|------|
| `DatePicker` | ❌ 缺失 | — | T2-C / T3 |
| `DateRangePicker` | ❌ 缺失 | — | T3 |
| `TimePicker` | ❌ 缺失 | — | T3 |

---

## §6 实施路线图

### Phase A：核心组合封装（1-2 周） ✅ 已完成

> 不需要新 NodeType，纯 DSL + Defaults，可立即开始。
> **前置依赖**：WIDGET_PROPERTY_AUDIT Phase 2.1（elevation Modifier）对 ElevatedCard 和 FAB 有帮助，但不阻塞其他项。

| # | 组件 | 工作量 | 依赖 | 状态 |
|---|------|--------|------|------|
| A.1 | Card (Filled/Outlined/Elevated) | 0.5 天 | — | ✅ |
| A.2 | AlertDialog | 0.5 天 | — | ✅ |
| A.3 | ListItem | 0.5 天 | — | ✅ |
| A.4 | TopAppBar | 1 天 | — | ✅ |
| A.5 | BottomAppBar | 0.5 天 | — | ✅ |
| A.6 | FloatingActionButton / ExtendedFAB | 0.5 天 | elevation Modifier（可选） | ✅ |
| A.7 | Badge / BadgedBox | 0.5 天 | — | ✅ |
| A.8 | TextButton variant | 0.5 天 | — | ✅ |
| A.9 | Tooltip (Plain) | 0.5 天 | 依赖现有 Popup | ✅ |

### Phase B：核心新增虚拟组件（2-3 周）

> 需要新增 NodeType + 自定义 View/Layout + ViewBinder。

| # | 组件 | 工作量 | 依赖 | 状态 |
|---|------|--------|------|------|
| B.1 | LazyRow | 1 天 | 复用 LazyColumn 架构 | ✅ |
| B.2 | NavigationBar | 2-3 天 | — | |
| B.3 | Scaffold | 2-3 天 | TopAppBar, BottomAppBar (Phase A) | |
| B.4 | DropdownMenu | 2 天 | 复用 Popup 架构 | |
| B.5 | FlowRow / FlowColumn | 2 天 | — | |
| B.6 | ScrollableColumn / ScrollableRow | 1 天 | — | ✅ |

### Phase C：中等组件（3-4 周）

> 按业务需求分批实现。

| # | 组件 | 工作量 | 依赖 |
|---|------|--------|------|
| C.1 | Chip (4 variants) | 1 天 | — |
| C.2 | ModalBottomSheet | 3-4 天 | 手势系统 |
| C.3 | PullToRefresh | 2 天 | LazyColumn 集成 |
| C.4 | LazyVerticalGrid | 2 天 | 复用 LazyColumn 架构 |
| C.5 | HorizontalPager / VerticalPager | 2 天 | 复用 TabPager 架构 |
| C.6 | SearchBar | 1 天 | TextField |
| C.7 | ElevatedCard | 0.5 天 | elevation Modifier |
| C.8 | SwipeToDismiss | 2-3 天 | 手势系统 |
| C.9 | NavigationDrawer | 2-3 天 | — |
| C.10 | RangeSlider | 2 天 | — |
| C.11 | DatePicker | 3-4 天 | 或走 AndroidView |

### Phase D：持续（按需）

| 组件 | 建议 |
|------|------|
| NavigationRail | 平板/大屏需求时实现 |
| StaggeredGrid | 瀑布流需求时实现 |
| Carousel | 轮播需求时实现（依赖 HorizontalPager） |
| Adaptive Scaffold | 多屏适配需求时实现 |

---

## §7 统计摘要

### 框架 vs Compose 覆盖率

| 维度 | 数量 |
|------|------|
| Compose M3 核心组件（去重、去 Expressive） | ~90 |
| 框架已覆盖 | 40 |
| 框架部分覆盖 | 4 |
| T1 核心缺口 | 10 → 2 (Phase A 关闭 7, Phase B 关闭 1) |
| T2 中等缺口 | 20 → 17 (Phase A 关闭 2, Phase B 关闭 1) |
| T3 AndroidView | 14 |
| T4 可组合封装 | 14 → 5 (Phase A 关闭 9) |

### 缺口分布

```
已覆盖 (28)     ████████████████░░░░░░░░░░░░░░░░  31%
部分覆盖 (4)    ██░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░   4%
T4 组合封装 (14) ████████░░░░░░░░░░░░░░░░░░░░░░░░  16%
T1+T2 新增 (30) █████████████████░░░░░░░░░░░░░░░░  33%
T3 AndroidView (14) ████████░░░░░░░░░░░░░░░░░░░░░░  16%
```

### 与 WIDGET_ROADMAP.md 的关系

| WIDGET_ROADMAP 分类 | 本文档归类 | 说明 |
|--------------------|----------|------|
| P1（已闭环） | 已覆盖 | 16 种核心控件已完成 |
| P2（框架稳定后做） | T1 + T2 | 本文档细化了 P2 的优先级和实现方式 |
| P3（继续 AndroidView） | T3 | 与 WIDGET_ROADMAP §7 一致 |
| — | T4（新增） | WIDGET_ROADMAP 未涉及的组合封装层 |

---

## §8 附录

### 8.1 Compose 组件到框架映射快速参考

| 你在 Compose 中写 | 在框架中等效于 |
|-------------------|---------------|
| `Text("Hello")` | `Text("Hello")` ✅ |
| `Button(onClick) { Text("OK") }` | `Button("OK", onClick = onClick)` ✅ |
| `TextField(value, onValueChange)` | `TextField(value, onValueChange)` ✅ |
| `Card { ... }` | `Surface(variant = Default) { ... }` → 待 `Card()` |
| `LazyColumn { items(list) { ... } }` | `LazyColumn(list) { ... }` ✅ |
| `Scaffold(topBar = {...}) { ... }` | ❌ 手动 Column + Box |
| `AlertDialog(onDismiss, ...) { ... }` | `Dialog(visible) { 手动布局 }` → 待 `AlertDialog()` |
| `NavigationBar { ... }` | ❌ 手动 Row + Column |
| `FloatingActionButton(onClick) { Icon(...) }` | `IconButton(icon, onClick)` 近似 → 待 `FloatingActionButton()` |
| `DropdownMenu(expanded, onDismiss) { ... }` | `Popup(visible, anchorId) { 手动布局 }` → 待 `DropdownMenu()` |
| `LazyRow { items(list) { ... } }` | `LazyRow(list) { ... }` ✅ |
| `FlowRow { chips }` | ❌ 需要新增 |
| `ModalBottomSheet { ... }` | ❌ 需要新增 |
| `Chip(onClick, label = { Text("Tag") })` | ❌ 需要组合封装 |
| `Modifier.verticalScroll()` | `ScrollableColumn { ... }` ✅ |

### 8.2 命名对照约定

为保持与 Compose 生态的一致性，后续新增组件的命名应遵循以下规则：

| Compose 命名 | 框架命名 | 说明 |
|-------------|---------|------|
| `NavigationBar` | `NavigationBar` | 保持一致 |
| `NavigationBarItem` | `NavigationBarScope.Item` | 使用 Scope 模式 |
| `TopAppBar` | `TopAppBar` | 保持一致 |
| `FloatingActionButton` | `FloatingActionButton` | 保持一致 |
| `ModalBottomSheet` | `ModalBottomSheet` | 保持一致 |
| `LazyRow` | `LazyRow` | 保持一致 |
| `LazyVerticalGrid` | `LazyVerticalGrid` | 保持一致 |
| `FlowRow` | `FlowRow` | 保持一致 |
| `Card` / `ElevatedCard` / `OutlinedCard` | `Card(variant = Filled/Elevated/Outlined)` | 使用 variant 枚举 |
| `AssistChip` / `FilterChip` / `InputChip` | `Chip(variant = Assist/Filter/Input)` | 使用 variant 枚举 |
| `PullToRefreshBox` | `PullToRefreshBox` | 保持一致 |

### 8.3 关联文档

| 文档 | 关系 |
|------|------|
| `WIDGET_ROADMAP.md` | P1/P2/P3 优先级定义，本文档在此基础上细化 P2 |
| `WIDGET_PROPERTY_AUDIT.md` | 已有控件属性审计，本文档聚焦于缺失控件 |
| `MODIFIER.md` | Modifier vs Spec 边界规则，新组件必须遵守 |
| `THEMING.md` | 新组件 Defaults 必须接入 Theme 体系 |
| `OVERLAY_COMPONENTS_ROADMAP.md` | Dialog/Snackbar/Toast/Popup 的弹层体系设计 |
