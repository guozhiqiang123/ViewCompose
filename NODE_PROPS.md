# Node Props Roadmap

## 1. 目标

`typed props` 已经把 `UIFramework` 从“纯字符串 key + 动态 map”推进到了“带泛型 key 的动态 props”。

这一步已经足够支撑当前 v1，但它还不是长期终态。

如果框架后续要继续往更稳定、更健壮、更易扩展的方向走，下一阶段需要在高收益节点上引入更明确的 `NodeProps` 结构。

本文档定义：

1. 为什么要做 `NodeProps`
2. `NodeProps` 和当前 `VNode.props` 如何并存
3. 哪些节点优先做
4. 迁移顺序和收口标准

## 2. 当前问题

当前主链路已经基本迁到 `typed props`：

- DSL 侧通过 `props { set(TypedPropKeys.Xxx, ...) }`
- renderer / binder 侧通过 `node.props[TypedPropKeys.Xxx]`
- 测试侧也基本切到 typed accessor

这比早期字符串 key 已经强很多，但仍然存在 4 个长期问题：

1. 节点结构可读性仍偏弱
2. 节点不变式分散
3. 调试/文档/工具化能力受限
4. 后续做节点级优化时，结构约束不够强

## 3. 设计原则

`NodeProps` 不应推翻当前 `typed props`，而应建立在其之上。

原则如下：

1. `VNode.props` 继续保留
2. `NodeProps` 只先覆盖高收益节点
3. renderer 优先读 `NodeProps`
4. DSL 同时写入 `spec + props`
5. `NodeProps` 只表达节点语义，不承载 `Modifier`

## 4. 推荐模型

### 4.1 `VNode`

目标形态：

```kotlin
data class VNode(
    val type: NodeType,
    val key: Any? = null,
    val props: Props = Props.Empty,
    val spec: NodeSpec? = null,
    val modifier: Modifier = Modifier,
    val children: List<VNode> = emptyList(),
)
```

### 4.2 `NodeSpec`

先用轻量 marker：

```kotlin
interface NodeSpec
```

然后按节点定义：

```kotlin
data class ButtonNodeProps(...) : NodeSpec
data class TabPagerNodeProps(...) : NodeSpec
data class TextFieldNodeProps(...) : NodeSpec
```

### 4.3 renderer 读取顺序

统一规则：

1. 优先 `node.spec as? XxxNodeProps`
2. 否则回退到 `node.props[TypedPropKeys.Xxx]`

## 5. 优先级

### P1：先做，收益最高

1. `Button`
2. `TabPager`
3. `TextField`

### P2：框架稳定后继续推进

1. `SegmentedControl`
2. `LazyColumn`
3. `Image`
4. `IconButton`
5. `Surface`

### P3：可长期保留在 typed props

1. `Text`
2. `Divider`
3. `LinearProgressIndicator`
4. `CircularProgressIndicator`
5. `Checkbox / Switch / RadioButton / Slider`

## 6. 与 Compose 的关系

Compose 的本质是：

- composable 参数本身就是强类型节点输入
- 编译器参与参数稳定性和重组判断
- 默认值通过 `MaterialTheme` / `CompositionLocal` 动态解析

`UIFramework` 还到不了这个层级，但 `NodeProps` 可以把当前结构从：

- `typed dynamic props`

推进到：

- `semi-structured node model`

## 7. 分阶段计划

### Phase 1：基础设施

- 给 `VNode` 增加 `spec`
- 引入 `NodeSpec`

### Phase 2：P1 试点

- `ButtonNodeProps`
- `TabPagerNodeProps`
- `TextFieldNodeProps`

要求：

- DSL 写入 `spec + props`
- binder 优先读 `spec`
- 现有测试继续通过

### Phase 3：验证收益

重点验证：

1. binder 代码是否更短、更清晰
2. 节点不变式是否更集中
3. 测试断言是否更直接
4. 后续新增字段是否更容易

### Phase 4：按需扩展

仅在 P1 验证明显收益后，才推广到 `SegmentedControl / LazyColumn / Image / Surface`。

## 8. 当前执行策略

当前立即执行的顺序：

1. `ButtonNodeProps`
2. `TabPagerNodeProps`
3. `TextFieldNodeProps`

不同时推进更多节点。
