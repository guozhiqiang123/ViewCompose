# ViewCompose 审计报告（2026-03-05）

## 0. 审计范围与方法

本次审计覆盖根目录全部 `*.md` 文档与当前代码主干，重点回答三件事：

1. 规划是否有利于稳定性、可扩展性、可维护性、可理解性
2. 提交记录是否按文档规划推进
3. 实现是否干净、是否存在冗余

取证方法：

- 文档：逐份阅读根目录 17 份文档（`ARCHITECTURE.md`、`WORKFLOW.md`、`COMPOSE_COMPONENT_GAP.md` 等）
- 提交：审查最近 120 条 commit（`git log`）
- 代码：核对模块目录、关键实现文件、覆盖情况
- 质量基线：执行编译与测试命令并记录结果

---

## 1. 总结结论

总体判断：**规划方向正确、执行节奏较好，但当前工程质量门禁失效，导致“文档完成态”与“可运行事实”出现偏差**。

- 规划层：模块边界、分阶段路线、workflow 思路基本合理
- 执行层：小步提交和 docs/feat 同步总体成立
- 质量层：存在 **P0 级问题**（当前工作区有未提交编译断裂改动、UI 测试基线大面积失败）

如果以“可持续稳定演进”为目标，当前状态应定义为：**可演进，但未达到可发布稳定基线**。

---

## 2. 规划合理性审计（问题 1）

## 2.1 合理点

1. 模块边界总体正确：`viewcompose-runtime / viewcompose-renderer / viewcompose-widget-core / viewcompose-overlay-android / app` 的职责划分清晰，且代码目录已基本按此落位（见 `ARCHITECTURE.md:64-257`）。
2. Overlay 路线正确：将 Dialog/Popup 的平台实现下沉到 `viewcompose-overlay-android`，声明契约留在 `viewcompose-widget-core`，符合“平台实现不回流 DSL 模块”的规则（`ARCHITECTURE.md:250-253`，`OVERLAY_COMPONENTS_ROADMAP.md:92-155`）。
3. Workflow 设计正确：小步提交、文档同步、线程丢失恢复顺序等规则可操作性高（`WORKFLOW.md:16-143`）。

## 2.2 规划层风险与缺口

1. “完成态”定义不够严格：`WORKFLOW.md` 规定功能实现应补齐测试与 demo（`WORKFLOW.md:63-74`），但文档缺少强制质量门禁条款（如“标记完成前必须编译/回归通过”）。
2. 文档状态存在自冲突：`THEME_AUDIT.md` 同时写“7 个维度全部已处理”和“Android 桥接部分完成，仍待后续”，状态表达不一致（`THEME_AUDIT.md:12,19`）。
3. 文档快照存在滞后：`COMPOSE_COMPONENT_GAP.md` 顶部“当前状态”统计明显过期（`COMPOSE_COMPONENT_GAP.md:5`），且附录仍保留“Card/AlertDialog 待实现”的旧表述（`COMPOSE_COMPONENT_GAP.md:1029,1032`）。
4. 文档数量较多且重叠：主题相关（`THEMING`、`THEME_OVERRIDES`、`THEME_AUDIT`、`REFACTOR_PLAN`）和审计相关文档存在重复描述，后续维护成本高、易发生状态漂移。

---

## 3. 提交记录对照审计（问题 2）

## 3.1 与规划一致的证据

提交主线总体符合“先文档、再分步实现、再回写状态”的模式：

- Overlay 专项：`docs: add overlay components roadmap` (`3e73333`) → host/DSL/demo/test 分步提交（`98b1d6f`、`a8016ed`、`b872541`、`ebba47e`、`2d4b718`、`1a1e141`、`c80a1ec`）→能力补充（`6057dfd`、`2c87adf`）
- 主题重构：`REFACTOR_PLAN` 各 Sprint 对应提交可追溯（如 `62b072f`、`440f847`、`44b82b4`、`00cde4f`、`78295b1`、`44a51c9`）
- 目录整理：模块/目录重构类提交连续且粒度小（`620703f`、`3588430`、`578c641`、`de181d3`、`8a192e3`、`d051a53`、`3e74aad`）

## 3.2 与规划不一致的点

1. 部分“完成”仅到 DSL/Defaults 层，未见 demo 与测试落地。
   - `Chip`：`77b2d08` 仅改 `defaults + dsl`，无 demo/test 文件。
   - `SearchBar`：`41f766e` 仅改 `defaults + dsl`，无 demo/test 文件。
   - 随后 `ef1a4ca` 已将 Phase 项标记完成。
2. 新组件在 demo/test 的可见度不足。
   - 全仓检索仅能找到 DSL 定义，未发现 `app` 页面对 `Chip/SearchBar/Scaffold/NavigationBar/LazyRow/FlowRow/Scrollable*` 的调用。
3. 当前工作区存在进行中的断裂改动，建议补本地 pre-commit 质量门禁（见第 4 节 P0）。

结论：**提交节奏和规划轨迹“形式上高度一致”，但“完成定义”与“验证闭环”不一致。**

---

## 4. 实现洁净度与冗余审计（问题 3）

## 4.1 P0（立即处理）

1. `viewcompose-renderer` 在**当前工作区（含未提交改动）**无法编译通过。
   - 复现命令：`./gradlew :viewcompose-renderer:compileDebugKotlin`
   - 结果：FAIL
   - 主要原因：遗留 `TabRow` 相关引用仍在 binder/patch/factory 中，但对应类型/实现已缺失（如 `TabRowNodeProps`、`DeclarativeTabRowLayout` 等）。
   - 关键文件：`ContainerViewBinder.kt`、`NodeBindingPlan.kt`、`NodeBindingDiffer.kt`、`NodeViewBinderRegistry.kt`、`ViewNodeFactory.kt`、`ContainerNodePatchApplier.kt`
   - 对照验证：在独立临时 worktree（纯 `HEAD`）执行同命令结果为 `COMPILE_OK`，说明该问题来自当前未提交改动，而非已提交主干。

2. instrumentation UI 测试基线大面积失败。
   - 复现命令：`./gradlew :app:connectedDebugAndroidTest`
   - 结果：18 条中 17 条失败（报告：`app/build/outputs/androidTest-results/connected/debug/TEST-Pixel 4 XL - 13-_app-.xml`）
   - 失败模式高度集中在“滚动可见性/显示断言”，说明 UI 测试辅助策略脆弱（`DemoUiTestHelpers.kt:79-95,121-139`）。

## 4.2 P1（高优先级优化）

1. Overlay Host 存在重复实现。
   - `DialogOverlayHost` 与 `PopupOverlayHost` 的 commit/clear/activeEntries 逻辑高度重复（`DialogOverlayHost.kt`、`PopupOverlayHost.kt`），后续新增 overlay 类型时易复制粘贴。
2. `app` demo 目录仍然平铺。
   - `app/src/main/java/com/viewcompose` 下大量页面文件平铺（约 30 个），与“按模块/目录分类新增代码”的长期目标不一致（`WORKFLOW.md:75-95`）。
3. 多处大文件仍需继续拆分。
   - 例如 `ContainerViewBinder.kt`、`InputWidgetsDsl.kt`、`DemoFoundationsPage.kt`，认知负担较高，review 成本偏大。

## 4.3 P2（中期治理）

1. 主题/架构文档建议建立“单一状态源”与索引，减少重复维护成本。
2. 在 roadmap 文档增加“完成态门槛”字段（编译、单测、demo、UI 测试）并强制勾选。

---

## 5. 建议的整改顺序（可执行）

1. **清理当前工作区断裂改动（P0）**：先收敛 `TabRow` 遗留引用相关未提交变更，再进入后续功能迭代。
2. **恢复回归基线（P0）**：修复 UI 测试 helper 的滚动/可见性策略，再重跑 `connectedDebugAndroidTest`。
3. **收紧完成定义（P1）**：更新 `WORKFLOW.md`，增加“标记完成前必须通过的命令清单”。
4. **回填验证资产（P1）**：为近期标记完成但缺 demo/test 的组件补齐最小验证（至少 demo 场景 + 1 条 UI 测试）。
5. **消除重复实现（P1）**：抽象 overlay host 通用 commit/reconcile 模板，降低后续扩展成本。

---

## 6. 最终判断

当前项目已经具备较强的架构与提交纪律基础，但“质量闭环”尚未与“文档完成态”绑定。  
下一阶段的关键不是继续扩功能，而是先修复基线与门禁，让“完成”在文档、提交、代码、测试四个层面同时成立。
