# UIFramework Theme Overrides

## 1. 文档定位

本文档专门讨论 `UIFramework` 里的“局部主题复用与覆盖”。

它回答 4 个问题：

1. 为什么需要局部主题覆盖
2. 当前实现中真正的问题是什么
3. 正确的 override 应该覆盖什么，而不应该覆盖什么
4. 后续应该如何把 override 做成真正可扩展的能力

主文档见 [THEMING.md](/Users/gzq/AndroidStudioProjects/UIFramework/THEMING.md)。

## 2. 结论

局部主题覆盖是必须保留的能力。

但当前最需要纠正的不是“要不要 override”，而是：

> override 的对象应该是“语义主题 patch”和“组件默认值 patch”，而不是一整套预先解析好的组件样式结果。

后续正确方向应分为 3 类覆盖：

1. `Semantic Theme Override`
2. `Context Local Override`
3. `Component Default Override`

这三类覆盖不能继续混成一层。

## 3. 为什么必须有局部覆盖

没有局部覆盖，业务层很快会退化成这些写法：

- 某个 section 整片手写 `Modifier.backgroundColor(...)`
- 某组按钮每个都单独传颜色
- 某个表单区域每个输入框都单独改 error / disabled 样式
- 为了局部一处变化，复制整套主题对象

这会直接带来三个问题：

1. 样式逻辑分散
2. 主题无法表达“子树语义”
3. 后续大规模换肤和实验会很痛苦

所以 override 不是增强项，而是主题系统成立的前提。

## 4. 当前实现中的结构性问题

当前实现已经提供了：

- `UiTheme(tokens = ...)`
- `UiThemeOverride(...)`
- 对象级和 builder 风格覆盖

这些 API 本身没有问题。

真正的问题是当前的数据模型：

- `UiThemeTokens` 内直接持有 `components`
- `components` 里保存的是完整组件样式结果
- `override(colors = ...)` 时，组件默认值不会天然重算

这会导致局部语义覆盖与组件默认值覆盖的职责不清。

例子：

```kotlin
UiThemeOverride(
    colors = { copy(primary = brandRed) }
) {
    Button(text = "Delete")
}
```

这段代码的直觉语义应该是：

- 我改了这个子树的主色
- 未显式覆盖的按钮默认主色应跟着变

如果按钮默认值来自一份旧的 resolved component styles，这个语义就会失真。

## 5. 正确的 override 分层

### 5.1 Semantic Theme Override

这类覆盖解决“这个子树的语义主题是什么”。

典型内容：

- `colors`
- `typography`
- `shapes`
- `controls`
- `interactions`
- `input`

示例：

```kotlin
UiThemeOverride(
    colors = { copy(primary = brandRed) },
    shapes = { copy(controlCornerRadius = 24.dp) },
) {
    ...
}
```

职责：

- 改变后续默认值的推导来源
- 改变同一子树的整体语义

### 5.2 Context Local Override

这类覆盖解决“比整套主题更细的上下文是什么”。

后续建议承载：

- 文本默认样式
- 内容默认颜色
- 点击态 / indication

示例心智：

- 一段内容默认文字是次要色
- 一个区块里所有图标默认用强调色

这种需求不应全部塞进 `UiThemeOverride`。

### 5.3 Component Default Override

这类覆盖解决“某个组件组的默认值需要局部 patch”。

例如：

- 这块区域里的按钮主色特殊
- 这组输入框的 outlined border 特殊
- 这个面板里的 checkbox / switch / slider 强调色统一替换

关键原则：

> 它是 patch，不是完整结果。

## 6. 推荐的数据模型

### 6.1 不推荐的形态

```kotlin
data class UiThemeTokens(
    val colors: UiColors,
    val typography: UiTypography,
    val components: UiComponentStyles,
)
```

问题：

- `components` 太早被解析
- semantic override 和 component override 容易互相打架
- 扩展到更多控件后，维护成本会越来越高

### 6.2 推荐的形态

```kotlin
data class UiThemeTokens(
    val colors: UiColors,
    val typography: UiTypography,
    val shapes: UiShapes,
    val controls: UiControlSizing,
    val interactions: UiInteractionColors,
    val input: UiInputColors,
)
```

再配一层：

```kotlin
data class UiComponentOverrides(
    val button: UiButtonOverride? = null,
    val textField: UiTextFieldOverride? = null,
    val checkbox: UiCheckboxOverride? = null,
    ...
)
```

每个 override 对象应该尽量稀疏：

```kotlin
data class UiCheckboxOverride(
    val control: Int? = null,
    val controlDisabled: Int? = null,
    val label: Int? = null,
    val labelDisabled: Int? = null,
)
```

语义：

- `null` 表示“不覆盖，继续回落”
- 非 `null` 才表示真正 patch

## 7. 推荐 API

### 7.1 全局主题

```kotlin
UiTheme(tokens = appThemeTokens) {
    ...
}
```

### 7.2 局部语义覆盖

```kotlin
UiThemeOverride(
    colors = { copy(primary = brandRed) },
    shapes = { copy(controlCornerRadius = 24.dp) },
) {
    ...
}
```

### 7.3 局部组件默认值覆盖

推荐长期 API 方向：

```kotlin
UiThemeOverride(
    componentOverrides = {
        copy(
            button = button.copy(
                primaryContainer = brandRed,
            ),
        )
    },
) {
    ...
}
```

### 7.4 更细粒度局部上下文

未来应补：

```kotlin
ProvideTextStyle(...)
ProvideContentColor(...)
ProvideIndication(...)
```

这些能力可以减少 `UiThemeOverride` 的职责膨胀。

## 8. override 优先级

后续推荐固定为：

1. 显式组件参数 / `Modifier`
2. 局部 component override
3. 局部 semantic theme override
4. 当前全局主题
5. 框架默认值

说明：

- 显式参数永远最强
- component override 只改组件默认值
- semantic override 改变默认值的推导来源

## 9. 使用规则

### 9.1 什么场景应该用 semantic override

- 某个页面局部品牌色不同
- 某个卡片组需要不同 surface / accent
- 某个模块整体圆角或点击态不同

### 9.2 什么场景应该用 component override

- 只想让这块区域里的按钮默认更深
- 只想让输入控件组的 border / label 色变化
- 只想让一组 checkbox / switch / slider 使用实验色

### 9.3 什么场景不应该用 override

- 单个控件做一次性特殊视觉
- 某个业务控件只改一个很少复用的颜色
- Android 原生复杂控件的全部 setter 映射

这些场景更适合：

- 显式参数
- `Modifier`
- `AndroidView`

## 10. 和控件扩展的关系

override 设计必须和控件规划联动。

原则：

- `P1` 控件必须有稳定主题默认值和局部 override 入口
- `P2` 控件可以先只依赖语义主题，不急着补完整 component override
- `P3` 控件直接走 `AndroidView`，不强行纳入主题系统

更具体的控件分级见 [WIDGET_ROADMAP.md](/Users/gzq/AndroidStudioProjects/UIFramework/WIDGET_ROADMAP.md)。

## 11. 推荐迁移路径

### Phase 1

- 保留现有 `UiThemeOverride(...)` API
- 文档层冻结新的语义解释

### Phase 2

- 把当前 `components` 逐步从 resolved values 改成 sparse overrides

### Phase 3

- `ButtonDefaults / TextFieldDefaults / CheckboxDefaults / TabPagerDefaults` 等改成动态解析

### Phase 4

- 增加更细的 local，上下文复用不再只依赖 `UiThemeOverride`

## 12. 当前判断

当前 override 设计的结论是：

- `方向正确`
- `入口保留`
- `内部模型必须调整`

如果后续继续基于“完整 resolved component tokens”堆功能，局部复用会越来越难维护。

如果按本文档收敛，override 会更符合 Compose 心智，也更适合长期扩展。

