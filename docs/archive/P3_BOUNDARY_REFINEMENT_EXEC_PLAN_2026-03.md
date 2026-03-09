# P3 边界收口执行计划（2026-03）

## 1. 基线

- `NodeBinderDescriptors.kt` 当前约 697 行，descriptor 单源正确，但扩展时易演化为隐性 God Object。
- `ui-contract` 的 `Modifier.kt` 同时承载“声明元素 + 运行策略提取”：
  - `LazyContainerReusePolicy` / `FocusFollowKeyboardPolicy`
  - `lazyContainerReusePolicy()` / `focusFollowKeyboardPolicy()`
- 目标是保持现有能力不回退的前提下，继续收敛职责边界。

## 2. 范围与约束

- 本轮范围固定：
  1. renderer descriptor 分域拆分（保持单源聚合）
  2. modifier 运行策略提取从 `ui-contract` 下沉到 renderer
- 迁移策略：硬切，不保留旧入口。
- 执行纪律：每步一提交；每步同步更新本计划；每步 `qaQuick`，里程碑与收口跑 `qaFull`。

## 3. 完成标准

1. `NodeBinderDescriptors` 不再是单一超大文件，新增节点可按域扩展。
2. `NodeType -> binder`、`Patch -> applier`、`Spec -> factory` 仍由单源聚合派生。
3. `ui-contract` 的 `Modifier.kt` 只保留声明契约，不再承载 renderer 运行策略提取。
4. 相关守卫与回归测试补齐，`qaQuick` 通过；里程碑/最终 `qaFull` 通过或登记阻塞。

## 4. Checklist

- [x] Step 1 新增执行计划文档并首提
- [x] Step 2 descriptor 分域拆分并保留单源聚合
- [x] Step 3 descriptor 结构与覆盖守卫测试
- [x] Step 4 modifier 运行策略提取下沉到 renderer（硬切）
- [x] Step 5 policy 测试迁移到 renderer
- [x] Step 6 文档边界收口（ARCHITECTURE/WORKFLOW）
- [x] Step 7 最终门禁、归档与索引更新

## 5. 提交记录

- `DONE` docs: add p3 boundary refinement execution plan
- `DONE` refactor: split node binder descriptors by domain with single-source aggregator
- `DONE` refactor: group node binder descriptor sources under dedicated descriptor directory
- `DONE` test: add descriptor structure and coverage guards
- `DONE` refactor: move modifier runtime policy extraction out of ui-contract
- `DONE` test: migrate modifier policy tests to renderer policy resolver
- `DONE` docs: codify descriptor modularization and modifier contract boundaries
- `DONE` docs: close and archive p3 boundary refinement plan

## 6. 阻塞记录

- 暂无。
