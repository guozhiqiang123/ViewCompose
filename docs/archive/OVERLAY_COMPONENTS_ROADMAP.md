# Overlay Components Roadmap

## 1. 文档定位

本文档定义 `ViewCompose` 中“外挂组件 / overlay 组件”的专项规划。

这里的外挂组件指：

1. 不直接稳定挂在当前 `VNode` 父节点下
2. 显示位置、生命周期或宿主窗口关系不同于普通控件
3. 往往依赖 `Activity`、`Window`、锚点坐标、宿主容器或平台级 transient UI

本期范围聚焦：

1. `Dialog`
2. `Snackbar`
3. `Toast`
4. `PopupWindow`

后续同类能力，例如 `BottomSheet`、dropdown、tooltip，也必须先对齐本文档再实现。

## 2. 当前判断

这批能力不能再简单按“再加几个普通 widget”处理。

原因很明确：

1. `Dialog` 和 `PopupWindow` 带有独立 window / 宿主容器语义
2. `Snackbar` 和 `Toast` 更像宿主服务，不是普通节点树里的静态控件
3. 它们的显示/消失往往依赖外层生命周期，而不是单纯依赖 View 树是否存在
4. 其中一部分会脱离当前根容器显示，因此不能只靠 `ViewTreeRenderer` 的普通挂载路径解决

因此，正确方向不是“把所有 overlay 都塞进 `NodeType`”，而是先分层。

## 3. 能力分层

### 3.1 Session-bound overlay surface

这类能力需要挂在当前 `RenderSession` 所属宿主上，但显示位置不在普通子树里。

本期包含：

1. `Dialog`
2. `PopupWindow`

共同特征：

1. 需要 `visible -> show / hide` 生命周期
2. 需要和 `RenderSession` 绑定，session dispose 时必须自动清理
3. 可能带完整子内容树
4. 需要 `onDismissRequest`

### 3.2 Host-driven transient feedback

这类能力更接近宿主提供的短暂反馈服务。

本期包含：

1. `Snackbar`
2. `Toast`

共同特征：

1. 不是普通父子布局关系
2. 更像 effect / presenter 调度结果
3. 平台差异更大，尤其 `Toast`
4. UI 测试方式和普通控件不同

## 4. 架构目标

本期要达成 6 个目标：

1. overlay 显示逻辑保持声明式，状态变化能驱动 show/hide/update
2. session dispose、Activity finish、配置切换时不会泄漏 overlay
3. dismiss、action callback、外层状态同步路径可预测
4. demo 内能稳定人工测试，不依赖临时手工代码
5. 单元测试能覆盖 presenter/host 逻辑，instrumentation 能覆盖真实显示
6. 不把当前 `ViewTreeRenderer` 继续做成更大的“万能类”

## 5. 非目标

当前阶段不做下面这些事：

1. 不直接引入 `FragmentDialog` / `DialogFragment`
2. 不做完整 Compose 风格 `Scaffold/SnackbarHostState`
3. 不先做所有 popup 变体，例如 autocomplete dropdown、tooltip、menu
4. 不先做复杂 window 层级管理，例如多 overlay 栈、全局路由级 overlay
5. 不为了做 overlay 先大拆 runtime / renderer 模块

## 6. 推荐架构

### 6.1 引入 Overlay Host 层，而不是把平台 API 散落在 DSL 里

推荐新增一层宿主抽象：

1. `OverlayHost`
2. `OverlayPresenter`
3. `OverlayRequest` / `OverlaySpec`

职责建议：

1. DSL 层只描述“我想显示什么”
2. `RenderSession` 负责把最新 overlay 语义提交给 host
3. host/presenter 负责和 `Dialog`、`Snackbar`、`Toast`、`PopupWindow` 等平台对象交互

这层抽象的核心价值是：

1. 把平台生命周期和节点树生命周期隔离
2. 让单元测试可以不依赖真实平台弹层对象
3. 后续 `BottomSheet`、dropdown、tooltip 可以复用同一条路线

### 6.2 Dialog / PopupWindow 走“overlay surface”模型

推荐它们保留声明式 DSL，但底层不直接当普通 child view 挂到当前父节点。

建议模型：

1. `Dialog(...) { content }`
2. `Popup(...) { content }`

其中：

1. DSL 仍产生 overlay spec
2. content 需要独立 render surface
3. surface 内部仍使用 `RenderSession + ViewTreeRenderer`
4. 外层 session 更新时，overlay content 也要刷新

当前状态更新：

- `Dialog` / `PopupWindow` 已经按这条路线落地为 `OverlaySurfaceSession`
- 后续工作重点转到 surface 约束、定位和多 overlay 管理，而不是再回退到 presenter 直连 `ViewTreeRenderer`

这意味着它们虽然不是 `LazyColumn/TabPager` 那种延迟 session 容器，但同样属于：

> 需要单独设计刷新语义的宿主级容器。

### 6.3 Snackbar / Toast 走“host feedback request”模型

推荐不要把这两者做成带复杂子树的通用 node。

更合适的 v1 路线是：

1. `Snackbar`：声明式请求 + 宿主 presenter
2. `Toast`：声明式请求 + 宿主 presenter

v1 能力只覆盖：

1. 显示文案
2. duration
3. 可选 action（`Snackbar`）
4. dismiss/action 回调

不需要先把 `Snackbar` 做成完整 `Scaffold` 体系。

## 7. DSL 设计建议

### 7.1 Dialog

建议 v1 DSL：

```kotlin
Dialog(
    visible = state.value,
    onDismissRequest = { state.value = false },
    dismissOnBackPress = true,
    dismissOnClickOutside = true,
) {
    // regular ViewCompose content
}
```

v1 约束：

1. 单个 dialog content root
2. 宽高、背景、圆角先通过普通内容树和 surface token 组合完成
3. 不先做复杂对话框栈

### 7.2 Snackbar

建议 v1 DSL：

```kotlin
Snackbar(
    visible = state.value,
    message = "Saved",
    actionLabel = "Undo",
    onAction = { ... },
    onDismiss = { state.value = false },
    duration = SnackbarDuration.Short,
)
```

v1 约束：

1. 先做单条 snackbar
2. 后触发覆盖前触发，或采用简单串行队列，必须文档化
3. 宿主默认挂到 `DemoRenderActivity` 根布局底部

### 7.3 Toast

建议 v1 DSL：

```kotlin
Toast(
    visible = state.value,
    message = "Copied",
    onDismiss = { state.value = false },
    duration = ToastDuration.Short,
)
```

v1 约束：

1. 不自定义复杂 toast view
2. 先走系统 `Toast`
3. 真实 UI 测试以 presenter 注入和宿主 spy 为主，避免把 fragile 系统 toast 断言当主基线

### 7.4 PopupWindow

`PopupWindow` 必须晚于 `Dialog/Snackbar/Toast`。

原因：

1. 它需要 anchor 语义
2. 当前框架还没有正式的 anchor/overlay target 机制
3. 需要把节点测量结果安全地暴露给 overlay host

建议新增锚点能力后再支持：

```kotlin
PopupAnchor(anchorId = "more_menu")
Popup(
    visible = expanded.value,
    anchorId = "more_menu",
    onDismissRequest = { expanded.value = false },
) {
    ...
}
```

## 8. 宿主集成建议

### 8.1 RenderSession 侧

推荐给 `RenderSession` 增加 overlay commit 流程：

1. 正常构建主 `VNode` 树
2. 同时收集本次 render 的 overlay requests
3. 主树 render 成功后，再把 overlay requests 提交给 host
4. session dispose 时，host 同步清理当前 session 创建的 overlay

原因：

1. 避免 render 过程中直接触发 show/hide，产生时序混乱
2. 让 overlay 和主树更新共享同一次声明式输入

### 8.2 Activity / root 侧

当前 demo 的宿主基础已经够用：

1. `DemoRenderActivity` 通过 `ComponentActivity.setUiContent(...)` 统一创建根 `ViewGroup`
2. `renderInto(root)` 仍是底层统一入口

因此 v1 可以直接在 `DemoRenderActivity` 所在宿主上挂 overlay host，而不需要先引入 fragment 容器。

推荐路径：

1. root-level overlay host provider
2. `Dialog` 使用 `android.app.Dialog`
3. `Snackbar` 挂到 root container
4. `Toast` 使用 `Toast`
5. `PopupWindow` 挂到 anchor 对应 view

## 9. 分阶段实施

### Phase 0: 文档与宿主抽象

交付：

1. 本文档
2. overlay host 抽象
3. debug/测试注入点

完成标准：

1. 明确两类 overlay 模型
2. 明确 `RenderSession -> OverlayHost` 提交流程
3. 明确每种能力走哪条实现路线

### Phase 1: Toast + Snackbar

原因：

1. 内容模型简单
2. 能先打通宿主反馈型能力
3. 风险低于 dialog content session

交付：

1. `Snackbar` DSL
2. `Toast` DSL
3. host presenter
4. demo 页面
5. 单元测试
6. instrumentation

单测重点：

1. `visible true -> show`
2. `visible false -> dismiss`
3. 相同 request 更新语义
4. dismiss/action callback 只触发一次

UI 测试重点：

1. snackbar 文案可见
2. action 点击生效
3. 重复切换不会残留旧 snackbar
4. toast 至少有 presenter/spy 路径回归

### Phase 2: Dialog

这是第一批真正的 overlay content surface。

交付：

1. `Dialog` DSL
2. dialog content render surface
3. dismiss/back/outside click 语义
4. demo 页面
5. 单元测试
6. instrumentation

单测重点：

1. `visible` 切换生命周期
2. 更新同一个 dialog 时，content 刷新而不是重建错乱
3. `onDismissRequest` 不重复触发
4. session dispose 自动 dismiss

UI 测试重点：

1. dialog 显示/关闭
2. dialog 内部按钮和字段可交互
3. theme override 在 dialog content 内仍生效
4. 返回键 dismiss 生效

### Phase 3: PopupWindow

前提：

1. anchor 机制落地
2. dialog surface 已经稳定

交付：

1. anchor API
2. popup DSL
3. popup presenter
4. demo 页面
5. 单元测试
6. instrumentation

单测重点：

1. anchor 解析
2. anchor 位置更新
3. dismiss 语义
4. anchor 消失时自动 dismiss

UI 测试重点：

1. 点击锚点打开 popup
2. 内容位置正确
3. 外部点击关闭
4. 滚动/页面切换时不会悬空残留

## 10. 测试矩阵

### 10.1 单元测试

建议新增测试层：

1. overlay host reducer / request applier
2. dialog presenter
3. snackbar presenter
4. toast presenter
5. popup anchor resolver

固定覆盖点：

1. `false -> true`
2. `true -> false`
3. session dispose
4. callback once
5. 外层状态变化导致 request 内容更新

### 10.2 Instrumentation

推荐继续沿用当前 demo UI 测试体系：

1. `ActivityScenario`
2. View 树断言
3. `UiAutomator`
4. 截图辅助

新增规则：

1. overlay 测试不要在 `scenario.onActivity {}` 内触发 `UiAutomator` 滚动/输入
2. dialog/popup 先断言“显示存在”，再断言内容可见
3. toast 不把 fragile 平台动画时序作为唯一基线

### 10.3 Demo 覆盖

建议新增独立模块，而不是把这批能力塞回 `Foundations`。

推荐模块名：

1. `Feedback`
2. 或 `Overlay`

推荐页面：

1. `Transient`
   - Snackbar
   - Toast
2. `Dialog`
   - basic dialog
   - dismiss policy
   - local theme override
3. `Popup`
   - anchored popup
   - dismiss and reposition
4. `Verification`
   - viewcompose-benchmark anchor
   - checklist

## 11. Demo 与人工验证要求

每个能力至少提供：

1. 一个 viewcompose-benchmark anchor
2. 一个正常路径
3. 一个 stress path
4. 一段 verification notes

建议人工验证清单：

1. 重复开关 overlay，不残留旧实例
2. 切换 Light/Dark theme 后 overlay 样式正确
3. 外层状态变化能刷新已显示 overlay
4. 返回、外部点击、按钮动作的关闭路径一致
5. Activity finish / 页面跳转后无泄漏、无悬挂窗口

## 12. 关键风险

### 12.1 Dialog content 的刷新语义

这是最大风险点。

如果 dialog content 用独立 surface/session 渲染，就必须明确：

1. 外层状态变了，当前 dialog content 何时刷新
2. theme/local/environment 如何重新注入
3. session dispose 和 dialog dismiss 谁先执行

这本质上和延迟 session 容器是同类问题，只是宿主从 `RecyclerView/ViewPager2` 变成了 `Dialog/PopupWindow`。

### 12.2 Popup anchor 机制

这需要框架正式承认“节点测量结果可被 overlay 系统消费”。

如果这里设计草率，后续 dropdown/menu/tooltip 都会重复返工。

### 12.3 Toast 的可测试性

系统 toast 在 instrumentation 下往往不稳定。

因此 v1 必须允许 presenter 注入或宿主 spy，不能把真 toast 可见性断言当成唯一质量门。

## 13. 本期结论

本期最稳妥的路线是：

1. 先建立 overlay host 抽象
2. 先做 `Snackbar + Toast`
3. 再做 `Dialog`
4. 最后做 `PopupWindow`

原因不是“Dialog 不重要”，而是：

1. `Snackbar/Toast` 能先验证宿主反馈型能力
2. `Dialog` 会引入真正的 overlay content session
3. `PopupWindow` 又额外引入 anchor 坐标问题

如果顺序反过来，容易一开始就把生命周期、content refresh、anchor 三类问题搅在一起。
