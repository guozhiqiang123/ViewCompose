# Graphics 阻塞上下文（2026-03）

## 时间
- 首次记录：2026-03-11 17:55 (Asia/Shanghai)
- 最近更新：2026-03-11 18:34 (Asia/Shanghai)

## 当前分支与工作区
- branch: `main`
- graphics 相关最近提交：
  - `33c0e4a` docs: add graphics execution plan and blocker context
  - `badb6be` build: add graphics modules and purity guardrails
  - `f17e68b` feat: add graphics-core geometry paint path command and cache primitives
  - `3c6fdec` feat: add canvas node and draw modifier contracts in ui-contract
  - `e1ba923` feat: add graphics dsl canvas and draw modifier wrappers
  - `3b7264b` feat: integrate canvas node and draw modifier pipeline in renderer
  - `14ef741` feat: add android graphics interop bridge in host-android
  - `325b0e6` demo: add graphics showcase scenarios and visual regression coverage
  - `ff55a32` test: add graphics preview catalog and paparazzi baseline coverage

## 阻塞原因
- `qaFull`（Pixel 4 XL）失败，失败模式统一为 instrumentation `ActivityScenario` 无法进入 `RESUMED`：
  - 典型报错：`Activity never becomes requested state "[RESUMED]" (last lifecycle transition = "STOPPED")`
  - 命令：`ANDROID_SERIAL=98101FFBA003AE ./gradlew qaFull`
  - 现象：多条历史用例和本次新增 graphics 用例均同样失败，属于设备/UI 测试运行态不稳定，非单一 graphics 场景逻辑断言失败。

## 已验证事实
1. `./gradlew :viewcompose-renderer:testDebugUnitTest` 通过。
2. `./gradlew qaQuick` 通过。
3. `./gradlew qaPreview` 通过（缺失 baseline 已补齐并提交）。
4. `ANDROID_SERIAL=98101FFBA003AE ./gradlew qaFull` 在 Pixel 4 XL 上可启动，但持续出现 `ActivityScenario` 生命周期失败。

## 下一条恢复命令
```bash
adb logcat -c
ANDROID_SERIAL=98101FFBA003AE ./gradlew :app:connectedDebugAndroidTest --tests "com.viewcompose.DemoVisualUiTest.graphicsPage_*" --info --stacktrace
```
