# Delayed Session Container Checklist

## 1. 文档定位

本文档用于追踪“延迟创建 + 复用 holder/session”的容器风险。

这类容器的共同特点是：

1. 内容不是立即挂在父节点下
2. 内部存在 holder / session 复用
3. 结构 diff 和内容闭包更新可能解耦

因此它们比普通 `VNode` 容器更容易出现“结构没变，但可见内容没刷新”的 bug。

## 2. 当前已知容器

### 已落地

1. `LazyColumn`
2. `TabPager`

### 后续候选

1. `LazyRow`
2. `LazyGrid`
3. `Pager / Carousel`
4. keep-alive page host
5. 任何基于 `RecyclerView` / `ViewPager2` 的复用型容器

## 3. 架构规则

这类容器必须满足：

1. diff 为空时，不能回退到旧 item/page 实例
2. 已绑定 holder/session 必须有“无结构变化刷新”路径
3. `localSnapshot`、主题、环境、父层闭包都要在 update 路径重新注入
4. 创建路径和更新路径都必须能驱动 `RenderSession.render()`
5. `dispose/recycle` 语义必须和 holder 生命周期对齐

## 4. 必测场景

每个延迟 session 容器至少覆盖下面 6 类场景。

### 4.1 结构稳定，闭包变化

1. `key` 不变
2. `contentToken` 不变或 diff 为空
3. 外层状态变化后，可见内容立即更新

这是最高优先级场景，也是最容易藏 bug 的路径。

### 4.2 结构稳定，局部上下文变化

1. `key` 不变
2. 主题、`ContentColor`、环境 local 变化
3. 可见内容颜色/文案/样式立即更新

### 4.3 内容 token 变化

1. `key` 不变
2. `contentToken` 变化
3. session 复用或受控重建语义符合预期

### 4.4 keyed reorder

1. 重排后已存在项状态不串位
2. 当前可见项内容顺序正确
3. 本地 `remember` 状态按 `key` 保留

### 4.5 detach / attach / recycle

1. holder detach 后重新 attach
2. recycle 后重绑
3. session 不泄漏，不丢必要状态

### 4.6 空 diff 刷新

1. diff 结果为空
2. 但 page/item 实例已经更新
3. 当前可见 holder 仍会被刷新

## 5. 当前测试映射

### `LazyColumn`

单元测试：

1. [LazyListDiffTest.kt](/Users/gzq/AndroidStudioProjects/UIFramework/ui-renderer/src/test/java/com/gzq/uiframework/renderer/reconcile/LazyListDiffTest.kt)
2. [LazyItemSessionControllerTest.kt](/Users/gzq/AndroidStudioProjects/UIFramework/ui-renderer/src/test/java/com/gzq/uiframework/renderer/view/LazyItemSessionControllerTest.kt)

UI 测试：

1. [DemoVisualUiTest.kt](/Users/gzq/AndroidStudioProjects/UIFramework/app/src/androidTest/java/com/gzq/uiframework/DemoVisualUiTest.kt)
   - `collectionsStress_toggleUpdatesVisibleControls`

### `TabPager`

单元测试：

1. [TabPagerDiffTest.kt](/Users/gzq/AndroidStudioProjects/UIFramework/ui-renderer/src/test/java/com/gzq/uiframework/renderer/reconcile/TabPagerDiffTest.kt)

UI 测试：

1. [DemoVisualUiTest.kt](/Users/gzq/AndroidStudioProjects/UIFramework/app/src/androidTest/java/com/gzq/uiframework/DemoVisualUiTest.kt)
   - `statePatchStress_refreshesStableTabPagerPageContent`

## 6. 新容器接入流程

只要新增了延迟 session 容器，就必须同步完成：

1. 架构登记：在 [ARCHITECTURE.md](/Users/gzq/AndroidStudioProjects/UIFramework/ARCHITECTURE.md) 记录该容器属于延迟 session 容器
2. 测试登记：把容器加入本清单
3. 单元测试：覆盖 `diff empty but closure changed`
4. instrumentation：补真实 Activity 内的稳定交互回归
5. diagnostics：确认该容器出现问题时能被现有 render/layout 诊断看见

## 7. 当前结论

延迟 session 容器已经被证明是当前框架最容易藏“结构稳定但内容过期”问题的地方。

因此，后续排查同类 bug 时，优先级顺序应固定为：

1. 先看是否属于延迟 session 容器
2. 再看 diff 是否保留了最新实例
3. 再看已绑定 holder 是否在空 diff 路径下刷新
4. 最后才看 demo 页面本身的写法
