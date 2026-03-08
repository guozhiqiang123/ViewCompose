# Renderer 复杂度收敛执行计划（2026-03）

## 1. 目标与范围

目标：收敛 renderer 核心绑定链路复杂度，重点治理：

1. `ViewModifierApplier` 大文件职责过载
2. `NodeViewBinderRegistry` / `NodeBindingDiffer` 手工映射重复

范围固定：

1. `viewcompose-renderer` 内部重构（不改外部 API）
2. 绑定注册“单源 descriptor”落地
3. guard test 防回归

非目标：

1. 不引入 KSP/代码生成插件
2. 不做跨模块依赖调整
3. 不改 DSL/宿主公开接口签名

## 2. 基线

当前基线（2026-03-08）：

1. `ViewModifierApplier.kt` 约 768 行
2. `NodeViewBinderRegistry.kt` 约 389 行
3. `NodeBindingDiffer.kt` 维护独立 patch factory 映射，与 registry 存在重复维护

## 3. 验收标准

1. `ViewModifierApplier` 拆分为样式/交互/insets/容器策略子职责，主 facade 显著瘦身。
2. bind/patch/diff 映射从单源 descriptor 派生，删除并行手工映射。
3. 新增 guard test 能拦截 NodeType/NodeViewPatch 覆盖缺失与映射重复。
4. 每步 `qaQuick` 通过；Step 4 与最终收口 `qaFull` 通过。

## 4. 实施 Checklist

- [x] Step 1 新增执行计划文档并首提
- [x] Step 2 拆分 ViewModifierApplier（样式/交互/insets/容器策略）
- [x] Step 3 NodeViewBinderRegistry 单源 descriptor 自动化
- [x] Step 4 NodeBindingDiffer 改为 descriptor 派生 patch factory
- [x] Step 5 补齐 guard tests 与关键回归
- [x] Step 6 文档收口与归档

## 5. 提交记录

- `DONE` docs: add renderer complexity convergence execution plan
- `DONE` refactor: split view modifier responsibilities into focused appliers
- `DONE` refactor: automate binder registration with descriptor-driven registry
- `DONE` refactor: drive node binding differ patch factories from binder descriptors
- `DONE` test: add binder descriptor coverage and consistency guards
- `DONE` docs: codify renderer binder and modifier complexity boundaries
- `DONE` docs: close and archive renderer complexity convergence plan

## 6. 阻塞记录

暂无。
