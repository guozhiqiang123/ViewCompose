# Graphics V2 收口执行计划（2026-03）

## Summary

本轮目标：将 `viewcompose-graphics-core + viewcompose-graphics + renderer` 从“可用 v1”收口为“语义正确 + 易扩展 + 与 Compose 更接近”的 v2 基线。

执行策略：

1. 硬切修复错误语义（不保留错误实现分支）
2. 先修正确性（P0），再做 API/架构收敛（P1），最后补能力完整度（P2）
3. 每步一提交；每步回写文档；每步 `qaQuick`（必要时加 `qaPreview`）

## Baseline Gaps

1. `DrawRoundRect` 未正确使用四角半径（仅使用 `topLeft`）
2. `DrawImage` 的 `Drawable` 分支未应用 `DrawPaint`（alpha/blend/colorFilter/imageFilter）
3. `ImageFilterModel.Chain` 已暴露但执行层未生效
4. `drawWithCache` 仅手写 key 模型，缺少更 Compose-like 的依赖失效模型
5. draw modifier API 在 `ui-contract` 与 `viewcompose-graphics` 存在重复出口

## Scope

### P0（本轮必须完成）

1. 修复 RoundRect 四角半径语义
2. 修复 Drawable 绘制分支的 DrawPaint 生效路径
3. 让 `ImageFilterModel.Chain` 在执行层真实生效（高版本完整，低版本合理降级）
4. 补至少一组针对 P0 的自动化回归测试（纯逻辑可测优先）

### P1（本轮可完成项）

1. 梳理 `drawWithCache` 依赖失效语义（先定义契约并补文档）
2. 明确 draw API 单一出口策略（避免 contract 与 graphics 模块重复入口漂移）

### P2（后续迭代）

1. 增补 DrawScope 便捷 API（`withTransform`/`inset`/`drawIntoCanvas`）
2. 增补图形能力（PathEffect/更完整 text/image 控制）与更强像素级回归

## Checklist

- [x] Step 1: 落地 v2 执行文档
- [ ] Step 2: 修复 RoundRect 四角半径语义 + 回归
- [ ] Step 3: 修复 Drawable 分支 DrawPaint 生效 + 回归
- [ ] Step 4: 修复 ImageFilter Chain 生效路径 + 回归
- [ ] Step 5: 文档同步（ARCHITECTURE/ROADMAP/PERFORMANCE/MODIFIER）
- [ ] Step 6: 收口与归档

## Validation

1. 每步：`./gradlew qaQuick`
2. P0 完成后：`./gradlew qaQuick` + `./gradlew qaPreview`
3. 设备可用时：按 Pixel 4 XL 跑一次 `qaFull`；如阻塞，记录 blocker 上下文

## Commit Log

1. `docs: add graphics v2 convergence execution plan`

## Blockers

- 暂无（如出现阻塞，追加到 `GRAPHICS_BLOCKER_CONTEXT_2026-03.md`）
