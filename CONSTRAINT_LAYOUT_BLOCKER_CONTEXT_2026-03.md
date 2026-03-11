# ConstraintLayout 阻塞上下文（2026-03，已关闭）

## 时间
- 首次记录：2026-03-11
- 关闭时间：2026-03-11

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
4. 重新执行前将设备拉回前台 Home（唤醒 + dismiss keyguard + HOME）。
5. 复跑通过：
   - `ANDROID_SERIAL=98101FFBA003AE ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.viewcompose.ComponentFamilySmokeUiTest#keyComponentFamilies_haveVisibleSmokeAnchors`
   - `ANDROID_SERIAL=98101FFBA003AE ./gradlew qaFull`

## 阻塞现象
1. 历史现象：instrumentation 在设备非前台可交互状态下会长时间停留在 `0/n`。
2. 复验结果：将设备恢复到可交互前台状态后，原阻塞用例与 `qaFull` 均可稳定完成。

## 下一条恢复命令
```bash
adb -s 98101FFBA003AE shell input keyevent KEYCODE_WAKEUP
adb -s 98101FFBA003AE shell wm dismiss-keyguard
adb -s 98101FFBA003AE shell input keyevent 82
adb -s 98101FFBA003AE shell am start -W -a android.intent.action.MAIN -c android.intent.category.HOME
ANDROID_SERIAL=98101FFBA003AE ./gradlew qaFull
```
