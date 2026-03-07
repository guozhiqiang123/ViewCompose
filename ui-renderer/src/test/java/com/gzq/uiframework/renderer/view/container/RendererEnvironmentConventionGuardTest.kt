package com.gzq.uiframework.renderer.view.container

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class RendererEnvironmentConventionGuardTest {
    @Test
    fun `container layouts should not define private density or dpToPx`() {
        val sourceDir = resolveContainerSourceDir()
        val kotlinFiles = sourceDir.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .toList()

        val violations = mutableListOf<String>()
        kotlinFiles.forEach { file ->
            val content = file.readText()
            if (FORBIDDEN_PRIVATE_DENSITY.containsMatchIn(content)) {
                violations += "${file.name}: found private density from displayMetrics"
            }
            if (FORBIDDEN_PRIVATE_DP_TO_PX.containsMatchIn(content)) {
                violations += "${file.name}: found private dpToPx helper"
            }
        }

        assertTrue(
            "Renderer environment convention violated in ${sourceDir.path}:\n${violations.joinToString("\n")}",
            violations.isEmpty(),
        )
    }

    private fun resolveContainerSourceDir(): File {
        val candidates = listOf(
            File("src/main/java/com/gzq/uiframework/renderer/view/container"),
            File("ui-renderer/src/main/java/com/gzq/uiframework/renderer/view/container"),
            File(System.getProperty("user.dir"), "src/main/java/com/gzq/uiframework/renderer/view/container"),
            File(System.getProperty("user.dir"), "ui-renderer/src/main/java/com/gzq/uiframework/renderer/view/container"),
        )
        return candidates.firstOrNull { it.exists() && it.isDirectory }
            ?: error("Cannot locate renderer container source directory from user.dir=${System.getProperty("user.dir")}")
    }

    companion object {
        private val FORBIDDEN_PRIVATE_DENSITY =
            Regex("""private\s+val\s+\w*density\w*\s*=\s*.*displayMetrics\.density""")
        private val FORBIDDEN_PRIVATE_DP_TO_PX = Regex("""private\s+fun\s+dpToPx\s*\(""")
    }
}
