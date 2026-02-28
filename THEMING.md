# UIFramework Theming

## 1. 文档定位

本文档定义 `UIFramework` 的主题系统设计、分层关系、与 Android View 主题体系的边界，以及后续分阶段落地路径。

这份文档是后续主题相关实现的基线。若实现需要偏离本文档，必须先更新本文档再继续开发。

补充专题：

- 主题公共属性单独覆盖规划见 [THEME_OVERRIDES.md](/Users/gzq/AndroidStudioProjects/UIFramework/THEME_OVERRIDES.md)

当前实现状态：

- `Phase 1` 已完成
- `Phase 2` 已完成
- `Phase 3` 已完成
- `Phase 4` 已完成

主题覆盖补充状态：

- 对象级 `UiThemeOverride(...)` 已完成
- builder 风格 `UiThemeOverride(...)` 已完成

## 2. 结论

`UIFramework` 有必要和 Android View 主题系统打通，但不能直接把 Android `Theme` 当成框架主题系统本身。

正确分层应当是：

1. `UIFramework Theme`
   - 框架自己的声明式 token 系统。
   - 面向 DSL、`Modifier`、Widget 默认样式。
2. `Android Theme Bridge`
   - 把 Android `Context` / `Theme` / `TextAppearance` / 颜色资源映射到框架 token。
   - 面向互操作和兼容现有 View 生态。

简化结论：

> 框架主题系统是主模型，Android View 主题系统是桥接层。

## 3. 为什么要做主题系统

当前项目已经出现了主题系统缺失的典型问题：

- sample 中直接散落 `Color.parseColor(...)`
- `backgroundColor`、`Divider` 等样式值直接写死在业务节点里
- 没有品牌切换、暗色模式、局部主题覆盖的统一入口
- `AndroidView` 互操作以后，样式来源会进一步分裂

如果继续沿用“节点里直接写具体颜色”的方式，后续会出现这些问题：

- 业务层和视觉设计强耦合
- 替换品牌色或增加夜间模式成本很高
- 不同 widget 默认样式无法统一演进
- 框架层无法提供“语义化样式”，只能暴露原始颜色值

## 4. 设计目标

- 提供框架级主题 token，而不是只暴露裸颜色值
- 主题作用域是声明式的，可在子树局部覆盖
- Widget 和 Modifier 可以读取当前主题作为默认值
- 支持未来接入 Android `Theme`、`ContextThemeWrapper`、资源 attr 解析
- 支持多主题切换、暗色模式和品牌定制
- 不破坏现有 `VNode -> Reconciler -> ViewTreeRenderer` 结构

## 5. 非目标

- v1 不做完整 Material Design 体系复刻
- v1 不做复杂排版系统和动态字体缩放策略
- v1 不强制所有样式都必须来自主题
- v1 不做 Android XML `style` 的双向自动同步
- v1 不实现 Compose `CompositionLocal` 的完整泛型系统

## 6. 分层设计

### 6.1 Framework Theme Layer

框架内部维护自己的主题 token 数据结构。

当前已实现结构：

```kotlin
data class UiColors(
    val background: Int,
    val surface: Int,
    val surfaceVariant: Int,
    val primary: Int,
    val accent: Int,
    val divider: Int,
    val textPrimary: Int,
    val textSecondary: Int,
)

data class UiTextStyle(
    val fontSizeSp: Int,
)

data class UiTypography(
    val title: UiTextStyle,
    val body: UiTextStyle,
    val label: UiTextStyle,
)

data class UiThemeTokens(
    val colors: UiColors,
    val typography: UiTypography,
)
```

说明：

- 当前已完成 `colors` + `typography`
- `shapes`、`spacing` 后续再补
- token 名称用语义化命名，不用 `red500`、`gray90` 这种原始视觉值命名

### 6.2 Theme Scope Layer

主题应该像 `remember` / `key` 一样，具备显式子树作用域。

当前实现已经从专用 `ThemeContext` 收敛到通用 local 机制，后续可复用到其他上下文。

示例：

```kotlin
UiTheme(AppTheme.light()) {
    Column {
        Text(
            text = "Title",
            modifier = Modifier.textColor(Theme.colors.textPrimary)
        )
    }
}
```

语义要求：

- 子树默认继承父主题
- 子树可以局部覆盖主题
- 没有显式主题时，使用框架默认主题

当前补充能力：

- `UiTheme(tokens = ...)` 用于整套主题切换
- `UiThemeOverride(...)` 用于父主题基础上的局部 patch
- `Theme.components` 用于承载组件默认样式 token

### 6.3 Widget/Modifier Consumption Layer

主题不能只存在于 DSL 外围，必须能被具体节点消费。

v1 建议优先接入：

- `Modifier.backgroundColor(...)`
- `Modifier.textColor(...)`
- `Divider(...)`
- `Text(...)`
- `Button(...)`

原则：

- 业务方可以显式传具体值覆盖主题默认值
- 没有显式值时，widget 使用主题 token 作为默认值

### 6.4 Android Theme Bridge Layer

和 Android View 系统打通是必要的，但应该放在桥接层。

桥接职责：

- 从 `Context` 解析 `colorPrimary` 等 attr
- 为 `AndroidView` / 自定义 `View` 提供主题化上下文
- 允许框架主题由 Android `Theme` 派生
- 允许未来做 `UiThemeTokens.fromContext(context)`

不建议的做法：

- 业务 DSL 直接满屏读取 Android attr
- 把 Android `Theme` 当作框架主题数据结构
- 让 `Modifier` 直接依赖 `Context` 才能工作

## 7. 运行时模型

主题系统不需要进入 `VNode` diff 作为单独节点类型。

更合适的方式是：

- 在构建 `VNode` 树时，使用 thread-local 主题上下文
- `Theme.current()` 读取当前主题
- `UiTheme {}` 在构建子树时压栈/出栈

原因：

- 主题本质上是声明式上下文，不是一个真实控件
- 不需要额外挂载 View
- 不需要引入专门的 theme patch 类型
- 和现有 `RememberContext` / `GroupKeyContext` 模型一致

## 8. API 设计

### 8.1 v1 推荐 API

```kotlin
UiTheme(AppTheme.light()) {
    Column(
        modifier = Modifier.backgroundColor(Theme.colors.background)
    ) {
        Text(
            text = "Hello",
            modifier = Modifier.textColor(Theme.colors.textPrimary)
        )
        Divider(color = Theme.colors.divider)
    }
}
```

局部覆盖推荐 API：

```kotlin
UiTheme(AppTheme.light()) {
    UiThemeOverride(
        colors = Theme.colors.copy(primary = 0xFF5C8DFF.toInt())
    ) {
        Button(text = "Scoped Primary")
    }
}
```

builder 风格：

```kotlin
UiTheme(AppTheme.light()) {
    UiThemeOverride(
        colors = { copy(primary = 0xFF5C8DFF.toInt()) },
        shapes = { copy(controlCornerRadius = 24.dp) },
    ) {
        Button(text = "Scoped Primary")
    }
}
```

组件默认样式覆盖：

```kotlin
UiTheme(AppTheme.light()) {
    UiThemeOverride(
        components = {
            copy(
                button = button.copy(
                    primaryContainer = 0xFF1F1F1F.toInt(),
                    primaryContent = 0xFFFFFFFF.toInt(),
                )
            )
        },
    ) {
        Button(text = "Scoped Button Default")
    }
}
```

### 8.2 推荐对象

```kotlin
object Theme {
    val current: UiThemeTokens
    val colors: UiColors
    val components: UiComponentStyles
}
```

```kotlin
object AppTheme {
    fun light(): UiThemeTokens
    fun dark(): UiThemeTokens
}
```

## 8.3 覆盖优先级

主题系统当前遵循下面的优先级：

1. 组件显式参数 / `Modifier`
2. 当前子树 `UiThemeOverride(...)`
3. 父主题 / 当前 `UiTheme(...)`
4. 框架默认主题

示例：

```kotlin
UiThemeOverride(
    colors = Theme.colors.copy(primary = green)
) {
    Button(
        text = "Delete",
        modifier = Modifier.backgroundColor(red)
    )
}
```

上面这个例子里，按钮最终背景应以显式 `Modifier.backgroundColor(red)` 为准，而不是 override 后的 `primary`。

## 8.4 使用边界

推荐：

- 页面或模块切换品牌主题时，用 `UiTheme(tokens = ...)`
- 某个 section 只改局部颜色/圆角/点击态时，用 `UiThemeOverride(...)`
- 某组按钮、输入框、分段控件只改组件默认样式时，用 `UiThemeOverride(components = ...)`
- 单个控件有特殊视觉要求时，用显式组件参数或 `Modifier`

不推荐：

- 为了只改一个字段，重新手写一整套 `UiThemeTokens`
- 在业务层同时大量混用主题 override 和硬编码样式
- 在 override API 里直接耦合 Android attr 解析

### 8.5 为什么 v1 不直接上泛型 CompositionLocal

当前项目仍然没有必要一开始就引入完整的 `CompositionLocal<T>` 抽象。

原因：

- 当前 runtime 还在早期
- 先收敛“主题”这一类高价值上下文即可
- 后续如果 theme/elevation/locale/density 都需要类似能力，再抽象成通用 local 系统更稳

当前实现补充：

- 已有可复用的 `LocalValue` / `LocalContext`
- `Theme` 已经迁到 local 机制
- `UiEnvironment`、`Environment`、`AndroidEnvironmentBridge` 已落地

## 9. 与 Android View Theme 的关系

### 9.1 必须打通的场景

- `AndroidView` 挂载已有业务 `View`
- 复用已有自定义控件，它们依赖 `context.theme`
- 从资源 attr 派生默认品牌色
- 与宿主 Activity / Fragment 的夜间模式切换协同

### 9.2 不应该强耦合的场景

- 基础 `Text/Box/Row/Column` 的主题读取
- 纯框架 DSL 的颜色/排版 token 分发
- Widget 默认视觉语义

### 9.3 建议桥接方式

后续提供：

```kotlin
object AndroidThemeBridge {
    fun fromContext(context: Context): UiThemeTokens
}
```

以及：

```kotlin
fun UiTheme(
    bridgeFromContext: Boolean = false,
    tokens: UiThemeTokens? = null,
    content: UiTreeBuilder.() -> Unit,
)
```

规则建议：

- 显式 `tokens` 优先级最高
- 若开启 `bridgeFromContext`，则从宿主 `Context` 派生默认 token
- 两者都没有时，回落到框架内置默认主题

## 10. 分阶段落地

### Phase 1

状态：已完成

目标：

- 引入 `UiColors`、`UiThemeTokens`
- 引入 `ThemeContext` / `Theme.colors`
- 引入 `UiTheme {}` 作用域
- 补 `Modifier.textColor(...)`
- sample 改为使用主题 token

不做：

- typography
- Android attr 解析
- 主题变化动画

### Phase 2

状态：已完成

目标：

- 引入 `AndroidThemeBridge.fromContext(context)`
- 支持从宿主 `Activity` / `Fragment` 主题派生默认 token
- 为 `AndroidView` 互操作提供主题化上下文约定

### Phase 3

状态：已完成

目标：

- 增加 `UiTypography`
- 增加 widget 默认主题样式
- 增加 `ButtonDefaults`、`TextDefaults` 这类默认值入口

当前实现补充：

- 已新增 `ButtonDefaults`
- 已新增 `TextDefaults`
- 已新增 `DividerDefaults`
- 已新增 `SurfaceDefaults`
- `Text` / `Button` / `Divider` 已接入默认主题样式
- sample 已基本收口到默认值入口

### Phase 4

状态：已完成

目标：

- 抽象为更通用的 local/context 系统
- 让 density、locale、layout direction 等上下文共享同一机制

当前实现补充：

- 已新增通用 local 运行时
- 已新增 `UiEnvironment`
- 已新增 `Environment.density`
- 已新增 `Environment.localeTags`
- 已新增 `Environment.layoutDirection`
- sample 已开始使用环境上下文和 density 驱动尺寸

## 11. 当前实现约束

按本项目当前状态，建议先做下面这条最短路径：

1. 先做框架主题，不直接依赖 Android `Theme`
2. 用主题 token 替换 sample 中散落的颜色常量
3. 补 `textColor`，让主题 token 能覆盖文本层
4. 再做 Android 主题桥接

这是当前最稳的路线。因为如果现在直接先做 Android attr 解析，框架 DSL 层会被平台上下文反向侵入，主题系统会从一开始就失去独立性。
