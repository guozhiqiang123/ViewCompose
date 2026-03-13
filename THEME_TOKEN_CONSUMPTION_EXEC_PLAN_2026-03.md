# Theme Token 实际消费收口执行计划（2026-03）

## Summary

目标是把现有 Theme token 从“定义存在”收口为“组件默认值真实消费”，并按 Android View / Material 语义清理默认值映射漂移。

本轮固定范围：

1. 扩展 `UiColors` 的 surface 前景语义。
2. 把 colors / typography / shapes / controls 真正落到组件 defaults。
3. 修复复合组件仅传 `textSizeSp` 的结构缺口。
4. 增加 token usage audit，防止新增 token 再次闲置。

明确不做：

1. 不把 `success / warning / info` 强绑到现有核心组件。
2. 不从 Android 原主题系统反推 framework-owned control sizing。
3. 若本轮不补 elevation tint 渲染，则 `surfaceTint` 仅保留为 reserved token。

## Baseline

当前扫描结论：

1. 多数新增语义色只存在于 `ThemeTokens` / `AndroidThemeBridge`，未进入组件 defaults。
2. tiered typography 已定义，但大量组件仍使用旧 `title/body/label` 或只下发 `textSizeSp`。
3. `small / medium / large` shape tier 与多数 controls sizing 未真实驱动组件。
4. Tooltip、SearchBar、FAB、ListItem、NavigationBar、DropdownMenu 等仍存在大量硬编码颜色/尺寸/圆角。

## Checklist

- [ ] Step 1: 新增执行文档并提交
- [x] Step 2: 扩展 theme token 模型与 bridge/defaults
- [x] Step 3: 收口组件 defaults 的颜色 / 形状 / 尺寸语义映射
- [x] Step 4: 升级复合组件文本样式链路，去除仅 `textSizeSp` 限制
- [ ] Step 5: 增加测试、守卫与文档同步
- [ ] Step 6: 归档执行计划

## Acceptance

1. `UiColors` 具备 `onSurface / onSurfaceVariant`，并被 surface 系 defaults 消费。
2. Button/FAB/TextField/SearchBar/NavigationBar/Surface/Card/Dialog/Menu/Tooltip 至少完成一轮语义色收口。
3. `UiShapes.small/medium/large` 与新增 `UiControlSizing` 子域被组件 defaults 实际消费。
4. 复合组件内部文本可传递完整文本样式，而不只依赖字号。
5. 仓库存在 token usage audit，未消费 token 必须进入 reserved whitelist。

## Commit Log

1. `docs: add theme token consumption execution plan`
2. `feat: extend surface semantic theme tokens and bridge mappings`
3. `refactor: expand control sizing tokens across component families`
4. `refactor: align component defaults with semantic theme colors`
5. `refactor: propagate rich text styles through composite widget specs`

## Blockers

当前无阻塞。
