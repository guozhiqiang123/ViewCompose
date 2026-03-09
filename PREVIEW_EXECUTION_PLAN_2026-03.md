# ViewCompose 开发预览执行计划（2026-03）

## 1. 目标与范围

目标：按 `1 + 3` 组合落地开发预览能力。

1. 方案 1：Compose Preview 桥接 ViewCompose DSL
2. 方案 3：Paparazzi 快照回归与预览画廊
3. 新建模块：`:viewcompose-preview`
4. Overlay 策略：Preview 仅做静态内容模拟，不做真实窗口弹层

非目标：

1. 不改 `setUiContent/renderInto` 对外签名
2. 不改变现有 `ui-contract/widget-core/renderer/host-android` 依赖边界
3. 不在 Preview 中承诺真实 Dialog/Popup/BottomSheet 窗口行为

## 2. 基线

执行前基线：

1. 仓库无独立 preview 模块
2. 无统一的组件预览目录与单源预览注册
3. 无 Paparazzi 快照门禁任务

## 3. 验收标准

1. Android Studio 可直接预览 `PreviewShells` 与 `CatalogPreviews`。
2. `PreviewCatalog` 作为 Studio Preview 与 Paparazzi 单源。
3. `qaPreview` 可执行并验证快照差异。
4. Guard 覆盖：id 唯一、覆盖清单一致、preview 禁止依赖 `:app`。
5. 文档写清模块边界与流程约束，并归档本执行计划。

## 4. 实施 Checklist

- [x] Step A 新增 `:viewcompose-preview` 模块并接入构建门禁
- [x] Step B 落地 `ViewComposePreviewHost`（Compose Preview 桥接）
- [x] Step C 落地 `PreviewSpec/PreviewCatalog` 分域化覆盖
- [x] Step D 接入 Paparazzi 与 `qaPreview`
- [x] Step E 增加 preview guard tests（唯一性/覆盖/依赖边界）
- [ ] Step F 文档收口与归档

## 5. 提交记录

- `DONE` build: add viewcompose-preview module and compose preview toolchain
- `DONE` feat: add compose-preview host bridge for viewcompose render sessions
- `DONE` feat: add component preview catalog with domain-grouped specs
- `DONE` test: add paparazzi snapshot coverage backed by preview catalog
- `DONE` build: add qaPreview task for preview snapshot verification
- `DONE` test: add preview catalog guardrails for uniqueness and coverage
- `TODO` docs: codify preview module boundaries and workflow requirements
- `TODO` docs: close and archive preview execution plan

## 6. 阻塞记录

暂无。
