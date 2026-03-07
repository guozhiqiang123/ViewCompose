# Environment 一致性收口执行计划（2026-03）

## 1. Baseline

- 当前环境语义主入口：`ui-widget-core/context/Environment.kt` + `AndroidEnvironmentBridge`
- 当前问题：
  - 宿主入口未默认注入 `UiEnvironment`，demo 侧存在重复手动包裹
  - renderer 存在 2 处自行 density/dp 转换（`DeclarativeNavigationBarLayout`、`DeclarativeSegmentedControlLayout`）

## 2. Scope

本轮只收口环境语义（density/locale/layoutDirection）与 renderer 尺寸换算入口，不涉及 runtime `ThreadLocal` 机制重构。

## 3. Acceptance Criteria

1. `setUiContent/createUiContent` 默认注入 `UiEnvironment(androidContext = root.context)`。
2. demo 去掉冗余手动包裹，仅保留 1 个局部覆盖示例。
3. renderer 不再保留私有 `density + dpToPx` 写法，统一走 renderer 工具。
4. 新增静态约束测试可阻止 renderer 回归到私有 density/dpToPx。
5. `ARCHITECTURE.md` 与 `WORKFLOW.md` 明确写入边界规则。

## 4. Checklist

- [x] Step 1: 新增执行计划文档并首提
- [x] Step 2: 宿主入口默认注入 `UiEnvironment`
- [x] Step 3: demo 去冗余并保留 1 个覆盖示例
- [x] Step 4: renderer 尺寸转换统一工具化
- [x] Step 5: 新增 renderer 环境约束防回归测试
- [ ] Step 6: 架构与流程文档写入核心约束
- [ ] Step 7: 收口归档并更新 archive 索引

## 5. Commit Log

- `docs: add environment unification execution plan`
- `feat: auto-provide UiEnvironment in host bridge entrypoints`
- `refactor: remove redundant demo UiEnvironment wrappers and keep one override sample`
- `refactor: unify renderer density conversion via shared dimension utilities`
- `test: add renderer environment convention guard`

## 6. Blockers

- 暂无
