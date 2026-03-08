# widget-core 解除 renderer 直依赖执行计划（2026-03）

## 1. 目标与范围

目标：按硬切方案完成分层重构，把 `viewcompose-widget-core -> viewcompose-renderer` 的直依赖收敛为：

`viewcompose-runtime + viewcompose-ui-contract + viewcompose-widget-core + viewcompose-renderer(android) + viewcompose-host-android`

并同步迁移相关测试到对应模块。

范围固定：

1. 新增 `:viewcompose-ui-contract`（纯 Kotlin）与 `:viewcompose-host-android`（Android 宿主实现）。
2. `widget-core` 不再依赖 `renderer`。
3. Node/Modifier 等公共契约迁到 `ui-contract`。
4. `RenderSession/setUiContent/AndroidView/nativeView` 迁到 `host-android`。
5. `renderer/overlay-android/image-coil` 适配新契约。
6. 同步迁移测试与 guard 约束。

## 2. 验收标准

1. `viewcompose-widget-core/build.gradle.kts` 不再出现 `project(":viewcompose-renderer")`。
2. `viewcompose-ui-contract` 主源码无 `android.*` / `androidx.*` import。
3. `viewcompose-widget-core` 主源码无 `com.viewcompose.renderer.*` import。
4. `qaQuick` 每步通过；里程碑与收口 `qaFull` 通过。
5. 迁移后的 demo overlay/lazy/pager/input 路径无行为回退。

## 3. Checklist

- [x] Step 1 建立执行计划文档并首提
- [x] Step 2 新增模块并改构建拓扑
- [x] Step 3 抽取 UI 契约到 ui-contract
- [x] Step 4 Android 专属 API 迁出到 host-android
- [ ] Step 5 renderer 适配 ui-contract
- [ ] Step 6 overlay/image-coil 同步迁移
- [ ] Step 7 测试同步迁移与 guard
- [ ] Step 8 文档收口与归档

## 4. 提交记录

- `DONE` docs: add widget-core renderer decouple execution plan
- `DONE` build: add ui-contract and host-android modules and rewire dependencies
- `DONE` refactor: move node and modifier contracts into pure ui-contract module
- `DONE` refactor: move android host runtime and interop APIs to host-android module

## 5. 阻塞记录

暂无。
