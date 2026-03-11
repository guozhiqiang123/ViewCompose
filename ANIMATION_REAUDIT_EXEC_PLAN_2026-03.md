# Animation 复扫问题收口执行计划（2026-03）

## 1. 基线

- 当前动画分层已完成：`viewcompose-animation-core`（纯 Kotlin/JVM）+ `viewcompose-animation`（DSL 集成）+ renderer/host 平台实现。
- 复扫发现 4 个高优先级问题：
  1. `Transition` 使用 `RepeatableSpec/InfiniteRepeatableSpec` 时长与采样语义不正确。
  2. `InfiniteRepeatableSpec + SnapSpec` 存在潜在忙等路径。
  3. `animateContentSize` 对 `AnimationSpec` 语义折损（easing/spring/keyframes/repeat 细节丢失）。
  4. `graphicsLayer.transformOrigin` 在尺寸变化后可能出现 pivot 过期。

## 2. 范围与约束

- 范围：动画模块与相关 renderer 绑定实现收口，不改业务调用入口签名。
- 策略：按问题优先级分步修复，先语义正确性，再一致性与回归覆盖。
- 纪律：每完成一步回写计划文档并提交；每步执行相关测试。

## 3. 完成标准

1. `Repeatable/Infinite` 在 `animationDurationNanos/sampleAnimationValue/runAnimation` 语义一致。
2. `Infinite + Snap` 路径不再出现无帧等待的紧循环。
3. `animateContentSize` 保留 tween easing、spring 参数、keyframes 关键帧、repeat 语义。
4. `graphicsLayer.transformOrigin` 在 view 尺寸变化后 pivot 可自动更新。
5. 动画核心与集成测试补齐并通过。

## 4. Checklist

- [x] Step 1 新增执行计划文档并首提
- [x] Step 2 修复 animation-core repeat/infinite 语义与 busy-loop 风险
- [x] Step 3 收口 animateContentSize spec 模型与 renderer 执行语义
- [x] Step 4 修复 graphicsLayer transformOrigin 的尺寸变化同步
- [x] Step 5 补齐测试并执行门禁回归
- [ ] Step 6 收口文档并归档执行计划

## 5. 提交记录

- `DONE` docs: add animation re-audit execution plan
- `DONE` fix: align repeatable/infinite animation-core sampling semantics and guard infinite-snap loop
- `DONE` fix: preserve animateContentSize easing/spring/keyframes/repeat semantics across animation and renderer
- `DONE` fix: keep graphicsLayer transformOrigin pivot in sync with host view size changes
- `DONE` test: run animation-core/animation/renderer unit suites and qaQuick

## 6. 阻塞记录

- 无
