package com.viewcompose

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.viewcompose.image.coil.CoilRemoteImageLoader
import com.viewcompose.renderer.modifier.Modifier
import com.viewcompose.renderer.modifier.backgroundColor
import com.viewcompose.renderer.modifier.fillMaxSize
import com.viewcompose.renderer.node.ImageSource
import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.widget.core.HorizontalPager
import com.viewcompose.widget.core.NavigationBar
import com.viewcompose.widget.core.ProvideRemoteImageLoader
import com.viewcompose.widget.core.Scaffold
import com.viewcompose.widget.core.SideEffect
import com.viewcompose.widget.core.Theme
import com.viewcompose.widget.core.UiTheme
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.remember

internal fun UiTreeBuilder.DemoHomeScaffold(
    root: ViewGroup,
) {
    val themeModeState = remember { mutableStateOf(DemoThemeSession.mode) }
    val remoteImageLoader = remember { CoilRemoteImageLoader(root.context.applicationContext) }
    val activity = root.context as? AppCompatActivity
    val resolvedTheme = DemoThemeTokens.resolve(
        mode = themeModeState.value,
        context = root.context,
    )
    ProvideRemoteImageLoader(remoteImageLoader) {
        UiTheme(tokens = resolvedTheme) {
            val navIndex = remember { mutableStateOf(0) }
            val diagnosticsPageState = remember { mutableStateOf(0) }
            SideEffect {
                activity?.title = "UIFramework · ${DemoThemeTokens.modeLabel(themeModeState.value, root.context)}"
            }
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        selectedIndex = navIndex.value,
                        onItemSelected = { navIndex.value = it },
                    ) {
                        Item(label = "目录", icon = ImageSource.Resource(R.drawable.demo_media_icon))
                        Item(label = "诊断", icon = ImageSource.Resource(R.drawable.demo_media_icon))
                        Item(label = "设置", icon = ImageSource.Resource(R.drawable.demo_media_icon))
                        Item(label = "关于", icon = ImageSource.Resource(R.drawable.demo_media_icon))
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .backgroundColor(Theme.colors.background),
            ) {
                HorizontalPager(
                    currentPage = navIndex.value,
                    onPageChanged = { navIndex.value = it },
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Page(key = "catalog") { DemoCatalogPage(root) }
                    Page(key = "diagnostics") { DiagnosticsPage(diagnosticsPageState) }
                    Page(key = "settings") { SettingsPage(themeModeState, root) }
                    Page(key = "about") { AboutPage() }
                }
            }
        }
    }
}
