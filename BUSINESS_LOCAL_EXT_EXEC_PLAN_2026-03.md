# 业务侧自定义 Local 扩展执行计划（2026-03）

## 1. Baseline

1. 框架内部已存在 Local 机制（`LocalValue` / `LocalContext`），并支持 snapshot 传播到 lazy/overlay。
2. 当前业务侧无法自定义 Local，因为 `LocalValue` / `LocalContext` 为 `internal`。
3. 现有调用存在专用包装（如 `ProvideContentColor`、`ProvideTextStyle`），不利于统一扩展范式。

## 2. Scope

本轮目标是开放“业务 token + Local + Provider”能力，并执行到位重构：

1. 新增公开的通用 Local API（业务侧可直接定义/提供/读取）。
2. 不保留旧调用方式：移除 `ProvideContentColor` / `ProvideTextStyle`，全量切到统一 Local API。
3. 保持 lazy/overlay 的 local snapshot 行为不回退。
4. 补齐单元测试、demo 示意与文档规范。

## 3. Acceptance Criteria

1. 业务侧可通过公开 API 创建 `uiLocalOf(...)` 并在 `UiTreeBuilder` 子树中注入读取。
2. 内置内容色/文本样式也使用统一 Local API，不再保留旧 Provide 入口。
3. `qaQuick` 全绿；收口时执行 `qaFull`。
4. 主文档写明“业务可自定义 Local”能力边界与推荐用法。
5. 计划文档完成后归档到 `docs/archive/`。

## 4. Checklist

- [x] Step 1: 新增执行计划文档并首提。
- [x] Step 2: 新增公开 Local API（`uiLocalOf` / `UiLocals.current` / `ProvideLocal(s)`）。
- [x] Step 3: 移除旧调用方式并全量迁移（`ProvideContentColor` / `ProvideTextStyle`）。
- [ ] Step 4: 补齐业务自定义 Local 的单测（默认值/嵌套/snapshot）。
- [ ] Step 5: 增加 demo 中业务 Local 示例（BizTokens 场景）。
- [ ] Step 6: 更新 `THEMING.md`、`ARCHITECTURE.md`、`WORKFLOW.md`。
- [ ] Step 7: 收口门禁、计划归档、archive 索引更新。

## 5. Commit Log

| Date | Step | Commit | Notes |
| --- | --- | --- | --- |
| 2026-03-07 | Step 1 | `3ff5840` | Add execution plan doc. |
| 2026-03-07 | Step 2 | `2cd7c80` | Add public Local APIs for business-defined tokens. |
| 2026-03-07 | Step 3 | pending | Remove legacy local provider APIs and migrate all call sites. |

## 6. Blocker Log

若出现阻塞（如 `qaFull` 设备/环境问题），记录到 `BUSINESS_LOCAL_BLOCKER_CONTEXT_2026-03.md`，至少包含：

1. 时间与阻塞点。
2. 已完成步骤与最新 commit。
3. 当前分支与工作区状态。
4. 恢复命令。
