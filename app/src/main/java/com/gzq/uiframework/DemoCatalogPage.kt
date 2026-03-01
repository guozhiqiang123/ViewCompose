package com.gzq.uiframework

import android.content.Intent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.fillMaxSize
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.margin
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.widget.core.Button
import com.gzq.uiframework.widget.core.ButtonVariant
import com.gzq.uiframework.widget.core.Column
import com.gzq.uiframework.widget.core.LazyColumn
import com.gzq.uiframework.widget.core.Surface
import com.gzq.uiframework.widget.core.SurfaceVariant
import com.gzq.uiframework.widget.core.Text
import com.gzq.uiframework.widget.core.TextDefaults
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.dp

internal fun UiTreeBuilder.DemoCatalogPage(
    root: ViewGroup,
) {
    val activity = root.context as? AppCompatActivity
    LazyColumn(
        items = listOf("intro", "available", "planned"),
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "intro" -> DemoSection(
                title = "Capability Modules",
                subtitle = "The demo now follows module-level manual test paths so each activity corresponds to one framework concern and one benchmark-friendly entry.",
            ) {
                Text(
                    text = "Structure follows the Compose Tutorials idea: foundations, state, layouts, input, collections, interop, diagnostics, then planned chapters for gestures, animation, graphics, and navigation.",
                )
                Text(
                    text = "Use this catalog as the stable entry for manual regression and macrobenchmark flows.",
                    color = TextDefaults.secondaryColor(),
                )
            }

            "available" -> DemoSection(
                title = "Implemented Modules",
                subtitle = "Each card opens a dedicated Activity so state, diagnostics, and benchmark setup do not depend on a giant in-page pager.",
            ) {
                AVAILABLE_DEMO_MODULES.forEach { module ->
                    ModuleCard(
                        module = module,
                        actionLabel = "Open ${module.title}",
                        actionVariant = ButtonVariant.Primary,
                        onClick = {
                            val target = module.activityClass ?: return@ModuleCard
                            activity?.startActivity(Intent(activity, target))
                        },
                    )
                }
            }

            else -> DemoSection(
                title = "Planned Modules",
                subtitle = "These chapters stay visible so the demo roadmap keeps matching the Compose capability map even before implementation lands.",
            ) {
                PLANNED_DEMO_MODULES.forEach { module ->
                    ModuleCard(
                        module = module,
                        actionLabel = "Preview",
                        actionVariant = ButtonVariant.Outlined,
                        onClick = {
                            val target = module.activityClass ?: return@ModuleCard
                            activity?.startActivity(Intent(activity, target))
                        },
                    )
                }
            }
        }
    }
}

private fun UiTreeBuilder.ModuleCard(
    module: DemoModule,
    actionLabel: String,
    actionVariant: ButtonVariant,
    onClick: () -> Unit,
) {
    Surface(
        variant = if (module.status == DemoModuleStatus.Available) {
            SurfaceVariant.Default
        } else {
            SurfaceVariant.Variant
        },
        modifier = Modifier
            .fillMaxWidth()
            .margin(bottom = 12.dp),
    ) {
        Column(
            spacing = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(text = module.title)
            Text(
                text = module.subtitle,
                color = TextDefaults.secondaryColor(),
            )
            Text(
                text = "Manual focus: ${module.manualFocus}",
                color = TextDefaults.secondaryColor(),
            )
            Text(
                text = "Benchmark path: ${module.benchmarkPath}",
                color = TextDefaults.secondaryColor(),
            )
            Button(
                text = actionLabel,
                variant = actionVariant,
                onClick = onClick,
                modifier = Modifier.margin(top = 4.dp),
            )
        }
    }
}
