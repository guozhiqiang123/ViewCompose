package com.viewcompose.widget.core

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeDefaultsHardcodedColorGuardTest {
    @Test
    fun `defaults should not hardcode legacy error literal`() {
        val defaultsDir = resolveDefaultsSourceDir()
        val kotlinFiles = defaultsDir.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .toList()

        val violations = kotlinFiles.filter { file ->
            LEGACY_ERROR_LITERAL.containsMatchIn(file.readText())
        }.map { it.path }

        assertTrue(
            "Theme defaults hardcoded color guard violated in ${defaultsDir.path}:\n${violations.joinToString("\n")}",
            violations.isEmpty(),
        )
    }

    private fun resolveDefaultsSourceDir(): File {
        val candidates = listOf(
            File("src/main/java/com/viewcompose/widget/core/defaults"),
            File("viewcompose-widget-core/src/main/java/com/viewcompose/widget/core/defaults"),
            File(System.getProperty("user.dir"), "src/main/java/com/viewcompose/widget/core/defaults"),
            File(System.getProperty("user.dir"), "viewcompose-widget-core/src/main/java/com/viewcompose/widget/core/defaults"),
        )
        return candidates.firstOrNull { it.exists() && it.isDirectory }
            ?: error("Cannot locate widget-core defaults source directory from user.dir=${System.getProperty("user.dir")}")
    }

    companion object {
        private val LEGACY_ERROR_LITERAL = Regex("""0xFFB3261E""", RegexOption.IGNORE_CASE)
    }
}
