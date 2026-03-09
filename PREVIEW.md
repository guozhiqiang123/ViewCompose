# ViewCompose Preview

`viewcompose-preview` 模块提供两类开发态预览能力：

- Android Studio Compose Preview：通过 `ViewComposePreviewHost` 桥接渲染 ViewCompose DSL。
- Paparazzi 快照回归：消费同一份 `PreviewCatalog`，避免 Preview 与截图测试双维护。

## Studio Preview

1. 在 Android Studio 打开 `viewcompose-preview` 模块中的以下入口：
   - `com.viewcompose.preview.shell.PreviewShellsKt`
   - `com.viewcompose.preview.catalog.ui.CatalogPreviewsKt`
2. 直接使用 IDE 的 Preview 面板查看 Light/Dark、Phone/Tablet、分域组件预览。

## Snapshot Regression

执行模块级快照验证：

```bash
./gradlew :viewcompose-preview:verifyPaparazziDebug
```

快照基线与差异报告输出在：

`viewcompose-preview/build/reports/paparazzi/`

## Overlay 预览策略

Preview 场景下 Overlay 采用静态内容模拟，不渲染真实窗口层（Dialog/Popup/BottomSheet 的真实行为由 instrumentation 覆盖）。
