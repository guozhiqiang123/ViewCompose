package com.gzq.uiframework

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gzq.uiframework.widget.core.Box
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.buildVNodeTree

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

        val tree = buildVNodeTree {
            Column {
                Text(text = "UIFramework")
                Box {
                    Text(text = "Phase 1 bootstrap")
                }
            }
        }
        Log.d("UIFramework", "Bootstrapped vnode tree size=${tree.size}")
    }
}
