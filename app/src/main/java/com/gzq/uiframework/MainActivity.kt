package com.gzq.uiframework

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.Visibility
import com.gzq.uiframework.renderer.modifier.alpha
import com.gzq.uiframework.renderer.modifier.align
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.clickable
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.offset
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.textColor
import com.gzq.uiframework.renderer.modifier.visibility
import com.gzq.uiframework.renderer.modifier.width
import com.gzq.uiframework.renderer.modifier.zIndex
import com.gzq.uiframework.runtime.derivedStateOf
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.AndroidView
import com.gzq.uiframework.widget.core.Box
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.Divider
import com.gzq.uiframework.widget.core.DisposableEffect
import com.gzq.uiframework.widget.core.dp
import com.gzq.uiframework.widget.core.Environment
import com.gzq.uiframework.widget.core.FlexibleSpacer
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.SideEffect
import com.gzq.uiframework.widget.core.SurfaceDefaults
import com.gzq.uiframework.widget.core.Theme
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.UiEnvironment
import com.gzq.uiframework.widget.core.UiTheme
import com.gzq.uiframework.widget.core.key
import com.gzq.uiframework.widget.core.produceState
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.renderInto
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val reversedState = mutableStateOf(false)
    private val alternateLabelsState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val root = findViewById<android.view.ViewGroup>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        renderInto(
            container = root,
            debug = true,
            debugTag = "UIFrameworkSample",
        ) {
            val clickCountState = remember { mutableStateOf(0) }
            val textToggleState = remember { mutableStateOf(false) }
            val transientPanelVisibleState = remember { mutableStateOf(true) }
            val clickSummaryState = remember {
                derivedStateOf {
                    val clickCount = clickCountState.value
                    if (clickCount == 0) {
                        "No clicks yet"
                    } else if (clickCount % 2 == 0) {
                        "Even clicks: $clickCount"
                    } else {
                        "Odd clicks: $clickCount"
                    }
                }
            }
            val listOrderState = produceState(
                initialValue = "List order: A-B-C",
                reversedState.value,
            ) {
                value = if (reversedState.value) {
                    "List order: C-B-A"
                } else {
                    "List order: A-B-C"
                }
                null
            }
            SideEffect {
                title = "UIFramework - ${clickSummaryState.value}"
            }
            DisposableEffect {
                {
                    title = "UIFramework"
                }
            }
            UiEnvironment(androidContext = root.context) {
                val density = Environment.density
                val pagePadding = 24.dp
                val blockPadding = 8.dp
                val cardPadding = 12.dp
                val boxHeight = 72.dp
                val listHeight = 220.dp
                val environmentLabel = "Env: ${Environment.localeTags.firstOrNull() ?: "und"} · " +
                    "${Environment.layoutDirection.name} · " +
                    "${"%.2f".format(Locale.US, density.density)}x"
                UiTheme(
                    androidContext = root.context,
                ) {
                    Column(
                        modifier = Modifier.Empty
                            .backgroundColor(Theme.colors.background)
                            .padding(pagePadding),
                    ) {
                    Text(
                        text = "UIFramework",
                        style = TextDefaults.titleStyle(),
                        modifier = Modifier.Empty
                            .backgroundColor(SurfaceDefaults.backgroundColor())
                            .padding(blockPadding),
                    )
                    Text(
                        text = "Declarative UI on Android Views",
                        modifier = Modifier.Empty
                            .textColor(TextDefaults.secondaryColor())
                            .clickable {
                                textToggleState.value = !textToggleState.value
                            }
                            .padding(blockPadding),
                        )
                    Text(
                        text = environmentLabel,
                        modifier = Modifier.Empty
                            .textColor(TextDefaults.secondaryColor())
                            .padding(blockPadding),
                    )
                    Text(
                        text = if (textToggleState.value) {
                            "Text modifier click: ON"
                        } else {
                            "Text modifier click: OFF"
                        },
                        modifier = Modifier.Empty.padding(
                            horizontal = blockPadding,
                            vertical = 4.dp,
                        ),
                    )
                    Text(text = "Clicks: ${clickCountState.value}")
                    Text(
                        text = clickSummaryState.value,
                        modifier = Modifier.Empty
                            .textColor(TextDefaults.secondaryColor())
                            .alpha(0.7f)
                            .padding(blockPadding),
                    )
                    Text(
                        text = listOrderState.value,
                        modifier = Modifier.Empty.padding(blockPadding),
                    )
                    Divider(
                        thickness = 1,
                        modifier = Modifier.Empty.margin(vertical = blockPadding),
                    )
                    Box(
                        contentAlignment = BoxAlignment.Center,
                        modifier = Modifier.Empty
                            .fillMaxWidth()
                            .height(boxHeight)
                            .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                            .margin(vertical = blockPadding),
                    ) {
                        Text(
                            text = "Centered in Box",
                            modifier = Modifier.Empty
                                .backgroundColor(Theme.colors.primary)
                                .padding(horizontal = cardPadding, vertical = blockPadding),
                        )
                        Text(
                            text = "Pinned",
                            modifier = Modifier.Empty
                                .align(BoxAlignment.BottomEnd)
                                .offset(x = -blockPadding.toFloat(), y = -blockPadding.toFloat())
                                .zIndex(1f)
                                .backgroundColor(Theme.colors.accent)
                                .padding(horizontal = blockPadding, vertical = 4.dp),
                        )
                    }
                    Row(
                        arrangement = MainAxisArrangement.Start,
                        spacing = 0,
                        verticalAlignment = VerticalAlignment.Center,
                        modifier = Modifier.Empty
                            .fillMaxWidth()
                            .padding(horizontal = blockPadding, vertical = 4.dp),
                    ) {
                        Text(
                            text = "Left pane",
                            modifier = Modifier.Empty
                                .align(VerticalAlignment.Top)
                                .backgroundColor(SurfaceDefaults.backgroundColor())
                                .padding(cardPadding),
                        )
                        FlexibleSpacer(modifier = Modifier.Empty.width(cardPadding))
                        Text(
                            text = "Right pane",
                            modifier = Modifier.Empty
                                .align(VerticalAlignment.Bottom)
                                .backgroundColor(Theme.colors.accent)
                                .padding(cardPadding),
                        )
                    }
                    AndroidView(
                        key = "legacy_summary",
                        modifier = Modifier.Empty
                            .alpha(0.85f)
                            .padding(blockPadding),
                        factory = { context ->
                            TextView(context)
                        },
                        update = { view ->
                            (view as TextView).text = "Legacy TextView mirror: ${clickSummaryState.value}"
                        },
                    )
                    Button(
                        text = "Increment",
                        modifier = Modifier.Empty.padding(blockPadding),
                        onClick = {
                            clickCountState.value = clickCountState.value + 1
                        },
                    )
                    Button(
                        text = if (reversedState.value) "Show A-B-C" else "Show C-B-A",
                        modifier = Modifier.Empty.padding(blockPadding),
                        onClick = {
                            reversedState.value = !reversedState.value
                        },
                    )
                    Button(
                        text = if (alternateLabelsState.value) {
                            "Show primary labels"
                        } else {
                            "Show alternate labels"
                        },
                        modifier = Modifier.Empty.padding(blockPadding),
                        onClick = {
                            alternateLabelsState.value = !alternateLabelsState.value
                        },
                    )
                    Button(
                        text = if (transientPanelVisibleState.value) {
                            "Hide transient panel"
                        } else {
                            "Show transient panel"
                        },
                        modifier = Modifier.Empty.padding(blockPadding),
                        onClick = {
                            transientPanelVisibleState.value = !transientPanelVisibleState.value
                        },
                    )
                    if (transientPanelVisibleState.value) {
                        key("transient-panel") {
                            val panelTapState = remember { mutableStateOf(0) }
                            Column(
                                arrangement = MainAxisArrangement.SpaceAround,
                                horizontalAlignment = HorizontalAlignment.Center,
                                spacing = 4,
                                modifier = Modifier.Empty
                                    .backgroundColor(SurfaceDefaults.variantBackgroundColor())
                                    .margin(vertical = blockPadding)
                                    .padding(blockPadding),
                            ) {
                                Text(
                                    text = "Transient keyed panel",
                                    modifier = Modifier.Empty
                                        .align(HorizontalAlignment.Start)
                                        .padding(4.dp),
                                )
                                Button(
                                    text = "Panel taps: ${panelTapState.value}",
                                    modifier = Modifier.Empty
                                        .align(HorizontalAlignment.End)
                                        .padding(4.dp),
                                    onClick = {
                                        panelTapState.value = panelTapState.value + 1
                                    },
                                )
                            }
                        }
                    }
                    Text(
                        text = "Visibility sample: hidden when reversed",
                        modifier = Modifier.Empty
                            .visibility(if (reversedState.value) Visibility.Gone else Visibility.Visible)
                            .padding(blockPadding),
                    )
                    val keyedItems = if (reversedState.value) {
                        listOf("C", "B", "A")
                    } else {
                        listOf("A", "B", "C")
                    }.map { id ->
                        DemoListItem(
                            id = id,
                            title = if (alternateLabelsState.value) {
                                "Lazy item $id (alt)"
                            } else {
                                "Lazy item $id"
                            },
                        )
                    }
                    LazyColumn(
                        items = keyedItems,
                        key = { item -> item.id },
                        modifier = Modifier.Empty
                            .fillMaxWidth()
                            .height(listHeight)
                            .padding(blockPadding),
                    ) { item ->
                        val itemCountState = remember { mutableStateOf(0) }
                        Column(
                            key = item.id,
                            modifier = Modifier.Empty
                                .backgroundColor(SurfaceDefaults.backgroundColor())
                                .padding(blockPadding),
                        ) {
                            Text(
                                text = item.title,
                                modifier = Modifier.Empty.padding(4.dp),
                            )
                            Button(
                                text = "Item ${item.id} taps: ${itemCountState.value}",
                                modifier = Modifier.Empty.padding(4.dp),
                                onClick = {
                                    itemCountState.value = itemCountState.value + 1
                                },
                            )
                        }
                    }
                }
                }
            }
        }
    }

    private data class DemoListItem(
        val id: String,
        val title: String,
    )
}
