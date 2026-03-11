# ConstraintLayout Demo API Coverage (2026-03)

## 基线
- 覆盖口径：函数级全覆盖（每个业务 API 至少一个 demo 场景 + 一个 testTag 锚点）。
- 覆盖范围：
  - `ConstraintLayoutScope` 入口
  - `ConstraintConstrainScope` 约束函数与关键维度属性
  - `constraintSet {}` / `ConstraintSetBuilder` 入口

## API -> Demo 映射
| API | Demo 场景 | 锚点 testTag | 人工验证动作 | 预期 |
|---|---|---|---|---|
| `UiTreeBuilder.ConstraintLayout` | Constraint 基础锚点 | `layouts_constraint_basic_container` | 进入约束页 | 容器稳定渲染 |
| `Modifier.constrainAs(ref)` | Constraint 基础锚点 | `layouts_constraint_basic_badge` | 观察 badge 位置 | 位于右侧偏置位置 |
| `Modifier.constrain("id")` | Decoupled ConstraintSet / Virtual Helpers | `layouts_constraint_set_marker` | 切换布局模式 | marker 位置变化 |
| `createRef/createRefs` | 多数约束场景 | `layouts_constraint_anchor_advanced_container` | 切到 Anchor Advanced | 引用型布局稳定 |
| `constraintSet {}` | Decoupled ConstraintSet | `layouts_constraint_set_toggle` | 点击切换 | 约束集即时生效 |
| `createGuidelineFromStart(fraction)` | Guideline + Barrier | `layouts_constraint_helpers_container` | 长短文案切换 | marker 仍稳定 |
| `createGuidelineFromEnd/Top/Bottom` (offset/fraction) | Guideline + Barrier Full | `layouts_constraint_helpers_full_toggle` | offset/fraction 切换 | 布局分区与 marker 同步变化 |
| `createStart/End/Top/BottomBarrier` | Guideline + Barrier / Full | `layouts_constraint_helpers_full_marker` | 点击 Full toggle | marker 随 barrier 位移 |
| `createFlow` | Virtual Helpers | `layouts_constraint_virtual_container` | A/B 模式切换 | Flow 排布列数变化 |
| `createGroup` | Virtual Helpers | `layouts_constraint_virtual_group_member` | A/B 模式切换 | group member `VISIBLE/GONE` 切换 |
| `createLayer` | Virtual Helpers | `layouts_constraint_virtual_chip_a` | A/B 模式切换 | chip A 位置/变换变化 |
| `createPlaceholder` | Virtual Helpers | `layouts_constraint_virtual_status` | A/B 模式切换 | Placeholder 承载对象切换 |
| `createHorizontalChain` | Chain 编排 | `layouts_constraint_chain_container` | 观察 A/B/C 顺序 | 横向顺序稳定 |
| `createVerticalChain(weights+bias+style)` | Vertical Chain | `layouts_constraint_vertical_chain_toggle` | Packed/SpreadInside 切换 | 中间项纵向位置变化 |
| `ConstraintSetBuilder.createHorizontalChain/createVerticalChain` | ConstraintSet Helper Mirror | `layouts_constraint_set_helpers_toggle` | A/B helper-set 切换 | marker 位置重算 |
| `ConstraintSetBuilder.createEndBarrier/createTopBarrier` | ConstraintSet Helper Mirror | `layouts_constraint_set_helpers_marker` | A/B helper-set 切换 | marker 位移 |
| `startToStart/startToEnd/endToStart/endToEnd/topToTop/topToBottom/bottomToBottom` | 基础锚点/Helper 场景 | `layouts_constraint_basic_container` | 观察布局 | 锚点关系正确 |
| `bottomToTop` | Anchor Advanced | `layouts_constraint_anchor_advanced_status` | 观察 `bottomToTop` 标记块 | 位于 target 上方 |
| `baselineToBaseline/baselineToTop/baselineToBottom` | Anchor Advanced | `layouts_constraint_anchor_advanced_baseline` | 观察基线文本组 | 基线关系符合文案 |
| `centerHorizontallyTo/centerVerticallyTo` | Anchor Advanced | `layouts_constraint_anchor_advanced_container` | 观察 `center*` 卡片 | 居中于父容器 |
| `circular` | Anchor Advanced | `layouts_constraint_anchor_advanced_circle` | 观察 circular 节点 | 围绕中心节点摆放 |
| `width/height + Fill/Fixed/Wrap` | 基础锚点/Chain/Dimension | `layouts_constraint_dimension_advanced_container` | 切换尺寸模式 | 节点宽高按策略变化 |
| `widthMin/widthMax/widthPercent` | Dimension Advanced | `layouts_constraint_dimension_advanced_status` | 点击尺寸 toggle | 文案与宽度同步变化 |
| `heightMin/heightMax/heightPercent` | Dimension Advanced | `layouts_constraint_dimension_advanced_status` | 点击尺寸 toggle | 文案与高度同步变化 |
| `constrainedWidth/constrainedHeight` | Dimension Advanced | `layouts_constraint_dimension_advanced_container` | 点击尺寸 toggle | 约束上限/下限生效 |
| `dimensionRatio` | Dimension Advanced | `layouts_constraint_dimension_advanced_ratio` | 点击尺寸 toggle | 比例容器形态变化 |

## 人工验证建议路径
1. `Layouts -> 约束`，先验证旧场景（基础/helper/chain/set/virtual）无回退。
2. 进入 `Anchor Advanced`，核对 `baseline*`、`circular`、`center*`、`bottomToTop`。
3. 进入 `Dimension Advanced`，点击 toggle，观察 ratio 卡片和状态文案。
4. 进入 `Guideline + Barrier Full` 和 `Vertical Chain`，确认 toggle 后 marker 与链布局变化。
5. 进入 `ConstraintSet Helper Mirror`，确认 A/B helper-set 切换有明确位移反馈。
