# Gesture 模块跨平台分层重构执行计划（2026-03）

## 基线
- 当前手势能力以 `:viewcompose-gesture` + renderer 内嵌策略实现，未形成与 animation 对齐的 core/DSL 分层。
- `ModifierGestureDispatcher` 仍持有策略判定（axis lock、transform slop、swipe settle），renderer 职责偏重。
- 纯策略测试分散在 renderer，无法作为独立手势内核进行跨平台回归。

## 本轮范围与完成标准
- 范围固定：
  - 新增 `:viewcompose-gesture-core`（纯 Kotlin/JVM）。
  - 迁移手势策略内核到 `gesture-core`。
  - renderer 硬切为 Android 事件适配层。
  - `viewcompose-gesture` 拆分 DSL/State 入口并保持业务调用兼容。
  - 测试迁移 + 回归补强 + 文档收口归档。
- 完成标准：
  - `gesture-core` 不含 `android.*`/`androidx.*` import。
  - renderer 不再保留手势策略实现源头。
  - 业务 API 包名与调用方式不变。
  - 每步 `qaQuick` 通过；Step 4、Step 6、最终收口执行 `qaFull`（设备可用）。

## Checklist
- [x] Step 1: 新增执行计划文档并提交。
- [x] Step 2: 新增 `:viewcompose-gesture-core` 并接入构建守卫。
- [ ] Step 3: 手势策略内核从 renderer 硬切迁入 `gesture-core`。
- [ ] Step 4: renderer 改为事件适配层（调用 `gesture-core`）。
- [ ] Step 5: `viewcompose-gesture` 拆分 DSL/State 入口。
- [ ] Step 6: 测试迁移与补强（unit + instrumentation）。
- [ ] Step 7: 文档收口与归档。

## 提交记录
1. `docs: add gesture architecture convergence execution plan`
2. `build: add gesture-core module and purity guardrails`

## 阻塞记录
- 阻塞文件：`GESTURE_ARCH_BLOCKER_CONTEXT_2026-03.md`
- 当前：暂无阻塞。
