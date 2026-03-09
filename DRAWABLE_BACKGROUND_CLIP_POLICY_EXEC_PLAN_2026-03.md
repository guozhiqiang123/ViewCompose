# Drawable 背景圆角裁剪策略调整执行计划（2026-03）

## 1. 背景与目标

当前策略：`backgroundDrawableRes + cornerRadius` 仅在显式 `.clip()` 时裁剪内容。  
目标策略：`backgroundDrawableRes + cornerRadius` 默认自动裁剪；`.clip()` 继续作为通用强制裁剪开关。

## 2. 约束与验收

1. 仅调整 drawable 背景路径，不改变纯 `backgroundColor` 路径的既有裁剪语义。
2. 仅在存在有效 `cornerRadius` 时自动裁剪。
3. 文档先更新，代码后落地。
4. `qaQuick` 必跑；补充一条可见回归验证。

## 3. 执行步骤

- [x] Step 1 新增执行文档并提交（本文件）。
- [ ] Step 2 更新规范文档：声明 `backgroundDrawableRes + cornerRadius` 自动裁剪语义。
- [ ] Step 3 renderer 实现调整：按新触发条件设置 `clipToOutline`。
- [ ] Step 4 demo 与 UI 测试更新：去掉示例中的显式 `.clip()` 并补充断言。
- [ ] Step 5 回归：`qaQuick` + 目标 UI 用例验证。

## 4. 提交记录

1. （待补充）

## 5. 阻塞记录

当前无阻塞。

