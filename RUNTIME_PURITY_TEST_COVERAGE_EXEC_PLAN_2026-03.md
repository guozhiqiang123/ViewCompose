# Runtime 纯度与测试覆盖收口执行计划（2026-03）

## 1. 基线

- `viewcompose-runtime` 当前源码未使用 `android.*` / `androidx.*`，但模块仍是 Android library 形态。
- `viewcompose-runtime/build.gradle.kts` 仍依赖 `androidx.core.ktx`，当前源码未使用该依赖。
- runtime 测试已从单文件扩展到：
  - `DerivedStateTest`
  - `SnapshotStateTest`
  - `ComposerLiteTest`
- 当前测试仍缺 `policy/observation/invalidation/composer` 多个边界分支。

## 2. 范围与约束

- 模块形态：硬切为 Kotlin/JVM。
- 测试目标：核心分支全覆盖（snapshot + observation + composition 关键路径）。
- 迁移策略：不保留 Android-library 兼容分支，不扩展到 compiler plugin。

## 3. 完成标准

1. `viewcompose-runtime` 变更为 Kotlin/JVM 模块，移除 Android plugin 与 `androidx.core.ktx`。
2. `qaQuick` 任务链适配 runtime 新任务名。
3. 新增 runtime 纯度守卫并接入 `qaQuick`。
4. runtime 核心测试矩阵补齐并通过。
5. 文档同步更新并归档执行计划。

## 4. Checklist

- [x] Step 1 新增执行计划文档并首提
- [x] Step 2 runtime 硬切为 Kotlin/JVM
- [ ] Step 3 同步 `qaQuick` runtime 任务链
- [ ] Step 4 新增 runtime 纯度守卫并接入门禁
- [ ] Step 5 扩充 runtime 核心测试矩阵
- [ ] Step 6 文档收口（ARCHITECTURE/WORKFLOW/ROADMAP）
- [ ] Step 7 执行计划归档

## 5. 提交记录

- `DONE` docs: add runtime purity and test coverage execution plan
- `DONE` build: hard-switch runtime module to pure kotlin-jvm

## 6. 阻塞记录

- 2026-03-09: Step 2 后根 `qaQuick` 旧 runtime 任务名（`compileDebugKotlin/testDebugUnitTest`）失效，按计划在 Step 3 同步任务链恢复；Step 2 已用 `:viewcompose-runtime:compileKotlin :viewcompose-runtime:test` 验证通过。
