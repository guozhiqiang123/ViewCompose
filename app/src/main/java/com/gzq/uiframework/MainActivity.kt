package com.gzq.uiframework

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.renderInto

class MainActivity : AppCompatActivity() {
    private val statusState = mutableStateOf("Phase 1: first render pipeline")

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
            Column {
                Text(text = "UIFramework")
                Text(text = "Declarative UI on Android Views")
                Text(text = statusState.value)
            }
        }

        root.postDelayed(
            {
                statusState.value = "Phase 2: root re-render from mutableStateOf"
            },
            1200L,
        )
    }
}
