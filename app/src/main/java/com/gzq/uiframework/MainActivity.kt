package com.gzq.uiframework

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.renderInto

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        renderInto(findViewById(R.id.main)) {
            Column {
                Text(text = "UIFramework")
                Text(text = "Declarative UI on Android Views")
                Text(text = "Phase 1: first render pipeline")
            }
        }
    }
}
