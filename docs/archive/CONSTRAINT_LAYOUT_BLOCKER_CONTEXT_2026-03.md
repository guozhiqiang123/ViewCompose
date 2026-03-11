# ConstraintLayout 阻塞上下文（2026-03，重新打开）

## 时间
- 首次记录：2026-03-11
- 首次关闭：2026-03-11
- 重新打开：2026-03-11
- 再次关闭：2026-03-11

## 当前分支与工作区
- branch: `main`
- 当前范围相关提交：
  1. `8ab2f2b` docs: add constraintlayout virtual helpers execution plan
  2. `5d260da` feat: add virtual helper specs to constraintlayout contract
  3. `7ed27d7` feat: add constraintlayout dsl apis for virtual helpers
  4. `4463404` feat: add renderer support for constraintlayout virtual helpers
  5. `005f8f5` test: add virtual helper demo anchors and ui regression coverage

## 已验证事实
1. `qaQuick` 通过。
2. `:viewcompose-ui-contract:test`、`:viewcompose-widget-constraintlayout:test`、`:viewcompose-renderer:test` 通过。
3. 设备 `Pixel 4 XL (serial: 98101FFBA003AE)` 在线可用。
4. 已执行设备恢复动作（唤醒 + dismiss keyguard + HOME）。
5. instrumentation 仍失败：
   - `ANDROID_SERIAL=98101FFBA003AE ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.viewcompose.ComponentFamilySmokeUiTest#keyComponentFamilies_haveVisibleSmokeAnchors`
6. 失败模式：
   - `Activity never becomes requested state "[RESUMED]" (last lifecycle transition = "STOPPED")`
   - `Instrumentation run failed due to Process crashed`
7. 参考日志：
   - `app/build/outputs/androidTest-results/connected/debug/Pixel 4 XL - 13/testlog/test-results.log`
   - `app/build/outputs/androidTest-results/connected/debug/Pixel 4 XL - 13/logcat-com.viewcompose.ComponentFamilySmokeUiTest-keyComponentFamilies_haveVisibleSmokeAnchors.txt`
8. 复跑恢复结果（2026-03-11）：
   - `ANDROID_SERIAL=98101FFBA003AE ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.viewcompose.ComponentFamilySmokeUiTest#keyComponentFamilies_haveVisibleSmokeAnchors` 通过。
   - `ANDROID_SERIAL=98101FFBA003AE ./gradlew qaFull` 通过。

## 阻塞原因
已解除。当前无阻塞，文档转归档保留历史追溯。

## 下一条恢复命令
```bash
adb -s 98101FFBA003AE shell input keyevent KEYCODE_WAKEUP
adb -s 98101FFBA003AE shell wm dismiss-keyguard
adb -s 98101FFBA003AE shell input keyevent 82
adb -s 98101FFBA003AE shell am start -W -a android.intent.action.MAIN -c android.intent.category.HOME
ANDROID_SERIAL=98101FFBA003AE ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.viewcompose.ComponentFamilySmokeUiTest#keyComponentFamilies_haveVisibleSmokeAnchors
ANDROID_SERIAL=98101FFBA003AE ./gradlew qaFull
```
