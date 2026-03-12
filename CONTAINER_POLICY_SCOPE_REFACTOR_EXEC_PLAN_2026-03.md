# P0：容器策略 API 作用域收口执行计划（2026-03）

## 1. 基线

- 当前存在 3 个全局 Modifier API：
  - `Modifier.lazyContainerReuse(...)`
  - `Modifier.lazyContainerMotion(...)`
  - `Modifier.focusFollowKeyboard(...)`
- 上述 API 会出现在所有组件上，但只有部分容器生效，形成“可调用但可能无效”的语义漂移。
- 当前 renderer 通过 `ContainerModifierPolicyResolver + ModifierContainerPolicyApplier` 从 `node.modifier` 读取策略。

## 2. 本轮范围

- 仅处理 P0：容器策略 API 作用域收口。
- 迁移策略：硬切（删除旧 Modifier API，不保留兼容层）。
- 参数模型：双策略对象。
- 焦点跟随范围：仅垂直容器暴露。

## 3. 完成标准

1. 新增公开策略模型：
   - `CollectionReusePolicy(sharePool)`
   - `CollectionMotionPolicy(disableItemAnimator, animateInsert, animateRemove, animateMove, animateChange)`
2. 5 个 Recycler-backed 容器（LazyColumn/LazyRow/LazyVerticalGrid/HorizontalPager/VerticalPager）暴露并生效 `reusePolicy + motionPolicy`。
3. 4 个垂直容器（LazyColumn/LazyVerticalGrid/VerticalPager/ScrollableColumn）暴露并生效 `focusFollowKeyboard`。
4. 删除全局旧 Modifier API 与 renderer modifier 策略解析链路。
5. demo、测试、文档同步收口，`qaQuick` 通过；里程碑与最终各执行一次 `qaFull`（Pixel 4 XL 可用时）。

## 4. 实施 Checklist

- [x] Step 1 新增执行计划文档并首提
- [x] Step 2 新增双策略模型并删除旧 Modifier policy API/元素
- [x] Step 3 容器 DSL 签名硬切（含垂直 focusFollowKeyboard 范围控制）
- [x] Step 4 NodeSpec + renderer 主链从 modifier 解析改为 spec 读取
- [ ] Step 5 demo 与测试迁移
- [ ] Step 6 文档收口（MODIFIER/ARCHITECTURE/PERFORMANCE/WORKFLOW）
- [ ] Step 7 收口归档执行计划到 `docs/archive/`

## 5. 提交记录

1. `docs: add container policy scope refactor execution plan (p0 hard-cut)`
2. `feat: add collection reuse and motion policies to container specs`
3. `refactor: hard-cut container policy modifiers to spec-driven container parameters`

## 6. 阻塞记录

- 暂无。
