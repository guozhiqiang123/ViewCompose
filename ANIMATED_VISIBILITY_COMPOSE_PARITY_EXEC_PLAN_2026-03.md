# AnimatedVisibility 语义对齐执行计划（Compose 完整语义，2026-03）

## 1. 背景与目标

当前 `AnimatedVisibility` 为 alpha-only 语义（`fadeIn/fadeOut`），未覆盖 Compose 默认的尺寸参与布局过渡（`expand/shrink`），导致隐藏过程占位不缩、显示过程在部分场景出现闪烁。  
本轮目标：硬切到 Compose 对齐语义与默认值（含 Row/Column 特化），并消除上述行为缺口。

## 2. 约束与验收

1. 本轮为硬切，不保留旧 `fade-only` 结构与行为兼容层。
2. 默认语义对齐 Compose：
   - 通用：`fadeIn + expandIn` / `shrinkOut + fadeOut`
   - Row：水平 expand/shrink
   - Column：垂直 expand/shrink
3. 退出态内容在 exit 动画全部完成后才移除。
4. 通过专用 `NodeType` 容器承载尺寸动画，确保真实参与父布局重排。
5. 每步提交并同步更新本计划；每步 `qaQuick`，里程碑与收口跑 `qaFull`（设备可用时）。

## 3. 执行步骤

- [x] Step 1 新增执行文档并提交（本文件）。
- [x] Step 2 动画 API 硬切为可组合过渡模型（含默认值切换）。
- [x] Step 3 新增可见性状态机与 `MutableTransitionState<Boolean>` 版本 API。
- [ ] Step 4 新增 `AnimatedVisibilityHost` 节点与 renderer 容器实现。
- [ ] Step 5 `AnimatedVisibility` DSL 改为发射专用节点并完成语义切换。
- [ ] Step 6 Demo 与回归用例补齐（显隐、占位缩放、无闪烁）。
- [ ] Step 7 文档收口并归档执行计划。

## 4. 提交记录

1. `1b61215` docs: add animated visibility compose-parity execution plan
2. `TBD` refactor: hard-cut animated visibility transitions to compose-like combinable model
3. `TBD` feat: add compose-like visibility state machine with post-exit removal

## 5. 阻塞记录

当前无阻塞。
