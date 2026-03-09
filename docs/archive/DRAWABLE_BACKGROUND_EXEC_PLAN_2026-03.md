# Drawable 背景 API 执行计划（2026-03）

## 1. 基线与范围

当前能力仅支持 `Modifier.backgroundColor(color: Int)`，不支持通过 drawable 资源设置通用背景。

本轮范围固定：

1. 新增 `Modifier.backgroundDrawableRes(resId: Int)`。
2. renderer 打通解析与样式链路，语义为 drawable 优先于 backgroundColor。
3. 保持现有组合规则：`border/ripple` 继续生效；仅 `clip()` 时裁剪内容。
4. 补 demo 与文档，补充必要测试。

不在本轮范围：

1. `Drawable` 实例输入。
2. 远程图片背景。
3. 自动按圆角裁剪（不改变现有 clip 语义）。

## 2. 验收标准

1. `Modifier.backgroundDrawableRes` 可用，且不引入 `ui-contract` 的 Android 依赖。
2. 同时设置 `backgroundColor + backgroundDrawableRes` 时，最终按 drawable 渲染。
3. drawable 缺失或加载失败时不崩溃，走现有安全回退。
4. demo 有可视化样例并带 testTag。
5. `qaQuick` 通过；本轮收口补跑 `qaFull`（设备可用）。

## 3. 执行清单

- [x] Step 1 `ui-contract` 新增 modifier element + DSL API + contract test
- [x] Step 2 renderer `ResolvedModifiers/NodeStyle` 打通
- [x] Step 3 `ModifierSurfaceStyleApplier` 增加 drawable 分支与优先级
- [x] Step 4 文档同步：`MODIFIER.md` 新增 API、优先级与组合语义
- [x] Step 5 demo 用例 + instrumentation 回归补充（drawable 可见与状态切换稳定）
- [x] Step 6 收口：`qaQuick` + `qaFull`，同步本文档状态

## 4. 提交记录

1. `76928d1` feat: add backgroundDrawableRes modifier element in ui-contract
2. `f6137d6` refactor: propagate background drawable modifier through renderer style resolution
3. `38ae3ea` feat: support drawable resource backgrounds in surface style applier
4. `a337c6d` docs: add drawable background execution plan and backfill progress
5. `a608513` docs: document drawable background modifier semantics
6. `5e8d7cc` demo: add drawable background modifier showcase
7. `c493ec5` test: add visual coverage for drawable background modifier

## 5. 阻塞记录

1. `2026-03-09` 首次 `qaFull` 出现过 `ComponentFamilySmokeUiTest#keyComponentFamilies_haveVisibleSmokeAnchors` 进程崩溃；同日重跑 `./gradlew qaFull` 已通过（33/33）。
2. 新增用例 `./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.viewcompose.DemoVisualUiTest#modifiersPage_drawableBackgroundOverridesColorBackground` 已通过。
