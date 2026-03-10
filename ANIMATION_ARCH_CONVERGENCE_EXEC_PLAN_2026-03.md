# Animation 架构级重构一次性收口执行计划（2026-03）

## 基线
- 当前 `:viewcompose-animation` 同时承载“动画内核 + DSL 集成”，模块职责偏重。
- `TweenSpec.delayMillis` 语义未对齐（delay 阶段仍参与插值）。
- `updateTransition` 为薄包装，缺少共享时钟与统一状态机语义。
- `Modifier.animateContentSize(...)` 仍是 no-op，未参与真实布局尺寸过渡。
- `Animatable.animateTo` 仍要求调用侧显式传 `frameClock`，易用性不足。
- `InfiniteTransition` 仅提供 `animateFloat`，类型覆盖不足。

## 本轮范围与完成标准
- 范围固定：animation 三层边界收口（core / DSL / Android interop）+ 语义硬切 + 测试矩阵补齐 + 文档收口归档。
- 完成标准：
  - 动画内核迁入 `:viewcompose-animation-core`（纯 Kotlin/JVM）。
  - `tween(delayMillis)` 修正为 delay 阶段恒定 `startValue`。
  - `updateTransition` 升级为共享 transition 时钟语义。
  - `animateContentSize` 落地为布局尺寸动画（非视觉缩放）。
  - `Animatable` 移除显式 `frameClock` 入参，并新增 `rememberAnimatable`。
  - `InfiniteTransition` 覆盖 `Float/Int/Color/Dp/Value`。
  - `qaQuick` 每步通过；里程碑与最终收口通过 `qaFull`（设备可用）。

## Checklist
- [x] Step 1: 新增执行文档并提交。
- [x] Step 2: 新增 `:viewcompose-animation-core` 并迁移动画内核。
- [x] Step 3: 修复 `TweenSpec.delayMillis` 与动画播放/中断模型。
- [x] Step 4: `updateTransition` / `AnimatedVisibility` 统一 transition 状态机语义。
- [x] Step 5: `animateContentSize` 落地（ui-contract + renderer 包装宿主）。
- [x] Step 6: `Animatable` 易用性重构 + `rememberAnimatable` + `InfiniteTransition` typed API。
- [x] Step 7: Android interop 补齐（ViewPropertyAnimator + DynamicAnimation）。
- [x] Step 8: 测试体系补强（core/unit/instrumentation）与门禁跑通。
- [ ] Step 9: 文档收口与归档。

## 提交记录
1. `docs: add animation architecture convergence execution plan`
2. `build: split animation core into standalone kotlin-jvm module`
3. `refactor: fix tween delay semantics and standardize animation run results`
4. `refactor: hard-cut transition and animated visibility onto shared transition timeline`
5. `feat: implement layout-participating animateContentSize host wrapping`
6. `feat: simplify animatable usage and add typed infinite transition apis`
7. `feat: extend android animation interop with property and dynamic animations`

## 阻塞记录
- 暂无。
