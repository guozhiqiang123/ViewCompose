# UIFramework Performance

## 1. 文档定位

本文档是性能规范版，定义：

1. 当前性能基线
2. 性能门禁指标
3. 设计与实现层约束
4. 后续优化路线

历史长版分析见：

- [PERFORMANCE_FULL_2026-03-06.md](/Users/gzq/AndroidStudioProjects/UIFramework/docs/archive/PERFORMANCE_FULL_2026-03-06.md)

## 2. 当前性能基线（2026-03）

### 2.1 已建立能力

1. benchmark 模块已接入，具备稳定测试入口。
2. renderer 已具备节点级“是否重绑”判断能力（`rebound/skipped` 统计）。
3. diagnostics 已有 render/layout 基础指标可观测能力。

### 2.2 当前结论

1. 当前阶段优先级不是“追求极限 FPS”，而是先控制回归风险和错误用法。
2. 最关键收益来自：
   - 正确复用
   - 跳过不必要更新
   - 容器刷新语义稳定
3. 当前阻塞（2026-03-06）：`connectedDebugAndroidTest` 基线未稳定（19 条中 16 条失败），性能阶段推进需与 UI 回归稳定化并行，避免“性能优化掩盖功能回归”。

## 3. 性能门禁指标

每次性能相关改动，至少关注下面 4 类成本：

1. 重建成本：状态变化后产树与 reconcile 开销
2. 绑定成本：View rebind 与 patch 执行开销
3. 布局成本：measure/layout 次数与深度
4. 容器成本：延迟 session 容器的刷新与复用稳定性

建议固定输出：

1. benchmark 数据（同机型、同路径）
2. render stats（含 rebound/skipped）
3. layout pass 关键计数

## 4. 设计约束（必须遵守）

1. 新控件必须先定义“高频路径”和“可接受开销”，再扩参数。
2. 新 modifier/props 不得引入无条件全量 rebind。
3. 复用型容器必须有“结构稳定仍刷新可见内容”路径。
4. `AndroidView` 视为性能隔离区，复杂逻辑优先在外部宿主层控制。
5. 不为短期优化破坏模块边界和可维护性。

## 5. 反模式清单

1. 用深层嵌套布局代替明确的容器策略。
2. 无基准数据支撑就引入复杂优化。
3. 把性能问题都归因于运行时，而忽略页面/容器结构问题。
4. 在无回归测试情况下改动核心渲染热点。

## 6. 分阶段路线

### Phase 1：基线与可观测性

状态：已完成基础落地  
目标：benchmark 路径稳定、核心指标可读取

### Phase 2：跳过更新能力

状态：进行中（已有起步）  
目标：扩大节点级 skip 更新覆盖，降低无效 rebind

### Phase 3：诊断增强

状态：待继续推进  
目标：让 render/patch/layout 问题定位更直接可用

### Phase 4：容器与布局收口

状态：待推进  
目标：收敛高频容器和复杂页面的布局开销

### Phase 5：发布态优化

状态：待推进  
目标：baseline profile 等发布链路优化稳定化

## 7. 评审与提交流程

性能相关 PR 至少满足：

1. 说明改动针对哪类成本
2. 提供改动前后关键指标
3. 说明是否影响容器刷新语义
4. 同步更新本文件或相关规范文档

若改动涉及可见行为（布局、交互、overlay、输入），额外要求：

1. 至少补一条对应 instrumentation 回归，或登记明确豁免与补齐时间

协作规则见：

- [WORKFLOW.md](/Users/gzq/AndroidStudioProjects/UIFramework/WORKFLOW.md)

## 8. 关联文档

1. 架构规范：[ARCHITECTURE.md](/Users/gzq/AndroidStudioProjects/UIFramework/ARCHITECTURE.md)
2. 容器专项清单：[SESSION_CONTAINER_CHECKLIST.md](/Users/gzq/AndroidStudioProjects/UIFramework/SESSION_CONTAINER_CHECKLIST.md)
3. 统一路线图：[ROADMAP.md](/Users/gzq/AndroidStudioProjects/UIFramework/ROADMAP.md)
4. 文档入口：[CONTEXT.md](/Users/gzq/AndroidStudioProjects/UIFramework/CONTEXT.md)
