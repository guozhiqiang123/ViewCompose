# UIFramework Demo Roadmap

## 1. 文档定位

本文档定义 `UIFramework` 的 demo 规划。

目标不是再继续堆零散示例，而是把 demo 升级成两类资产：

1. 人工验收入口
2. 框架能力地图

后续所有 demo 相关开发，都应优先回答两个问题：

1. 这个页面是在验证哪一类框架能力？
2. 这个页面是否能暴露真实 bug，而不只是展示 happy path？

参考来源：

- [SmartToolFactory/Jetpack-Compose-Tutorials](https://github.com/SmartToolFactory/Jetpack-Compose-Tutorials)
- 该仓库 README 当前明确强调了 `Jetpack Compose Tutorials`、`Layouts`、`State`、`Widgets`、`Navigation`、`Gestures`、`Animation`、`Canvas` 等方向  
  参考页：<https://github.com/SmartToolFactory/Jetpack-Compose-Tutorials>

## 2. 当前问题

当前 demo 已经比最早版本清晰，但仍然有明显问题：

1. 页面仍按“当前已做能力”分组，而不是按“人工测试路径”分组。
2. 很多示例是能力堆叠，难以快速定位 bug 属于布局、状态、主题还是 renderer。
3. 当前 demo 更像 sample，而不是 testbed。
4. 当前章节数量偏少，无法承接后续 `P2` 能力扩展。
5. demo 与 Compose 的能力地图没有建立映射，后续优化方向不够直观。

## 3. 设计原则

后续 demo 必须遵守下面这些规则。

### 3.1 Demo 优先服务人工测试

每个页面至少要能覆盖：

- 正常态
- 交互态
- 动态更新
- 边界态
- 主题切换

### 3.2 页面分层必须稳定

建议层级固定为：

1. `Chapter`
2. `Page`
3. `Scenario`

解释：

- `Chapter` 对应一类框架能力，例如 `State & Effects`
- `Page` 对应该能力下的一组具体主题，例如 `remember / derivedStateOf`
- `Scenario` 对应一个可手动操作的验证片段

### 3.3 一页只验证一类核心问题

例如：

- 布局页重点验证测量、摆放、margin、weight、alignment
- 输入页重点验证 value、focus、IME、error、readOnly
- 动画页重点验证插值、过渡、状态切换

不要把多个不相关能力揉进同一页。

### 3.4 每个 Scenario 都要带“验证点”

每个示例块建议固定包含：

- `What`
- `How to verify`
- `Expected`

这样后续你人工回归时不需要再靠记忆判断。

### 3.5 Demo 结构要先于控件数量

后续即使继续新增控件，也不应该先加更多零散 section。

应先决定：

- 它属于哪个 `Chapter`
- 应挂在哪个 `Page`
- 它验证的是框架哪一层

## 4. 参考 Compose 教程的章节映射

参考 `SmartToolFactory/Jetpack-Compose-Tutorials` 的组织方式，建议把我们自己的 demo 主结构定成下面这 11 章。

### 4.1 Chapter 1: Foundations

对应 Compose 教程里的基础、控件、Material 组件入口。

建议页面：

- `Text & Typography`
- `Surface & Theming`
- `Image & Icon`
- `Button Family`
- `Progress & Feedback`

当前状态：

- 已有基础能力
- 需要重排 demo，而不是继续堆新 section

### 4.2 Chapter 2: State & Effects

对应 Compose 教程里的 `State`、`remember`、`side effects`。

建议页面：

- `mutableStateOf / remember`
- `derivedStateOf`
- `produceState`
- `SideEffect / DisposableEffect`
- `Key / local identity`

当前状态：

- 核心 runtime 已有
- demo 不够系统

### 4.3 Chapter 3: Layouts

对应 Compose 教程里的 `Layouts`、`Custom Layout`。

建议页面：

- `Row / Column`
- `Box / Surface`
- `Size / Weight / Fill`
- `Arrangement / Alignment`
- `Layout edge cases`

当前状态：

- 已有较多能力
- 还缺系统化的“布局压力测试页”

### 4.4 Chapter 4: Input

对应 Compose 教程中的输入控件、表单、Material 输入组件。

建议页面：

- `TextField family`
- `Selection controls`
- `ReadOnly / Error / Disabled`
- `Keyboard / IME`
- `Form scenarios`

当前状态：

- `P1` 语义已基本到位
- 还缺 focus、表单态、校验态的系统 demo

### 4.5 Chapter 5: Lists & Collections

对应 Compose 教程中的 lazy 列表、分页容器、列表状态。

建议页面：

- `LazyColumn basics`
- `Keyed reorder`
- `Item local state`
- `Item effects`
- `Pager / tab container`

当前状态：

- `LazyColumn` 可用
- `TabPager` 已实现但应视为实验能力
- 还缺 `LazyRow / LazyGrid / sticky headers / list state`

### 4.6 Chapter 6: Gestures

对应 Compose 教程中的 gesture / pointer input。

建议页面：

- `click / long click / double click`
- `drag`
- `swipe`
- `scroll conflict`
- `nested gesture`

当前状态：

- 目前基本没有体系化支持
- 这是和 Compose 的明显差距之一

### 4.7 Chapter 7: Animation

对应 Compose 教程里的 animation 系列。

建议页面：

- `state driven alpha/size`
- `visibility transitions`
- `content transitions`
- `list item transitions`
- `gesture + animation coupling`

当前状态：

- 目前没有正式动画系统
- 这是和 Compose 的明显差距之一

### 4.8 Chapter 8: Graphics & Canvas

对应 Compose 教程里的 `Canvas`、draw 系统。

建议页面：

- `basic shapes`
- `paths`
- `gradients`
- `custom badges`
- `chart primitives`

当前状态：

- 目前没有正式绘制层
- 这是和 Compose 的结构性差距之一

### 4.9 Chapter 9: Navigation

对应 Compose 教程里的 navigation 页面组织方式。

建议页面：

- `host integration`
- `screen state switching`
- `page stack model`
- `deep link simulation`

当前状态：

- 目前不做框架内 router
- 但 demo 仍需要为后续导航能力预留章节

### 4.10 Chapter 10: Interop

对应 Compose 的 `AndroidView`、宿主集成、现有 View 互操作。

建议页面：

- `AndroidView basics`
- `custom view interop`
- `Fragment host`
- `themed native views`

当前状态：

- `AndroidView` 已有
- 这是当前框架相对 Compose 的现实优势

### 4.11 Chapter 11: Diagnostics

这是 Compose 教程里没有完整独立成章，但对我们更重要。

建议页面：

- `render tree`
- `patch logs`
- `key warnings`
- `theme/local inspection`
- `performance counters`

当前状态：

- 只有基础 debug 日志
- 需要继续做成可人工使用的调试入口

## 5. 建议的 Demo App 壳结构

建议新的 demo 结构不要再只保留 5 个顶级 tab。

推荐改成两级导航：

1. 顶层 `Chapter` 导航
2. 章内 `Page` 导航

建议顶层章节：

1. `Foundations`
2. `State`
3. `Layouts`
4. `Input`
5. `Collections`
6. `Gestures`
7. `Animation`
8. `Graphics`
9. `Navigation`
10. `Interop`
11. `Diagnostics`

实现建议：

- 第一版可以继续沿用现有 `TabPager`
- 如果顶层页签过多，建议改成 `LazyColumn + segmented filter` 或左侧目录 + 内容区
- 章内页面仍可使用 `TabPager` 或 `SegmentedControl`

## 6. 每个页面的固定模板

后续每个 demo page 建议统一为下面结构：

### 6.1 页面头

- 页面标题
- 当前验证目标
- 关联框架模块

### 6.2 Core Scenarios

至少 2 到 5 个场景：

- 标准场景
- 边界场景
- 主题场景
- 状态更新场景
- 压力场景

### 6.3 Verification Notes

每页固定一段：

- 点击什么
- 观察什么
- 预期什么

### 6.4 Related Gaps

如果某页的能力还不完整，明确写：

- 当前缺什么
- 是否属于框架缺口
- 是否只是 demo 缺口

## 7. UIFramework 与 Compose 的当前差距

这部分是后续优化方向的关键。

### 7.1 已有基础能力

当前已具备，且可以进入 demo 正式章节的能力：

- 声明式 `VNode` 树
- keyed diff 与基本增量渲染
- `remember / derivedStateOf / produceState / SideEffect / DisposableEffect`
- `Box / Row / Column / Surface`
- `Text / Image / Icon / Button / IconButton`
- `TextField family`
- `Checkbox / Switch / RadioButton / Slider`
- `LinearProgressIndicator / CircularProgressIndicator`
- `LazyColumn`
- `AndroidView`
- 主题系统、局部主题 override、环境 local

### 7.2 比 Compose 明显缺失的方向

#### A. 交互与手势

缺失或很弱：

- long press
- double tap
- drag
- swipe
- nested scroll
- pointer input
- transform gesture

#### B. 动画系统

缺失：

- `animate*AsState`
- `updateTransition`
- `AnimatedVisibility`
- `AnimatedContent`
- 列表项进入/离开动画

#### C. 绘制系统

缺失：

- Canvas DSL
- Brush / gradient / path
- draw modifier 体系
- 自定义绘制节点

#### D. 导航与宿主状态

缺失：

- 框架内页面栈模型
- screen state 保存/恢复
- 导航级别生命周期

#### E. 输入与焦点系统

仍然偏弱：

- focus 管理
- keyboard action 回调链
- selection / cursor / composition 更细控制
- 表单状态抽象

#### F. 容器与集合

缺失：

- `LazyRow`
- `LazyGrid`
- sticky headers
- scroll container 语义层
- pager state 抽象

#### G. Scaffold 与高阶 Material 结构

缺失：

- Scaffold
- AppBar
- BottomBar
- Drawer
- Snackbar
- Dialog / BottomSheet

#### H. Tooling 与语义树

仍然偏弱：

- sematics/accessibility 抽象
- testing hooks
- 更直观的 inspector
- 性能采样面板

### 7.3 比 Compose 更适合先发挥的方向

当前框架并不需要和 Compose 完全正面复制。

更值得先放大的优势是：

- 原生 `View` 互操作成本低
- 现有业务控件迁移成本低
- 可渐进接入老项目
- 主题桥接到 Android Theme 更直接

所以 demo 中也应体现这条差异化路线，而不是只做“Compose 低配版展示”。

## 8. Demo 建设优先级

### Phase A: 重构壳结构

目标：

- 把 demo 从现有 5 个大 tab 重构成稳定章节结构

优先章节：

1. `Foundations`
2. `State`
3. `Layouts`
4. `Input`
5. `Collections`
6. `Interop`

说明：

- 这些章节直接基于当前能力即可落地

### Phase B: 把现有 sample 重写成 testbed

目标：

- 每页都具备明确验证点
- 每页都有边界场景
- 每页支持主题切换和动态状态操作

### Phase C: 为缺失能力预留空章节

目标：

- 即使 `Gestures / Animation / Graphics / Navigation` 还没实现，也先把章节和占位页建立起来

这样后续不会继续打散结构。

### Phase D: 章节驱动框架开发

后续每次新增框架能力时，必须同时做两件事：

1. 增加或更新单元测试
2. 把能力接入对应 demo chapter

## 9. 推荐的首批执行顺序

如果按这份文档继续推进，建议顺序如下：

1. 先把 demo 顶层结构按 11 个章节重构
2. 再把现有 `Overview / Layout / Input / State / Collection` 内容迁移到新章节
3. 然后补 `Diagnostics` 页面，作为人工回归入口
4. 最后为 `Gestures / Animation / Graphics / Navigation` 建立占位页和 gap 标注

## 10. 当前结论

结论很明确：

1. demo 现在必须从“sample 展示”升级到“能力验证平台”
2. 参考 Compose 教程仓库的章节组织方式是合理的
3. 但我们不能直接照抄 Compose 页面，而应加入 `Interop` 和 `Diagnostics` 这两个更适合当前框架的章节
4. `P1` 基础控件层已经闭环，下一阶段更适合转向 demo 体系化和能力差距驱动开发

后续只要继续做 demo，就默认按本文档推进。
