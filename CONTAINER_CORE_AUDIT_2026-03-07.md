# Container Core Audit（2026-03-07）

## 1. Scope

- 审计目录：`ui-renderer/src/main/java/com/gzq/uiframework/renderer/view/container`
- 关联关键链路：
  - `ui-renderer/src/main/java/com/gzq/uiframework/renderer/view/tree/pipeline/ViewTreePatchPipeline.kt`
  - `ui-renderer/src/main/java/com/gzq/uiframework/renderer/view/tree/pipeline/ViewTreeDisposer.kt`
  - `ui-renderer/src/main/java/com/gzq/uiframework/renderer/view/lazy/*`

## 2. Audit Dimensions

1. 性能（高频布局、绑定、容器刷新路径）
2. 冗余嵌套（不必要层级与查找）
3. 代码质量（边界收口、可维护性）
4. 缺陷风险（越界、泄漏、状态错配）

## 3. Findings & Closure

### F-01（已修复）TabRow 指示器每帧轮询

- 问题：`TabRow` 通过 `postOnAnimation` 持续轮询 `PagerState`，静态页面也会持续消耗主线程。
- 影响：无交互时仍有无效帧级开销。
- 修复：改为 `PagerState` 快照监听（事件驱动），`TabRow` 按状态变化更新指示器。
- 提交：`3402094`。

### F-02（已修复）TabRow 选中索引越界与重复滚动

- 问题：
  - `selectedIndex` 未做边界收口，异常输入可能导致指示器状态残留。
  - 高频 bind 下每次 `smoothScrollTo`，存在无效动画与滚动抖动风险。
- 修复：
  - `selectedIndex` 统一 clamp。
  - 仅在选中变化或 tab 结构变化时滚动。
  - 无有效 tab 时清空 indicator，避免旧态残留。
- 提交：`062c128`。

### F-03（已修复）容器状态引用未收口（stale reference）

- 问题：
  - `HorizontalPager/VerticalPager` 切换 `pagerState` 时未解绑旧 `viewPager` 引用。
  - `LazyVerticalGrid` 切换 `state` 时未解绑旧 `recyclerView`。
  - `ViewTreeDisposer` 仅清理 `LazyColumnNodeProps.state`，未覆盖 `LazyRowNodeProps.state`。
- 风险：状态对象持有失效 View 引用，可能触发泄漏和错误滚动控制。
- 修复：统一在 rebind/dispose/树销毁路径解绑旧引用。
- 提交：`c6ac853`。

### F-04（已修复）NavigationBar 每次 bind 依赖 tag 查找

- 问题：`findViewWithTag` 在每次更新执行，带来不必要层级遍历与弱类型依赖。
- 修复：引入内部 `ItemViewRefs` 强类型引用缓存，移除 tag 查找路径。
- 提交：`49b9934`。

### F-05（已修复）LinearLayout 高频布局分配抖动

- 问题：`DeclarativeLinearLayout.onLayout` 使用链式 `map/filter/any/map`，高频场景会产生可避免临时对象。
- 修复：改为单次循环收集与规格构造，降低分配压力。
- 提交：`6bb0366`。

### F-06（已修复）SegmentedControl 重复 layoutParams/background churn

- 问题：
  - 每次 bind 无条件重设 child `layoutParams`（触发布局请求）。
  - 容器背景每次分配新 `GradientDrawable`。
- 修复：
  - 仅在 margin 变化时回写 `layoutParams`。
  - 复用容器背景 `GradientDrawable` 实例。
- 提交：`37ea7ea`。

### F-07（已修复）TabRow 尺寸变化后选中项中心漂移

- 问题：宿主尺寸变化（旋转/分屏）后，选中 tab 可能不再居中。
- 修复：`onSizeChanged` 时执行非动画居中校正。
- 提交：`5436393`。

### F-08（已修复）FlowRow/FlowColumn 缺少布局统计接入

- 问题：`FlowRow/FlowColumn` 未写入 `LayoutPassTracker`，容器级布局诊断存在盲区。
- 影响：复杂流式布局场景下，无法在 render 统计中定位 measure/layout 热点来源。
- 修复：在 `onMeasure/onLayout` 统一记录 `LayoutPassTracker.recordMeasure/recordLayout`。
- 提交：`ff2fb18`。

### F-09（已修复）NavigationBar/SegmentedControl 细粒度 patch 缺失

- 问题：两类容器在 patch 路径上仍使用整容器 bind，轻量状态变更也会全量刷新。
- 影响：高频切换场景下产生可避免的 View 更新与 drawable churn。
- 修复：
  - `NavigationBar`：引入“结构变更/样式变更/内容变更/选中态变更”分层策略，仅更新受影响 index。
  - `SegmentedControl`：引入“样式全量更新 + 选中态局部更新”路径，纯选中切换仅更新前后两个分段。
- 提交：`232a42c`。

### F-10（已修复）TabRow/NavigationBar/SegmentedControl 缺少专项回归断言

- 问题：容器专项回归测试此前偏重 Lazy/Pager，导航与分段选择容器缺少独立断言。
- 修复：
  - unit：补 `NodeBindingDiffer` 对 `NavigationBar/TabRow` 的 patch 断言。
  - instrumentation：补 `NavigationBar` 选中态摘要、`SegmentedControl` 状态摘要、`TabRow` 选中摘要 UI 回归。
- 提交：`8eaf43e`。

## 4. Redundant Nesting Review

- `ScrollableColumn/ScrollableRow` 的内层 `DeclarativeLinearLayout` 为必要结构（`ScrollView` 单子节点约束 + child host 分发），不属于可删除冗余。
- `NavigationBar` 的层级（item -> icon container -> indicator/icon/badge）具备明确视觉语义，当前不建议拍平。
- `TabRow` 的 `HorizontalScrollView + TabRowContainer` 为指示器绘制与滚动能力所需，结构合理。

## 5. Current Gaps (Not Closed Yet)

当前审计范围内未关闭项：无。

## 6. Validation

- 每条修复提交后均执行 `./gradlew qaQuick`，当前均通过。
- 本轮收口执行 `./gradlew qaFull`，`connectedDebugAndroidTest` 26/26 全绿。
