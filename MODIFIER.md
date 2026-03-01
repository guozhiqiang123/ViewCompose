# Modifier Architecture

## 1. 文档定位

本文档定义 `UIFramework` 中 `Modifier`、控件 `Prop`、主题默认值三者的职责边界。

目标不是描述当前实现细节，而是给后续重构和新增控件提供统一规则，避免继续把控件语义、布局语义、主题默认值混在一起。

本文档优先级高于局部实现。如果实现与本文档冲突，应先更新本文档，再继续开发。

## 2. 当前问题

当前实现已经暴露出两个结构性问题：

1. `Modifier.Empty` 作为 identity 写法不自然，调用成本高
2. `Modifier` 同时承担了布局、装饰、交互、文本样式、控件默认样式等多种职责，边界已经模糊

最典型的例子：

- `padding`、`margin`、`width`、`alpha` 这类能力，天然适合放在 `Modifier`
- `textColor`、`textSize` 这类能力，当前也被放进了 `Modifier`
- 但 `textColor` 对 `Row`、`Column`、`Box` 这类非文本节点并不具备稳定语义

这会带来三个后果：

1. `Modifier` 失去通用性边界，任何节点都能挂不适合自己的能力
2. 父布局约束型能力缺少作用域限制，比如 `weight` 可以在任意节点上调用
3. 主题默认值不得不通过“先算 token，再拼 `Modifier`”的方式下发，链路过长

## 3. 设计目标

后续 `Modifier` 体系必须满足以下目标：

1. 调用体验接近 Compose，不再依赖 `Modifier.Empty`
2. `Modifier` 只承载跨节点通用修饰能力
3. 控件自身语义回到控件参数 / `Prop`
4. 父容器相关布局数据通过作用域约束暴露
5. 主题只负责默认值，不直接驱动通用 `Modifier`

## 4. 角色划分

### 4.1 Modifier

`Modifier` 负责“外部修饰”，而不是“控件是什么”。

允许放入 `Modifier` 的能力：

- 布局尺寸：`width`、`height`、`size`、`minHeight`
- 布局占位：`padding`、`margin`
- 外观修饰：`backgroundColor`、`border`、`cornerRadius`、`alpha`
- 可见性与层级：`visibility`、`offset`、`zIndex`
- 通用交互：`clickable`

这些能力的共同点是：

- 对大多数节点都成立
- 本质上是节点外层约束或外层外观
- 不依赖某个具体控件类型

### 4.2 Scoped Modifier

一部分 modifier 不是“全局通用修饰”，而是“父容器给子节点的布局数据”。

这类能力不应作为全局 `Modifier` 扩展暴露，而应通过作用域限制：

- `RowScope.weight`
- `RowScope.align`
- `ColumnScope.weight`
- `ColumnScope.align`
- `BoxScope.align`

这类能力的共同点是：

- 只在特定父布局内成立
- 语义依赖父容器
- 如果全局开放，会制造大量无意义调用

### 4.3 Props

控件 `Prop` 负责“控件自身语义”。

应进入 `Prop` 或控件参数层的能力包括：

- `Text`
  - `color`
  - `style`
  - `maxLines`
  - `overflow`
  - `textAlign`
- `Image`
  - `contentScale`
  - `tint`
  - `placeholder`
  - `error`
  - `fallback`
- `Button`
  - `variant`
  - `size`
  - `enabled`
  - `leadingIcon`
  - `trailingIcon`
- `TextField`
  - `label`
  - `placeholder`
  - `supportingText`
  - `readOnly`
  - `imeAction`
  - `isError`

规则很简单：

> 只要某个属性离开具体控件类型就不稳定，它就不该放在通用 `Modifier` 中。

### 4.4 Theme

主题只负责“默认值来源”，不负责定义通用 `Modifier`。

更准确地说：

- `Theme` 提供 token 和默认样式
- `Defaults` 根据 `Theme` 解析控件默认值
- 控件参数 / `Prop` 作为显式覆盖
- `Modifier` 只做外层修饰

正确链路应为：

```text
Theme -> Defaults -> Props -> Renderer
```

而不是：

```text
Theme -> Modifier -> Renderer
```

## 5. 推荐规则

后续新增能力时，按以下顺序判断落点：

1. 这是所有节点都能稳定理解的外部修饰吗？
   - 是：进入 `Modifier`
2. 这是只在某个父容器内成立的布局数据吗？
   - 是：进入对应 scope modifier
3. 这是某个控件自己的语义吗？
   - 是：进入控件参数 / `Prop`
4. 这是控件的默认值来源吗？
   - 是：进入 `Theme` / `Defaults`

## 6. Compose 对齐原则

`UIFramework` 不需要复刻 Compose Runtime，但应在 API 分层上尽量对齐 Compose 的成熟经验：

- `Modifier` 是通用修饰链
- 父布局相关能力通过 scope 暴露
- 控件自身语义是参数，不是 modifier
- 主题通过 `Theme + Local` 提供默认值

因此，后续不建议继续新增以下类型的通用 modifier：

- `textColor`
- `textSize`
- `hintColor`
- `labelColor`
- `contentScale`
- 其他明显依赖具体控件类型的能力

## 7. 重构路线

### Phase 1

- 引入 `Modifier` identity 写法
- 调用侧默认改为 `Modifier`
- `Modifier.Empty` 保留兼容，但逐步淘汰

### Phase 2

- 禁止继续新增控件语义型 modifier
- 新增能力优先回到控件参数 / `Prop`
- 文本、输入、按钮等控件默认值链路逐步从 `Modifier` 转为 `Prop`

### Phase 3

- 引入 `RowScope`、`ColumnScope`、`BoxScope`
- 将 `weight`、`align` 等父布局相关能力迁入 scope

### Phase 4

- 清理历史兼容层
- `Modifier.Empty` 标记移除
- `textColor` / `textSize` 等历史通用 modifier 彻底退场

## 8. 当前实施结论

当前建议按以下顺序执行：

1. 先完成 `Modifier` identity API 收口
2. 再把文本样式类能力移出通用 `Modifier`
3. 再做 scope modifier
4. 最后统一清理主题默认值到 `Prop` 的链路

这是当前最稳、最不容易引入回归的重构顺序。
