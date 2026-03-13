# Android Theme Bridge 收口执行计划（2026-03）

## Summary

目标是把 `AndroidThemeBridge` 从“少量颜色 + 3 个字号”的轻量桥接，升级为“尽可能衔接 Android / AppCompat / Material 原主题字段”的正式桥接层，同时保持当前 `Theme -> Defaults -> NodeSpec -> Renderer` 边界不回退。

本轮范围固定为：

1. `colors`：补齐更多 Android / Material 语义色，并明确推导 / fallback 规则。
2. `typography`：从“只桥接字号”升级为“桥接字号、字重、字族、字距、行高、includeFontPadding，以及 tiered typography”。
3. `shapes`：补齐 small / medium / large 三级圆角，并桥接 Material `shapeAppearance*Component`。
4. `overlays`：桥接 Android 原生 dim amount，优先喂给 `scrimOpacity`。
5. `ripple`：优先桥接 `colorControlHighlight`，不再只由 `textPrimary` 派生。

本轮明确不做：

1. 不把组件级 resolved style 提前塞回 `UiThemeTokens`。
2. 不把 renderer 或 DSL 组件默认值逻辑搬进 bridge。
3. 不承诺完整桥接 `controls` 三档尺寸体系；Android 原主题系统没有等价统一来源，本轮仅保留 defaults。

## Baseline

当前实现问题：

1. `AndroidThemeBridge` 逐项调用 `obtainStyledAttributes()`，读取路径零散。
2. `UiColors` 对 Android / Material 主题颜色的承接不完整。
3. `UiTypography` 已支持 tier，但 bridge 仍只桥接 `title/body/label` 的字号。
4. `UiShapes` 无法表达 small / medium / large 三级 shape。
5. `UiOverlays.scrimOpacity` 未衔接 Android 原主题系统。

## Done Criteria

满足以下条件视为本轮完成：

1. `AndroidThemeBridge` 代码拆成“snapshot 读取 + token 映射”两层，不再是单文件堆叠读取逻辑。
2. `UiColors` 能桥接更多 Android / Material 语义色，并保留兼容默认值。
3. `UiTypography` tier 字段由 bridge 实际填充，不再全部回退到旧字段。
4. `UiShapes` 支持 small / medium / large，且旧 `cardCornerRadius / interactiveCornerRadius` 行为不回退。
5. `scrimOpacity` 支持读取 Android dim amount；`ripple` 支持读取 `colorControlHighlight`。
6. 单测覆盖 mapper 语义与 fallback；编译 / `qaQuick` 通过。
7. `THEMING.md`、`ARCHITECTURE.md` 同步更新桥接边界与当前能力矩阵。

## Checklist

- [x] Step 1: 新增执行计划文档并提交。
- [x] Step 2: 扩 `UiColors` 语义字段，新增 Android 主题 snapshot 颜色读取与 ripple / dim amount 桥接。
- [x] Step 3: 扩 `UiShapes` 三级圆角模型，并桥接 Material shape appearance。
- [x] Step 4: 重构 typography bridge，补齐 tiered text appearance 与 richer text style 字段。
- [ ] Step 5: 补齐 `AndroidThemeBridgeTest` 与相关兼容测试，运行 `qaQuick`。
- [ ] Step 6: 更新 `THEMING.md`、`ARCHITECTURE.md`，完成后归档执行计划。

## Commit Log

- [ ] `docs: add android theme bridge convergence execution plan`
- [x] `feat: expand theme color bridge and android theme snapshot mapping`
- [x] `feat: bridge material shape appearance into semantic shape tokens`
- [x] `feat: enrich android typography bridge with tiered text appearances`
- [ ] `test: add android theme bridge coverage for expanded token mapping`
- [ ] `docs: codify expanded android theme bridge coverage`
- [ ] `docs: close and archive android theme bridge convergence plan`

## Blockers

当前无阻塞。
