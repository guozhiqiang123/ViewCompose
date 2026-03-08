# 状态系统升级执行计划（2026-03）

## 1. 基线

- 当前 `MutableState` 采用单值存储（`backingValue`）+ `==` 去抖。
- 当前观测机制是 `RuntimeObservation` 的直接读观察，无 snapshot 事务隔离。
- 当前无 `SnapshotMutationPolicy`、无可合并并发事务、无全局 snapshot id 管理。

## 2. 范围与约束

- 范围：`viewcompose-runtime` + `viewcompose-widget-core` 观测接入 + 文档/测试收口。
- 目标：`SnapshotMutationPolicy + MVCC snapshot + mutable snapshot apply/merge`。
- 迁移策略：硬切，不保留旧实现分支开关。
- 决策固定：
  - 冲突策略：Custom merge（无法合并则失败）
  - 写入策略：`state.value = x` 保留，内部 autocommit

## 3. 完成标准

1. `mutableStateOf` 支持 mutation policy，默认结构相等策略。
2. `MutableState` 基于 `StateRecord` 实现 MVCC。
3. 新增 `Snapshot` / `MutableSnapshot` 与全局 snapshot 管理。
4. 读写具备 snapshot 一致性；冲突 apply 支持 merge 与失败语义。
5. `RuntimeObservation` / `ComposerLite` / `DerivedState` 接入 snapshot 版本语义。
6. `qaQuick` 通过；关键里程碑补跑 `qaFull`（设备可用时）。

## 4. 实施 Checklist

- [x] Step 1 新增执行计划文档并首提
- [ ] Step 2 Runtime 状态内核重构（policy + MVCC）
- [ ] Step 3 Snapshot 引擎与事务模型
- [ ] Step 4 观察与重组链路接入
- [ ] Step 5 单测补齐（runtime + composition）
- [ ] Step 6 文档收口（ARCHITECTURE/PERFORMANCE/WORKFLOW + STATE_SNAPSHOT）

## 5. 提交记录

- `DONE` docs: add state snapshot execution plan

## 6. 阻塞记录

- 暂无
