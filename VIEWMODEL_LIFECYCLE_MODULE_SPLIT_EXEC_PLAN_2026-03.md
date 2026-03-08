# ViewModel/Lifecycle 模块化改造执行计划（2026-03）

## 1. 基线

- 当前相关能力位于 `viewcompose-widget-core`：
  - `FlowCollectAsState.kt`
  - `ViewModelComposition.kt`
  - `SavedStateHandleComposition.kt`
  - `LifecycleLocals.kt`
  - `ViewModelLocals.kt`
- `setUiContent` 位于 `viewcompose-widget-core` 且已负责 Local 注入。
- 尚未存在独立模块：`:viewcompose-lifecycle`、`:viewcompose-viewmodel`。

## 2. 范围与约束

- 范围固定：
  - 新增 `:viewcompose-lifecycle`、`:viewcompose-viewmodel`
  - API 迁移到新包名（硬切，不保留旧入口）
  - demo 与文档同步收口
- 约束固定：
  - `setUiContent` 继续保留在 `viewcompose-widget-core`
  - 宿主自动注入能力保持可用

## 3. 完成标准

1. 新模块接入并可参与 `qaQuick`。
2. 旧包 `com.viewcompose.widget.core` 下不再保留相关 API。
3. `app` 使用新模块与新包导入可编译运行。
4. 文档状态与实现一致，执行计划归档。

## 4. 实施 Checklist

- [x] Step 1 新增执行计划文档并首提
- [x] Step 2 新增两个模块并接入构建拓扑
- [ ] Step 3 Local API 包名硬切并更新 host 注入
- [ ] Step 4 迁移 Flow 收集 API 到 lifecycle 模块
- [ ] Step 5 迁移 ViewModel API 到 viewmodel 模块
- [ ] Step 6 demo 与消费方同步迁移
- [ ] Step 7 文档收口与归档

## 5. 提交记录

- `DONE` docs: add viewmodel/lifecycle module split execution plan
- `DONE` build: add lifecycle and viewmodel integration modules

## 6. 阻塞记录

- 暂无
