# Node Props

## 1. 文档定位

本文档是 `NodeSpec/Props` 规范版，定义：

1. 当前模型边界
2. `NodeSpec` 与 `Props` 的角色分工
3. 新节点接入规则
4. 后续演进方向

历史长版见：

- [NODE_PROPS_FULL_2026-03-06.md](/Users/gzq/AndroidStudioProjects/UIFramework/docs/archive/NODE_PROPS_FULL_2026-03-06.md)

## 2. 当前模型结论

当前第一方高频节点已以 `NodeSpec` 为主链路，`Props` 角色已收敛为：

1. 兼容层
2. 扩展层（冷门/第三方扩展）

不再接受“第一方核心语义长期停留在动态 map”。

## 3. 角色边界

### 3.1 `NodeSpec`

负责：

1. 第一方组件字段语义
2. 字段级 diff/patch 的稳定输入
3. 编译期可读的参数结构

### 3.2 `Props`

负责：

1. 向后兼容
2. 扩展透传
3. 非核心临时能力过渡

约束：

1. 不新增第一方长期核心语义到 `Props`
2. 新实现优先 `NodeSpec`

## 4. 新节点接入清单

新增第一方节点时必须完成：

1. `NodeSpec` 数据结构定义
2. DSL 参数映射到 `NodeSpec`
3. renderer 绑定与 patch 路径
4. 覆盖样式变化、状态变化、交互变化测试
5. demo 场景验证

## 5. 与 Modifier/Theme 的分工

1. 组件语义字段进入 `NodeSpec`
2. 通用修饰进入 `Modifier`
3. 默认值由 `Theme -> Defaults` 解析后写入 `NodeSpec`

相关规范：

- [MODIFIER.md](/Users/gzq/AndroidStudioProjects/UIFramework/MODIFIER.md)
- [THEMING.md](/Users/gzq/AndroidStudioProjects/UIFramework/THEMING.md)

## 6. 当前风险与防线

风险：

1. 新能力为赶进度回流到 `Props` 动态字段
2. `NodeSpec` 字段增长后 patch 路径缺测试

防线：

1. 新字段必须带最小 patch 回归
2. 评审时把“是否应入 `NodeSpec`”作为必查项
3. 变更后同步更新本文件

## 7. 后续方向

1. 利用现有 `NodeSpec` 覆盖继续扩大“跳过更新”收益。
2. 对高频热点节点继续优化字段级 patch 粒度。
3. 保持 `Props` 轻量，不再回到“动态 map 主链路”。

总体路线见：

- [ROADMAP.md](/Users/gzq/AndroidStudioProjects/UIFramework/ROADMAP.md)
