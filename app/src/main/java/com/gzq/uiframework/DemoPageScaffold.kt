package com.gzq.uiframework

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.gzq.uiframework.image.coil.CoilRemoteImageLoader
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.ColumnScope
import com.gzq.uiframework.widget.core.Environment
import com.gzq.uiframework.widget.core.ProvideRemoteImageLoader
import com.gzq.uiframework.widget.core.SegmentedControl
import com.gzq.uiframework.widget.core.SegmentedControlSize
import com.gzq.uiframework.widget.core.SideEffect
import com.gzq.uiframework.widget.core.SurfaceDefaults
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.UiEnvironment
import com.gzq.uiframework.widget.core.UiTextStyle
import com.gzq.uiframework.widget.core.UiTheme
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.sp
import java.util.Locale

internal fun UiTreeBuilder.DemoPageScaffold(
    root: ViewGroup,
    title: String,
    subtitle: String,
    showBackButton: Boolean,
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
                val environmentLabel = "Env: ${Environment.localeTags.firstOrNull() ?: "und"} · " +
                    "${Environment.layoutDirection.name} · " +
                    "${"%.2f".format(Locale.US, Environment.density.density)}x · " +
                    DemoThemeTokens.modeLabel(themeModeState.value, root.context)
                SideEffect {
                    activity?.title = "$title · ${DemoThemeTokens.modeLabel(themeModeState.value, root.context)}"
                }
                Column(
                    spacing = 10.dp,
                    modifier = Modifier
                        .fillMaxSize()
                        .backgroundColor(Theme.colors.background)
                        .padding(24.dp),
                ) {
                    DemoPageHeader(
                        title = title,
                        subtitle = subtitle,
                        environmentLabel = environmentLabel,
                        showBackButton = showBackButton,
                        onBack = { activity?.finish() },
                    )
                    ThemeModeSection(
                        themeModeState = themeModeState,
                    )
                    Column(
                        spacing = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .margin(top = 8.dp),
                    ) {
                        content(this)
                    }
                }
            }
        }
    }
}

private fun ColumnScope.DemoPageHeader(
    title: String,
    subtitle: String,
    environmentLabel: String,
    showBackButton: Boolean,
    onBack: () -> Unit,
) {
    if (showBackButton) {
        Button(
            text = "Back to catalog",
            onClick = onBack,
            modifier = Modifier.margin(bottom = 4.dp),
        )
    }
    Text(
        text = title,
        style = UiTextStyle(fontSizeSp = 30.sp),
    )
    Text(
        text = subtitle,
        style = UiTextStyle(fontSizeSp = 14.sp),
        color = TextDefaults.secondaryColor(),
    )
    Text(
        text = environmentLabel,
        style = UiTextStyle(fontSizeSp = 12.sp),
        color = TextDefaults.secondaryColor(),
        modifier = Modifier.padding(vertical = 4.dp),
    )
}

private fun ColumnScope.ThemeModeSection(
    themeModeState: com.gzq.uiframework.runtime.MutableState<DemoThemeMode>,
) {
    Column(
        spacing = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .backgroundColor(SurfaceDefaults.variantBackgroundColor())
            .cornerRadius(SurfaceDefaults.cardCornerRadius())
            .padding(12.dp),
    ) {
        Text(
            text = "Demo Theme",
            style = UiTextStyle(fontSizeSp = 14.sp),
        )
        SegmentedControl(
            items = listOf("System", "Light", "Dark"),
            selectedIndex = themeModeState.value.ordinal,
            onSelectionChange = { index ->
                val mode = DemoThemeMode.entries[index]
                DemoThemeSession.mode = mode
                themeModeState.value = mode
            },
            size = SegmentedControlSize.Medium,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
