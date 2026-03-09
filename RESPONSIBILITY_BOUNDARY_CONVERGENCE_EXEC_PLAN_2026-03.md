# 职责边界收口执行计划（P1+P2，2026-03）

## 1. 基线

- `widget-core` 当前仍承载 Android 渲染会话执行细节（`RenderSession` + frame dispatcher + frame clock）。
- `CollectionWidgetsDsl` 内嵌 `WidgetLazyListItemSession`，DSL 文件混入平台执行实现。
- `host-android` 对外 API 仍暴露 renderer 诊断类型（`RenderStats` / `RenderTreeResult`）。
- overlay 默认装配仍使用字符串反射 `Class.forName(...)`。

## 2. 范围与约束

- 本轮范围固定：`P1 + P2`。
- 迁移策略：硬切，不保留 `@Deprecated` 兼容入口。
- 不新增模块，沿用当前模块拓扑。

## 3. 完成标准

1. 渲染会话执行实现迁到 `host-android`，`widget-core` 仅保留会话契约与 provider。
2. `CollectionWidgetsDsl` 不再内嵌平台会话实现，DSL 仅负责声明式组装。
3. `setUiContent/renderInto` 回调类型改为 renderer-agnostic 诊断类型。
4. overlay 默认装配改为服务契约（`ServiceLoader`），移除字符串反射路径。
5. 增加边界守卫与回归测试，`qaQuick` 通过；里程碑节点执行 `qaFull`（阻塞则登记）。
6. 文档收口并归档计划文档。

## 4. Checklist

- [x] Step 1 新增执行计划文档并首提
- [ ] Step 2 渲染会话执行实现迁移到 host-android
- [ ] Step 3 从 Collection DSL 抽离平台会话实现
- [ ] Step 4 host 回调类型硬切为 renderer-agnostic 诊断类型
- [ ] Step 5 overlay 默认装配改为 ServiceLoader 契约
- [ ] Step 6 边界守卫与回归补齐
- [ ] Step 7 文档收口与归档

## 5. 提交记录

- `DONE` docs: add responsibility boundary convergence execution plan (p1-p2 hard-cut)

## 6. 阻塞记录

- 暂无。

