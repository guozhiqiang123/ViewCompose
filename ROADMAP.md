# UIFramework Unified Roadmap

## 1. 文档定位

本文档是统一路线图，合并以下历史分散文档的“仍有效”部分：

1. `WIDGET_ROADMAP.md`
2. `DEMO_ROADMAP.md`
3. `OVERLAY_COMPONENTS_ROADMAP.md`
4. `UI_TESTING.md`

目标：

1. 让路线图只有一个主入口
2. 避免多份 roadmap 状态漂移
3. 让 AI 上下文聚焦在当前有效计划，而不是历史阶段文档

性能专项仍保留独立深度文档见 [PERFORMANCE.md](/Users/gzq/AndroidStudioProjects/UIFramework/PERFORMANCE.md)。

## 2. 当前基线（2026-03）

### 2.1 框架层

1. `NodeSpec` 已覆盖第一方高频控件（`Spacer` 仍属于轻量例外）
2. `Modifier` 边界已收口到“通用修饰 + scoped parent-data”
3. `Overlay` 已分层为：
   - session-bound surface：`Dialog`、`Popup`、`ModalBottomSheet`
   - host-driven feedback：`Snackbar`、`Toast`
4. `Activity` 宿主接入已经提供 `setUiContent(...)`，并内部管理 `RenderSession` 生命周期
5. `system bars insets` 已转为组件侧 `Modifier.systemBarsInsetsPadding(...)`

### 2.2 Demo 与验证层

1. demo 已稳定在多 `Activity` 结构
2. 已实现章节具备统一 scenario 模板
3. instrumentation 已覆盖关键 smoke 回归路径，延迟 session 容器专项已覆盖 `LazyVerticalGrid`、`HorizontalPager`、`VerticalPager` 与 `ModalBottomSheet`
4. 基线更新（2026-03-07）：已完成 tag-first UI 测试迁移并补关键组件族 smoke，`:app:connectedDebugAndroidTest` 21/21 全绿，`qaQuick` 与 `qaFull` 均可通过。

## 2.3 里程碑进度快照（2026-03-07）

| Milestone | 状态 | 完成态字段（C/U/D/UI） | 说明 |
| --- | --- | --- | --- |
| A：Overlay 稳定性收口 | Completed | C:✅ U:✅ D:✅ UI:✅ | Overlay host 已统一 reconcile 模板，Dialog/Popup/ModalBottomSheet/反馈流均已回归 |
| B：Collections 与容器扩展 | In Progress | C:✅ U:✅ D:✅ UI:✅ | 已补 `LazyVerticalGrid/HorizontalPager/VerticalPager` 专项回归，并新增 rotate-order 可见刷新断言（`qaFull` 21/21）；下一步聚焦 sticky headers 与 list state 抽象 |
| C：Input 与表单态增强 | Next | C:✅ U:✅ D:✅ UI:⚠ | 已补 Input/Navigation smoke 基线；focus/IME/表单组合专项仍待系统化补齐 |
| D：Diagnostics + Performance 联动 | In Progress | C:✅ U:✅ D:✅ UI:✅ | 已补 `DiffUtil + payload` 与 `SkipSubtree/skippedSubtrees` 主路径，下一步聚焦可视化与发布态优化 |

## 3. 统一设计原则

1. 组件参数负责语义，`Modifier` 负责通用修饰，`Theme/Defaults` 负责默认值。
2. 平台实现不回流到 DSL 模块：Android 宿主实现进入 `ui-overlay-android` 或 bridge 层。
3. 新能力以“最小可验证步”推进：文档、实现、测试、demo 逐步落地并小步提交。
4. 路线图文档必须和实现同步更新，禁止“代码已变、roadmap 未收口”。

## 4. 能力状态矩阵

| 方向 | 当前状态 | 下一阶段重点 |
| --- | --- | --- |
| Foundations / Input / Layout / State | 已形成 v1 主能力 | 聚焦边界态、表单/焦点态与复杂组合场景 |
| Collections | `LazyColumn/LazyRow/LazyVerticalGrid` + 基础分页容器可用 | sticky headers、list state 抽象 |
| Overlay | Dialog/Popup/ModalBottomSheet/Snackbar/Toast 主链路已打通 | Popup 锚点定位增强、反馈队列策略收口 |
| Theming | 语义 token + override + defaults 路线稳定 | Android 动态色/shape 桥接与细节一致性 |
| Interop | `AndroidView` 可用 | 强化复杂原生 View 场景与主题/生命周期协同 |
| Diagnostics | 基础 render/layout 诊断已落地 | locals/render tree/patch 可视化与告警可读性 |
| UI Testing | 核心 instrumentation 路径已建立 | 扩展容器专项、overlay 宿主专项、主题断言覆盖 |
| Performance | 已有 benchmark 基线，且 `DiffUtil + payload + subtree skip` 主路径已落地 | 继续扩大 skip 覆盖、增强诊断指标、推进发布态优化 |

### 4.1 完成态字段定义（C/U/D/UI）

统一字段：

1. `C`（Compile）：编译门禁
2. `U`（Unit）：单元测试门禁
3. `D`（Demo）：demo 场景与验证说明
4. `UI`（Instrumentation）：设备 UI 回归门禁

状态值：

1. `✅` 已通过
2. `⚠` 部分通过或存在阻塞
3. `❌` 未通过

默认判定口径：

1. `C`：`qaQuick` 中编译任务通过
2. `U`：`qaQuick` 中 unit test 通过
3. `D`：对应能力已有 demo 页面和验证点说明
4. `UI`：`qaFull` 中 instrumentation 通过，或在 roadmap 登记豁免范围与补齐时间

## 5. 里程碑计划

### Milestone A：Overlay 稳定性收口

交付：

1. `Dialog`：位置、蒙层、dismiss 语义全量回归
2. `Popup`：对齐策略、锚点刷新、窗口切换稳定性
3. `Snackbar/Toast`：队列和重复触发策略文档化并测试覆盖
4. `ModalBottomSheet`：show/update/dismiss 行为与宿主生命周期回归

完成标准：

1. 单测覆盖 show/hide/update/dismiss 一致性
2. instrumentation 覆盖真实宿主中的可见性与交互（含 bottom sheet 路径）
3. `Activity` finish / 配置变化无泄漏

### Milestone B：Collections 与容器扩展

交付：

1. `LazyRow`、`LazyVerticalGrid`、`HorizontalPager/VerticalPager` 最小可用实现
2. 新增容器纳入 [SESSION_CONTAINER_CHECKLIST.md](/Users/gzq/AndroidStudioProjects/UIFramework/SESSION_CONTAINER_CHECKLIST.md)
3. 结构稳定 + 闭包变化刷新路径专项测试

完成标准：

1. 容器级空 diff 刷新能力可验证
2. keyed reorder 与本地状态保持稳定
3. demo 提供 stress 场景可人工验证

### Milestone C：Input 与表单态增强

交付：

1. focus/IME action 回调链增强
2. 表单校验与只读/错误态组合场景
3. 主题和状态切换下输入控件视觉一致性回归

完成标准：

1. 输入交互路径可预测且无跨控件串扰
2. 文案裁剪与控件高度问题可通过 UI 测试稳定复现和防回归

### Milestone D：Diagnostics + Performance 联动

交付：

1. render/patch/layout 指标可视化增强
2. benchmark 路线固定化并持续更新基线
3. 发布态优化项（baseline profile 等）推进

完成标准：

1. 性能回归具备可量化证据
2. 诊断面板能直观定位高频问题

## 6. 测试与 Demo 的统一门禁

每个“进入已实现状态”的能力必须补齐：

1. 单元测试
2. demo 场景（含验证点）
3. 必要的 demo UI 测试

新增下列能力时，必须补延迟 session 容器专项：

1. 基于 `RecyclerView/ViewPager2` 的复用型容器
2. 结构 diff 与可见内容刷新可能解耦的容器
3. overlay surface 的独立 session 容器

里程碑标记为 `Completed` 之前，必须满足：

1. `:ui-renderer:compileDebugKotlin` 与 `:app:compileDebugKotlin` 通过
2. `:app:connectedDebugAndroidTest` 全绿（或在 roadmap 中登记明确豁免范围与截止时间）

## 7. 非目标（当前阶段）

1. 不追求完整复刻 Compose Runtime/Compiler 模型
2. 不在 v1 阶段引入复杂全局 overlay 路由系统
3. 不为了文档“完整性”继续维护重复 roadmap 文件

## 8. 历史文档迁移映射

| 旧文档 | 当前去向 |
| --- | --- |
| `WIDGET_ROADMAP.md` | 本文档 + 归档保留 |
| `DEMO_ROADMAP.md` | 本文档 + 归档保留 |
| `OVERLAY_COMPONENTS_ROADMAP.md` | 本文档 + 归档保留 |
| `UI_TESTING.md` | 本文档 + 归档保留 |

归档目录见 [docs/archive/README.md](/Users/gzq/AndroidStudioProjects/UIFramework/docs/archive/README.md)。
