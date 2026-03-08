# 严格单包根迁移执行计划（2026-03）

## 1. 基线

- 目标规则：每个模块仅允许一个包根前缀，覆盖 `src/main`、`src/test`、`src/androidTest`。
- 扫描结果：当前仅 2 处违规，均在 `viewcompose-widget-core`：
  - `viewcompose-widget-core/src/main/java/com/viewcompose/lifecycle/LifecycleLocals.kt`
  - `viewcompose-widget-core/src/main/java/com/viewcompose/viewmodel/ViewModelLocals.kt`
- 其余模块当前已满足“包名前缀与模块归属一致”。

## 2. 固定映射（唯一标准）

- `app -> com.viewcompose`
- `viewcompose-runtime -> com.viewcompose.runtime`
- `viewcompose-ui-contract -> com.viewcompose.ui`
- `viewcompose-renderer -> com.viewcompose.renderer`
- `viewcompose-widget-core -> com.viewcompose.widget.core`
- `viewcompose-host-android -> com.viewcompose.host.android`
- `viewcompose-overlay-android -> com.viewcompose.overlay.android`
- `viewcompose-image-coil -> com.viewcompose.image.coil`
- `viewcompose-benchmark -> com.viewcompose.benchmark`
- `viewcompose-lifecycle -> com.viewcompose.lifecycle`
- `viewcompose-viewmodel -> com.viewcompose.viewmodel`

## 3. 范围与约束

- 迁移策略：硬切，不保留兼容层，不加 `@Deprecated` 过渡。
- 验收范围：主源码 + 单测 + instrumentation。
- 统一口径：“单一包根”指单一前缀，允许该前缀下的子包分层。

## 4. 完成标准

1. 所有模块 `main/test/androidTest` 的源码包声明均满足模块包根前缀。
2. Android 模块 `namespace` 与模块包根一致（`viewcompose-ui-contract` 例外）。
3. `qaQuick` 全程通过。
4. Step 3 与最终收口各执行一次 `qaFull`（设备可用时）。
5. 文档补充“单包根”准入规则并归档执行计划。

## 5. 实施 Checklist

- [x] Step 1 新增执行计划文档并首提
- [x] Step 2 固化模块到包根映射（构建侧单一真源）
- [ ] Step 3 迁移 Lifecycle/ViewModel Locals 到所属模块
- [ ] Step 4 同步迁移相关测试并清理错误归属测试
- [ ] Step 5 增加 `verifyModulePackageRoots` 守卫并接入 `qaQuick`
- [ ] Step 6 增加 namespace 一致性守卫
- [ ] Step 7 文档收口与归档

## 6. 提交记录

- `DONE` docs: add strict single-package-root migration execution plan
- `DONE` build: define canonical module package root mapping

## 7. 阻塞记录

- 暂无
