# Demo 主题诊断页执行计划（2026-03）

## Summary

目标是在 `Diagnostics` 内新增“主题诊断”页签，把 Theme token 的实际消费结果变成可人工验证的 demo 控制台。

本轮固定范围：

1. `Diagnostics` 扩为 `运行时 / 主题 / 渲染器 / 缺口` 四页签。
2. 新增主题诊断页，覆盖 token snapshot、关键组件默认值消费、shape/size 诊断、页面内验证说明。
3. 抽取 demo 内部共享的主题诊断 helper，避免 `Foundations` 与 `Diagnostics` 主题样本漂移。
4. 补充稳定 `DemoTestTags`，但本轮不新增一轮 instrumentation。
5. 更新 `ROADMAP.md` / `THEMING.md`，完成后归档本计划。

明确不做：

1. 不新增框架公共 API。
2. 不在主题诊断页重复堆叠完整 live overlay 功能演示。
3. 不把本轮扩大成新的 UI 自动化项目。

## Checklist

- [x] Step 1: 新增执行计划文档并提交
- [x] Step 2: 扩展 Diagnostics 页签与活动入口常量
- [x] Step 3: 提取共享 theme diagnostic helper 并落主题诊断页主体
- [x] Step 4: 补稳定 testTag 与页面内人工验证说明
- [x] Step 5: 更新 `ROADMAP.md` / `THEMING.md` 并归档执行计划

## Acceptance

1. `Diagnostics` 内可直接进入“主题诊断”页。
2. 页面能直接观察 `token -> defaults -> 组件视觉` 的映射，而不只是颜色数值。
3. 至少覆盖 Surface、Action、Input、Navigation、Shape/Size 五类诊断样本。
4. 页面内存在清晰的人工回归说明，并标注真实功能页抽查路径。
5. `qaQuick` 通过。

## Commit Log

1. `docs: add demo theme diagnostics execution plan`
2. `feat: add diagnostics theme page entry skeleton`
3. `feat: add diagnostics theme component coverage sections`
4. `demo: add theme diagnostic probes and manual verification notes`
5. `docs: codify diagnostics theme verification entry`
6. `docs: close and archive demo theme diagnostics execution plan`

## Blockers

当前无阻塞。
