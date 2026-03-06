package com.gzq.uiframework

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.gzq.uiframework.image.coil.CoilRemoteImageLoader
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.systemBarsInsetsPadding
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.IconButton
import com.gzq.uiframework.widget.core.ProvideRemoteImageLoader
import com.gzq.uiframework.widget.core.Scaffold
import com.gzq.uiframework.widget.core.SideEffect
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.TopAppBar
import com.gzq.uiframework.widget.core.TopAppBarDefaults
import com.gzq.uiframework.widget.core.UiEnvironment
import com.gzq.uiframework.widget.core.UiTheme
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember

internal fun UiTreeBuilder.DemoSubPageScaffold(
    root: ViewGroup,
    title: String,
    content: (UiTreeBuilder) -> Unit,
) {
    val themeModeState = remember { mutableStateOf(DemoThemeSession.mode) }
    val remoteImageLoader = remember { CoilRemoteImageLoader(root.context.applicationContext) }
    val activity = root.context as? AppCompatActivity
    UiEnvironment(androidContext = root.context) {
        val resolvedTheme = DemoThemeTokens.resolve(
            mode = themeModeState.value,
            context = root.context,
        )
        ProvideRemoteImageLoader(remoteImageLoader) {
            UiTheme(tokens = resolvedTheme) {
                SideEffect {
                    activity?.title = "$title · ${DemoThemeTokens.modeLabel(themeModeState.value, root.context)}"
                }
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = title,
                            navigationIcon = {
                                IconButton(
                                    icon = ImageSource.Resource(R.drawable.ic_arrow_back),
                                    contentDescription = "返回",
                                    onClick = { activity?.finish() },
                                    tint = TopAppBarDefaults.titleColor(),
                                )
                            },
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsInsetsPadding()
                        .backgroundColor(Theme.colors.background),
                ) {
                    Column(
                        spacing = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                    ) {
                        content(this)
                    }
                }
            }
        }
    }
}
