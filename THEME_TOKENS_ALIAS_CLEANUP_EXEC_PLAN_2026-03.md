# ThemeTokens 冗余收口硬切计划（2026-03）

## Summary

本轮目标是把 `ThemeTokens` 从“主语义 + 历史兼容 alias + reserved palette”收敛为更稳定的长期模型。

固定范围：

1. 删除 compatibility aliases：
   - `UiColors`: `textPrimary`、`textSecondary`、`divider`
   - `UiTypography`: `title`、`body`、`label`
   - `UiShapes`: `cardCornerRadius`
2. 保留 reserved semantic palette：
   - `success`、`warning`、`info`、`surfaceTint`
3. 不保留兼容层，不加 `@Deprecated`，调用侧、demo、测试、bridge、文档同轮迁完。

## Checklist

- [x] Step 1: 新增执行计划文档并提交
- [ ] Step 2: 硬切 ThemeTokens/Defaults/Bridge/Local 到纯语义主入口
- [ ] Step 3: 同步迁移 demo、测试与审计守卫
- [ ] Step 4: 更新 `THEMING.md` 并归档执行计划

## Acceptance

1. `ThemeTokens` 不再包含 `textPrimary/textSecondary/divider/title/body/label/cardCornerRadius`。
2. `LocalTextStyle` 与所有 defaults 只走 tiered typography 和新颜色/shape 语义。
3. `AndroidThemeBridge` / `ThemeTokenUsageAuditTest` 不再保留 compatibility aliases 白名单。
4. 现行源码不再残留被删除字段名。
5. `:viewcompose-widget-core:testDebugUnitTest`、`:app:compileDebugKotlin`、`qaQuick` 通过。

## Commit Log

1. `docs: add theme tokens alias cleanup execution plan`

## Blockers

当前无阻塞。
