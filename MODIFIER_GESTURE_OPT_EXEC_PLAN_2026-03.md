# ModifierGestureApplier 手势优化执行计划（2026-03）

## 基线
- 当前入口：`viewcompose-renderer/.../ModifierGestureDispatcher.kt`
- 已知问题：
  - `pointerInput` 消费未强制短路后续手势链路。
  - transform 采用“非 0 即派发”，缺少 slop 激活门槛。
  - swipe 判定仅固定距离阈值，未结合速度与 anchor span。
  - 手势跟踪状态与 `VelocityTracker` 生命周期仍有冗余与串扰风险。

## 本轮范围与完成标准
- 范围固定：`pointerInput/transform/drag/swipe` 仲裁与阈值模型收口 + 自动化补强 + 架构/流程文档同步。
- 完成标准：
  - `pointerInput == Consumed` 时强拦截后续手势。
  - transform 改为 Compose-like slop 激活 + 单次合并派发。
  - swipe 升级为 anchor 距离 + 速度联合 settle。
  - 单测与 UI 测试覆盖仲裁与阈值核心路径。
  - `qaQuick` 每步通过；Step 3、Step 4、最终收口执行 `qaFull`。

## Checklist
- [x] Step 1: 新增执行文档并提交。
- [x] Step 2: 分发仲裁硬切（pointerInput 强短路 + 流保持）。
- [x] Step 3: transform 阈值模型对齐 Compose 语义。
- [x] Step 4: swipe 判定升级为 anchor+velocity 联合。
- [x] Step 5: 清理 tracker 生命周期与日志策略。
- [ ] Step 6: 单测/Instrumentation 回归补强。
- [ ] Step 7: 文档收口并归档执行文档。

## 提交记录
1. `docs: add modifier gesture optimization execution plan`
2. `refactor: enforce pointer-input-first gesture arbitration with strong consumption`
3. `feat: add compose-like transform slop gating and merged delta dispatch`
4. `feat: upgrade swipe settle policy to anchor-distance and velocity combined thresholds`
5. `refactor: harden gesture tracker lifecycle and remove redundant motion bookkeeping`

## 阻塞记录
- 暂无。
