package com.gzq.uiframework

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.Visibility
import com.gzq.uiframework.renderer.modifier.alpha
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.clickable
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.visibility
import com.gzq.uiframework.renderer.modifier.weight
import com.gzq.uiframework.runtime.derivedStateOf
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.AndroidView
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.DisposableEffect
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.Row
import com.gzq.uiframework.widget.core.SideEffect
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.key
import com.gzq.uiframework.widget.core.produceState
import com.gzq.uiframework.widget.core.remember
import com.gzq.uiframework.widget.core.renderInto

class MainActivity : AppCompatActivity() {
    private val reversedState = mutableStateOf(false)

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
            Column(
                modifier = Modifier.Empty
                    .backgroundColor(Color.parseColor("#F4F1EA"))
                    .padding(24),
            ) {
                Text(
                    text = "UIFramework",
                    modifier = Modifier.Empty
                        .backgroundColor(Color.parseColor("#E6D9C6"))
                        .padding(8),
                )
                Text(
                    text = "Declarative UI on Android Views",
                    modifier = Modifier.Empty
                        .clickable {
                            textToggleState.value = !textToggleState.value
                        }
                        .padding(8),
                )
                Text(
                    text = if (textToggleState.value) {
                        "Text modifier click: ON"
                    } else {
                        "Text modifier click: OFF"
                    },
                    modifier = Modifier.Empty.padding(8),
                )
                Text(text = "Clicks: ${clickCountState.value}")
                Text(
                    text = clickSummaryState.value,
                    modifier = Modifier.Empty
                        .alpha(0.7f)
                        .padding(8),
                )
                Text(
                    text = listOrderState.value,
                    modifier = Modifier.Empty.padding(8),
                )
                Row(
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .padding(8),
                ) {
                    Text(
                        text = "Left pane",
                        modifier = Modifier.Empty
                            .backgroundColor(Color.parseColor("#DCEBFF"))
                            .weight(1f)
                            .padding(12),
                    )
                    Text(
                        text = "Right pane",
                        modifier = Modifier.Empty
                            .backgroundColor(Color.parseColor("#FFE4D6"))
                            .weight(1f)
                            .padding(12),
                    )
                }
                AndroidView(
                    key = "legacy_summary",
                    modifier = Modifier.Empty
                        .alpha(0.85f)
                        .padding(8),
                    factory = { context ->
                        TextView(context)
                    },
                    update = { view ->
                        (view as TextView).text = "Legacy TextView mirror: ${clickSummaryState.value}"
                    },
                )
                Button(
                    text = "Increment",
                    modifier = Modifier.Empty.padding(8),
                    onClick = {
                        clickCountState.value = clickCountState.value + 1
                    },
                )
                Button(
                    text = if (reversedState.value) "Show A-B-C" else "Show C-B-A",
                    modifier = Modifier.Empty.padding(8),
                    onClick = {
                        reversedState.value = !reversedState.value
                    },
                )
                Button(
                    text = if (transientPanelVisibleState.value) {
                        "Hide transient panel"
                    } else {
                        "Show transient panel"
                    },
                    modifier = Modifier.Empty.padding(8),
                    onClick = {
                        transientPanelVisibleState.value = !transientPanelVisibleState.value
                    },
                )
                if (transientPanelVisibleState.value) {
                    key("transient-panel") {
                        val panelTapState = remember { mutableStateOf(0) }
                        Column(
                            modifier = Modifier.Empty
                                .backgroundColor(Color.parseColor("#FDECC8"))
                                .padding(8),
                        ) {
                            Text(
                                text = "Transient keyed panel",
                                modifier = Modifier.Empty.padding(4),
                            )
                            Button(
                                text = "Panel taps: ${panelTapState.value}",
                                modifier = Modifier.Empty.padding(4),
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
                        .padding(8),
                )
                val keyedItems = if (reversedState.value) {
                    listOf("C", "B", "A")
                } else {
                    listOf("A", "B", "C")
                }
                LazyColumn(
                    items = keyedItems,
                    key = { item -> item },
                    modifier = Modifier.Empty
                        .fillMaxWidth()
                        .height(220)
                        .padding(8),
                ) { item ->
                    val itemCountState = remember { mutableStateOf(0) }
                    Column(
                        key = item,
                        modifier = Modifier.Empty
                            .backgroundColor(Color.parseColor("#FFF7ED"))
                            .padding(8),
                    ) {
                        Text(
                            text = "Lazy item $item",
                            modifier = Modifier.Empty.padding(4),
                        )
                        Button(
                            text = "Item $item taps: ${itemCountState.value}",
                            modifier = Modifier.Empty.padding(4),
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
