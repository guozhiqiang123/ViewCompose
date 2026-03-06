# Archive Docs

## 1. 目录定位

本目录存放“历史阶段文档”与“一次性审计快照”。

这些文档用于追溯背景，不作为当前开发规范。

当前规范请从 [CONTEXT.md](/Users/gzq/AndroidStudioProjects/UIFramework/CONTEXT.md) 进入。

## 2. 判断规则

文档进入归档目录通常满足任一条件：

1. 阶段性任务已完成，仅保留历史记录价值
2. 内容已被上层主文档合并
3. 存在明显时间戳属性（如某次审计报告）

## 3. 使用规则

1. 归档文档原则上不再增量维护状态。
2. 如果历史结论仍有效，应回填到当前有效文档后，再引用归档文档作为证据。
3. AI 上下文恢复默认不读取本目录，除非需要追溯历史决策。

## 4. 已归档文档清单

| 文档 | 归档原因 |
| --- | --- |
| `COMPOSE_COMPONENT_GAP.md` | 阶段性对照快照，数据口径易过期 |
| `WIDGET_ROADMAP.md` | 已合并到 `ROADMAP.md` |
| `DEMO_ROADMAP.md` | 已合并到 `ROADMAP.md` |
| `OVERLAY_COMPONENTS_ROADMAP.md` | 已合并到 `ROADMAP.md` |
| `UI_TESTING.md` | 已合并到 `ROADMAP.md` |
| `THEME_OVERRIDES.md` | 规则已并入 `THEMING.md` |
| `THEME_AUDIT.md` | 一次性审计快照 |
| `WIDGET_PROPERTY_AUDIT.md` | 一次性属性审计快照 |
| `REVIEW.md` | 一次性架构审查快照 |
| `PROJECT_AUDIT_2026-03-05.md` | 一次性项目审计快照 |
| `REFACTOR_PLAN.md` | 已完成的阶段性执行计划 |
