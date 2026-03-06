# UIFramework Theming

## 1. 文档定位

本文档是主题系统规范版，定义：

1. 主题模型边界
2. 默认值解析链路
3. 局部覆盖规则
4. 新增主题能力时的落地约束

历史长版见：

- [THEMING_FULL_2026-03-06.md](/Users/gzq/AndroidStudioProjects/UIFramework/docs/archive/THEMING_FULL_2026-03-06.md)

## 2. 当前主题模型

`UiThemeTokens` 当前核心字段保持为：

1. `colors`
2. `typography`
3. `shapes`
4. `controls`

关键原则：

1. 顶层主题只承载语义 token，不承载每个组件的完整 resolved style。
2. 组件默认值在 `Defaults` 层按需从 `Theme` 派生，不做全量预计算。
3. 组件显式参数优先级高于主题默认值。

## 3. 默认值链路

标准链路必须保持：

`Theme -> Defaults -> NodeSpec/Props -> Renderer`

约束：

1. 不把主题直接变成通用 `Modifier`。
2. 不在 renderer 中写业务语义默认值。
3. 不在 DSL 层散落重复主题推导逻辑。

## 4. 局部覆盖（Override）规则

局部覆盖能力保留，但必须是稀疏覆盖：

1. 只覆盖必要字段
2. 未覆盖字段回落到上层主题或默认值
3. 覆盖逻辑通过 `LocalContext` 作用域传播

适用场景：

1. 局部品牌色/强调色
2. 局部文本样式调整
3. 单区域对比度或可读性增强

非目标：

1. 把 override 做成“每个组件所有字段都能填”的全量配置
2. 用 override 替代组件参数

## 5. Android Bridge 边界

Android 主题桥接只做“平台语义到框架语义”的映射：

1. 读取系统/主题关键颜色与文本尺寸
2. 映射暗色模式与基础环境信息

不做：

1. 在 bridge 层写组件业务默认值
2. 在 bridge 层引入组件级条件分支

## 6. 与组件和 Modifier 的边界

1. 主题负责默认值来源
2. 组件参数负责语义表达
3. `Modifier` 负责通用外层修饰

对应规范：

- [MODIFIER.md](/Users/gzq/AndroidStudioProjects/UIFramework/MODIFIER.md)
- [NODE_PROPS.md](/Users/gzq/AndroidStudioProjects/UIFramework/NODE_PROPS.md)

## 7. 新增主题能力的必经清单

新增主题字段或覆盖能力时，至少完成：

1. 模型归属判断：`tokens` / `defaults` / 组件参数
2. 优先级规则定义：默认值与显式参数冲突时谁生效
3. renderer 验证：样式变化可触发预期 patch/rebind
4. demo 验证：Light/Dark + 局部覆盖场景
5. 测试补齐：单测或 instrumentation 至少覆盖一种回归路径

## 8. 当前阶段重点

1. 保持主题模型稳定，不回退到“组件全量 token 预计算”。
2. 优先补桥接细节一致性（如动态色/形状映射）。
3. 与 `ROADMAP` 中 overlay、input、容器场景联动完善主题回归。

路线图见：

- [ROADMAP.md](/Users/gzq/AndroidStudioProjects/UIFramework/ROADMAP.md)
