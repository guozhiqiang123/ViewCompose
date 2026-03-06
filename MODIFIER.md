# Modifier Architecture

## 1. 文档定位

本文档定义 `Modifier`、组件 `Prop/NodeSpec`、`Theme/Defaults` 的当前边界。

目标是保证新增能力时落点明确，避免语义混放。

## 2. 当前基线（2026-03）

1. identity 入口统一为 `Modifier`（`Modifier.Empty` 已移除）
2. 文本语义类历史 modifier（如 `textColor/textSize`）已退场
3. `weight/align/FlexibleSpacer` 仅通过 `RowScope/ColumnScope/BoxScope` 暴露
4. system bars 适配走组件侧 `Modifier.systemBarsInsetsPadding(...)`

## 3. 角色边界

### 3.1 Modifier（通用外层修饰）

适合放入 `Modifier` 的能力：

1. 尺寸与占位：`size/width/height/minWidth/minHeight/padding/margin`
2. 外观修饰：`backgroundColor/border/cornerRadius/alpha/elevation`
3. 可见性与层级：`visibility/offset/zIndex`
4. 通用交互与可访问性：`clickable/contentDescription`
5. 测试定位：`testTag`
6. 系统栏内边距：`systemBarsInsetsPadding`
7. 逃生通道：`nativeView(key, configure)`

### 3.2 Scoped Modifier（父容器相关 parent-data）

只在特定父容器内成立的能力，通过作用域暴露：

1. `RowScope.weight`
2. `RowScope.align`
3. `ColumnScope.weight`
4. `ColumnScope.align`
5. `BoxScope.align`

### 3.3 Props / NodeSpec（组件语义）

组件自身语义进入组件参数与 `NodeSpec`，例如：

1. `Text`：`color/style/maxLines/overflow/textAlign`
2. `Image`：`contentScale/tint/placeholder/error/fallback`
3. `Button`：`variant/size/enabled/leadingIcon/trailingIcon`
4. `TextField`：`label/placeholder/supportingText/readOnly/imeAction/isError`

### 3.4 Theme / Defaults（默认值来源）

默认值链路固定为：

`Theme -> Defaults -> NodeSpec/Props -> Renderer`

约束：

1. 不把主题默认值直接编码为通用 `Modifier`
2. 不在 renderer 写组件业务默认值

## 4. 新能力落点判断

新增一个属性时，按顺序判断：

1. 是否对大多数节点都稳定成立的外层修饰？
2. 是否父容器相关的布局数据？
3. 是否某个组件自身语义？
4. 是否默认值来源（主题/默认样式）？

命中哪一类，就落到对应层，不跨层混放。

## 5. 反模式清单

1. 在通用 `Modifier` 新增组件专属语义字段
2. 在全局 `Modifier` 暴露父容器特定能力
3. 为了快速接入把第一方长期语义回流到动态 map
4. 把主题覆盖当作组件参数替代方案

## 6. Compose 对齐原则

`UIFramework` 不复刻 Compose runtime/compiler，但在 API 分层上保持对齐：

1. `Modifier` = 通用修饰链
2. parent-data = scope API
3. 组件语义 = 参数/`NodeSpec`
4. 主题 = 默认值来源

## 7. 变更门禁

涉及 `Modifier` 边界变化时，至少完成：

1. 本文档同步
2. 对应 `NodeSpec/renderer` 路径回归
3. demo 验证与必要 UI 测试

流程规则见 [WORKFLOW.md](/Users/gzq/AndroidStudioProjects/UIFramework/WORKFLOW.md)。

## 8. 关联文档

1. [NODE_PROPS.md](/Users/gzq/AndroidStudioProjects/UIFramework/NODE_PROPS.md)
2. [THEMING.md](/Users/gzq/AndroidStudioProjects/UIFramework/THEMING.md)
3. [ARCHITECTURE.md](/Users/gzq/AndroidStudioProjects/UIFramework/ARCHITECTURE.md)
