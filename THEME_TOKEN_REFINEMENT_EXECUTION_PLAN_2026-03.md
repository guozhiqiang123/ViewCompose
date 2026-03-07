# Theme Token 收口与升级执行计划（2026-03）

## 1. Baseline

1. 当前 `UiThemeTokens` 已包含 `overlays`，但根文档 `THEMING.md` 仍停留在 4 域描述。
2. `UiColors` 仅有 `accent`，缺少 `secondary/error/success/warning/info` 语义色。
3. `TextFieldDefaults`、`BadgeDefaults`、`NavigationBarDefaults` 仍存在关键硬编码颜色。
4. `UiTypography` 仅有 `title/body/label`，组件分级样式表达不足。
5. `UiShapes` 使用 `controlCornerRadius` 命名，语义不够清晰。

## 2. Scope

本轮范围固定为 `P0 + P1`：

1. 文档和实现对齐（含 overlays）。
2. 语义色补齐与关键硬编码去除。
3. typography 分级扩展与 shape 命名语义增强（兼容式）。
4. 兼容保留旧字段并标记弃用，不做破坏性迁移。

## 3. Acceptance Criteria

1. `THEMING.md` 与实现一致，明确 5 域模型与迁移策略。
2. `UiColors` 支持 `secondary/error/success/warning/info`，`accent` 处于兼容过渡状态。
3. 关键错误/徽标色不再硬编码，改为读取主题语义色。
4. `UiTypography` 支持 tier 字段并可回落到旧字段。
5. `UiShapes` 支持 `interactiveCornerRadius` 并与旧字段兼容。
6. 单测覆盖兼容与新语义生效路径，并新增硬编码颜色守卫。
7. 根文档状态与归档索引同步更新。

## 4. Checklist

- [x] Step 1: 新增执行计划文档并首提。
- [x] Step 2: 更新 `THEMING.md` 基线与兼容迁移策略。
- [x] Step 3: `UiColors` 语义色升级与 bridge/defaults/demo 同步。
- [x] Step 4: 去除关键硬编码颜色并同步文档禁用清单。
- [x] Step 5: `UiTypography` 分级语义扩展并迁移首批 defaults。
- [x] Step 6: `UiShapes` 引入 `interactiveCornerRadius` 兼容别名并更新 demo 文案。
- [ ] Step 7: 测试补齐（兼容与语义覆盖 + 硬编码守卫）。
- [ ] Step 8: 文档收口、计划归档与归档索引更新。

## 5. Commit Log

| Date | Step | Commit | Notes |
| --- | --- | --- | --- |
| 2026-03-07 | Step 1 | `4d4eab7` | Add execution plan doc. |
| 2026-03-07 | Step 2 | `eae4da3` | Align theming baseline with overlays and migration policy. |
| 2026-03-07 | Step 3 | `c2d78f3` | Add semantic color tokens and migrate internal usage to secondary-first path. |
| 2026-03-07 | Step 4 | `e0e5f69` | Replace critical hardcoded colors with semantic `Theme.colors.error`. |
| 2026-03-07 | Step 5 | `11dccd3` | Add tiered typography tokens and migrate core defaults to tier APIs. |
| 2026-03-07 | Step 6 | `f7abeb0` | Introduce interactive corner radius and migrate core/demo usage. |
| 2026-03-07 | Step 7a | pending | Add compatibility + semantic token unit coverage. |

## 6. Blocker Log

若出现阻塞（需要确认 / `qaFull` 设备不可用）：

1. 记录到 `THEME_TOKEN_BLOCKER_CONTEXT_2026-03.md`。
2. 记录时间、已完成步骤、最新 commit、阻塞原因、恢复命令。

当前记录：

1. 2026-03-07：Step 3 里程碑 `qaFull` 触发 instrumentation 失败（`connectedDebugAndroidTest`，29 测 9 失败），已保留失败报告路径用于后续回归定位。
2. 2026-03-07：Step 6 里程碑 `qaFull` 复现同一批 9 条 instrumentation 失败，报告路径同上。
