# UIFramework Theme Overrides

## 1. 文档定位

本文档定义 `UIFramework` 中“主题公共属性单独覆盖”的设计目标、范围、API 方案、运行时模型与分阶段落地计划。

这份文档是后续主题覆盖能力实现的基线。后续实现默认遵循本文档；如果实现过程中需要偏离，必须先更新本文档中的对应章节，再继续开发。

当前状态：

- 日期：2026-02-28
- 仓库：`/Users/gzq/AndroidStudioProjects/UIFramework`
- 当前能力：
  - 已有完整 `UiThemeTokens`
  - 已有 `LocalValue` / `LocalContext`
  - 已有 `Theme.colors / typography / input / shapes / controls / interactions`
  - 已有组件级显式覆盖能力，例如 `Modifier.backgroundColor(...)`
  - 已有对象级 `UiThemeTokens.override(...)`
  - 已有对象级 `UiThemeOverride(...)`
- 当前缺口：
  - 当前专题范围内已无实现缺口

文档更新：

- `Phase 1` 已完成
- `Phase 2` 已完成
- `Phase 3` 已完成
- `Phase 4` 已完成

## 2. 结论

`UIFramework` 有必要支持“主题公共属性单独覆盖”，而且应该尽快做。

更准确地说，主题系统后续必须同时支持两层覆盖：

1. 组件级覆盖
   - 组件调用时显式传值
   - 例如 `Modifier.backgroundColor(...)`、`Text(style = ...)`
2. 主题级局部覆盖
   - 不改整套主题，只改当前子树需要的 token
   - 例如只改 `primary`、`controlCornerRadius`、`pressedOverlay`

简化结论：

> 组件级覆盖解决“单个控件”，主题级覆盖解决“子树语义”。

## 3. 为什么现在要做

当前主题系统已经能支撑整页换肤，但还没有解决下面这些高频场景：

- 某个页面局部主色不同
- 某个 section 想保留整体主题，只改 surface/background
- 某组按钮想统一换成更大的圆角
- 某个输入表单想统一改输入框容器色和 error 色
- 某个实验模块想只改 pressed/ripple，而不复制一整套主题
- 某张卡片或组件组想做局部品牌化，而不影响整页

如果不补主题级局部覆盖，业务侧很快会退回到这几种不理想写法：

- 到处写 `Theme.colors.copy(...)`
- 每个局部都重新构造完整 `UiThemeTokens`
- 继续在业务组件里塞大量 `Modifier` 显式值
- 形成“样式在主题里一半，在节点上写死一半”的混合模型

这会让主题系统失去语义层价值。

## 4. 设计目标

- 支持按 theme 子域单独覆盖：
  - `colors`
  - `typography`
  - `input`
  - `shapes`
  - `controls`
  - `components`
  - `interactions`
- 覆盖行为是声明式的，作用于当前子树
- 没有显式 override 时，保持父主题继承
- API 足够轻，不要求业务每次手写完整 token 树
- 不破坏现有 `UiTheme(tokens = ...)` 用法
- 能和现有 `LocalValue` 机制平滑衔接
- 后续可扩展到 environment / component defaults / design tokens

## 5. 非目标

- v1 不做 Material 级 token alias 系统
- v1 不做 CSS Variables 式字符串 token 查表
- v1 不做运行时动画主题插值
- v1 不做 XML style/theme 到 override 的自动双向映射
- v1 不做无限细粒度的字段级 diff 优化

## 6. 当前问题拆解

### 6.1 当前 `UiTheme` 的能力边界

当前 `UiTheme` 已支持：

- 提供一整套 `UiThemeTokens`
- 子树继承父主题
- 使用 `Theme.xxx` 读取当前 token

当前 `UiTheme` 不支持：

- 只传 `colors = ...` 这种分项覆盖
- 只改单字段，例如 `primary = xxx`
- 局部 patch 父主题

### 6.2 当前业务侧可行但不理想的做法

理论上业务可以这样写：

```kotlin
UiTheme(
    tokens = Theme.current.copy(
        colors = Theme.colors.copy(primary = 0xFF123456.toInt())
    )
) {
    ...
}
```

但这有几个问题：

- `Theme.current` 目前没有正式公开对象
- 覆盖逻辑太啰嗦
- 很容易误复制不需要的字段
- 不利于后面统一审查和优化 override 行为

## 7. 核心设计

### 7.1 总体策略

保留现有 `UiTheme(tokens = ...)` 作为“整套主题替换入口”，新增“分项 override 入口”。

两者关系如下：

- `UiTheme(tokens = ...)`
  - 用于整套主题切换
  - 例如浅色/深色/品牌主题切换
- `UiThemeOverride(...)`
  - 用于父主题基础上的局部 patch
  - 例如局部改 `primary`、局部改 `controlCornerRadius`

### 7.2 推荐 API

第一阶段推荐提供两个入口：

```kotlin
fun UiTreeBuilder.UiTheme(
    tokens: UiThemeTokens? = null,
    androidContext: Context? = null,
    content: UiTreeBuilder.() -> Unit,
)
```

```kotlin
fun UiTreeBuilder.UiThemeOverride(
    colors: UiColors? = null,
    typography: UiTypography? = null,
    input: UiInputColors? = null,
    shapes: UiShapes? = null,
    controls: UiControlSizing? = null,
    components: UiComponentStyles? = null,
    interactions: UiInteractionColors? = null,
    content: UiTreeBuilder.() -> Unit,
)
```

语义：

- 如果某项传 `null`，继承父主题对应子域
- 如果某项传具体对象，则只替换该子域
- override 后形成一份新的 `UiThemeTokens`，压入当前子树 local

### 7.3 更细一层的 API

在 `UiThemeOverride(...)` 稳定后，再补一层字段级 builder API：

```kotlin
fun UiTreeBuilder.UiThemeOverride(
    colors: (UiColors.() -> UiColors)? = null,
    typography: (UiTypography.() -> UiTypography)? = null,
    input: (UiInputColors.() -> UiInputColors)? = null,
    shapes: (UiShapes.() -> UiShapes)? = null,
    controls: (UiControlSizing.() -> UiControlSizing)? = null,
    components: (UiComponentStyles.() -> UiComponentStyles)? = null,
    interactions: (UiInteractionColors.() -> UiInteractionColors)? = null,
    content: UiTreeBuilder.() -> Unit,
)
```

示例：

```kotlin
UiThemeOverride(
    colors = { copy(primary = 0xFF5C8DFF.toInt()) },
    shapes = { copy(controlCornerRadius = 24.dp) },
) {
    ...
}
```

这个 API 比直接传完整对象更顺手，但不应当作为第一步。

原因：

- 先落对象级覆盖更简单
- 运行时实现和测试边界更清楚
- 后续可以在它上面再包装 DSL

## 8. 运行时模型

主题 override 不需要新增节点类型，也不进入 `VNode` diff。

它仍然是构建期上下文行为：

1. 读取当前主题 `baseTokens`
2. 根据 override 参数构造 `nextTokens`
3. 通过现有 `LocalValue` 机制把 `nextTokens` 压入子树
4. 子树里 `Theme.xxx` 读取的就是 override 后结果

这意味着：

- 不需要新 View
- 不需要新 patch 类型
- 不增加 reconcile 复杂度
- 与现有 `UiTheme` / `UiEnvironment` 模型一致

## 9. 数据模型建议

### 9.1 维持 `UiThemeTokens` 不变

不建议为了 override 引入另一套平行 token 结构。

继续保留：

```kotlin
data class UiThemeTokens(
    val colors: UiColors,
    val typography: UiTypography,
    val input: UiInputColors,
    val shapes: UiShapes,
    val controls: UiControlSizing,
    val interactions: UiInteractionColors,
)
```

### 9.2 新增统一 patch 合并函数

建议新增：

```kotlin
internal fun UiThemeTokens.override(
    colors: UiColors? = null,
    typography: UiTypography? = null,
    input: UiInputColors? = null,
    shapes: UiShapes? = null,
    controls: UiControlSizing? = null,
    interactions: UiInteractionColors? = null,
): UiThemeTokens
```

职责：

- 把父主题和 override 合并成新的 token
- 集中主题 patch 规则
- 避免 merge 逻辑分散在 DSL 入口里

后续如果要支持 builder override，再加：

```kotlin
internal fun UiThemeTokens.override(
    colors: (UiColors.() -> UiColors)? = null,
    ...
): UiThemeTokens
```

## 10. API 使用示例

### 10.1 整个 section 改主色

```kotlin
UiThemeOverride(
    colors = Theme.colors.copy(primary = 0xFF4B8B6E.toInt())
) {
    Button(text = "Save")
    Slider(...)
}
```

### 10.2 整个卡片组改圆角

```kotlin
UiThemeOverride(
    shapes = Theme.shapes.copy(cardCornerRadius = 28.dp)
) {
    DemoSection(...)
}
```

### 10.3 整个输入区改 input token

```kotlin
UiThemeOverride(
    input = Theme.input.copy(
        fieldContainer = Theme.colors.surfaceVariant,
        fieldError = 0xFFD64545.toInt(),
    )
) {
    InputPage()
}
```

## 11. 与组件级覆盖的边界

必须明确优先级：

1. 组件显式参数 / `Modifier`
2. 当前子树 override 后的 theme
3. 父主题默认值

例如：

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

此时按钮背景应以显式 `Modifier.backgroundColor(red)` 为准，而不是 override 的 `primary`。

## 12. 与 Android Theme Bridge 的关系

主题 override 应发生在 framework token 层，而不是 Android attr 层。

推荐顺序：

1. `AndroidThemeBridge.fromContext(context)` 得到 `baseTokens`
2. `UiTheme(baseTokens)` 进入子树
3. 子树内局部使用 `UiThemeOverride(...)`

不建议：

- 在 override API 里直接读 Android `Theme`
- 把 Android attr 和 framework override 混成一个入口

原因：

- 会让 API 语义变脏
- 不利于单测
- 不利于未来跨宿主扩展

## 13. Demo 规划

完成后 demo 需要至少补 3 类示例：

1. 局部颜色覆盖
   - Overview 某个 section 改主色/按钮色
2. 局部形状覆盖
   - 某块卡片和输入框统一换圆角
3. 局部交互覆盖
   - 某组控件使用不同 pressed overlay

目标：

- 证明 override 生效范围只在当前子树
- 证明离开 override 作用域后恢复父主题
- 证明组件显式传值仍然拥有更高优先级

## 14. 测试规划

### 14.1 单元测试

需要新增测试覆盖：

- `UiThemeOverride(colors = ...)` 只替换颜色，不影响其他 token
- 嵌套 override 时，内层覆盖优先于外层
- 离开作用域后，父主题恢复
- 组件显式参数优先于 theme override
- builder 风格 override 的 copy 结果正确

### 14.2 Sample 验证

需要验证：

- Demo 里至少 3 处局部主题覆盖都能正确渲染
- 主题切换和局部 override 同时存在时，override 仍正确工作

## 15. 分阶段落地计划

### Phase 1：对象级 Theme Override

目标：

- 新增 `UiThemeOverride(...)`
- 新增 `UiThemeTokens.override(...)`
- 支持 `colors / typography / input / shapes / controls / interactions` 分项覆盖
- 补基础单测

完成标志：

- 业务可以只改某个主题子域，而不重建整套主题

### Phase 2：Demo 验证

目标：

- 在 demo 中加入局部主题覆盖样例
- 覆盖颜色、圆角、点击态至少三类

完成标志：

- 可以从 sample 直观看到作用域覆盖效果

### Phase 3：字段级 Builder Override

目标：

- 支持 `colors = { copy(primary = ...) }` 这类 API
- 降低业务写法噪音

完成标志：

- 常见 override 不再需要手写完整对象

### Phase 4：文档与约束收口

目标：

- 把 override 优先级、适用场景、反例写清楚
- 视情况回写到 `THEMING.md`

完成标志：

- 主题系统与主题覆盖的边界清晰，不再混淆

## 16. 当前推荐执行顺序

建议严格按这个顺序推进：

1. 先做 `UiThemeTokens.override(...)`
2. 再做 `UiThemeOverride(...)`
3. 先做对象级 override，不急着做 builder 风格
4. 先补单测，再接 demo 示例
5. 最后再决定是否把 override API 合并进 `UiTheme(...)`

## 17. 当前状态标记

当前阶段状态：

- `Phase 1` 已完成
- `Phase 2` 已完成
- `Phase 3` 已完成
- `Phase 4` 已完成

补充实现状态：

- `Theme.components` 已完成
- `Button/TextField/SegmentedControl` 已切到组件默认样式 token
