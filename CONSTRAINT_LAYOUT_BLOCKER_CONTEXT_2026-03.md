# ConstraintLayout 阻塞上下文（2026-03）

## 时间
- 2026-03-11

## 当前分支与工作区
- branch: `main`
- 相关提交：
  1. `43a19da` docs: add constraint layout api parity execution plan
  2. `892513c` feat: extend constraintlayout contracts and dsl for advanced constraints
  3. `8eccb72` feat: complete advanced constraintlayout renderer constraint application

## 已完成事实
1. `qaQuick` 通过。
2. `:viewcompose-ui-contract:test`、`:viewcompose-widget-constraintlayout:test`、`:viewcompose-renderer:test` 通过。
3. 设备 `Pixel 4 XL (serial: 98101FFBA003AE)` 在线可用。

## 阻塞现象
1. `ANDROID_SERIAL=98101FFBA003AE ./gradlew qaFull` 在 instrumentation 起跑后长时间停留在 `0/46`。
2. 单测复现命令 `:app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.viewcompose.ComponentFamilySmokeUiTest#keyComponentFamilies_haveVisibleSmokeAnchors` 同样长时间停留在 `0/1`。
3. 手动停止后，任务返回 `Instrumentation run failed due to Process crashed`，报告路径：
   - `app/build/reports/androidTests/connected/debug/index.html`

## 下一条恢复命令
```bash
ANDROID_SERIAL=98101FFBA003AE ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.viewcompose.ComponentFamilySmokeUiTest#keyComponentFamilies_haveVisibleSmokeAnchors --info
```

