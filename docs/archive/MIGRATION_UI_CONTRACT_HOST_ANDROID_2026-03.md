# ViewCompose 分层迁移说明（2026-03）

## 1. 迁移目标

本次为硬切迁移：将 `widget-core -> renderer` 直依赖收口为：

`runtime + ui-contract + widget-core + renderer(android) + host-android`

不提供旧 API 的 `@Deprecated` 兼容层。

## 2. 模块变化

1. 新增 `:viewcompose-ui-contract`（纯 Kotlin 契约层）。
2. 新增 `:viewcompose-host-android`（Android 宿主运行时与入口）。
3. `:viewcompose-widget-core` 已移除 `project(":viewcompose-renderer")` 依赖。
4. `:viewcompose-overlay-android` 不再依赖 renderer 资源。

## 3. API 导入路径迁移

### 3.1 宿主入口

1. `com.viewcompose.widget.core.setUiContent` -> `com.viewcompose.host.android.setUiContent`
2. `com.viewcompose.widget.core.renderInto` -> `com.viewcompose.host.android.renderInto`
3. `com.viewcompose.widget.core.RenderSession` -> `com.viewcompose.host.android.RenderSession`

### 3.2 Android 互操作

1. `UiTreeBuilder.AndroidView(...)` 改为从 `com.viewcompose.host.android` 导入。
2. `Modifier.nativeView(...)` 改为从 `com.viewcompose.host.android` 导入。

### 3.3 契约类型

`Modifier`、`VNode/NodeSpec`、layout 枚举、collection/state 协议已收敛在 `com.viewcompose.ui.*`（`viewcompose-ui-contract`）。

## 4. 行为与约束

1. `setUiContent(...)` 仍默认自动注入 `UiEnvironment`、`LifecycleOwner`、`ViewModelStoreOwner`。
2. overlay 锚点标记不再依赖 renderer `R.id`，改为契约常量 `OVERLAY_ANCHOR_TAG_KEY`。
3. core 渲染引擎改为接口注册装配（`installCoreRenderEngine`），不再依赖反射加载 host 引擎实现。
4. guard test 已生效：
   - `widget-core` 主源码禁止 `com.viewcompose.renderer.*` import。
   - `ui-contract` 主源码禁止 `android.*` / `androidx.*` import。

## 5. 下游迁移检查清单

1. 更新模块依赖：增加 `:viewcompose-host-android`、`:viewcompose-ui-contract`。
2. 批量替换宿主入口与 interop 导入路径。
3. 运行 `./gradlew qaQuick` 验证编译与单测。
