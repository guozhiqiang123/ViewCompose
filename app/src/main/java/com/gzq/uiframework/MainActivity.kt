package com.gzq.uiframework

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.runtime.derivedStateOf
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.AndroidView
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.DisposableEffect
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.Text
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
            DisposableEffect(key = clickSummaryState.value) {
                title = "UIFramework - ${clickSummaryState.value}"
                {
                    if (title == "UIFramework - ${clickSummaryState.value}") {
                        title = "UIFramework"
                    }
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
                    modifier = Modifier.Empty.padding(8),
                )
                Text(text = "Clicks: ${clickCountState.value}")
                Text(
                    text = clickSummaryState.value,
                    modifier = Modifier.Empty.padding(8),
                )
                AndroidView(
                    key = "legacy_summary",
                    modifier = Modifier.Empty.padding(8),
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
                val keyedItems = if (reversedState.value) {
                    listOf("C", "B", "A")
                } else {
                    listOf("A", "B", "C")
                }
                LazyColumn(
                    items = keyedItems,
                    key = { item -> item },
                    modifier = Modifier.Empty.padding(8),
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
