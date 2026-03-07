# Focus Follow Re-Audit（2026-03）

## 1. 审计范围

基线提交：`bf0760d88e9e80bc1291e07043caa71bd75c3ace`  
审计区间：`bf0760d88e9e80bc1291e07043caa71bd75c3ace..HEAD`

本次重点审查：

1. 输入框随软键盘上滚链路是否存在冗余/耦合设计  
2. 改动后是否更利于扩大容器兼容范围  
3. `ViewNodeFactory` 当前容器支持中，哪些应纳入 `focusFollowKeyboard`

## 2. 主要问题与已落地优化

### 2.1 焦点跟随策略与 LayoutManager 强耦合（已修复）

问题：

1. `focusAutoScrollEnabled` 绑定在 `LazyLinearLayoutManager/LazyGridLayoutManager`，策略状态分散  
2. 容器重建与策略切换的生命周期关系不够清晰

处理：

1. 改为容器视图级状态（`R.id.ui_framework_focus_follow_enabled`）  
2. `LazyColumnAdapter` 锚点恢复改为读取容器策略，不再依赖 LM 字段  
3. LayoutManager 回归“仅负责布局”的职责

对应提交：`11168e8`

### 2.2 viewport 计算逻辑重复（已修复）

问题：

1. RecyclerView 方案与后续扩展容器可能重复维护同一套 IME 可视区计算  
2. 容易出现修一处漏一处

处理：

1. 提取 `FocusFollowViewportResolver` 统一处理 `globalVisibleRect + windowVisibleFrame + IME inset`  
2. `LazyFocusFollowLayoutMonitor` 复用该解析器  
3. 新增 `ScrollableFocusFollowLayoutMonitor` 复用同一解析器

对应提交：`38273ff`

### 2.3 API 语义偏“lazy 实现细节”（已修复）

问题：

1. `Modifier.lazyContainerFocusFollowKeyboard` 暴露实现倾向  
2. 不利于扩展到非 lazy 的可滚动容器

处理：

1. 新增语义化 API：`Modifier.focusFollowKeyboard(enabled)`  
2. 旧 API 保留兼容并标记废弃  
3. demo 切换到新 API

对应提交：`13a18db`

### 2.4 容器覆盖面缺口（已部分补齐）

问题：

1. 原能力主要覆盖 Lazy 列表  
2. `VerticalPager/ScrollableColumn` 未纳入统一策略

处理：

1. `VerticalPager` 增加焦点跟随开关并接入  
2. `ScrollableColumn` 新增监视器并接入

对应提交：`13a18db`、`38273ff`

### 2.5 水平容器误用缺少诊断信号（已修复）

问题：

1. `LazyRow/HorizontalPager/ScrollableRow` 上误配 `focusFollowKeyboard(true)` 时静默忽略  
2. 排查现场只能依赖代码阅读，缺少运行期提示

处理：

1. 在 `ViewModifierApplier` 对水平容器增加一次性 warning 输出  
2. 新增 `ui_framework_focus_follow_warning_emitted` 视图 tag，保证每个实例只告警一次  
3. 语义保持不变：水平容器仍不执行键盘上顶，仅补齐可诊断性

对应提交：`feat: add one-shot warning for unsupported horizontal focus follow`

## 3. ViewNodeFactory 容器扩展矩阵（当前）

| 容器 NodeType | 当前状态 | 说明 |
|---|---|---|
| `LazyColumn` | ✅ 支持 | 软键盘跟随主路径 |
| `LazyVerticalGrid` | ✅ 支持 | 与 LazyColumn 一致 |
| `VerticalPager` | ✅ 支持 | 通过内部 RecyclerView 接入 |
| `ScrollableColumn` | ✅ 支持 | 通过 ScrollView 监视器接入 |
| `LazyRow` | ⛔ 不执行 | 水平语义，不做键盘上顶 |
| `HorizontalPager` | ⛔ 不执行 | 水平语义，不做键盘上顶 |
| `ScrollableRow` | ⛔ 不执行 | 水平语义，不做键盘上顶 |
| `PullToRefresh` | ➖ 依赖子容器 | 容器本身不处理，交给内部可滚动子树 |

## 4. 仍可继续优化项

1. 为 `VerticalPager` 与 `ScrollableColumn` 增加独立 instrumentation 回归用例  
2. `PullToRefresh` 场景补充“子容器 focus-follow 行为”专项回归
