# RenderSession 帧对齐调度执行计划（2026-03）

## 1. 目标与范围

目标：将 `RenderSession` 的失效重绘调度从 `container.post` 升级为基于 `Choreographer` 的帧对齐调度，实现同帧合并与可取消回调。

范围固定：

1. `viewcompose-widget-core` 内部调度实现替换
2. `RenderSession` 接入新调度器
3. 测试与文档收口

非目标：

1. 不新增公开 `withFrameNanos`/动画时钟 API
2. 不改变 `setUiContent` / `renderInto` / `RenderSession.render()` 对外签名
3. 不改变“节点组级重组 + 根级遍历渲染”模型

## 2. 基线现状

1. 当前 `RenderSession.scheduleRender()` 使用 `container.post(renderRunnable)` 合并重绘。
2. 失效频繁场景存在与系统帧节奏不对齐风险。
3. `render()` 是立即执行语义，首帧依赖该行为保证首屏可见。

## 3. 验收标准

1. 同一帧内多次 invalidation 仅触发一次渲染提交。
2. 显式 `session.render()` 仍为立即执行。
3. `dispose()` 后无延迟回调继续触发渲染。
4. `qaQuick` 每步通过，关键节点与最终 `qaFull` 通过（设备可用时）。

## 4. 实施 Checklist

- [x] Step 1 新增执行计划文档并首提
- [x] Step 2 新增内部帧调度抽象（RenderFrameClock / AndroidChoreographerFrameClock / FrameAlignedRenderDispatcher）
- [x] Step 3 RenderSession 硬切接入帧调度器
- [ ] Step 4 统一影响面确认（overlay/lazy 语义保持）
- [ ] Step 5 测试补齐（合帧/取消/跨线程/重入）
- [ ] Step 6 文档收口与归档

## 5. 提交记录

- `DONE` docs: add frame-aligned render scheduler execution plan
- `DONE` feat: add choreographer-backed frame scheduler for render sessions
- `DONE` refactor: route render invalidations through frame-aligned dispatcher

## 6. 阻塞记录

暂无。
