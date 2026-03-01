# UI Testing Roadmap

## 1. 目标

本文档定义 demo 的 UI 测试策略。

当前单元测试已经覆盖了不少运行时和渲染逻辑，但还不能稳定发现下面这类真实 UI 问题：

1. 文案显示不全、被裁剪、被省略
2. 主题 token 映射错误，导致最终颜色不符合预期
3. demo 页面结构调整后，关键 benchmark 入口跑偏
4. 组件在真实 Activity/View 树里的测量与视觉表现和预期不一致

因此，后续 UI 测试的目标不是替代单元测试，而是补上“真实 Activity + 真实 View 树 + 真实主题切换”的回归校验。

## 2. 分层策略

### 2.1 结构断言

优先落地，成本最低，能最快定位当前 demo 里的问题。

覆盖：

1. 关键按钮和文本是否可见
2. 关键文本是否被 ellipsize 或裁剪
3. benchmark anchor 是否仍在稳定位置

### 2.2 主题断言

第二层直接检查真实 View 上的颜色值，而不是只验证 token 对象。

覆盖：

1. 页面标题是否跟随 Light/Dark mode 切换
2. 关键控件文本色、背景色是否命中主题 token
3. 局部 override 是否真正生效到 View

### 2.3 截图产物

先作为辅助诊断，而不是正式截图回归平台。

策略：

1. instrumentation 测试在关键场景产出截图
2. 截图用于排查“看起来不对但结构断言没报错”的问题
3. 后续如果场景稳定，再评估是否引入正式 screenshot regression

## 3. 当前测试范围

第一批 UI 测试优先覆盖高风险 demo 页面：

1. `Foundations`
   - benchmark 按钮是否被截断
   - Light/Dark 标题颜色是否正确
2. `Input`
   - benchmark 按钮和输入框是否完整可见
   - 带图标按钮是否被截断
3. `State`
   - benchmark anchor 是否稳定
4. `Diagnostics`
   - benchmark 刷新入口是否稳定

## 4. 当前技术方案

使用 instrumentation test，组合三类能力：

1. `ActivityScenario`
   - 启动目标 Activity
2. `Espresso`
   - 触发主题切换等简单交互
3. `UiAutomator`
   - 产出测试截图

辅助断言基于真实 `View` 树遍历：

1. 查找 `TextView`
2. 检查 `GlobalVisibleRect`
3. 检查 `Layout` 的 `ellipsize` / `ellipsisCount`
4. 检查 `currentTextColor`

## 5. 不做什么

当前阶段不做下面这些事：

1. 不引入重量级截图回归平台
2. 不试图覆盖所有 demo 页面
3. 不用截图像素比对替代结构断言
4. 不把 benchmark 测试和 UI 正确性测试混在同一个用例里

## 6. 后续扩展

当第一批 UI 测试稳定后，再继续扩展：

1. `Collections`
   - key reorder 后 item 文案和局部状态是否正常
2. `Interop`
   - `AndroidView` 显示和主题桥接
3. `Layout`
   - weight / wrap / nested surface 的可见性和布局边界
4. 局部主题 override 的视觉断言
5. 截图目录和 artifact 汇总

## 7. 结论

当前最合理的路线不是直接上截图平台，而是：

1. 先用 instrumentation 补结构断言
2. 再补主题颜色断言
3. 同时产出截图辅助排查

这样最适合当前这套 View-based 声明式框架，也最容易和框架层改动互相印证。
