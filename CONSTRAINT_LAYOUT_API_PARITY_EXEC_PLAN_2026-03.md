# ConstraintLayout API 补齐执行计划（P0+P1，2026-03）

## 基线
- 当前 `:viewcompose-widget-constraintlayout` 已支持 anchors/dimension/bias/baseline/guideline/barrier/chain/decoupled constraintSet。
- 与 `androidx.constraintlayout.widget.ConstraintLayout`（`2.2.1`）对比仍有关键缺口：
  - `P0`：match-constraint 进阶维度（min/max/percent/constrained）、chain weight。
  - `P1`：circle 约束、`baselineToTop`/`baselineToBottom`、`Barrier.allowsGoneWidgets=false` 实际生效。
- 现有 constraint 相关测试覆盖偏基础，缺少上述高级能力回归断言。

## 本轮范围与完成标准
- 范围固定：`P0 + P1`（不含 Flow/Group/Layer/Placeholder）。
- 完成标准：
  - `ConstraintItemSpec` 与 DSL 支持 min/max/percent/constrained、circle、baselineToTop/bottom。
  - `ConstraintChainSpec` 与 DSL 支持 weight。
  - renderer 真实应用上述约束，并让 `allowsGoneWidgets` 参数生效。
  - DSL 与 renderer 契约测试覆盖新增能力。
  - 每步 `qaQuick` 通过，最终补跑 `qaFull`（设备可用时）。

## Checklist
- [x] Step 1: 新增执行文档并提交。
- [x] Step 2: 扩展 ui-contract 约束数据模型（P0+P1 字段）。
- [x] Step 3: 扩展 widget DSL（constrain/constraintSet helper API）。
- [x] Step 4: renderer 约束应用引擎补齐（dimension/chain/circle/baseline/barrier）。
- [x] Step 5: 测试补强（DSL + spec reader + renderer 关键路径）。
- [ ] Step 6: 文档收口并归档执行计划。

## 提交记录
1. `docs: add constraint layout api parity execution plan`
2. `feat: extend constraintlayout contracts and dsl for advanced constraints`
3. `feat: complete advanced constraintlayout renderer constraint application`

## 阻塞记录
- `qaFull` 在 Pixel 4 XL 执行 `ComponentFamilySmokeUiTest#keyComponentFamilies_haveVisibleSmokeAnchors` 时长时间停留在 `0/n` 进度，手动停止后 instrumentation 报告 `Process crashed`；本轮先以 `qaQuick + 模块单测` 收口，待 smoke 用例稳定后补跑全量 `qaFull`。
