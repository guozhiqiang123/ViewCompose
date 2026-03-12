# Gesture 架构收口与 Compose 对齐（Hard Cut）执行计划（2026-03）

## 1. 基线

- 当前 `swipeable` API 接收多锚点 `Map<Float, T>`，但行为仅使用 min/max 两个锚点，语义与接口不一致。
- `combinedClickable` 在 `enabled=true` 且回调为空时仍可能吞掉触摸流，影响父容器与回落链路。
- transform 多指处理依赖 pointer index（0/1），在 pointer 重排场景可能出现跳变。
- renderer 手势路径测试以集成用例为主，分发器状态机缺少充分单测防回归。

## 2. 范围与约束

- 本轮策略：一次性硬切（不保留旧 API 兼容层）。
- 新主能力：`AnchoredDraggableState` + `Modifier.anchoredDraggable(...)`。
- 分层边界：`gesture-core` 提供决策与阈值模型，renderer 仅做 Android 事件采集与分发适配。
- `GestureOrientation.Free` 不进入 anchored API，仅支持 `Horizontal` / `Vertical`。

## 3. 完成标准

1. 移除 `SwipeableState/rememberSwipeableState/Modifier.swipeable`，调用侧迁移到 anchored API。
2. `gesture-core` 提供多锚点 settle 决策、阈值策略、anchor 校验与 current 锚点收口规则。
3. renderer 改为 core-driven 决策，transform 使用 pointerId 跟踪。
4. `combinedClickable` 无回调时不消费触摸流。
5. demo、unit、instrumentation 同步迁移并通过门禁。
6. 文档收口并归档执行计划。

## 4. Checklist

- [x] Step 1 新增执行计划文档并首提
- [x] Step 2 业务 API 硬切到 Anchored 模型
- [x] Step 3 `gesture-core` 补齐多锚点决策内核
- [ ] Step 4 renderer 手势分发器硬切重构
- [ ] Step 5 `combinedClickable` 行为语义收口
- [ ] Step 6 demo 与调用侧同步硬切
- [ ] Step 7 测试体系同轮补齐
- [ ] Step 8 文档与流程收口并归档

## 5. 提交记录

- `DONE` docs: add gesture re-audit hard-cut execution plan
- `DONE` refactor: hard-migrate swipeable api to anchored draggable model
- `DONE` feat: add anchored settle policy and threshold model in gesture-core

## 6. 阻塞记录

- 无
