package com.gzq.uiframework

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.renderInto

class MainActivity : AppCompatActivity() {
    private val clickCountState = mutableStateOf(0)
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

        renderInto(root) {
            Column(
                modifier = Modifier.Empty.padding(24),
            ) {
                Text(
                    text = "UIFramework",
                    modifier = Modifier.Empty.padding(8),
                )
                Text(
                    text = "Declarative UI on Android Views",
                    modifier = Modifier.Empty.padding(8),
                )
                Text(text = "Clicks: ${clickCountState.value}")
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
                keyedItems.forEach { item ->
                    Text(
                        text = "Keyed item $item",
                        key = item,
                        modifier = Modifier.Empty.padding(8),
                    )
                }
            }
        }
    }
}
