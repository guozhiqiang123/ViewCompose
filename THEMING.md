# ViewCompose Theming

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
5. `overlays`

关键原则：

1. 顶层主题只承载语义 token，不承载每个组件的完整 resolved style。
2. 组件默认值在 `Defaults` 层按需从 `Theme` 派生，不做全量预计算。
3. 组件显式参数优先级高于主题默认值。

当前 token 语义补充：

1. `colors` 同时承载基础色、`on*` 前景色、`*Container` 容器色、轮廓色、逆表面色与 ripple。
2. `typography` 同时保留旧 `title/body/label` 和 tiered `title*/body*/label*`。
3. `shapes` 当前保留 `cardCornerRadius` 兼容入口，并以语义化 `small / medium / large` 三级圆角为主入口。
4. `controls` 仍是框架自有尺寸 token，不承诺与 Android 原主题系统一一对齐。
5. `overlays` 当前由语义 token 承载跨组件蒙层配置。

## 2.3 Token 使用闭环

公开 token 不允许长期停留在“已定义但无消费”的状态。当前规则固定为二选一：

1. 至少被一个 core defaults/composite 默认值明确消费。
2. 被列入 whitelist，并在文档中说明原因。

当前 whitelist 分两类：

1. `reserved semantic palette`
   - `success`
   - `warning`
   - `info`
   - `surfaceTint`
   说明：本轮不强绑到现有核心组件，避免为了提高使用率污染语义。
2. `compatibility aliases`
   - colors: `textPrimary`、`textSecondary`、`divider`
   - typography: `title`、`body`、`label`
   - shapes: `cardCornerRadius`
   说明：这些字段保留兼容职责，不再作为内部 defaults 首选入口。

为防止回流，仓库有 `ThemeTokenUsageAuditTest` 守卫：

1. 新增 token 时，若未消费也未加入 whitelist，测试必须直接失败。
2. defaults 若从语义 token 回退到旧 alias，也会在审计时暴露。

## 2.1 兼容迁移策略

主题 token 扩展默认采用“先兼容后移除”：

1. 新语义字段先引入并成为推荐入口。
2. 旧字段进入 `@Deprecated` 过渡期，保持行为不变。
3. 文档必须写明迁移路径与预期移除窗口。
4. 新增默认值逻辑优先走新语义字段，旧字段只保留兼容别名职责。

## 2.2 硬编码禁用清单

以下语义色禁止在 `Defaults` 里直接写字面量，必须走 `Theme.colors`：

1. 错误态（如 `0xFFB3261E`）统一使用 `Theme.colors.error`。
2. 徽标/提醒色统一使用 `Theme.colors.error` 或其他语义色，不允许组件私有常量重复声明。
3. 语义色文本前景统一通过 `contentColorFor(semanticColor)` 推导，不手写黑白常量。

## 3. 默认值链路

标准链路必须保持：

`Theme -> Defaults -> NodeSpec -> Renderer`

约束：

1. 不把主题直接变成通用 `Modifier`。
2. 不在 renderer 中写业务语义默认值。
3. 不在 DSL 层散落重复主题推导逻辑。
4. 复合组件内部文本必须把完整文本样式写入 `NodeSpec`，不能只下发 `textSizeSp`。
5. renderer 只负责应用 `NodeSpec` 中已经解析好的文本样式，不重新发明主题语义。

## 4. 局部覆盖（Override）规则

局部覆盖能力保留，但必须是稀疏覆盖：

1. 只覆盖必要字段
2. 未覆盖字段回落到上层主题或默认值
3. 覆盖逻辑通过 `LocalContext` 作用域传播
4. 对外统一通过 `UiLocal/uiLocalOf/ProvideLocal(s)/UiLocals.current` 使用，避免专用包装 API 漂移

适用场景：

1. 局部品牌色/强调色
2. 局部文本样式调整
3. 单区域对比度或可读性增强

非目标：

1. 把 override 做成“每个组件所有字段都能填”的全量配置
2. 用 override 替代组件参数

## 4.1 业务自定义 Local 扩展

当业务侧 token 体系与框架默认主题不一致时，允许按下面方式扩展：

1. 在业务模块通过 `uiLocalOf { ... }` 定义自有 token。
2. 在局部子树通过 `ProvideLocal(...)` 或 `ProvideLocals(...)` 注入。
3. 在组件内部通过 `UiLocals.current(...)` 读取。
4. 新增 Local 能力时优先复用统一 API，不再新增专用 `ProvideXxx` 包装。

边界约束：

1. 业务 Local 只承载语义值，不承载 renderer 平台实现细节。
2. Local 作用域恢复与 snapshot 传播语义必须保持（lazy/overlay 不回退）。

## 5. Android Bridge 边界

Android 主题桥接只做“平台语义到框架语义”的映射，内部固定为：

`AndroidThemeSnapshotReader -> ThemeTokenMapper -> UiThemeTokens`

其中：

1. `SnapshotReader` 负责批量读取 Android / AppCompat / Material 主题字段。
2. `ThemeTokenMapper` 负责把平台字段映射到框架 token，并处理 fallback。
3. bridge 不直接产出组件级默认值，不绕过 `Defaults` 层。

当前 bridge 覆盖矩阵：

1. `colors`
   - 已桥接：`background / surface / surfaceVariant / primary / secondary / error`
   - 已桥接：`onPrimary / onSecondary / onError`
   - 已桥接：`primaryContainer / secondaryContainer / errorContainer`
   - 已桥接：`onPrimaryContainer / onSecondaryContainer / onErrorContainer`
   - 已桥接：`outline / outlineVariant / inverseSurface / inverseOnSurface`
   - 已桥接：`textPrimary / textSecondary`
   - 已桥接：`ripple`（优先读 `colorControlHighlight`）
   - best-effort：`surfaceTint` 当前优先对接 `colorAccent` 语义来源
2. `typography`
   - 已桥接：Material 3 `textAppearanceTitle*/Body*/Label*`
   - fallback：旧 Android `textAppearanceLarge/Medium/Small`
   - 已桥接字段：`fontSizeSp / fontWeight / fontFamily / letterSpacingEm / lineHeightSp / includeFontPadding`
3. `shapes`
   - 已桥接：`shapeAppearanceSmallComponent / Medium / Large`
   - 当前仅桥接“统一圆角”语义；若四角不一致，则安全回退 framework defaults
4. `overlays`
   - 已桥接：`android:backgroundDimAmount -> scrimOpacity`
5. `controls`
   - 当前不做主题级强桥接，继续走 framework defaults
   - 原因：Android 原主题系统没有与 `compact / medium / large` 一一对应的统一来源

不做：

1. 在 bridge 层写组件业务默认值
2. 在 bridge 层引入组件级条件分支
3. 为了“看起来全覆盖”而猜测性映射非统一 shape 或控件尺寸

实现约束：

1. bridge 的 fallback 必须显式落到 `UiThemeDefaults.light/dark()`，禁止散落字面量。
2. 新增桥接字段时，必须同时定义“读取来源 + fallback 规则 + token 归属”。
3. bridge 新能力若改变可视结果，必须补 `AndroidThemeBridgeTest` 或 Android 侧桥接测试。

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

当前主题语义的权威 demo 验证入口为 `Diagnostics -> 主题诊断`。`Foundations` 中的 theme/overrides/typography 页面继续保留为教学与示例入口，不承担最终人工回归口径。

## 8. 当前阶段重点

1. 保持主题模型稳定，不回退到“组件全量 token 预计算”。
2. 优先补桥接细节一致性（如动态色/形状映射）与 token 语义补齐。
3. 与 `ROADMAP` 中 overlay、input、容器场景联动完善主题回归。

路线图见：

- [ROADMAP.md](/Users/gzq/AndroidStudioProjects/UIFramework/ROADMAP.md)
