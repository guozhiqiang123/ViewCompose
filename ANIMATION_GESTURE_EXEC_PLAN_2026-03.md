# ViewCompose 动画 + 手势框架升级执行计划（2026-03）

## 1. 基线

1. 当前存在 `AnimationActivity`、`GesturesActivity` 占位页，尚未落地正式 API。
2. `RenderSession` 已具备 `Choreographer` 帧对齐调度能力，可复用为动画时钟基础设施。
3. `runtime` 已具备 `Snapshot` + `ComposerLite`，可承载状态驱动动画。
4. 当前手势仅有 `clickable` 等离散交互，无统一 pointer/drag/swipe/transform 体系。

## 2. 本轮范围

1. 新增模块：`:viewcompose-animation`、`:viewcompose-gesture`。
2. 新增动画 API：`AnimationSpec`、`Animatable`、`animate*AsState`、`Transition`、`AnimatedVisibility`、`AnimatedContent`、`Crossfade`、`animateContentSize`。
3. 新增手势 API：`pointerInput`、`combinedClickable`、`draggable`、`swipeable`、`transformable`。
4. 新增 `graphicsLayer` modifier，并接入 renderer。
5. Android 高阶动画通过 host interop 接入（TransitionManager/MotionLayout/Animator）。

## 3. 约束

1. `runtime` 继续保持纯 Kotlin/JVM，不引入 Android 依赖。
2. 新能力默认 opt-in，不破坏现有 `clickable` 与容器行为。
3. 新增代码必须遵循单包根与模块边界守卫。
4. 每步提交前执行 `./gradlew qaQuick`。
5. 里程碑步骤（Step 6、Step 10、收口）执行 `qaFull`（设备可用时）。

## 4. 执行步骤

- [x] Step 1：落地执行计划文档并提交
- [x] Step 2：新增 animation/gesture 模块并接入构建守卫
- [x] Step 3：统一 MonotonicFrameClock（runtime + core local + host 注入）
- [x] Step 4：`ui-contract` 新增 `graphicsLayer` modifier
- [ ] Step 5：renderer 接入 `graphicsLayer`
- [ ] Step 6：animation 模块落地 Compose-like 动画 API
- [ ] Step 7：host-android 增加 AndroidAnimationInterop
- [ ] Step 8：容器 motion 策略（lazy/pager）
- [ ] Step 9：ui-contract + gesture 模块落地 pointer/gesture API
- [ ] Step 10：renderer 手势分发内核
- [ ] Step 11：demo/preview/snapshot 覆盖
- [ ] Step 12：文档收口与归档

## 5. 提交记录

1. `0ccf905` docs: add animation and gesture execution plan (compose-like + android-view aware)
2. `ef15848` build: add animation and gesture modules with package-root guards
3. `14baee1` feat: add host-provided monotonic frame clock for composition animation runtime
4. （待补充）

## 6. 阻塞记录

阻塞文件：`ANIMATION_GESTURE_BLOCKER_CONTEXT_2026-03.md`

当前无阻塞。
