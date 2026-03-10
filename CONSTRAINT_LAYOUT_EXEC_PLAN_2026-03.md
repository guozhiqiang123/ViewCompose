# ConstraintLayout 组件模块化执行计划（2026-03）

## 基线
- 当前框架缺少 `ConstraintLayout` 映射节点与 DSL，业务侧只能使用 `Row/Column/Box/Flow` 等布局。
- `ui-contract` 尚未提供约束布局的节点契约、子项约束元数据与 decoupled `ConstraintSet` 结构。
- renderer 尚未接入 `androidx.constraintlayout.widget.ConstraintLayout` 的工厂、binder、patch、layout params 翻译链路。
- demo/preview/UI 回归中没有约束布局专用场景与自动化锚点。

## 本轮范围与完成标准
- 范围固定：
  - 新增模块 `:viewcompose-widget-constraintlayout`（包根 `com.viewcompose.widget.constraintlayout`）。
  - 新增 Compose 对齐 API + 简洁短写 API：
    - `ConstraintLayout + createRef(s) + constrainAs`
    - `Modifier.constrain("id") { ... }`
  - 首版覆盖 anchors / dimension / bias / baseline / guideline / barrier / chain / decoupled `ConstraintSet`。
  - renderer 主链完成 Android `ConstraintLayout` 映射与动态 patch 生效。
  - demo / preview / docs / tests 同步收口。
- 完成标准：
  - 新模块接入 `modulePackageRoots`、`verifyAndroidModuleNamespaces`、`qaQuick`。
  - `ui-contract` 与 renderer 全链路通过 `ConstraintLayout` 节点渲染布局。
  - inline 约束与 decoupled set 冲突时 inline 优先并输出 warning。
  - 约束 dimension 优先于 `Modifier.width/height/size`。
  - `qaQuick` 每步通过；里程碑与最终收口执行 `qaFull`（仅 Pixel 4 XL）。

## 设备与阻塞约束
- 设备门禁：只使用 Pixel 4 XL。
- 若 Pixel 4 XL 不可用，立即写入 `CONSTRAINT_LAYOUT_BLOCKER_CONTEXT_2026-03.md`，记录：
  - 时间、已完成 step/commit、当前分支、阻塞原因、已验证事实、恢复命令。

## Checklist
- [x] Step 1: 新增执行文档并提交。
- [x] Step 2: 新增模块并接入构建拓扑/守卫/qa 任务。
- [x] Step 3: `ui-contract` 约束布局契约与 modifier 元数据落地。
- [x] Step 4: `viewcompose-widget-constraintlayout` DSL 与 scope 落地。
- [ ] Step 5: renderer 映射、binder/patch/layout params、ConstraintSet 应用引擎接入。
- [ ] Step 6: demo + preview + docs 同步。
- [ ] Step 7: unit/instrumentation 回归补齐与门禁收口。
- [ ] Step 8: 完结归档。

## 提交记录
1. `docs: add constraint layout execution plan`
2. `build: add constraintlayout widget module and wire quality guards`
3. `feat: add constraint layout contracts and modifier metadata primitives`
4. `feat: add constraint layout widget dsl with compose-aligned and concise APIs`

## 阻塞记录
- 暂无。
