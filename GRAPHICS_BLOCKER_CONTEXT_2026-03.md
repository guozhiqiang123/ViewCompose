# Graphics 阻塞上下文（2026-03）

## 时间
- 首次记录：TBD

## 当前分支与工作区
- branch: `main`
- graphics 相关最近提交：TBD

## 阻塞原因
- TBD

## 已验证事实
1. TBD

## 下一条恢复命令
```bash
adb -s 98101FFBA003AE shell input keyevent KEYCODE_WAKEUP
adb -s 98101FFBA003AE shell wm dismiss-keyguard
adb -s 98101FFBA003AE shell input keyevent 82
adb -s 98101FFBA003AE shell am start -W -a android.intent.action.MAIN -c android.intent.category.HOME
ANDROID_SERIAL=98101FFBA003AE ./gradlew qaFull
```
